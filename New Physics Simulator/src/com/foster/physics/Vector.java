package com.foster.physics;

/**2d Vector class
 * @author reed
 */
public class Vector
{
	private double x, y;
	static final Vector zeroVector = new Vector(0,0); //vector with 0 magnitude
	static final Vector ihat = new Vector(1, 0); //unit vector in the x-direction
	static final Vector jhat = new Vector(0, 1); //unit vector in the y-direction
	
	Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**Gets a copy of the vector
	 * @return a copy of vector
	 */
	Vector get()
	{
		return new Vector(x, y);
	}
	
	/**Gets the x component of the vector
	 * @return this.x
	 */
	double getx()
	{
		return this.x;
	}
	
	/**Gets the y component of the vector
	 * @return this.y
	 */
	double gety()
	{
		return this.y;
	}
	
	/**Increments a vector by another vector "a"
	* @param a = vector to add to this vector
	*/
	void increment(Vector a)
	{
		x += a.x;
		y += a.y;
	}
	
	/**Decrements a vector by another vector "a"
	* @param a = vector to subtract from this vector
	*/
	void decrement(Vector a)
	{
		x -= a.x;
		y -= a.y;
	}
	
	/**Scales a vector by a factor of "a"
	* @param a = scalar to scale this vector by
	*/
	void scale(double a)
	{
		x *= a;
		y *= a;
	}
	
	/**Adds two vectors
	* @param a = 1st vector to add
	* @param b = 2nd vector to add
	* @return vector s; the sum of vector a added to vector b
	*/
	static Vector add(Vector a, Vector b)
	{
		double elementx = a.x + b.x;
		double elementy = a.y + b.y;
		return new Vector(elementx, elementy);
	}
	
	/**Subtracts two vectors
	* @param a = Subtrahend
	* @param b = Minuend
	* @return vector d; the difference of vector b subtracted from vector a
	*/
	static Vector sub(Vector a, Vector b)
	{
		double elementx = a.x - b.x;
		double elementy = a.y - b.y;
		return new Vector(elementx, elementy);
	}
	
	/**Multiplies a vector by a scalar
	* @param a = vector to mpy by
	* @param b = scalar
	* @return vector p; the product of vector a multiplied by double b
	*/
	static Vector mpy(Vector a, double b)
	{
		double elementx = a.x * b;
		double elementy = a.y * b;
		return new Vector(elementx, elementy);
	}
	/**Multiples a Matrix by a vector
	 * @param a = matrix
	 * @return multiplied vector
	 */
	Vector mpy(Matrix a)
	{
		double x = this.x * a.get(0, 0) + this.y * a.get(0, 1);
		double y = this.x * a.get(1, 0) + this.y * a.get(1, 1);
		return new Vector(x, y);
	}
	
	/**Finds the magnitude of a vector
	* @return magnitude
	*/
	double mag()
	{
		double magnitude = (double) Math.sqrt(this.magSq());
		return magnitude;
	}
	
	/**Finds the squared magnitude of a vector
	* @return magnitude^2
	*/
	double magSq()
	{
		double mg2 = this.x * this.x + this.y * this.y;
		return mg2;
	}
	
	/**Computes the dot product of vectors a and b
	* @param a = 1st vector
	* @param b = 2nd vector
	* @return a dot b
	*/
	static double dot(Vector a, Vector b)
	{
		return a.x * b.x + a.y * b.y;
	}
	
	/**Computes the magnitude of the cross product of vectors a and b
	 * (magnitude only because a is a member of R2, so there is no axis perpendicular to the plane containing a and b)
	 * @param a = 1st vector
	 * @param b = 2nd vector
	 * @return a cross b
	 */
	static double cross(Vector a, Vector b)
	{
		return dot(a, b.perp().norm()) * b.mag();
	}
	
	/**Finds the Vector parallel to axis that, when projected onto c, has length projection
	 * @param axis
	 * @param c
	 * @param projection
	 * @return
	 */
	static Vector invdot(Vector axis, Vector c, double projection)
	{
		double mag = invdotmag(axis, c, projection);
		Vector projector = axis.norm();
		projector.scale(mag);
		return projector;
	}
	
	/**Finds the length of the vector parallel to axis that, when projected onto c, has length projection
	 * @param axis
	 * @param c
	 * @param projection
	 * @return
	 */
	static double invdotmag(Vector axis, Vector c, double projection)
	{
		double denom;
		denom = axis.magSq() == 1 ? axis.getx() * c.getx() + axis.gety() * c.gety() : (axis.getx() * c.getx() + axis.gety() * c.gety())/axis.mag();
		return (c.magSq()/denom);
	}
	
	/**Projects a onto b
	* @param a = 1st vector
	* @param b = 2nd vector
	* @return resulting distance
	*/
	static double project(Vector a, Vector b)
	{
		Vector b_norm = b.norm();
		double proj = Vector.dot(a, b_norm);
		return proj;
	}
	
	/**Normalizes the vector
	* @return vector * (1/vector.mag())
	*/
	Vector norm()
	{
		Vector a = this.get();
		return Vector.mpy(a, (1 / a.mag()));
	}
	
	/**Finds a perpendicular vector
	* @return new perpendicular vector (rotated clockwise 90 degrees)
	*/
	Vector perp()
	{
		return new Vector(this.y, -this.x);
	}
	
	/**Reflects vector across axis
	* @param axis = axis to reflect across
	* @return vector that has been reflected
	*/
	Vector reflect(Vector axis)
	{
		Vector thisvector = new Vector(this.x, this.y);
		Vector axisperp = axis.perp();
		Vector projection = axisperp.norm();
		projection.scale(Vector.project(thisvector, axisperp.norm()));
		Vector reflection = Vector.sub(thisvector, Vector.mpy(projection, 2));
		return reflection;
	}
	
	/**Rotates a Vector counterclockwise by theta radians
	 * @param theta = angle (in radians) to rotate vector by
	 * @return rotated vector
	 */
	Vector rotate(double theta)
	{
		Matrix rotationmatrix = new Matrix(Math.cos(theta), -Math.sin(theta), Math.sin(theta), Math.cos(theta));
		Vector rotatedvector = this.mpy(rotationmatrix);
		return rotatedvector;
	}
	
	//debugging
	
	/**Returns a string tuple of the x and y components of the vector
	*/
	String getString()
	{
		return("(" + Double.toString(this.x) + ", " + Double.toString(this.y) + ")");
	}
	
	/**Prints the vector using getString function
	 */
	void printV()
	{
		System.out.println(this.getString());
	}
}