package com.foster.physics;

/**Subclass of Body - creates Circle objects
 * @author reed
 */
public class Circle extends Body
{
	double radius;
	AABB bounds;
	
	/**Default Circle constructor
	 * @param mass = mass of circle
	 * @param pos = position of circle's center of mass
	 * @param vel = velocity of circle
	 * @param acc = acceleration of circle
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 * @param radius = radius of circle
	 */
	Circle(double mass, Vector pos, Vector vel, Vector acc, double mu, double e, double radius)
	{
		super(mass, pos, vel, acc, mu, e);
		this.radius = radius;
		this.I = 0.5 * mass * radius * radius;
		this.invI = this.I == 0 ? 0 : 1/this.I;
		bounds = getAABB(pos, radius);
	}
	
	/**Constructor for circles with 0 velocity and 0 acceleration
	 * @param mass = mass of circle
	 * @param pos = position of circle's center of mass
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 * @param radius = radius of circle
	 */
	Circle(double mass, Vector pos, double mu, double e, double radius)
	{
		super(mass, pos, mu, e);
		this.radius = radius;
		this.I = 0.5 * mass * radius * radius;
		this.invI = this.I == 0 ? 0 : 1/this.I;
		bounds = getAABB(pos, radius);
	}
	
	/**Constructor for circles with 0 velocity, 0 acceleration, 0 friction, and e of 1
	 * @param mass = mass of circle
	 * @param pos = position of circle's center of mass
	 * @param radius = radius of circle
	 */
	Circle(double mass, Vector pos, double radius)
	{
		super(mass, pos);
		this.radius = radius;
		this.I = 0.5 * mass * radius * radius;
		this.invI = this.I == 0 ? 0 : 1/this.I;
		bounds = getAABB(pos, radius);
	}
	
	/**Gets the AABB of a Circle based on its pos and radius
	 * @param pos = position vector of the circle
	 * @param radius = radius of the circle
	 * @return AABB (position vectors of minimum vertex and maximum vertex for the smallest AABB around the circle)
	 */
	private static AABB getAABB(Vector pos, double radius)
	{
		Vector vertex = new Vector(radius, radius);
		return new AABB(Vector.sub(pos, vertex), Vector.add(pos, vertex)); //AABB = (pos-(rad,rad), pos+(rad,rad))
	}
	
	/**Updates object position, velocity and acceleration
	 * @param tstep = interval over which acceleration is applied (smaller values mean smoother, slower movement)
	 */
	void integrate(double tstep)
	{
		super.integrate(tstep);
		AABB aabb = getAABB(this.pos, this.radius);
		this.bounds = aabb.get();
	}
	
	/**Gets the minimum and maximum values of the projection of a Circle onto a Vector axis
	 * @param axis = axis to project Circle onto
	 * @return Vector (minimum, maximum)
	 */
	Vector project(Vector axis)
	{
		double center = Vector.project(this.pos, axis);
		return new Vector(center - this.radius, center + this.radius);
	}
	
	Type getType()
	{
		return Type.circle;
	}
}