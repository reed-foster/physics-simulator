package com.foster.physics;

/**Subclass of Body - creates shapes with multiple vertices
 * @author reed
 */
public class Polygon extends Body
{
	Vector[] vertices;
	AABB bounds;
	
	Type type;
	
	/**Default Polygon constructor
	 * @param mass = mass of polygon
	 * @param pos = position of polygon's center of mass
	 * @param vel = velocity of polygon
	 * @param acc = acceleration of polygon
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 * @param vertices = array of vectors that define vertices from the center of mass
	 */
	Polygon(double mass, Vector pos, Vector vel, Vector acc, double mu, double e, Vector[] vertices)
	{
		super(mass, pos, vel, acc, mu, e);
		this.vertices = vertices;
		bounds = getAABB(pos, vertices);
		type = Type.polygon;
	}
	
	/**Constructor for polygons with 0 velocity and 0 acceleration
	 * @param mass = mass of polygon
	 * @param pos = position of polygon's center of mass
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 * @param vertices = array of vectors that define vertices from the center of mass
	 */
	Polygon(double mass, Vector pos, double mu, double e, Vector[] vertices)
	{
		super(mass, pos, mu, e);
		this.vertices = vertices;
		bounds = getAABB(pos, vertices);
		type = Type.polygon;
	}
	
	/**Constructor for polygons with 0 velocity, 0 acceleration, 0 friction, and e of 1
	 * @param mass = mass of polygon
	 * @param pos = position of polygon's center of mass
	 * @param vertices = array of vectors that define vertices from the center of mass
	 */
	Polygon(double mass, Vector pos, Vector[] vertices)
	{
		super(mass, pos);
		this.vertices = vertices;
		bounds = getAABB(pos, vertices);
		type = Type.polygon;
	}
	
	/**Gets the AABB of a Polygon based on its pos and vertices
	 * @param pos = position vector of the polygon
	 * @param vertices = list of vertices
	 * @return AABB (position vectors of minimum vertex and maximum vertex for the smallest AABB around the polygon)
	 */
	private AABB getAABB(Vector pos, Vector[] vertices)
	{
		double max_x, max_y, min_x, min_y;
		max_x = max_y = min_x = min_y = 0;
		for (int i = 0; i < vertices.length; i++)
		{
			double x = vertices[i].getx();
			if (x > max_x)
			{
				max_x = x;
			}
			if (x < min_x)
			{
				min_x = x;
			}
			double y = vertices[i].gety();
			if (y > max_y)
			{
				max_y = y;
			}
			if (y < min_y)
			{
				min_y = y;
			}
		}
		Vector max_vertex = new Vector(max_x, max_y);
		Vector min_vertex = new Vector(min_x, min_y);
		return new AABB(Vector.add(pos, min_vertex), Vector.add(pos, max_vertex)); //AABB = (pos+min, pos+max)
	}
	
	/**Gets the minimum and maximum values of the projection of a Polygon onto a Vector axis
	 * @param axis = axis to project Polygon onto
	 * @return Vector (minimum, maximum)
	 */
	Vector project(Vector axis)
	{
		double minproj, maxproj, currentproj;
		minproj = maxproj = Vector.project(this.pos, axis);
		for (int i = 0; i < vertices.length; i++)
		{
			currentproj = Vector.project(vertices[i], axis);
			if (currentproj < minproj)
				minproj = currentproj;
			if (currentproj > maxproj)
				maxproj = currentproj;
		}
		return new Vector(minproj, maxproj);
	}
}