package fr.eurecom.engine;

import java.util.Random;

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
	private Point pos;
	private int type;
	private int value;
	private DynamicBitmap candyImg;
	private int candySize;
	private Boolean visible;

	public Candy(Point pos, int type) {
		this.pos = pos;
		this.type = type;
		this.visible = true;

		Bitmap bmp = null;
		Random rand = new Random(System.currentTimeMillis());

		switch (type) {
		case 1: // jelly beans
			this.value = 1;
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.jellybean);
			// Helper.changeHue(bmp, rand.nextInt(360));
			break;
		case 2: // kitkats
			this.value = 5;
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.kitkat);
			break;
		case 3: // lollipops
			this.value = 10;
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.lollipop);
			break;
		}

		this.candySize = 3 * Parameters.dZoomParam;
		this.candyImg = new DynamicBitmap(bmp, this.pos, candySize, candySize);
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
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

	public void show(Canvas canvas) {
		if (this.visible) {
			Point imgPos = new Point(this.pos.x - candySize / 2, this.pos.y
					- candySize / 2);

			candyImg.setPosition(imgPos);
			candyImg.show(canvas);
		}
	}
	
	public void reset() {
		this.visible = true;
	}
	
	public int getEaten() {
		this.visible = false;
		return this.value;
	}
}
