package com.foster.physics;

import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;

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
		
		Circle circle1 = new Circle(2, new Vector(50, 500), 10);
		//Circle circle2 = new Circle(5, new Vector(200, 300), 20);
		Circle circle3 = new Circle(1, new Vector(200, 500), 10);
		
		environment.newEntity(circle1);
		//environment.newEntity(circle2);
		environment.newEntity(circle3);
		
		circle1.vel = new Vector(2, 0);
		circle3.vel = new Vector(-5, -30);
		circle1.addForce(new Vector(0, -9.8 * circle1.mass));// = (new Vector(5, -50));
		circle3.addForce(new Vector(0, -9.8 * circle3.mass));
		
		while (true)
		{
			main.loop(environment);
			Thread.sleep(10);
		}
	}
}