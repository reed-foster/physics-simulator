package com.foster.physics;

class Environment
{
	public static final double tstep = 0.001;
	Body[] entitylist;
	private int bodypointer;
	
	Environment()
	{
		bodypointer = 0;
	}
	
	void collideAll()
	{
		for (int i = 0; i < entitylist.length - 1; i++)
		{
			for (int j = i + 1; j < entitylist.length; j++)
			{
				//Body a = entitylist[i];
				//Body b = entitylist[j];
				//Collision.collide(a, b);
			}
		}
	}
	
	void newEntity(Body a)
	{
		entitylist[++bodypointer] = a;
	}
	
	//void collide(Body a, Body b)
	{
		
	}
}