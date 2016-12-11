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
		frame.setSize(Environment.dispwidth, Environment.dispheight + 40);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Circle circle1 = new Circle(10, new Vector(50, 500), 50);
		//Circle circle2 = new Circle(5, new Vector(200, 300), 20);
		Circle circle3 = new Circle(10, new Vector(50, 380), 50);
		
		environment.newEntity(circle1);
		//environment.newEntity(circle2);
		environment.newEntity(circle3);
		
		circle1.addForce(new Vector(0, -20));
		
		while (true)
		{
			main.loop(environment);
			Thread.sleep(10);
		}
	}
}