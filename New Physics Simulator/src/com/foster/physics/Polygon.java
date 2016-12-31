package com.foster.physics;

/**Subclass of Body - creates shapes with multiple vertices (supplied in clockwise order)
 * supports both convex and nonconvex polygons; complex polygons are unsupported
 * @author reed
 */
public class Polygon extends Body
{
	Vector[] vertices;
	AABB bounds;
	
	Triangle[] subsections;
	private int polypointer;
	
	enum Style {convex, nonconvex};
	
	private Style style;
	
	/**Default Polygon constructor
	 * @param mass = mass of polygon
	 * @param pos = position of polygon's center (will be recalculated to center of mass)
	 * @param vel = velocity of polygon
	 * @param acc = acceleration of polygon
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 * @param vertices = clockwise array of vectors that define vertices from the center of mass
	 */
	Polygon(double mass, Vector pos, Vector vel, Vector acc, double mu_s, double mu_k, double e, Vector[] vertices)
	{
		super(mass, pos, vel, acc, mu_s, mu_k, e);
		
		this.vertices = vertices;
		Vector newpos = this.getCenterOfMass();
		Vector absvert;
		for (Vector i: this.vertices)
		{
			absvert = Vector.add(this.pos, i);
			i = Vector.sub(absvert, newpos);
		}
		this.I = this.getI();
		this.invI = this.I == 0 ? 0 : 1 / this.I;
		
		bounds = getAABB(pos, vertices);
		
		this.style = getconvexity();
		
		subsections = new Triangle[this.vertices.length - 2]; //all n-sided polygons can be triangulated into n-2 triangles
		this.clipears(this.vertices); //triangulates polygon using an earclipping algorithm
	}
	
	/**Constructor for polygons with 0 velocity and 0 acceleration
	 * @param mass = mass of polygon
	 * @param pos = position of polygon's center of mass
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 * @param vertices = array of vectors that define vertices from the center of mass
	 */
	Polygon(double mass, Vector pos, double mu_s, double mu_k, double e, Vector[] vertices)
	{
		this(mass, pos, Vector.zeroVector, Vector.zeroVector, mu_s, mu_k, e, vertices);
	}
	
	/**Constructor for polygons with 0 velocity, 0 acceleration, 0 friction, and e of 1
	 * @param mass = mass of polygon
	 * @param pos = position of polygon's center of mass
	 * @param vertices = array of vectors that define vertices from the center of mass
	 */
	Polygon(double mass, Vector pos, Vector[] vertices)
	{
		this(mass, pos, 0, 0, 1, vertices);
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
	
	/**Rotates the vertices of a polygon by theta radians
	 * @param vertices = array of vertices
	 * @param theta = angle to rotate each vertex by
	 * @return rotated array of vertices
	 */
	private Vector[] rotatevertices(Vector[] vertices, double theta) {
		for (int i = 0; i < vertices.length; i++)
		{
			vertices[i] = vertices[i].rotate(theta);
		}
		return vertices;
	}
	
	/**Rotates the triangulations of a polygon by theta radians
	 * @param subsections = array of triangulations
	 * @param theta = angle to rotate each triangle by
	 * @return rotated array of triangles
	 */
	private Triangle[] rotatesubsections(Triangle[] subsections, double theta) {
		for (int i = 0; i < subsections.length; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				subsections[i].setp(j, subsections[i].getp(j).rotate(theta));
			}
		}
		return subsections;
	}
	
	/**Gets the convexity of a polygon
	 * @return Style.convex if convex, otherwise, Style.nonconvex
	 */
	private Style getconvexity()
	{
		for (int i = 0; i < this.vertices.length; i++)
		{
			Vector v1 = Vector.sub(this.vertices[(i + 1) % this.vertices.length], this.vertices[i]); //vector between the point at vertices[i] and vertices[i + 1]
			Vector v2 = Vector.sub(this.vertices[(i + 2) % this.vertices.length], this.vertices[(i + 1) % this.vertices.length]);
			Vector v2p = new Vector(-v2.gety(), v2.getx());
			if (Vector.dot(v1, v2p) < 0)
				return Style.nonconvex;
		}
		return Style.convex;
	}
	
	/**Recursive polygon triangulation method that uses an ear-clipping algorithm
	 * @param a = array of vertices that define a polygon to triangulate
	 */
	private void clipears(Vector[] a)
	{
		if (a.length <= 3) //polygon a is fully triangulated, 0 ears left
		{
			this.subsections[polypointer] = new Triangle(a[0], a[1], a[2]);
			//resize subsecgtions array
			Triangle[] newsubsections = new Triangle[polypointer + 1];
			for (int i = 0; i < newsubsections.length; i++)
			{
				newsubsections[i] = this.subsections[i];
			}
			this.subsections = newsubsections;
			return;
		}
		else //polygon a has at least 1 ear
		{
			int earindex;
			for (earindex = 0; earindex < a.length; earindex++)
			{
				Vector v1 = a[earindex == 0 ? a.length - 1 : earindex - 1].get(); //vertex before a[earindex]
				Vector v2 = a[earindex].get();
				Vector v3 = a[(earindex + 1) % a.length].get(); //vertex after a[earindex]
				Vector s1 = Vector.sub(v2, v1); //vector pointing from v1 to v2
				Vector s2 = Vector.sub(v3, v2); //vector pointing from v2 to v3
				Vector s2p = new Vector(-s2.gety(), s2.getx()); //perpendicular vector to s2
				boolean convex = Vector.dot(s1, s2p) >= 0;
				boolean inside = false; //does ear contain any other points?
				if (convex)
				{
					Vector[] earverts = {v1, v2, v3};
					for (int i = 0; i < a.length; i++)
					{
						if (i != (earindex == 0 ? a.length - 1 : earindex - 1) && i != earindex && i != ((earindex + 1) % a.length))
						{
							if (intriangle(a[i], earverts))
							{
								inside = true;
								break;
							}
						}
					}
				}
				if (convex && (!inside)) //a[earindex] is a valid ear, snip it from the polygon and add it to the triangle array
				{
					this.subsections[polypointer++] = new Triangle(v1, v2, v3);
					break;
				}
			}
			//remove the ear vertex from the new polygon
			Vector[] newvert = new Vector[a.length - 1];
			for(int i = 0; i < newvert.length; i++)
			{
				newvert[i] = a[i >= earindex ? i + 1 : i];
			}
			//recurse
			clipears(newvert);
		}
	}
	
	/**Determine if a point is inside of a triangle defined by point vectors relative to the origin
	 * @param pt = point vector of the point
	 * @param vertlist = list of vectors that define the points of the triangle
	 * @return boolean intersection
	 */
	private boolean intriangle(Vector pt, Vector[] vertlist)
	{
		Vector[] axes = new Vector[vertlist.length];
		for (int i = 0; i < vertlist.length; i++)
		{
			axes[i] = Vector.sub(vertlist[(i + 1) % vertlist.length], vertlist[i]).perp();
		}
		for (int i = 0; i < axes.length; i++)
		{
			double min, max;
			min = max = Vector.dot(vertlist[0], axes[i]);
			for (int j = 0; j < vertlist.length; j++)
			{
				double proj = Vector.dot(vertlist[j], axes[i]);
				if (proj < min)
					min = proj;
				if (proj > max)
					max = proj;
			}
			double ptprj = Vector.dot(pt, axes[i]);
			if (ptprj < min || ptprj > max)
				return false;
		}
		return true;
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
		if (this.style == Style.convex)
			this.vertices = rotatevertices(this.vertices, dtheta);
		else
			this.subsections = rotatesubsections(this.subsections, dtheta);
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
	
	/**Gets the position vector of the Polygon's center of mass
	 * @return Vector
	 */
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
	
	/**Gets the mass moment of inertia (angular analogue to mass)
	 * @return double
	 */
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
	
	Style getstyle()
	{
		return this.style;
	}
	
	Type getType()
	{
		return Type.polygon;
	}
}