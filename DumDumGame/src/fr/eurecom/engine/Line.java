package fr.eurecom.engine;

import fr.eurecom.utility.Helper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Line {

	private Point _firstPoint = new Point(0, 0);
	private Point _secondPoint = new Point(0, 0);
	private int A, B, C; // The 3 coefficients of the line equation Ax + By + C
							// = 0;

	public Line(Point firstPoint, Point secondPoint) // construct the line using
														// 2 points
	{
		_firstPoint = firstPoint;
		if (firstPoint.equals(secondPoint.x, secondPoint.y))
			_secondPoint = new Point(firstPoint.x + 1, firstPoint.y);
		else
			_secondPoint = secondPoint;

		A = _secondPoint.y - _firstPoint.y;
		B = _firstPoint.x - _secondPoint.x;
		C = -(A * +_firstPoint.x) - (B * _firstPoint.y);
	}

	// construct the line using 1 point and the line's normal vector
	public Line(Point root, int normalVector_HorizontalPart,
			int normalVector_VerticalPart) throws Exception {
		if (normalVector_HorizontalPart == 0 && normalVector_VerticalPart == 0)
			throw new Exception("The line is not well defined");

		_firstPoint = root;
		A = normalVector_HorizontalPart;
		B = normalVector_VerticalPart;
		C = -(A * +_firstPoint.x) - (B * _firstPoint.y);
		if (B == 0) {
			_secondPoint.x = _firstPoint.x;
			_secondPoint.y = _firstPoint.y + 1;
		} else {
			_secondPoint.x = _firstPoint.x + 1;
			_secondPoint.y = -A / B + _firstPoint.y;
		}
	}

	public Point getFirstPoint() {
		return _firstPoint;
	}

	public void setFirstPoint(Point firstPoint) throws Exception {
		if (_secondPoint.equals(firstPoint.x, firstPoint.y))
			throw new Exception("The line is not well-defined");
		_firstPoint = firstPoint;

		A = _secondPoint.y - _firstPoint.y;
		B = _firstPoint.x - _secondPoint.x;
		C = -(A * +_firstPoint.x) - (B * _firstPoint.y);
	}

	public Point getSecondPoint() {
		return _secondPoint;
	}

	public void setSecondPoint(Point secondPoint) throws Exception {
		if (_firstPoint.equals(secondPoint.x, secondPoint.y))
			throw new Exception("The line is not well-defined");
		_secondPoint = secondPoint;

		A = _secondPoint.y - _firstPoint.y;
		B = _firstPoint.x - _secondPoint.x;
		C = -(A * +_firstPoint.x) - (B * _firstPoint.y);
	}

	public boolean IsValid() {
		return !(_firstPoint.equals(_secondPoint.x, _secondPoint.y));
	}

	public Point GetNormalVector() {
		return new Point(A, B);
	}

	public Point GetDirectionalVector() {
		return new Point(B, -A);
	}

	public boolean StrictlyContains(Point p) {
		return (A * p.x + B * p.y + C == 0);
	}

	public boolean RoughlyContains(Point p) throws Exception {
		double distance = Helper.Point_GetDistanceFrom(p, this);
		if (distance < 1.5)
			return true;
		else
			return false;
	}

	public Point GetCommonPointWith(Line otherLine) throws Exception {
		SysLinEq equation = new SysLinEq(this.A, this.B, -this.C, otherLine.A,
				otherLine.B, -otherLine.C);
		return new Point((int) equation.GetXSolution(),
				(int) equation.GetYSolution());
	}

	public static Point GetCommonPointBetween(int x1, int y1, int x2, int y2,
			int x3, int y3, int x4, int y4) throws Exception {
		return GetCommonPointBetween(new Point(x1, y1), new Point(x2, y2),
				new Point(x3, y3), new Point(x4, y4));
	}

	public static Point GetCommonPointBetween(Point point1, Point point2,
			Point point3, Point point4) throws Exception {
		Line line1 = new Line(point1, point2);
		Line line2 = new Line(point3, point4);
		return line1.GetCommonPointWith(line2);
	}

	public Point GetMirrorPointOf(Point point) throws Exception
	// Return the image point of a given point through the calling-line
	{
		Line normalLine = new Line(point, this.B, -this.A);
		Point commonPoint = this.GetCommonPointWith(normalLine);

		return new Point(2 * commonPoint.x - point.x, 2 * commonPoint.y
				- point.y);
	}

	public Line GenerateReflectedLineFrom(Line obstacle) throws Exception {
		int flag = getRelationShipWith(obstacle);
		if (flag != 1 && flag != 2) {
			throw new Exception(
					"Line.GenerateReflectedLineFrom(Line obstacle) method encounters an error: The obstacle is not reachable!");
		}

		return this.FastGenerateReflectedLineFrom(obstacle);
	}

	public Line FastGenerateReflectedLineFrom(Line obstacle) throws Exception
	// Use this method at your own risk
	{
		Point commonPoint; // between incident line and obstacle line
		Point mirrorPoint; // on the reflected line
							// mirrorPoint, together with commonPoint form up
							// the reflected line.
		try {
			// The first point of the reflected line
			commonPoint = this.GetCommonPointWith(obstacle);
			// Normal line of the obstacle line from the commonPoint
			Line normalLine = new Line(commonPoint, obstacle.B, -obstacle.A); 

			mirrorPoint = normalLine.GetMirrorPointOf((this._firstPoint.equals(
					commonPoint.x, commonPoint.y)) ? this._secondPoint
					: this._firstPoint);
		} catch (Exception ex) {
			throw new Exception(
					"Line.FastGenerateReflectedLineFrom(Line obstacle) method encounters an error!");
		}

		return new Line(commonPoint, mirrorPoint);
	}

	public void Show(Canvas canvas)
	// Show the line from its firstPoint till its secondPoint
	{
		canvas.drawLine(_firstPoint.x, _firstPoint.y, _secondPoint.x,
				_secondPoint.y, new Paint());
	}

	public int getRelationShipWith(Line anotherLine) throws Exception
	// Return 1 if anotherLine and this calling-line share a common Point
	// between their firstPoint and secondPoint
	// Return 2 if anotherLine is the outside target of this calling-line
	// Return 4 if anotherLine is parallel with this calling-line
	// Return 0 if anotherLine is simply unreachable
	{
		Point commonPoint;

		try {
			commonPoint = GetCommonPointWith(anotherLine);
		} catch (Exception ex) {
			return 4;
		}

		// if (this.RoughlyContains(anotherLine.FirstPoint) &&
		// this.RoughlyContains(anotherLine.SecondPoint))
		// return 4;

		if (Helper.Point_GetDistanceFrom(this._firstPoint, commonPoint) <= 1.5)
			return 0;

		Segment s = new Segment(this._firstPoint, this._secondPoint);
		if (s.roughlyContains(commonPoint))
			return 1;

		Ray r = new Ray(this._firstPoint, this._secondPoint);
		if (r.roughlyContains(commonPoint))
			return 2;

		return 0;
	}
}
