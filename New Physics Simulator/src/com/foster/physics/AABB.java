package com.foster.physics;

/**Axis Aligned Bounding Box Class - creates a box and detects collisions between boxes
 * @author reed
 */
public class AABB
{
	Vector min;
	Vector max;
	
	/**Constructor for AABB
	 * @param min = position vector of minimum vertex
	 * @param max = position vector of maximum vertex
	 */
	AABB(Vector min, Vector max)
	{
		this.min = min;
		this.max = max;
	}
	
	AABB get()
	{
		return new AABB(min, max);
	}
	
	/**Determines if two AABBs are colliding
	 * @param a = 1st AABB
	 * @param b = 2nd AABB
	 * @return boolean; collision or no collision
	 */
	static boolean collide(AABB a, AABB b)
	{
		//determine if there's a collision
		if (a.max.getx() < b.min.getx() || a.min.getx() > b.max.getx())
		{
			//not colliding
			return false;
		}
		if (a.max.gety() < b.min.gety() || a.min.gety() > b.max.gety())
		{
			//not colliding
			return false;
		}
		return true;
	}
}