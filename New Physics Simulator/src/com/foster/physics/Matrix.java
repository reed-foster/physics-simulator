package com.foster.physics;

/**2x2 Matrix Class
 * @author reed
 */
class Matrix
{
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