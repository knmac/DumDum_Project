package fr.eurecom.dumdumgame;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Button extends DynamicBitmap {
	
	// variable
	private Object dID;

	// constructor
	public Button(Object ID, Bitmap[] btnAppearance, Point position, int width,
			int height) {
		super(btnAppearance, position, 0, width, height);
		this.dID = ID;
	}

	public Button(Object ID, Bitmap btnAppearance, Point position, int width,
			int height) {
		super(btnAppearance, position, 0, width, height);
		this.dID = ID;
	}

	public Object getID() {
		return this.dID;
	}

}
