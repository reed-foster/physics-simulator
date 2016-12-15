package com.foster.physics;

import java.awt.Graphics;
//import java.util.ArrayList;
//import java.awt.RenderingHints;
//import java.util.List;
//import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class Main extends JPanel
{
	Main main;
	
	static Environment environment;
	
	//static Circle circle1;
	//static Circle circle2;
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		environment.paintall(g);
	}
	
	public void update(Environment e)
	{
		e.integrateAll();
		//Collision.collide(circle1, circle2);
		//Collision.collidewalls(circle1);
		//Collision.collidewalls(circle2);
		e.collideAll();
		//System.out.println("Hello, world!");
	}
	
	public void loop(Environment e)
	{
		update(e);
		repaint();
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		environment = new Environment();
		JFrame frame = new JFrame("Physics Simulator");
		Main main = new Main();
		frame.add(main);
		frame.setSize(Environment.dispwidth, Environment.dispheight);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//List<Circle> circles = new ArrayList<Circle>();
		for(int i = 0; i <= 500; i++)
		{
			double density = (Math.random() + 2) * 5;
			Vector position = new Vector((Math.random() + 0.01) * (Environment.dispwidth - 20), (Math.random() + 0.01) * (Environment.dispheight - 20));
			double radius = (Math.random() + 2) * 5;
			double restitution = (Math.random() + 9) / 10;
			Circle newcirc = new Circle(density * Math.PI * radius * radius * radius, position, 0, restitution, radius/2.5);
			environment.newEntity(newcirc);
			Vector rand_vel = new Vector((Math.random() - 0.5) * 10000, (Math.random() - 0.5) * 10000);
			newcirc.vel = rand_vel.get();
			//newcirc.addForce(new Vector(0, -100 * newcirc.mass));
		}
		/*circle1 = new Circle(2, new Vector(50, 500), 10);
		circle2 = new Circle(5, new Vector(200, 300), 20);
		//Circle circle3 = new Circle(1, new Vector(200, 500), 10);
		
		environment.newEntity(circle1);
		environment.newEntity(circle2);
		//environment.newEntity(circle3);
		
		circle1.vel = new Vector(20, 0);
		circle2.vel = new Vector(50, -30);
		circle1.addForce(new Vector(0, -9.8 * circle1.mass));// = (new Vector(5, -50));
		circle2.addForce(new Vector(0, -9.8 * circle2.mass));*/
		
		while (true)
		{
			main.loop(environment);
			Thread.sleep(0, 1);
		}
	}
}