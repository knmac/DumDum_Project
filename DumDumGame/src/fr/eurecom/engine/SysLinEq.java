package fr.eurecom.engine;

public class SysLinEq {
	private double a, b, c, a1, b1, c1;

	public void setA(double a) {
		this.a = a;
	}

	public void setB(double b) {
		this.b = b;
	}

	public void setC(double c) {
		this.c = c;
	}

	public void setA1(double a1) {
		this.a1 = a1;
	}

	public void setB1(double b1) {
		this.b1 = b1;
	}

	public void setC1(double c1) {
		this.c1 = c1;
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getC() {
		return c;
	}

	public double getA1() {
		return a1;
	}

	public double getB1() {
		return b1;
	}

	public double getC1() {
		return c1;
	}

	public SysLinEq() {
		a = b = c = a1 = b1 = c1 = 0.0;
	}

	public SysLinEq(double a, double b, double c, double a1, double b1,
			double c1) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.a1 = a1;
		this.b1 = b1;
		this.c1 = c1;
	}

	public double GetXSolution() throws Exception {
		double D = this.D();
		if (D == 0.0) {
			throw new Exception(
					"Your system of linear equations has zero/infinite solutions");
		}
		return this.Dx() / D;
	}

	public double GetYSolution() throws Exception {
		double D = this.D();
		if (D == 0.0) {
			throw new Exception(
					"Your system of linear equations has zero/infinite solutions");
		}
		return this.Dy() / D;
	}

	private double D() {
		return a * b1 - a1 * b;
	}

	private double Dx() {
		return c * b1 - b * c1;
	}

	private double Dy() {
		return a * c1 - a1 * c;
	}
}
