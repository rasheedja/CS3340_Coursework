package Mars;

import java.util.ArrayList;

class Vehicle extends Entity{
	public boolean carryingSample;
	
	public Vehicle(Location l){
		super(l);	
		this.carryingSample = false;
	}

	public void act(Field f, Mothership m, ArrayList<Rock> rocksCollected)
	{
		actCollaborative(f,m,rocksCollected);
		//actSimple(f,m,rocksCollected);
	}
	
	
	public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected){
		//complete this method
	}

	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected){
		//complete this method
	}
}
