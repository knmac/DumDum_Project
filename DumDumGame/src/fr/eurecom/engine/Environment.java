package fr.eurecom.engine;

import android.graphics.Point;

public abstract class Environment {
	
	private Point[] forceList;
	private double depletingCoef;

	protected Point[] getForceList() {
		return forceList;
	}

	protected void setForceList(Point[] forceList) {
		this.forceList = forceList;
	}

	protected double getDepletingCoef() {
		return depletingCoef;
	}

	protected void setDepletingCoef(double depletingCoef) {
		this.depletingCoef = depletingCoef;
	}

}
