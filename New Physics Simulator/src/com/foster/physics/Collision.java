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
		  mtv = ||S_ab|| - (||S_ab|| - r_a + ||S_ab|| - r_b)
		  mtv = -||S_ab|| + r_a + r_b */
		Vector mtv_norm = cent_axis.norm();
		Vector mtv = mtv_norm.get();
		double mtvlen = -Math.sqrt(a_to_b_dist_sq) + a.radius + b.radius;
		mtv.scale(mtvlen);
		
		
		//move objects so there is zero penetration
		Vector v_ab = Vector.sub(a.vel, b.vel);
		double v_ab_mag = v_ab.mag();
		//get the time required to move objects
		double t_req = Vector.invdotmag(v_ab, mtv_norm, mtvlen) / v_ab_mag;
		//get the time left over post collision
		double t_post = Environment.tstep - t_req;
		a.pos.decrement(Vector.mpy(a.vel, t_req));
		b.pos.decrement(Vector.mpy(b.vel, t_req));
		
		//change objects velocity
		//va = (mb(2ub - ua) + maua) / (ma + mb)
		//vb = (ma(2ua - ub) + mbub) / (ma + mb)
		Vector va = Vector.mpy(Vector.add(Vector.mpy(Vector.sub(Vector.mpy(b.vel, 2), a.vel), b.mass), Vector.mpy(a.vel, a.mass)), 1 / (a.mass + b.mass));
		Vector vb = Vector.mpy(Vector.add(Vector.mpy(Vector.sub(Vector.mpy(a.vel, 2), b.vel), a.mass), Vector.mpy(b.vel, b.mass)), 1 / (a.mass + b.mass));
		a.vel = va;
		b.vel = vb;
		
		//update object position
		a.pos.increment(Vector.mpy(a.vel, t_post));
		b.pos.increment(Vector.mpy(b.vel, t_post));
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
	 * calls the collide(Circle a, Polygon b) with the arguments swapped
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
		if (a.pos.getx() - a.radius < 0 || a.pos.getx() + a.radius > Environment.dispwidth)
		{
			a.vel = new Vector(-a.vel.getx(), a.vel.gety());
		}
		if (a.pos.gety() - a.radius < 0 || a.pos.gety() + a.radius > Environment.dispheight)
		{
			a.vel = new Vector(a.vel.getx(), -a.vel.gety());
		}
	}
	
	static void collidewalls(Polygon a)
	{
		
	}
}