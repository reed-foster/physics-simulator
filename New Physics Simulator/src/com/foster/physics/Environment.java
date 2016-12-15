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
	static final double tstep = 0.0005;
	static final int dispwidth = 800;
	static final int dispheight = 600;
	List<Polygon> polygons = new ArrayList<Polygon>();
	List<Circle> circles = new ArrayList<Circle>();
	private int polysize, circlesize;
	
	Environment()
	{
		polysize = 0;
		circlesize = 0;
	}
	
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
	
	void integrateAll()
	{
		for (Polygon i : polygons)
		{
			i.update(tstep);
		}
		for (Circle i : circles)
		{
			//System.out.println("Updating " + i.toString());
			i.update(tstep);
		}
	}
	
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
	
	void paintall(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//test: g2d.drawRect(0, 0, 50, 50);
		for (Polygon i : polygons)
		{
			Vector[] vertices = i.vertices;
			for (int j = 0; j < vertices.length; j++)
			{
				int idx1 = j;
				int idx2 = (j + 1) % vertices.length;
				Vector v1 = vertices[idx1];
				Vector v2 = vertices[idx2];
				drawLine(g2d, v1, v2);//g2d.drawLine((int) v1.getx(), (int) v1.gety(), (int) v2.getx(), (int) v2.gety());
			}
		}
		for (Circle i : circles)
		{
			//int diam = (int) (2 * i.radius);
			//g2d.drawRect((int) (i.bounds.min.getx()), (int) (dispheight - i.bounds.min.gety() - 2*i.radius), (int) (2 * i.radius), (int) (2 * i.radius));
			drawCircle(g2d, i.pos, i.radius); //g2d.drawOval((int) i.pos.getx(), (int) i.pos.gety(), diam, diam);
		}
	}
	
	void drawLine(Graphics2D g2d, Vector p1, Vector p2)
	{
		
		g2d.drawLine((int) p1.getx(), (int) (dispheight - p1.gety()), (int) p2.getx(), (int) (dispheight - p2.gety()));
	}
	
	void drawCircle(Graphics2D g2d, Vector p, double radius)
	{
		int diam = (int) (2 * radius);
		g2d.drawOval((int) (p.getx() - radius), (int) (dispheight - p.gety() - radius), diam, diam);
	}
}