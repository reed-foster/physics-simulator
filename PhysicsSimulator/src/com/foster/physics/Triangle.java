package com.foster.physics;

/**Triangle Class
 * 3 vectors that define the  vertices of the triangle relative to the origin (0,0)
 * @author reed
 */
public class Triangle
{
	private Vector[] vertices;
	
	/**Constructor for triangles
	 * @param p1 = 1st point vector (from origin)
	 * @param p2 = 2nd vector
	 * @param p3 = 3rd vector
	 */
	Triangle(Vector p1, Vector p2, Vector p3)
	{
		this.vertices = new Vector[3];
		this.vertices[0] = p1;
		this.vertices[1] = p2;
		this.vertices[2] = p3;
	}
	
	/**Get the Vector of the vertex specified by idx
	 * @param idx = index of the desired vector [0,2]
	 * @return vertices[idx]
	 */
	Vector getp(int idx)
	{
		if (idx > 2 || idx < 0)
			System.err.println("Attempt to reference an invald triangle vertex");
		return vertices[idx];
	}
	
	/**Sets the Vector of the vertex specified by idx
	 * @param idx = index of desired vector [0,2]
	 * @param p1
	 */
	void setp(int idx, Vector p1)
	{
		if (idx > 2 || idx < 0)
			System.err.println("Attempt to reference an invald triangle vertex");
		this.vertices[idx] = p1;
	}
}