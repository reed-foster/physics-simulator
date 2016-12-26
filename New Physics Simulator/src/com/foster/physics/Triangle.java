package com.foster.physics;

/**Triangle Class
 * 3 vectors that define the  vertices of the triangle relative to the origin (0,0)
 * @author reed
 */
public class Triangle
{
	Vector p1;
	Vector p2;
	Vector p3;
	
	Triangle(Vector p1, Vector p2, Vector p3)
	{
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
}