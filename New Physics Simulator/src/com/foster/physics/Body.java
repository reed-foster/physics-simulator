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
	private Vector netforce;
	
	enum Type {body, circle, polygon};
	private Type type;
	
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
		this.invmass = this.mass == 0 ? 1/this.mass : 0.0;
		type = Type.body;
	}
	
	/**Constructor for Rigid Bodies with 0 velocity and 0 acceleration
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 * @param mu = coefficient of friction
	 * @param e = coefficient of restitution
	 */
	Body(double mass, Vector pos, double mu, double e)
	{
		this.mass = mass;
		this.pos = pos;
		this.vel = Vector.zeroVector;
		this.acc = Vector.zeroVector;
		this.mu = mu;
		this.e = e;
		this.invmass = this.mass == 0 ? 1/this.mass : 0.0;
		type = Type.body;
	}
	
	/**Constructor for Rigid Bodies with 0 velocity, 0 acceleration, 0 friction, and e of 1
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 */
	Body(double mass, Vector pos)
	{
		this.mass = mass;
		this.pos = pos;
		this.vel = Vector.zeroVector;
		this.acc = Vector.zeroVector;
		this.mu = 0;
		this.e = 1;
		this.invmass = this.mass == 0 ? 1/this.mass : 0.0;
		type = Type.body;
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
		this.acc = Vector.mpy(this.netforce, this.invmass); //acc = Fnet/m
		this.pos.increment(Vector.add(Vector.mpy(acc, 0.5*tstep*tstep),Vector.mpy(vel, tstep))); //pos += 0.5a*t^2+v*t
		this.vel.increment(Vector.mpy(acc, tstep)); //vel += a*t
	}
	
	Type getType()
	{
		return this.type;
	}
}