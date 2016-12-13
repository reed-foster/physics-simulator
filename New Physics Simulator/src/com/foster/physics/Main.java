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
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		environment.paintall(g);
	}
	
	public void update(Environment e)
	{
		e.integrateAll();
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
		for(int i = 0; i <= 30; i++)
		{
			double rand_prop = Math.random();
			Vector rand_pos = new Vector((Math.random() + 0.01) * (Environment.dispwidth - 20), (Math.random() + 0.01) * (Environment.dispheight - 20));
			Circle newcirc = new Circle((rand_prop + 1) * 5, rand_pos, (rand_prop + 1) * 10);
			environment.newEntity(newcirc);
			Vector rand_vel = new Vector((Math.random() - 0.5) * 50, (Math.random() - 0.5) * 50);
			newcirc.vel = rand_vel.get();
		}
		//Circle circle1 = new Circle(2, new Vector(50, 500), 10);
		//Circle circle2 = new Circle(5, new Vector(200, 300), 20);
		//Circle circle3 = new Circle(1, new Vector(200, 500), 10);
		
		//environment.newEntity(circle1);
		//environment.newEntity(circle2);
		//environment.newEntity(circle3);
		
		//circle1.vel = new Vector(20, 0);
		//circle3.vel = new Vector(50, -30);
		//circle1.addForce(new Vector(0, -9.8 * circle1.mass));// = (new Vector(5, -50));
		//circle3.addForce(new Vector(0, -9.8 * circle3.mass));
		
		while (true)
		{
			main.loop(environment);
			Thread.sleep(1, 0);
		}
	}
}