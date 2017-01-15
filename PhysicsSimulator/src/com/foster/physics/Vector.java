package com.foster.physics;

/**Vector class
 * @author reed
 */
public class Vector
{
	//private double x, y;
	static final Vector zeroVector = new Vector(0,0); //vector with 0 magnitude
	static final Vector ihat = new Vector(1, 0); //unit vector in the x-direction
	static final Vector jhat = new Vector(0, 1); //unit vector in the y-direction
	
	private double[] elements;
	
	final int length;
	
	/**Creates a 2d vector
	 * @param x = x component
	 * @param y = y component
	 */
	Vector(double x, double y)
	{
		new Vector(x, y);
		length = 2;
	}
	
	/**Creates an nd vector
	 * @param elements = ordered list of vector elements
	 */
	Vector(double... elements)
	{
		length = elements.length;
		this.elements = new double[elements.length];
		for (int i = 0; i < elements.length; i++)
			this.elements[i] = elements[i];
	}
	
	/**Gets a copy of the vector
	 * @return a copy of vector
	 */
	Vector get()
	{
		return new Vector(this.elements);
	}
	
	/**Gets the x component of the vector
	 * @return this.x
	 */
	double getx()
	{
		return get(0);
	}
	
	/**Gets the y component of the vector
	 * @return this.y
	 */
	double gety()
	{
		return get(1);
	}
	
	/**Gets the nth element of the vector
	 * @param i = index of the nth element
	 * @return this.elements[i]
	 */
	double get(int i)
	{
		if (i < 0 || i > this.length)
		{
			System.err.println("attempt to index a null element");
			return (double)Double.NaN;
		}
		return this.elements[i];
	}
	
	/**Increments a vector by another vector "a"
	* @param a = vector to add to this vector
	*/
	void increment(Vector a)
	{
		if (a.length != this.length)
		{
			System.err.println("attempt to add vectors of different length");
			return;
		}
		for (int i = 0; i < a.elements.length; i++)
			this.elements[i] += a.elements[i];
	}
	
	/**Decrements a vector by another vector "a"
	* @param a = vector to subtract from this vector
	*/
	void decrement(Vector a)
	{
		if (a.length != this.length)
		{
			System.err.println("attempt to subtract vectors of different length");
			return;
		}
		for (int i = 0; i < a.elements.length; i++)
			this.elements[i] -= a.elements[i];
	}
	
	/**Scales a vector by a factor of "a"
	* @param a = scalar to scale this vector by
	*/
	void scale(double a)
	{
		for (double i : this.elements)
			i = i * a;
	}
	
	/**Adds two vectors
	* @param a = 1st vector to add
	* @param b = 2nd vector to add
	* @return vector s; the sum of vector a added to vector b
	*/
	static Vector add(Vector a, Vector b)
	{
		if (a.length != b.length)
		{
			System.err.println("attempt to add vectors of different length");
			return null;
		}
		Vector sum = a.get();
		for (int i = 0; i < b.elements.length; i++)
			sum.elements[i] += b.elements[i];
		return sum;
	}
	
	/**Subtracts two vectors
	* @param a = Subtrahend
	* @param b = Minuend
	* @return vector d; the difference of vector b subtracted from vector a
	*/
	static Vector sub(Vector a, Vector b)
	{
		if (a.length != b.length)
		{
			System.err.println("attempt to subtract vectors of different length");
			return null;
		}
		Vector sum = a.get();
		for (int i = 0; i < b.elements.length; i++)
			sum.elements[i] -= b.elements[i];
		return sum;
	}
	
	/**Multiplies a vector by a scalar
	* @param a = vector to mpy by
	* @param b = scalar
	* @return vector p; the product of vector a multiplied by double b
	*/
	static Vector mpy(Vector a, double b)
	{
		Vector product = a.get();
		for (double i : product.elements)
			i = i * b;
		return product;
	}
	
	/**Multiples a Matrix by a vector
	 * @param a = matrix
	 * @return multiplied vector
	 */
	static Vector mpy(Matrix a, Vector b)
	{
		Vector product = b.get();
		for (int i = 0; i < a.m; i++)
			product.elements[i] = Vector.dot(a.getr(i), b);
		return product;
	}
	
	/**Finds the magnitude of a vector
	* @return magnitude
	*/
	double mag()
	{
		double magnitude = Math.sqrt(this.magSq());
		return magnitude;
	}
	
	/**Finds the squared magnitude of a vector
	* @return magnitude^2
	*/
	double magSq()
	{
		double mg2 = Vector.dot(this, this);
		return mg2;
	}
	
	/**Computes the dot product of vectors a and b
	* @param a = 1st vector
	* @param b = 2nd vector
	* @return a dot b
	*/
	static double dot(Vector a, Vector b)
	{
		if (a.length != b.length)
		{
			System.err.println("attempt to dot vectors of different length");
			return (double)Double.NaN;
		}
		double sum = 0;
		for (int i = 0; i < a.length; i++)
			sum += a.elements[i] * b.elements[i];
		return sum;
	}
	
	/**Computes the magnitude of the cross product of 2d vectors a and b
	 * (magnitude only because a is a member of R2, so there is no axis perpendicular to the plane containing a and b)
	 * @param a = 1st vector
	 * @param b = 2nd vector
	 * @return a cross b
	 */
	static double cross(Vector a, Vector b)
	{
		if (a.length != 2)
		{
			System.err.println("double cross(a, b) is a pseudo cross-product for 2d vectors");
		}
		return dot(a, b.perp().norm()) * b.mag();
	}
	
	static Vector cross_3d(Vector a, Vector b)
	{
		return null;
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
		denom = axis.magSq() == 1 ? Vector.dot(axis, c) : Vector.dot(axis, c) * (axis.magSq() == 0 ? 0 : 1 / axis.mag());
		return (c.magSq() * (denom == 0 ? 0 : 1 / denom));
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
		double mag = a.mag();
		return Vector.mpy(a, (mag == 0 ? 0 : 1 / mag));
	}
	
	/**Finds a perpendicular vector in the xy plane
	* @return new perpendicular vector (rotated clockwise 90 degrees in the xy plane)
	*/
	Vector perp()
	{
		return this.rotate(Math.PI/4);
		//return new Vector(this.y, -this.x);
	}
	
	/**Reflects vector across axis
	* @param axis = axis to reflect across
	* @return vector that has been reflected
	*/
	Vector reflect(Vector axis)
	{
		Vector thisvector = this.get();
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
	Vector rotate(double theta, double... angs)
	{
		//double phi;
		//phi = angs.length == 1 ? angs[0] : 0;
		Matrix rotationmatrix = new Matrix(Math.cos(theta), -Math.sin(theta), Math.sin(theta), Math.cos(theta));
		Vector rotatedvector = Vector.mpy(rotationmatrix, this);
		return rotatedvector;
	}
	
	//debugging
	
	/**Returns a string tuple of the x and y components of the vector
	*/
	String getString()
	{
		String retstr = "(";
		for (double i : this.elements)
			retstr = retstr + Double.toString(i);
		return retstr;
		//return("(" + Double.toString(this.x) + ", " + Double.toString(this.y) + ")");
	}
	
	/**Prints the vector using getString function
	 */
	void printV()
	{
		System.out.println(this.getString());
	}
}