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
		
		//System.out.println("A and B are not the same circle");
		
		//broad-phase: test if AABBs collide
		if (!collide(a.bounds, b.bounds))
			return;
		
		//System.out.println("A and B's AABBs are colliding");
		
		//narrow-phase: test if circles collide
		Vector cent_axis = Vector.sub(a.pos, b.pos);
		double a_to_b_dist_sq = cent_axis.magSq();
		double r_sq = (a.radius + b.radius) * (a.radius + b.radius);
		if (a_to_b_dist_sq > r_sq)
			return;
		
		//System.out.println("A and B are colliding");
		
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
		Vector vab = Vector.sub(a.vel, b.vel);
		double vx = vab.getx();
		double vy = vab.gety();
		double dx = cent_axis.getx();
		double dy = cent_axis.gety();
		double ra = a.radius;
		double rb = b.radius;
		double dx2vy2 = dx * dx * vy * vy;
		double dxdyvxvy = dx * dy * vx * vy;
		double dy2vx2 = dy * dy * vx * vx;
		double rarb2 = (ra + rb) * (ra + rb);
		double vx2vy2 = vx * vx + vy * vy;
		double dxvx = dx * vx;
		double dyvy = dy * vy;
		double t = -(Math.sqrt(-dx2vy2 + 2 * dxdyvxvy - dy2vx2 + rarb2 * vx2vy2) + dxvx + dyvy) / vx2vy2;
		//double t = -(Math.sqrt(-(dx * dx) * (vy * vy) + 2 * (dx * dy) * (vx * vy) - (dy * dy) * (vx * vx) + (ra + rb) * (ra + rb) * (vx * vx + vy * vy)) + dx * vx + dy * vy)/ (vx * vx + vy * vy);
		//double precollide_t = Environment.tstep * Vector.invdotmag(Vector.mpy(vab, Environment.tstep), mtv_norm, mtvlen) / vab.mag();
		a.pos.increment(Vector.mpy(a.vel, t)); //multiply by 1.01 to make sure shapes are no longer in contact
		b.pos.increment(Vector.mpy(b.vel, t));
		
		Vector rpap = Vector.mpy(mtv_norm, a.radius);
		Vector rpbp = Vector.mpy(mtv_norm, b.radius);
		
		Vector J = Vector.mpy(Vector.mpy(Vector.sub(a.vel, b.vel), -(1 + Math.min(a.e, b.e))), 1 / (a.invmass + b.invmass + (Vector.dot(rpap, mtv) * Vector.dot(rpap, mtv)) * a.invI + (Vector.dot(rpbp, mtv) * Vector.dot(rpbp, mtv)) * b.invI));
		double j = Vector.dot(J, mtv_norm);
		J = Vector.add(Vector.mpy(mtv_norm, j), Vector.mpy(Vector.mpy(mtv_norm.perp(), Vector.dot(vab, mtv_norm.perp())).norm(), j * Math.max(a.mu_kinetic, b.mu_kinetic)));
		a.vel.increment(Vector.mpy(J, a.invmass));
		b.vel.increment(Vector.mpy(J, -b.invmass));
		a.omega += Vector.dot(rpap, J) * a.invI;
		b.omega -= Vector.dot(rpbp, J) * b.invI;
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
		if (a.pos.getx() - a.radius < 0 || a.pos.getx() + a.radius > Environment.dispwidth) //colliding with walls
		{
			a.vel = new Vector(-a.e * a.vel.getx(), a.vel.gety());
		}
		if (a.pos.gety() - a.radius < 0 || a.pos.gety() + a.radius > Environment.dispheight) //colliding with floor/ceiling
		{
			a.vel = new Vector(a.vel.getx(), -a.e * a.vel.gety());
		}
	}
	
	static void collidewalls(Polygon a)
	{
		
	}
}