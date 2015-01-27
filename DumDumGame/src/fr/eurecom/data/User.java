package fr.eurecom.data;

import java.util.LinkedList;

public class User {
	private String name;
	private int unlockedLevel;
	private LinkedList<Integer> levelCandies = new LinkedList<Integer>();
	private int currentCandies;
	private int currentLives;
	private int maxLives;
	private int refillTime; // in seconds
	private String lastTime; //TODO: change format
	private LinkedList<Integer> gearAmount = new LinkedList<Integer>();
	
	public String getLastTime() {
		return this.lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	
	public void setCurrentLives(int lives) {
		this.currentLives = lives;
		if (this.gearAmount.size() != 0)
			this.gearAmount.set(0, this.currentLives);
	}

	public int getCurrentLives() {
		return this.currentLives;
	}
	
	public void setMaxLives(int lives) {
		this.maxLives = lives;
	}

	public int getMaxLives() {
		return this.maxLives;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setCurrentMoney(int currentMoney) {
		this.currentCandies = currentMoney;
	}

	public int getCurrentMoney() {
		return currentCandies;
	}
	
	public void setRefillTime(int refillTime) {
		this.refillTime = refillTime;
	}

	public int getRefillTime() {
		return this.refillTime;
	}

	public void setUnlockedLevel(int unlockedLevel) {
		this.unlockedLevel = unlockedLevel;
	}

	public int getUnlockedLevel() {
		return unlockedLevel;
	}

	public void setLevelScore(LinkedList<Integer> levelScore) {
		this.levelCandies = levelScore;
	}

	public LinkedList<Integer> getLevelScore() {
		return levelCandies;
	}
	
	public void setGearAmount(LinkedList<Integer> gearAmount) {
		this.gearAmount = gearAmount;
		this.currentLives = this.gearAmount.getFirst();
	}

	public LinkedList<Integer> getGearAmount() {
		gearAmount.set(0, this.currentLives);
		return gearAmount;
	}
}
