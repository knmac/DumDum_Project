package fr.eurecom.engine;

import android.graphics.Point;

public class Earth extends Environment{
	
	private final Point[] forceList = {new Point(0, 100), new Point(20, 0)};
	private final double depletingCoef = 0.7;
	
	
	public Earth() {		
		this.setForceList(forceList);
		this.setDepletingCoef(depletingCoef);
	}

}
