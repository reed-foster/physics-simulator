package com.foster.physics;

/**Matrix Class
 * @author reed
 */
class Matrix
{
	public static final Matrix I2 = new Matrix(1, 0, 0, 1);
	
	private double[][] elements;
	final int m, n;
	
	/**Constructor for 2x2 Matrices
	 * @param x_11 = a(1,1) <- upper left element
	 * @param x_12 = a(1,2)
	 * @param x_21 = a(2,1)
	 * @param x_22 = a(2,2) <- bottom right element
	 */
	Matrix(double x_11, double x_12, double x_21, double x_22)
	{
		new Matrix(2, 2, x_11, x_12, x_21, x_22);
		m = 2;
		n = 2;
	}
	
	/**Constructor for mxn Matrices
	 * @param m = number of rows
	 * @param n = number of columns
	 * @param x = list of elements (fills matrix with 0s if not enough elements are supplied
	 */
	Matrix(int m, int n, double... x)
	{
		elements = new double[m][n];
		this.m = m;
		this.n = n;
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				int idx = i * n + j;
				elements[i][j] = idx < x.length ? x[i * n + j] : 0;
			}
		}
	}
	
	/**Copies the Matrix
	 * @return new Matrix
	 */
	Matrix get()
	{
		double[] a = new double[elements.length * elements[0].length];
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				a[i * this.n + j] = elements[i][j];
			}
		}
		return new Matrix(this.m, this.n, a);
	}
	
	/**Returns the value at a(i,j)
	 * @param i = row number
	 * @param j = column number
	 * @return
	 */
	double get(int i, int j)
	{
		return elements[i][j];
	}
	
	Vector getr(int i)
	{
		return new Vector(elements[i]);
	}
	
	Vector getc(int j)
	{
		double[] a = new double[this.n];
		for (int i = 0; i < this.n; i++)
		{
			a[i] = elements[j][i];
		}
		return new Vector(a);
	}
	
	/**Multiplies two matrices
	 * @param a = 1st matrix
	 * @param b = 2nd matrix
	 * @return new product
	 */
	static Matrix mpy(Matrix a, Matrix b)
	{
		if (a.n != b.m)
		{
			System.err.printf("Multiplication of %dx%d with %dx%d matrices undefined\n", a.m, a.n, b.m, b.n);
			return null;
		}
		
		Matrix product = new Matrix(a.m, b.n);
		
		for (int i = 0; i < a.m; i++)
		{
			for (int j = 0; j < b.n; j++)
			{
				product.elements[i][j] = Vector.dot(a.getr(i), b.getc(j));
			}
		}
		
		return product;
		/*
		double a11 = a.get(0, 0);
		double a12 = a.get(0, 1);
		double a21 = a.get(1, 0);
		double a22 = a.get(1, 1);
		
		double b11 = b.get(0, 0);
		double b12 = b.get(0, 1);
		double b21 = b.get(1, 0);
		double b22 = b.get(1, 1);
		
		return new Matrix(a11 * b11 + a12 * b21, a11 * b12 + a12 * b22, a21 * b11 + a22 * b21, a21 * b12 + a22 * b22);*/
	}
	
	/*
	/**Prints a formatted matrix with 5 digits for each element and up to 3 decimal places
	 *
	void printmatrix()
	{
		System.out.printf("[%-5.3f,", elements[0][0]);
		System.out.printf("%-5.3f]", elements[0][1]);
		System.out.println("");
		System.out.printf("[%-5.3f,", elements[1][0]);
		System.out.printf("%-5.3f]", elements[1][1]);
	}*/
}