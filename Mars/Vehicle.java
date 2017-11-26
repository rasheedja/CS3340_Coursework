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
//		actCollaborative(f,m,rocksCollected);
//		actSimple(f,m,rocksCollected);
		actCollaborativeOptimised(f,m,rocksCollected);
	}

    /**
     * When vehicles are using this architecture, in addition to acting simply, they can indirectly communicate with
     * other vehicles by dropping crumbs. Vehicles will drop two crumbs for each step when they have picked up a
     * rock and travel towards the mothership. When other vehicles see these crumbs, they will pick one up and
     * travel away from the mothership, which will hopefully lead other vehicles to rock clusters, speeding up sample
     * collection.
     *
     * @param f The field that the vehicle is in
     * @param m The mothership
     * @param rocksCollected The rocks collected by the vehicle
     */
    public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
        // If the vehicle is carry a sample and is next to the mothership, drop the sample
        if (carryingSample && f.isNeighbourTo(this.location, m.getClass())) {
            this.carryingSample = false;
            return;
        }

        // If the vehicle is carry a sample and is NOT at the mothership, drop two crumbs and travel up the gradient.
        // The crumbs are used to give incentives to other vehicles to follow them, hopefully leading other vehicles
        // to rock clusters.
        if (carryingSample && !this.location.equals(m.getLocation())) {
            f.dropCrumbs(this.location, 2);
            moveToHigherGradient(f);
            return;
        }


        // If the vehicle has a rock next to it, pick it up
        if (f.isNeighbourTo(this.location, Rock.class)) {
            pickUpNeighbouringRock(f, rocksCollected);
            return;
        }

        // If the vehicle has found a crumb, pick it up and travel down the gradient
        if (f.getCrumbQuantityAt(this.location) > 0) {
            f.pickUpACrumb(this.location);
            moveToLowerGradient(f);
            return;
        }

        // If none of the above rules are true, the vehicle will move to a random location
        if (true) {
            moveToRandomAdjacentLocation(f);
            return;
        }
    }

    /**
     * When vehicles are using this architecture, they will move around randomly until they find a rock. When a vehicle
     * finds a rock, it will pick up the rock and then travel back towards the mothership. The vehicle will then drop
     * the rock at the ship and travel randomly again until it finds another rock.
     *
     * @param f The field that the vehicle is in
     * @param m The mothership
     * @param rocksCollected The rocks collected by the vehicle
     */
    public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
        // If the vehicle is carry a sample and is next to the mothership, drop the sample
	    if (carryingSample && f.isNeighbourTo(this.location, m.getClass())) {
            this.carryingSample = false;
            return;
        }


        // If the vehicle is carry a sample and is NOT at the mothership, travel up the gradient
        if (carryingSample && !this.location.equals(m.getLocation())) {
            moveToHigherGradient(f);
            return;
        }

        // If the vehicle has a rock next to it, pick it up
        if (f.isNeighbourTo(this.location, Rock.class)) {
            pickUpNeighbouringRock(f, rocksCollected);
	        return;
        }

        // If none of the above rules are true, the vehicle will move to a random location
        if (true) {
            moveToRandomAdjacentLocation(f);
            return;
        }
	}

    /**
     * When vehicles are using this architecture, in addition to acting collabarotively, when vehicles find a crumb,
     * they will follow the crumb trail rather than just travelling down the gradient, and for every two rocks the
     * the vehicle finds, it will reduce the number of crumbs by one across the whole field.
     *
     * @param f The field that the vehicle is in
     * @param m The mothership
     * @param rocksCollected The rocks collected by the vehicle
     */
    public void actCollaborativeOptimised(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
        // If the vehicle is carry a sample and is next to the mothership, drop the sample
        if (carryingSample && f.isNeighbourTo(this.location, m.getClass())) {
            this.carryingSample = false;
            return;
        }

        // If the vehicle is carry a sample and is NOT at the mothership, drop two crumbs and travel up the gradient.
        // The crumbs are used to give incentives to other vehicles to follow them, hopefully leading other vehicles
        // to rock clusters.
        if (carryingSample && !this.location.equals(m.getLocation())) {
            f.dropCrumbs(this.location, 2);
            moveToHigherGradient(f);
            return;
        }


        // If the vehicle has a rock next to it, pick it up. For every two rocks picked up by the vehicle, reduce the
        // number of crumbs across the entire field
        if (f.isNeighbourTo(this.location, Rock.class)) {
            pickUpNeighbouringRock(f, rocksCollected);
            if (rocksCollected.size() % 2 == 0) {
                f.reduceCrumbs();
            }
            return;
        }

        // If the vehicle has found a crumb, pick it up and travel to the next crumb or down the gradient
        if (f.getCrumbQuantityAt(this.location) > 0) {
            f.pickUpACrumb(this.location);
            moveToNextCrumbOrLowerGradient(f);
            return;
        }

        // If none of the above rules are true, the vehicle will move to a random location
        if (true) {
            moveToRandomAdjacentLocation(f);
            return;
        }
    }

    /**
     * Move the vehicle to a higher gradient. This is done by getting all free adjacent locations, and then checking
     * which of these locations has the highest signal strength. The ship is then moved to said location.
     *
     * @param f The field that contains the vehicle
     */
    private void moveToHigherGradient(Field f) {
        ArrayList<Location> freeLocations = f.getAllfreeAdjacentLocations(this.location);
        Location highestGradientLocation = null;
        for (Location freeLocation : freeLocations) {
            if (highestGradientLocation == null || f.getSignalStrength(freeLocation) > f.getSignalStrength(highestGradientLocation)) {
                highestGradientLocation = freeLocation;
            }
        }
        moveToLocation(highestGradientLocation, f);
    }

    /**
     * Move the vehicle to a lower gradient. This is done by getting all free adjacent locations, and then checking
     * which of these locations has the lowest signal strength. The ship is then moved to said location.
     *
     * @param f The field that contains the vehicle
     */
    private void moveToLowerGradient(Field f) {
        ArrayList<Location> freeLocations = f.getAllfreeAdjacentLocations(this.location);
        Location lowestGradientLocation = null;
        for (Location freeLocation : freeLocations) {
            if (lowestGradientLocation == null || f.getSignalStrength(freeLocation) < f.getSignalStrength(lowestGradientLocation)) {
                lowestGradientLocation = freeLocation;
            }
        }
        moveToLocation(lowestGradientLocation, f);
    }

    /**
     * Move the vehicle to the location with the most crumbs or a lower gradient. This should be more efficient
     * than just moving down the gradient as the vehicle will follow the crumb trial directly to a cluster of rocks.
     * If there are no crumbs to follow, the vehicle will simply move down the gradient as normal.
     *
     * @param f The field that contains the vehicle
     */
    private void moveToNextCrumbOrLowerGradient(Field f) {
        ArrayList<Location> freeLocations = f.getAllfreeAdjacentLocations(this.location);
        Location nextLocation = null;
        for (Location freeLocation : freeLocations) {
            if (nextLocation == null || f.getCrumbQuantityAt(freeLocation) > f.getCrumbQuantityAt(nextLocation)) {
                nextLocation = freeLocation;
            } else if (f.getCrumbQuantityAt(nextLocation) == 0) {
                if (f.getSignalStrength(freeLocation) < f.getSignalStrength(nextLocation)) {
                    nextLocation = freeLocation;
                }
            }
        }
        moveToLocation(nextLocation, f);
    }


    /**
     * Pick up a rock next to the vehicle, add it to the list of `s collected by this vehicle, and
     * remove the rock from the field.
     *
     * @param f              The field that the vehicle is in
     * @param rocksCollected The rocks that have been collected by this vehicle
     */
    private void pickUpNeighbouringRock(Field f, ArrayList<Rock> rocksCollected) {
        Rock rock = (Rock) f.getObjectAt(f.getNeighbour(this.location, Rock.class));
        rocksCollected.add(rock);
        f.clearLocation(rock.getLocation());
        this.carryingSample = true;
    }

    /**
     * Move the vehicle to the given location, and update the field to correctly represent the vehicles locatoin
     *
     * @param newLocation     The new location of the vehicle
     * @param f               The field that the vehicle is on
     */
	private void moveToLocation(Location newLocation, Field f) {
	    f.clearLocation(this.location);
	    this.location = newLocation;
	    f.place(this, newLocation);
    }


    /**
     * Move the vehicle to a random location next to it
     *
     * @param f The field that the vehicle is in
     */
    private void moveToRandomAdjacentLocation(Field f) {
        ArrayList<Location> freeLocations = f.getAllfreeAdjacentLocations(this.location);
        moveToLocation(freeLocations.get(ModelConstants.random.nextInt(freeLocations.size())), f);
    }
}
