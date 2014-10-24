package fr.eurecom.data;

import java.util.LinkedList;

import android.graphics.Point;

public class User {
	private String name;

    private int currentLevel;

    private int currentScore;

    private Point currentPos = new Point(0, 0);

    private int unlockedLevel;

    private LinkedList<Integer> levelScore = new LinkedList<Integer>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentPos(Point currentPos) {
		this.currentPos = currentPos;
	}

	public Point getCurrentPos() {
		return currentPos;
	}

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
