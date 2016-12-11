package com.foster.physics;

class Main
{
	public static void main(String[] args)
	{
		Vector v1 = new Vector(-4, -3);
		Vector v2 = new Vector(1, 1);
		//Vector v3 = new Vector(0, 1);
		System.out.println(v1.reflect(v2).getString());
	}
}