package fr.eurecom.data;

import java.util.Calendar;
import java.util.LinkedList;

import android.graphics.Point;

public class User {
	private String name;
	// private int currentLevel;
	// private int currentScore;
	// private Point currentPos = new Point(0, 0);
	private int unlockedLevel;
	private LinkedList<Integer> levelScore = new LinkedList<Integer>();
	private int currentMoney;
	private int currentLives;
	private int maxLives;
	private int refillTime; // in seconds
	private String lastTime; //TODO: change format
	
	public String getLastTime() {
		return this.lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	
	public void setCurrentLives(int lives) {
		this.currentLives = lives;
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
		this.currentMoney = currentMoney;
	}

	public int getCurrentMoney() {
		return currentMoney;
	}
	
	public void setRefillTime(int refillTime) {
		this.refillTime = refillTime;
	}

	public int getRefillTime() {
		return this.refillTime;
	}

	// public void setCurrentLevel(int currentLevel) {
	// this.currentLevel = currentLevel;
	// }
	//
	// public int getCurrentLevel() {
	// return currentLevel;
	// }

	// public void setCurrentScore(int currentScore) {
	// this.currentScore = currentScore;
	// }
	//
	// public int getCurrentScore() {
	// return currentScore;
	// }

	// public void setCurrentPos(Point currentPos) {
	// this.currentPos = currentPos;
	// }
	//
	// public Point getCurrentPos() {
	// return currentPos;
	// }

	public void setUnlockedLevel(int unlockedLevel) {
		this.unlockedLevel = unlockedLevel;
	}

	public int getUnlockedLevel() {
		return unlockedLevel;
	}

	public void setLevelScore(LinkedList<Integer> levelScore) {
		this.levelScore = levelScore;
	}

	public LinkedList<Integer> getLevelScore() {
		return levelScore;
	}
}
