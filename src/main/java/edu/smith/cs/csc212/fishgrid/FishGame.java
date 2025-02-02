package edu.smith.cs.csc212.fishgrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class manages our model of gameplay: missing and found fish, etc.
 * @author jfoley
 *
 */
public class FishGame {
	/**
	 * This is the world in which the fish are missing. (It's mostly a List!).
	 */
	World world;
	/**
	 * The player (a Fish.COLORS[0]-colored fish) goes seeking their friends.
	 */
	Fish player;
	/**
	 * The home location.
	 */
	FishHome home;
	/**
	 * These are the missing fish!
	 */
	List<Fish> missing;
	
	/**
	 * These are fish we've found!
	 */
	List<Fish> found;
	
	/**
	 * Number of steps!
	 */
	int stepsTaken;
	/**
	 * The food, inspired by Ben and Jerry's phish food icecream.
	 */
	FishFood phishFood;
	/**
	 * Score!
	 */
	int score;
	/**
	 * Number of rocks and falling rocks the game will generate
	 */
	public static final int NUM_ROCKS =20;
	public static final int NUM_FALLING_ROCKS =5;
	
	/**
	 * Create a FishGame of a particular size.
	 * @param w how wide is the grid?
	 * @param h how tall is the grid?
	 */
	public FishGame(int w, int h) {
		world = new World(w, h);
		
		missing = new ArrayList<Fish>();
		found = new ArrayList<Fish>();
		
		// Add a home!
		home = world.insertFishHome();
		
		
		for (int i=0; i<NUM_ROCKS; i++) {
			world.insertRockRandomly();
		}
		for (int i=0; i<NUM_FALLING_ROCKS;i++) {
			world.insertFallingRockRandomly();
		}
		
		phishFood = world.insertFishFoodRandomly();
		
		for(int i=0;i<2;i++) {
			world.insertSnailRandomly();
		}
		
		
		
		// Make the player out of the 0th fish color.
		player = new Fish(0, world);
		// Start the player at "home".
		player.setPosition(home.getX(), home.getY());
		player.markAsPlayer();
		world.register(player);
		
		// Generate fish of all the colors but the first into the "missing" List.
		for (int ft = 1; ft < Fish.COLORS.length; ft++) {
			Fish friend = world.insertFishRandomly(ft);
			missing.add(friend);
		}
	}
	
	
	/**
	 * How we tell if the game is over: if missingFishLeft() == 0.
	 * @return the size of the missing list.
	 */
	public int missingFishLeft() {
		return missing.size();
	}
	
	/**
	 * This method is how the Main app tells whether we're done.
	 * @return true if the player has won (or maybe lost?).
	 */
	public boolean gameOver() {
		
		if((player.getX()==home.getX())&&(player.getY()==home.getY())) {
			return missing.isEmpty();
		}
		return false;
	}
	public int incScore(Fish fish) {
		return fish.fishScore();
	}
	public int foodBonus(FishFood phishFood) {
		return phishFood.foodScore;
	}

	/**
	 * Update positions of everything (the user has just pressed a button).
	 */
	public void step() {
		// Keep track of how long the game has run.
		this.stepsTaken += 1;
				
		// These are all the objects in the world in the same cell as the player.
		List<WorldObject> overlap = this.player.findSameCell();
		// The player is there, too, let's skip them.
		overlap.remove(this.player);
		
		// If we find a fish, remove it from missing.
		for (WorldObject wo : overlap) {
			
			// if the player is on the food, give it points
			if(wo.isFood()) {
				score += foodBonus((FishFood) wo);
				//remove the food after it has been eaten
				wo.remove();
			}
			// It is missing if it's in our missing list.
			if (missing.contains(wo)) {
				// Remove this fish from the missing list.
				missing.remove(wo);
				
				// Remove from world.
				
				found.add((Fish) wo);
				
				
				// Increase score when you find a fish!
				score += incScore((Fish) wo);
				
				
			}
		}
		// if a missing fish gets to the fish food before the player, the food is removed 
		//and the player wont get the points 
		for(Fish fish : missing) {
			if((fish.getX() == phishFood.getX())&&(fish.getY()==phishFood.getY())) {
				phishFood.remove();
			}
		}
		
		// Make sure missing fish *do* something.
		wanderMissingFish();
		// When fish get added to "found" they will follow the player around.
		World.objectsFollow(player, found);
		// Step any world-objects that run themselves.
		world.stepAll();
	}
	
	/**
	 * Call moveRandomly() on all of the missing fish to make them seem alive.
	 */
	private void wanderMissingFish() {
		Random rand = ThreadLocalRandom.current();
		for (Fish lost : missing) {
			// 30% of the time, lost fish move randomly.
			if ((rand.nextDouble() < 0.3)&&!lost.fastScared) {
				lost.moveRandomly();
			//if the fish is fast.Scared, it has an 80% chance of moving randomly
			}else if((rand.nextDouble()<.8)&& lost.fastScared) {
				lost.moveRandomly();
			}
		}
	}

	/**
	 * This gets a click on the grid. We want it to destroy rocks that ruin the game.
	 * @param x - the x-tile.
	 * @param y - the y-tile.
	 */
	public void click(int x, int y) {
		// 
		System.out.println("Clicked on: "+x+","+y+ " world.canSwim(player,...)="+world.canSwim(player, x, y));
		List<WorldObject> atPoint = world.find(x, y);
		for(WorldObject wo : atPoint) {
			if(wo.isRock()) {
				wo.remove();
			}
		}
	}
	
}
