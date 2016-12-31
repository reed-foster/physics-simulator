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
	
	public void addcircles()
	{
		for(int i = 0; i <= 20; i++)
		{
			double density = 5;//(Math.random() + 2) * 5;
			Vector position = new Vector((Math.random() + 0.01) * (Environment.dispwidth - 20), (Math.random() + 0.01) * (Environment.dispheight - 20));
			double radius = (Math.random() + 0.2) * 20;
			double restitution = 1;//(Math.random() + 9) / 10;
			Circle newcirc = new Circle(density * Math.PI * radius * radius, position, 0, 1, restitution, radius);
			environment.newEntity(newcirc);
			Vector rand_vel = new Vector((Math.random() - 0.5) * 50, (Math.random() - 0.5) * 50);
			newcirc.vel = rand_vel.get();
			newcirc.addForce(new Vector(0, -9.8 * newcirc.mass));
		}
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
		
		/*Vector[] vertlist = new Vector[16];
		vertlist[0] = new Vector(0, -20);
		vertlist[1] = new Vector(20, 60);
		vertlist[2] = new Vector(120, 60);
		vertlist[3] = new Vector(50, 40);
		vertlist[4] = new Vector(120, -20);
		vertlist[5] = new Vector(80, -140);
		vertlist[6] = new Vector(100, -40);
		vertlist[7] = new Vector(60, -60);
		vertlist[8] = new Vector(40, -15);
		vertlist[9] = new Vector(-20, -60);
		vertlist[10] = new Vector(-20, -40);
		vertlist[11] = new Vector(-40, -60);
		vertlist[12] = new Vector(0, -120);
		vertlist[13] = new Vector(-100, -140);
		vertlist[14] = new Vector(-120, -20);
		vertlist[15] = new Vector(-60, 40);
		/*
		Vector[] vertlist = new Vector[10];
		vertlist[0] = new Vector(0,80);
		vertlist[1] = new Vector(40,20);
		vertlist[2] = new Vector(120,100);
		vertlist[3] = new Vector(130, 10);
		vertlist[4] = new Vector(80, -5);
		vertlist[5] = new Vector(30,-160);
		vertlist[6] = new Vector(0, -50);
		vertlist[7] = new Vector(-80,0);
		vertlist[8] = new Vector(-30, -10);
		vertlist[9] = new Vector(-100, 30);*/
		//Polygon p = new Polygon(1, new Vector(400, 300), vertlist);
		main.addcircles();
		/*Vector[] vertlist = new Vector[5];
		for(int i = 0; i < 5; i++)
		{
			vertlist[i] = new Vector(50*Math.sin(i * 2 * Math.PI/5), 50*Math.cos(i * 2 * Math.PI/5));
		}
		Polygon p = new Polygon(1, new Vector(500, 500), vertlist);
		//p.omega = 5;*/
		//p.addForce(new Vector(5, 0), new Vector(0, 10));
		//p.addForce(new Vector(-5, 0), new Vector(0, -10));
		//environment.newEntity(p);
		
		while (true)
		{
			main.loop(environment);
			Thread.sleep(0, 10);
		}
	}
}