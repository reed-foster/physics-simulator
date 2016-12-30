package com.foster.physics;

/**Body Class - superclass of all bodies
 * @author reed
 */
public class Body
{
	protected static final double tau = Math.PI * 2;
	//Linear Motion
	Vector pos;
	Vector vel;
	Vector acc;
	
	//Angular Motion
	double theta; //angular displacement
	double omega; //angular velocity
	double alpha; //angular acceleration
	
	double mu_static;
	double mu_kinetic;
	double e;
	
	double mass; //mass of body
	double invmass; // 1/mass
	
	double I; // moment of inertia
	double invI; // 1/moment of inertia
	
	protected Vector netforce;
	protected double nettorque;
	
	enum Type {body, circle, polygon};
	
	/**Constructor for Rigid Bodies
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 * @param vel = velocity
	 * @param acc = acceleration
	 * @param mu_s = static coefficient of friction
	 * @param mu_k = kinetic coefficient of friction
	 * @param e = coefficient of restitution
	 */
	Body(double mass, Vector pos, Vector vel, Vector acc, double mu_s, double mu_k, double e)
	{
		this.mass = mass;
		this.pos = pos;
		this.vel = vel;
		this.acc = acc;
		this.theta = 0;
		this.omega = 0;
		this.alpha = 0;
		this.mu_static = mu_s;
		this.mu_kinetic = mu_k;
		this.e = e;
		this.invmass = this.mass != 0 ? 1/this.mass : 0.0;
		this.netforce = new Vector(0, 0);
	}
	
	/**Constructor for Rigid Bodies with 0 velocity and 0 acceleration
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 * @param mu_s = static coefficient of friction
	 * @param mu_k = kinetic coefficient of friction
	 * @param e = coefficient of restitution
	 */
	Body(double mass, Vector pos, double mu_s, double mu_k, double e)
	{
		this(mass, pos, Vector.zeroVector, Vector.zeroVector, mu_s, mu_k, e);
	}
	
	/**Constructor for Rigid Bodies with 0 velocity, 0 acceleration, 0 friction, and e of 1
	 * @param mass = mass of body
	 * @param pos = position of body's center of mass
	 */
	Body(double mass, Vector pos)
	{
		this(mass, pos, 0, 0, 1);
	}
	
	/**Increments the netforce vector by vector f
	 * @param f = force vector
	 */
	void addForce(Vector f)
	{
		this.netforce.increment(f);
	}
	
	void addForce(Vector f, Vector r)
	{
		addForce(f);
		this.nettorque += Vector.dot(f, r.perp().norm()) * -r.mag();
	}
	
	double gettorque(Vector f)
	{
		return 5;
	}
	
	/**Updates object position, velocity, and acceleration
	 * @param tstep = interval over which acceleration is applied (smaller values mean smoother, slower movement)
	 */
	void integrate(double tstep)
	{
		integratelin(tstep);
		integrateang(tstep);
	}
	
	/**Updates position, velocity, and acceleration
	 * @param tstep = interval over which acceleration is applied (smaller values mean smoother, slower movement)
	 */
	protected void integratelin(double tstep)
	{
		//update linear variables
		Vector acceleration = Vector.mpy(this.netforce, this.invmass);
		Vector velocity = Vector.add(Vector.mpy(this.acc, tstep), this.vel);
		Vector position = Vector.add(Vector.add(Vector.mpy(this.acc, 0.5*tstep*tstep), Vector.mpy(this.vel, tstep)), this.pos);
		this.acc = acceleration.get();
		this.vel = velocity.get();
		this.pos = position.get();
	}
	
	/**Updates theta, omega, and alpha
	 * @param tstep = interval over which acceleration is applied (smaller values mean smoother, slower movement)
	 */
	protected void integrateang(double tstep)
	{
		//update angular variables
		this.alpha = this.nettorque * this.invI; //update acceleration
		this.omega += this.alpha * tstep; //update velocity
		this.theta = (0.5 * this.alpha * tstep * tstep + this.omega * tstep + this.theta) % tau; //update position
	}
	
	Type getType()
	{
		return Type.body;
	}
}