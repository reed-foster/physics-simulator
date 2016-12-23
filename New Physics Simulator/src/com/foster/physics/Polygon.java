package com.foster.physics;

/**Subclass of Body - creates shapes with multiple vertices
 * @author reed
 */
public class Polygon extends Body
{
	Vector[] vertices;
	AABB bounds;
	
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
		Vector newpos = this.getCenterOfMass();
		Vector absvert;
		for(Vector i: this.vertices)
		{
			absvert = Vector.add(this.pos, i);
			i = Vector.sub(absvert, newpos);
		}
		this.I = this.getI();
		this.invI = this.I == 0 ? 0 : 1 / this.I;
		bounds = getAABB(pos, vertices);
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
		Vector newpos = this.getCenterOfMass();
		Vector absvert;
		for(Vector i: this.vertices)
		{
			absvert = Vector.add(this.pos, i);
			i = Vector.sub(absvert, newpos);
		}
		this.I = this.getI();
		this.invI = this.I == 0 ? 0 : 1 / this.I;
		bounds = getAABB(pos, vertices);
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
		Vector newpos = this.getCenterOfMass();
		Vector absvert;
		for(Vector i: this.vertices)
		{
			absvert = Vector.add(this.pos, i);
			i = Vector.sub(absvert, newpos);
		}
		this.I = this.getI();
		this.invI = this.I == 0 ? 0 : 1 / this.I;
		bounds = getAABB(pos, vertices);
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
	
	private Vector[] getvertices(Vector[] vertices, double theta) {
		//Vector[] retvert = new Vector[vertices.length];
		for(int i = 0; i < vertices.length; i++)
		{
			vertices[i] = vertices[i].rotate(theta);
		}
		return vertices;
	}
	
	/**Updates object position, velocity and acceleration
	 * @param tstep = interval over which acceleration is applied (smaller values mean smoother, slower movement)
	 */
	void integrate(double tstep)
	{
		super.integratelin(tstep);
		//update angular variables
		this.alpha = this.nettorque * this.invI; //update acceleration
		this.omega += this.alpha * tstep; //update velocity
		double dtheta = 0.5 * this.alpha * tstep * tstep + this.omega * tstep; //get the change in theta
		this.theta = (dtheta + this.theta) % tau; //update position
		this.vertices = getvertices(this.vertices, dtheta);
		AABB aabb = getAABB(this.pos, this.vertices);
		this.bounds = aabb.get();
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
	
	private Vector getCenterOfMass()
	{
		double area = 0;
		double cx = 0;
		double cy = 0;
		int numvert = this.vertices.length;
		for (int i = 0; i < numvert; i++)
		{
			Vector v1 = vertices[i];
			Vector v2 = vertices[(i + 1) % numvert];
			double x1y2_x2y1 = v1.getx() * v2.gety() - v2.getx() * v1.gety();
			area += x1y2_x2y1;
			cx += (v1.getx() + v2.getx()) * x1y2_x2y1;
			cy += (v1.gety() + v2.gety()) * x1y2_x2y1;
		}
		cx *= 1 / (6 * area);
		cy *= 1 / (6 * area);
		return new Vector(cx, cy);
	}
	
	private double getI()
	{
		double numerator = 0, denominator = 0;
		int numvert = this.vertices.length;
		for (int i = 0; i < numvert; i++)
		{
			Vector p1 = vertices[i];
			Vector p2 = vertices[(i + 1) % numvert];
			numerator += Vector.cross(p1, p2) * (Vector.dot(p1, p1) + Vector.dot(p1, p2) + Vector.dot(p2, p2));
			denominator += Vector.cross(p1, p2);
		}
		return (this.mass / 6) * (numerator/denominator);
	}
	
	Type getType()
	{
		return Type.polygon;
	}
}