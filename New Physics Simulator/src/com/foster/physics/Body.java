package com.foster.physics;

/**Body Class - superclass of all bodies
 * @author reed
 */
public class Body
{
	double mass;
	Vector pos;
	Vector vel;
	Vector acc;
	double mu;
	double e;
	double invmass;
	Vector netforce;
	
	enum Type {body, circle, polygon};
	
	/**Constructor for Rigid Bodies
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 * @param vel = velocity
	 * @param acc = acceleration
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 */
	Body(double mass, Vector pos, Vector vel, Vector acc, double mu, double e)
	{
		this.mass = mass;
		this.pos = pos;
		this.vel = vel;
		this.acc = acc;
		this.mu = mu;
		this.e = e;
		this.invmass = this.mass != 0 ? 1/this.mass : 0.0;
		this.netforce = new Vector(0, 0);
	}
	
	/**Constructor for Rigid Bodies with 0 velocity and 0 acceleration
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 */
	Body(double mass, Vector pos, double mu, double e)
	{
		this(mass, pos, Vector.zeroVector, Vector.zeroVector, mu, e);
	}
	
	/**Constructor for Rigid Bodies with 0 velocity, 0 acceleration, 0 friction, and e of 1
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 */
	Body(double mass, Vector pos)
	{
		this(mass, pos, 0, 1);
	}
	
	/**Increments the netforce vector by vector f
	 * @param f = force vector
	 */
	void addForce(Vector f)
	{
		this.netforce.increment(f);
	}
	
	/**Updates object position, velocity and acceleration
	 * @param tstep = interval over which acceleration is applied (smaller values mean smoother, slower movement)
	 */
	void update(double tstep)
	{
		Vector acceleration = Vector.mpy(this.netforce, this.invmass);
		Vector velocity = Vector.add(Vector.mpy(this.acc, tstep), this.vel);
		Vector position = Vector.add(Vector.add(Vector.mpy(this.acc, 0.5*tstep*tstep), Vector.mpy(this.vel, tstep)), this.pos);
		this.acc = acceleration.get();
		this.vel = velocity.get();
		this.pos = position.get();
	}
	
	Type getType()
	{
		return Type.body;
	}
}