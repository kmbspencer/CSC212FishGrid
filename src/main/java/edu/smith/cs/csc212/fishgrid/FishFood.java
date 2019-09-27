package edu.smith.cs.csc212.fishgrid;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.Color;


public class FishFood extends WorldObject {
	// number of points the player gets for eating the food
	public int foodScore = 50;
	
	public Color foodcolor = new Color(5, 59, 20);
	
	public FishFood(World world) {
		super(world);
	}
	@Override
	public void step() {
		//food doesnt move
	}
	@Override
	public void draw(Graphics2D g) {
		g.setColor(foodcolor);
		Ellipse2D food = new Ellipse2D.Double(-.5,-.5,1,1);
		g.fill(food);
		
	}

}
