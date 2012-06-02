package lejos.robotics.pathfinding;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

// TODO: Make this an invisible inner class.
public class GridNode extends Node {

	private static float grid_space = 0; // TODO: Ruins possibility of using multiple grid sizes, saves memory. 
	
	// TODO: Override with alternate int, int constructor? Might make slightly faster, might not be worth it.
	// TODO: Technically grid_space only needs to be set once by FourWayGridMesh. Not each time in each constructor.
	public GridNode(float x, float y, float grid_space) {
		super(x, y);
		GridNode.grid_space = grid_space;
	}
	
	protected float calculateG(Node neighbor) {
		return grid_space;
	}
	
	protected float calculateH(Node neighbor) {
		return Math.abs(this.x - neighbor.x) + Math.abs(this.y - neighbor.y);
	}
	
}
