package com.foster.physics;

class Matrix
{
	private double[][] matrix;
	
	Matrix(double[][] newmatrix, int w, int h)
	{
		matrix = new double[h][w];
		for(int i = 0; i < newmatrix.length; i++)
		{
			matrix[i] = newmatrix[i];
		}
	}
	
	void printmatrix()
	{
		for (double[] i : matrix)
		{
			System.out.print("[");
			for (double j : i)
			{
				System.out.printf("%-5.3f,", j);
			}
			System.out.println("]");
		}
	}
}