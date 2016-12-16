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
		
		for(int i = 0; i <= 100; i++)
		{
			double density = (Math.random() + 2) * 5;
			Vector position = new Vector((Math.random() + 0.01) * (Environment.dispwidth - 20), (Math.random() + 0.01) * (Environment.dispheight - 20));
			double radius = (Math.random() + 2) * 5;
			double restitution = 1;//(Math.random() + 9) / 10;
			Circle newcirc = new Circle(density * Math.PI * radius * radius * radius, position, 0, restitution, radius/1.5);
			environment.newEntity(newcirc);
			Vector rand_vel = new Vector((Math.random() - 0.5) * 50, (Math.random() - 0.5) * 50);
			newcirc.vel = rand_vel.get();
			//newcirc.addForce(new Vector(0, -100 * newcirc.mass));
		}
		
		while (true)
		{
			main.loop(environment);
			Thread.sleep(1, 0);
		}
	}
}