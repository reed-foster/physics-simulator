package com.foster.physics;

/**2x2 Matrix Class
 * @author reed
 */
class Matrix
{
	public static final Matrix I2 = new Matrix(1, 0, 0, 1);
	
	private double[][] a;
	
	/**Constructor for 2x2 Matrices
	 * @param x_11 = a(1,1) <- upper left element
	 * @param x_12 = a(1,2)
	 * @param x_21 = a(2,1)
	 * @param x_22 = a(2,2) <- bottom right element
	 */
	Matrix(double x_11, double x_12, double x_21, double x_22)
	{
		a = new double[2][2];
		a[0][0] = x_11;
		a[0][1] = x_12;
		a[1][0] = x_21;
		a[1][1] = x_22;
	}
	
	/**Returns the value at a(i,j)
	 * @param i = row number (either 0 or 1)
	 * @param j = column number (either 0 or 1)
	 * @return
	 */
	double get(int i, int j)
	{
		return a[i][j];
	}
	
	/**Multiplies two matrices
	 * @param a = 1st matrix
	 * @param b = 2nd matrix
	 * @return new product
	 */
	static Matrix mpy(Matrix a, Matrix b)
	{
		double a11 = a.get(0, 0);
		double a12 = a.get(0, 1);
		double a21 = a.get(1, 0);
		double a22 = a.get(1, 1);
		
		double b11 = b.get(0, 0);
		double b12 = b.get(0, 1);
		double b21 = b.get(1, 0);
		double b22 = b.get(1, 1);
		
		return new Matrix(a11 * b11 + a12 * b21, a11 * b12 + a12 * b22, a21 * b11 + a22 * b21, a21 * b12 + a22 * b22);
	}
	
	/**Prints a formatted matrix with 5 digits for each element and up to 3 decimal places
	 */
	void printmatrix()
	{
		System.out.printf("[%-5.3f,", a[0][0]);
		System.out.printf("%-5.3f]", a[0][1]);
		System.out.println("");
		System.out.printf("[%-5.3f,", a[1][0]);
		System.out.printf("%-5.3f]", a[1][1]);
	}
}