package fr.eurecom.engine;

import fr.eurecom.utility.Helper;
import android.graphics.Point;

public class Ray {
	private Line line;

	public enum RelationShip {
		UNREACHABLE, PARALLEL, TARGET, PASS_BY
	}

	public Ray(Point root, Point anotherPoint) {
		line = new Line(root, anotherPoint);
	}

	public Point getRoot() {
		return line.getFirstPoint();
	}

	public void setRoot(Point root) throws Exception {
		line.setFirstPoint(root);
	}

	public Point getSecondPoint() {
		return line.getSecondPoint();
	}

	public void setSecondPoint(Point point) throws Exception {
		line.setSecondPoint(point);
	}

	public boolean isValid() {
		return line.IsValid();
	}

	public Point getNormalVector() {
		return line.GetNormalVector();
	}

	public boolean strictlyContains(Point p) throws Exception {
		if (!isValid())
			throw new Exception("The ray is not well-defined");

		if (line.StrictlyContains(p)) {
			Segment s = new Segment(line.getFirstPoint(), line.getSecondPoint());
			if (s.strictlyContains(p))
				return true;
			else {
				if (Helper.Point_GetDistanceFrom(p, line.getFirstPoint())
						- Helper.Point_GetDistanceFrom(p,
								line.getSecondPoint()) > 0)
					return true;
				else
					return false;
			}
		} else
			return false;
	}

	public boolean roughlyContains(Point p) throws Exception {
		if (!isValid())
			throw new Exception("The ray is not well-defined");

		if (line.RoughlyContains(p)) {
			Segment s = new Segment(line.getFirstPoint(), line.getSecondPoint());
			if (s.roughlyContains(p))
				return true;
			else {
				if (Helper.Point_GetDistanceFrom(p, line.getFirstPoint())
						- Helper.Point_GetDistanceFrom(p,
								line.getSecondPoint()) > 0)
					return true;
				else
					return false;
			}
		} else
			return false;
	}

	public Point getCommonPointWith(Segment segment) throws Exception {
		Point candidatePoint = line.GetCommonPointWith(segment.toLine());

		if (this.roughlyContains(candidatePoint)
				&& segment.roughlyContains(candidatePoint))
			return candidatePoint;
		else
			throw new Exception("Unable to find the common point");
	}

	public Point getMirrorPointOf(Point point) throws Exception {
		return line.GetMirrorPointOf(point);
	}

	public RelationShip getRelationShipWith(Segment s) throws Exception {
		int relationShip = line.getRelationShipWith(s.toLine());
		switch (relationShip) {
		case 0:
			return RelationShip.UNREACHABLE;
		case 4:
			return RelationShip.PARALLEL;
		case 1:
		case 2:
			try {
				this.getCommonPointWith(s);
			} catch (Exception ex) {
				return RelationShip.PASS_BY;
			}
			return RelationShip.TARGET;
		default:
			throw new Exception(
					"There was an error with method: Ray::getRelationShipWith(Segment s)");
		}
	}

	public Ray generateReflectedRayFrom(Segment obstacle) throws Exception {
		if (obstacle == null)
			return null;
		if (this.getRelationShipWith(obstacle) != RelationShip.TARGET)
			return null;

		Line reflectedLine = line.FastGenerateReflectedLineFrom(obstacle
				.toLine());
		return new Ray(reflectedLine.getFirstPoint(),
				reflectedLine.getSecondPoint());
	}

	public Ray fastGenerateReflectedRayFrom(Line obstacle) throws Exception
	// Use this method at your own risk
	{
		if (obstacle == null)
			throw new Exception("Generate reflected ray from a null obstacle!");
		Line reflectedLine = line.FastGenerateReflectedLineFrom(obstacle);
		return new Ray(reflectedLine.getFirstPoint(),
				reflectedLine.getSecondPoint());
	}

	public double getLength() {
		return Helper.Point_GetDistanceFrom(this.getRoot(),
				this.getSecondPoint());
	}

	public Line toLine() {
		return this.line;
	}
}
