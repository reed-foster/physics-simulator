package com.foster.physics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.RenderingHints;

/**Contains environment variables such as entity lists
 * @author reed
 */
public class Environment
{
	static final double tstep = 0.01;
	static final int dispwidth = 800;
	static final int dispheight = 600;
	List<Polygon> polygons = new ArrayList<Polygon>();
	List<Circle> circles = new ArrayList<Circle>();
	private int polysize, circlesize;
	
	/**Class constructor, creates an environment into which bodies can be added
	 */
	Environment()
	{
		polysize = 0;
		circlesize = 0;
	}
	
	/**Adds an entity to the environment
	 * @param a = body to add
	 */
	void newEntity(Body a)
	{
		switch (a.getType())
		{
		case polygon:
			polygons.add((Polygon) a);
			polysize++;
			break;
		case circle:
			circles.add((Circle) a);
			circlesize++;
			break;
		case body:
			System.err.println("Can't add body to entitylists");
			break;
		}
	}
	
	/**Integrates object acceleration (linear and angular) for all entities in the environment
	 */
	void integrateAll()
	{
		for (Polygon i : polygons)
		{
			i.integrate(tstep);
		}
		for (Circle i : circles)
		{
			i.integrate(tstep);
		}
	}
	
	/**Detects and resolves collisions between all entities
	 */
	void collideAll()
	{
		//Resolve body-body collisions
		for (int i = 0; i < polysize - 1; i++)
		{
			Polygon a = polygons.get(i);
			
			for (int j = i + 1; j < polysize; j++)
			{
				Polygon b = polygons.get(j);
				Collision.collide(a, b);
			}
			
			for (int j = 0; j < circlesize; j++)
			{
				Circle b = circles.get(j);
				Collision.collide(a, b);
			}
		}
		
		for (int i = 0; i < circlesize; i++)
		{
			Circle a = circles.get(i);
			for (int j = 0; j < circlesize; j++)
			{
				Circle b = circles.get(j);
				Collision.collide(a, b);
			}
		}
		
		//Resolve body-wall collisions
		for (Polygon i : polygons)
		{
			Collision.collidewalls(i);
		}
		
		for (Circle i : circles)
		{
			Collision.collidewalls(i);
		}
	}
	
	/**Paint all entities
	 * @param g = reference to Graphics object
	 */
	void paintall(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//test: g2d.drawRect(0, 0, 50, 50);
		for (Polygon i : polygons)
		{
			Triangle[] triangles = i.subsections;
			for (int j = 0; j < triangles.length; j++)
			{
				Vector p1 = Vector.add(triangles[j].getp(0), i.pos);
				Vector p2 = Vector.add(triangles[j].getp(1), i.pos);
				Vector p3 = Vector.add(triangles[j].getp(2), i.pos);
				drawLine(g2d, p1, p2);
				drawLine(g2d, p2, p3);
				drawLine(g2d, p3, p1);
			}
		}
		for (Circle i : circles)
		{
			drawCircle(g2d, i);
		}
	}
	
	/**Draws a line between two point vectors
	 * @param g2d = reference to Graphics2D object
	 * @param p1 = 1st point vector
	 * @param p2 = 2nd point vector
	 */
	private void drawLine(Graphics2D g2d, Vector p1, Vector p2)
	{
		g2d.drawLine((int) p1.getx(), (int) (dispheight - p1.gety()), (int) p2.getx(), (int) (dispheight - p2.gety()));
	}
	
	/**Draws a circle
	 * @param g2d = reference to Graphics2D object
	 * @param i = reference to Circle object
	 */
	private void drawCircle(Graphics2D g2d, Circle i)
	{
		int diam = (int) (2 * i.radius);
		g2d.drawOval((int) (i.pos.getx() - i.radius), (int) (dispheight - i.pos.gety() - i.radius), diam, diam);
		int x1 = (int) i.pos.getx();
		int y1 = dispheight - (int) i.pos.gety();
		int x2 = x1 + (int) (i.radius * Math.cos(i.theta));
		int y2 = y1 + (int) (i.radius * Math.sin(i.theta));
		g2d.drawLine(x1, y1, x2, y2);
	}
}