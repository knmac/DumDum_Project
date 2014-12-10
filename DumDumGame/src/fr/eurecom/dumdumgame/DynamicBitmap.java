package fr.eurecom.dumdumgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class DynamicBitmap {

	private Bitmap[] images;
	private Point _position = new Point(0, 0);
	private int _currentIndex;
	private int _width;
	private int _height;

	public DynamicBitmap(Bitmap[] images, Point position, int startIndex,
			int width, int height) {
		this.images = images;
		this._position = position;
		this._currentIndex = startIndex;
		this._width = width;
		this._height = height;
	}

	public DynamicBitmap(Bitmap image, Point position, int startIndex,
			int width, int height) {
		this.images = new Bitmap[1];
		this.images[0] = image;
		this._position = position;
		this._currentIndex = startIndex;
		this._width = width;
		this._height = height;
	}

	public DynamicBitmap(Bitmap image, Point position, int width, int height) {
		this.images = new Bitmap[1];
		this.images[0] = image;
		this._position = position;
		this._currentIndex = 0;
		this._width = width;
		this._height = height;
	}

	public DynamicBitmap(Bitmap[] images, Point position) {
		this.images = images;
		this._position = position;
		this._currentIndex = 0;
		this._width = images[0].getWidth();
		this._height = images[0].getHeight();
	}

	public DynamicBitmap(Bitmap image, Point position) {
		this.images = new Bitmap[1];
		this.images[0] = image;
		this._position = position;
		this._currentIndex = 0;
		this._width = images[0].getWidth();
		this._height = images[0].getHeight();
	}

	// Only for inherited classes
	protected DynamicBitmap() {
		this.images = null;
		this._position = new Point(0, 0);
		this._currentIndex = 0;
		this._width = 0;
		this._height = 0;
	}

	protected void constructor(Bitmap[] images, Point position, int startIndex,
			int width, int height) {
		this.images = images;
		this._position = position;
		this._currentIndex = startIndex;
		this._width = width;
		this._height = height;
	}

	public Point getPosition() {
		return _position;
	}

	public void setPosition(Point position) {
		this._position = position;
	}

	public void setCurrentIndex(int index) {
		this._currentIndex = index;
	}

	public int getCurrentIndex() {
		return _currentIndex;
	}

	public int getWidth() {
		return _width;
	}

	public void setWidth(int width) {
		this._width = width;
	}

	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		this._height = height;
	}

	public boolean isClicked(Point point) {
		Rect boundary = new Rect(_position.x, _position.y,
				_position.x + _width, _position.y + _height);
		return boundary.contains(point.x, point.y);
	}

	public void show(Canvas canvas) {
		Rect srcRect = new Rect(0, 0, images[_currentIndex].getWidth(),
				images[_currentIndex].getHeight());
		Rect dstRect = new Rect(_position.x, _position.y, _position.x + _width,
				_position.y + _height);
		canvas.drawBitmap(images[_currentIndex], srcRect, dstRect, new Paint());
	}

	public void show(Canvas canvas, Point offset) {
		Rect srcRect = new Rect(0, 0, images[_currentIndex].getWidth(),
				images[_currentIndex].getHeight());
		Rect dstRect = new Rect(_position.x + offset.x, _position.y + offset.y,
				_position.x + offset.x + _width, _position.y + offset.y
						+ _height);
		canvas.drawBitmap(images[_currentIndex], srcRect, dstRect, new Paint());
	}

	public void showEx(Canvas canvas, int srcX, int srcY, int srcWidth,
			int srcHeight) {
		Rect srcRect = new Rect(srcX, srcY, srcX + srcWidth, srcY + srcHeight);
		Rect dstRect = new Rect(_position.x + srcX, _position.y + srcY,
				_position.x + srcX + srcWidth, _position.y + srcY + srcHeight);
		canvas.drawBitmap(images[_currentIndex], srcRect, dstRect, new Paint());
	}

	public void updateToThePrevImage() {
		_currentIndex = (_currentIndex - 1) < 0 ? images.length - 1
				: (_currentIndex - 1) % images.length;
	}

	public void updateToTheNextImage() {
		_currentIndex = (_currentIndex + 1) % images.length;
	}

	public void recycle() {
		for (int i = 0; i < images.length; ++i) {
			images[i].recycle();
			while (images[i].isRecycled() == false)
				;
		}
		images = null;
	}

}
