package fr.eurecom.data;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Point;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

public class Bee {
	private LinkedList<Point> orbitVertices;
	private LinkedList<Point> orbit;
	private int orbitIdx;
	private DynamicBitmap beeImg;
	private int radius;

	// a segment (A,B) is divided into many subsegments, each with the length of
	// subSegLen
	private double subSegLen;

	public Bee(LinkedList<Point> orbitVertices) {
		this(orbitVertices, Parameters.dZoomParam);
	}

	public Bee(LinkedList<Point> orbitVertices, int radius) {
		this.orbitVertices = orbitVertices;
		this.radius = radius;
		this.orbitIdx = 0;
		this.subSegLen = Parameters.dZoomParam / 2;

		// dummmy point
		Point imgTopLeft = new Point(orbitVertices.getFirst().x - radius,
				orbitVertices.getFirst().y - radius);
		beeImg = new DynamicBitmap(Parameters.bmpBee, imgTopLeft,
				Parameters.randomGenerator.nextInt(Parameters.bmpBee.length),
				3 * radius, 3 * radius);
	}

	public LinkedList<Point> getOrbitVertices() {
		return orbitVertices;
	}

	public void setOrbitVertices(LinkedList<Point> points) {
		orbitVertices = points;
	}

	public void show(Canvas canvas, Point offset) {
		Point imgTopLeft = new Point(orbit.get(orbitIdx).x - radius,
				orbit.get(orbitIdx).y - radius);
		beeImg.setPosition(imgTopLeft);
		beeImg.show(canvas, offset);
		beeImg.updateToTheNextImage();

		orbitIdx = (orbitIdx == (orbit.size() - 1)) ? 0 : orbitIdx + 1;
	}

	public Boolean isOverlapped(Point objPos, int range) {
		return Helper.Point_GetDistanceFrom(objPos, this.orbit.get(orbitIdx)) < range
				+ radius / 2 ? true : false;
	}

	public void generateOrbit() {
		LinkedList<Point> tmp;
		orbit = new LinkedList<Point>();

		for (int i = 0; i < orbitVertices.size() - 1; i++) {
			tmp = generateSubSegments(orbitVertices.get(i),
					orbitVertices.get(i + 1), subSegLen);
			for (Point point : tmp) {
				orbit.add(point);
			}
		}

		tmp = generateSubSegments(orbitVertices.getLast(),
				orbitVertices.getFirst(), subSegLen);
		for (Point point : tmp) {
			orbit.add(point);
		}
	}

	private LinkedList<Point> generateSubSegments(Point p1, Point p2,
			double subLen) {
		LinkedList<Point> points = new LinkedList<Point>();

		/********************************************/
		double segLen = Math.sqrt(Math.pow(p2.x - p1.x, 2.0)
				+ Math.pow(p2.y - p1.y, 2.0));
		double directX = (p2.x - p1.x) / segLen;
		double directY = (p2.y - p1.y) / segLen;

		double l = 0;
		while (l < segLen) {
			int x = (int) (p1.x + directX * l);
			int y = (int) (p1.y + directY * l);

			points.add(new Point(x, y));
			l += subSegLen;
		}

		/********************************************/

		return points;
	}

	public void swithSpeed() {

	}
}
