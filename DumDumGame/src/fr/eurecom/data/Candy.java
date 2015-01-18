package fr.eurecom.data;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

public class Candy {
	private Point center;
	private int type;
	private int value;
	private DynamicBitmap candyImg;
	private int candySize;
	private Boolean visible;

	public Candy(Point center, int type) {
		this.center = center;
		this.type = type;
		this.visible = true;

		Bitmap bmp = null;

		switch (type) {
		case 1: // jelly beans
			this.value = 1;
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.jellybean);
			this.candySize = 2 * Parameters.dZoomParam;
			break;
		case 2: // kitkats
			this.value = 5;
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.kitkat);
			this.candySize = 3 * Parameters.dZoomParam;
			break;
		case 3: // lollipops
			this.value = 10;
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.lollipop);
			this.candySize = 3 * Parameters.dZoomParam;
			break;
		}

		this.candyImg = new DynamicBitmap(bmp, new Point(this.center.x
				- candySize / 2, this.center.y - candySize / 2), candySize,
				candySize);
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
		candyImg.setPosition(new Point(this.center.x - candySize / 2,
				this.center.y - candySize / 2));
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void show(Canvas canvas, Point offset) {
		if (this.visible) {
			// Point imgPos = new Point(this.pos.x - candySize / 2 + offset.x,
			// this.pos.y - candySize / 2 + offset.y);
			//
			// candyImg.setPosition(imgPos);
			// candyImg.show(canvas);

			candyImg.show(canvas, offset);
		}
	}

	public void reset() {
		this.visible = true;
	}

	public int getEaten() {
		this.visible = false;
		return this.value;
	}

	public Boolean isAvailable() {
		return this.visible;
	}

	public Boolean isOverlapped(Point objPos, int range) {
		if (this.visible == false)
			return false;

		return Helper.Point_GetDistanceFrom(objPos, this.center) < range
				+ this.candySize / 4 ? true : false;
	}
}
