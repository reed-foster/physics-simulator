package com.foster.physics;

/**Detects collisions
 * @author reed
 */
public class Collision
{
	/**Collides two Circles
	 * First detect then respond
	 * Broad-phase (AABB) and narrow-phase (modified SAT, uses the vector between centers as the testing axis)
	 * If Circles are colliding, resolve collision
	 * @param a = 1st Circle
	 * @param b = 2nd Circle
	 */
	static void collide(Circle a, Circle b)
	{
		//can't collide an object with itself
		if (a.equals(b))
			return;
		
		//broad-phase: test if AABBs collide
		if (!collide(a.bounds, b.bounds))
			return;
		
		//narrow-phase: test if circles collide
		Vector cent_axis = Vector.sub(a.pos, b.pos);
		double a_to_b_dist_sq = cent_axis.magSq();
		double r_sq = (a.radius + b.radius) * (a.radius + b.radius);
		if (a_to_b_dist_sq > r_sq)
			return;
		
		//circles collided, do collision response
		//get MTV (minimum translation vector)
		/*S_ab = vector between circle centers
		  r_a = radius of circle a
		  r_b = radius of circle b
		  ||S|| indicates the magnitude of S
		  mtv = -||S_ab|| + r_a + r_b */
		Vector mtv_norm = cent_axis.norm();
		Vector mtv = mtv_norm.get();
		double mtvlen = -Math.sqrt(a_to_b_dist_sq) + a.radius + b.radius;
		mtv.scale(mtvlen);
		
		//move objects so there is zero penetration
		Vector vab = Vector.sub(b.vel, a.vel);
		Vector displacement = Vector.invdot(vab.norm(), mtv_norm, mtvlen);
		a.pos.increment(Vector.mpy(a.vel.norm(), Vector.dot(a.vel, displacement)* Environment.tstep));
		b.pos.decrement(Vector.mpy(b.vel.norm(), Vector.dot(b.vel, displacement) * Environment.tstep));

		if (vab.magSq() < 0.0001 && vab.magSq() > -0.0001) //Circles are stationary but touching, apply normal force
		{
			a.addForce(Vector.mpy(mtv_norm, -Vector.dot(a.netforce, mtv_norm)));
			b.addForce(Vector.mpy(mtv_norm, -Vector.dot(b.netforce, mtv_norm)));
		}
		else
		{
			Vector rpap = Vector.mpy(mtv_norm, a.radius).perp();
			Vector rpbp = Vector.mpy(mtv_norm, b.radius).perp();
			
			Vector J = Vector.mpy(Vector.mpy(Vector.sub(a.vel, b.vel), -(1 + Math.min(a.e, b.e))),
					1 / (a.invmass + b.invmass + (Vector.dot(rpap, mtv) * Vector.dot(rpap, mtv)) * a.invI + (Vector.dot(rpbp, mtv) * Vector.dot(rpbp, mtv)) * b.invI));
			//double jmag = J.mag();
			if(((Double)J.getx()).isNaN())
			{
				System.out.println("J is NaN");
				System.out.printf("rpap = (%f, %f), rpbp = (%f, %f), mtv = (%f, %f)", rpap.getx(), rpap.gety(), rpbp.getx(), rpbp.gety(), mtv.getx(), mtv.gety());
				System.out.printf("a.im = %f, b.im = %f, rp.mtv = %f, rp.mtv = %f\n\n", a.invmass, b.invmass, Vector.dot(rpap, mtv), Vector.dot(rpbp, mtv));
			}
			double j = Vector.dot(J, mtv_norm);
			//J = mtv_norm * j + -(vab . mtv_norm.perp()) * max(a.muk, b.muk) * mtv_norm.perp()
			Vector fdir = Vector.mpy(mtv_norm.perp(), Vector.dot(vab, mtv_norm.perp())).norm();
			J = Vector.add(Vector.mpy(mtv_norm, j), Vector.mpy(fdir, j * Math.max(a.mu_kinetic, b.mu_kinetic)));
			a.vel.increment(Vector.mpy(J, a.invmass));
			b.vel.decrement(Vector.mpy(J, b.invmass));
			a.omega += Vector.dot(rpap, J) * a.invI;
			b.omega -= Vector.dot(rpbp, J) * b.invI;
		}
	}
	
	/**Collides a Circle with a Polygon
	 * First detect then respond
	 * Broad-phase (AABB) and narrow-phase (modified SAT)
	 * If Polygon and Circle are colliding, resolve collision
	 * @param a = Circle
	 * @param b = Polygon
	 */
	static void collide(Circle a, Polygon b)
	{
		//broad-phase: test if AABBs collide
		if (!collide(a.bounds, b.bounds))
			return;
		
		//narrow-phase: SAT on all of polygon's normals
		//first, get the axes for projection
		int numvert = b.vertices.length;
		Vector[] axes = new Vector[numvert];
		for (int i = 0; i < numvert; i++)
		{
			//subtracts the ith vertex from the i-1th vertex to get the i-1th edge
			//instead of using numvert for last index, use 0
			axes[i] = Vector.sub(b.vertices[(i + 1) % numvert], b.vertices[i]).perp();
		}
		
		//iterate over the axes, projecting each shape onto the axes to find overlap
		Vector circleproj, polyproj;
		double circle_min, circle_max, poly_min, poly_max;
		for (int i = 0; i < numvert; i++)
		{
			circleproj = a.project(axes[i]); //gets the projection of the circle onto the axis
			polyproj = b.project(axes[i]); //gets the projection of the polygon onto the axis
			circle_min = circleproj.getx();
			circle_max = circleproj.gety();
			poly_min = polyproj.getx();
			poly_max = polyproj.gety();
			if (circle_max < poly_min || circle_min > poly_max) //projections don't "overlap", so there's no collision
				return;
		}
		
		//shapes are intersecting, do collision response
		//normalize axes to find mtv
		for (int i = 0; i < axes.length; i++)
		{
			axes[i] = axes[i].norm();
		}
	}
	
	/**Collides a Polygon with a Circle
	 * calls collide(Circle a, Polygon b) with the arguments swapped
	 * @param a = Polygon
	 * @param b = Circle
	 */
	static void collide(Polygon a, Circle b)
	{
		collide(b, a);
	}
	
	/**Collides two Polygons
	 * First detect then respond
	 * Broad-phase (AABB) and narrow-phase (SAT)
	 * If Polygons are colliding, resolve collision
	 * @param a = 1st Polygon
	 * @param b = 2nd Polygon
	 */
	static void collide(Polygon a, Polygon b)
	{
		//broad-phase: test if AABBs collide
		if (!collide(a.bounds, b.bounds))
			return;
		
		//narrow-phase: SAT on all of polygon's normals
		//first, get the axes for projection
		int a_numvert = a.vertices.length;
		int b_numvert = b.vertices.length;
		Vector[] axes = new Vector[a_numvert + b_numvert];
		//first loop
		for (int i = 0; i < a_numvert; i++)
		{
			//subtracts the ith vertex from the i-1th vertex to get the i-1th edge
			//instead of using numvert for last index, use 0
			axes[i] = Vector.sub(b.vertices[(i + 1) % a_numvert], b.vertices[i]).perp();
		}
		//second loop
		for (int i = 0; i < b_numvert; i++)
		{
			//subtracts the ith vertex from the i-1th vertex to get the i-1th edge
			//instead of using numvert for last index, use 0
			axes[i + a_numvert] = Vector.sub(b.vertices[(i + 1) % b_numvert], b.vertices[i]).perp();
		}
		
		//iterate over the axes, projecting each shape onto the axes to find overlap
		Vector aproj, bproj;
		double a_min, a_max, b_min, b_max;
		int numaxes = axes.length;
		for (int i = 0; i < numaxes; i++)
		{
			aproj = a.project(axes[i]); //gets the projection of the circle onto the axis
			bproj = b.project(axes[i]); //gets the projection of the polygon onto the axis
			a_min = aproj.getx();
			a_max = aproj.gety();
			b_min = bproj.getx();
			b_max = bproj.gety();
			if (a_max < b_min || a_min > b_max) //projections don't "overlap", so there's no collision
				return;
		}
		
		//shapes collide, calc mtv and resolve collision
	}
	
	static boolean collide(AABB a, AABB b)
	{
		if (a.max.getx() < b.min.getx() || a.min.getx() > b.max.getx())
			return false;
		if (a.max.gety() < b.min.gety() || a.min.gety() > b.min.gety())
			return false;
		return true;
	}
	
	static void collidewalls(Circle a)
	{
		double xmin = a.pos.getx() - a.radius;
		double xmax = a.pos.getx() + a.radius;
		double ymin = a.pos.gety() - a.radius;
		double ymax = a.pos.gety() + a.radius;
		double xpenetration = xmin <= 0 ? xmax >= Environment.dispwidth ? xmax - Environment.dispwidth : -xmin : 0;
		double ypenetration = ymin <= 0 ? ymax >= Environment.dispheight ? ymax - Environment.dispheight : -ymin : 0;
		if (xmin <= 0)
		{
			double penetrationdepth = a.pos.getx() - a.radius;
			Vector displacement = Vector.invdot(a.vel, Vector.ihat, penetrationdepth);
			a.pos.increment(Vector.mpy(a.vel.norm(), Vector.dot(a.vel, displacement) * Environment.tstep));

			Vector rpap = Vector.mpy(Vector.ihat, -a.radius);
			Vector mtv = Vector.mpy(Vector.ihat, penetrationdepth);
			applyimpulse(a, rpap, mtv);
		}
		else if (xmax >= Environment.dispwidth)
		{
			double penetrationdepth = a.pos.getx() + a.radius - Environment.dispwidth;
			Vector displacement = Vector.invdot(a.vel, Vector.ihat, penetrationdepth);
			a.pos.increment(Vector.mpy(a.vel.norm(), -Vector.dot(a.vel, displacement) * Environment.tstep));
			
			Vector rpap = Vector.mpy(Vector.ihat, a.radius);
			Vector mtv = Vector.mpy(Vector.ihat, -penetrationdepth);
			applyimpulse(a, rpap, mtv);
		}
		if (ymin <= 0)
		{
			double penetrationdepth = a.pos.gety() - a.radius;
			Vector displacement = Vector.invdot(a.vel, Vector.jhat, penetrationdepth);
			a.pos.increment(Vector.mpy(a.vel.norm(), Vector.dot(a.vel, displacement) * Environment.tstep));

			Vector rpap = Vector.mpy(Vector.jhat, -a.radius);
			Vector mtv = Vector.mpy(Vector.jhat, penetrationdepth);
			applyimpulse(a, rpap, mtv);
		}
		else if (ymax >= Environment.dispheight)
		{
			double penetrationdepth = a.pos.gety() + a.radius - Environment.dispheight;
			Vector displacement = Vector.invdot(a.vel, Vector.jhat, penetrationdepth);
			a.pos.increment(Vector.mpy(a.vel.norm(), -Vector.dot(a.vel, displacement) * Environment.tstep));
			Vector rpap = Vector.mpy(Vector.jhat, a.radius);
			Vector mtv = Vector.mpy(Vector.jhat, -penetrationdepth);
			applyimpulse(a, rpap, mtv);
//			Vector J = Vector.mpy(Vector.mpy(a.vel, -(1 + a.e)), 1 / (a.invmass + (Vector.dot(rpap, mtv) * Vector.dot(rpap, mtv)) * a.invI));
//			double j = Vector.dot(J, Vector.jhat);
//			Vector fdir = Vector.mpy(Vector.ihat, Vector.dot(a.vel, Vector.ihat)).norm();
//			J = Vector.add(Vector.mpy(Vector.jhat, j), Vector.mpy(fdir, j * a.mu_kinetic));
//			a.vel.increment(Vector.mpy(J, a.invmass));
//			a.omega += Vector.dot(rpap, J) * a.invI;
		}
	}
	
	static void collidewalls(Polygon a)
	{
		
	}
	
	static private void applyimpulse(Body a, Vector rp, Vector mtv)
	{
		Vector J = Vector.mpy(Vector.mpy(a.vel, -(1 + a.e)), 1 / (a.invmass + (Vector.dot(rp, mtv) * Vector.dot(rp, mtv)) * a.invI));
		a.vel.increment(Vector.mpy(Vector.mpy(mtv.norm(), Vector.dot(mtv.norm(), J)), a.invmass));
		a.omega += Vector.dot(rp, J) * a.invI;
	}
}