package fr.eurecom.engine;

import java.util.LinkedList;

import fr.eurecom.dumdumgame.DynamicBitmap;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;

public class Polygon {
	private LinkedList<Point> points = new LinkedList<Point>();

	public LinkedList<Point> getPoints() {
		return this.points;
	}

	public void setPoints(LinkedList<Point> pointList) {
		this.points = pointList;
	}

	public void Fill(Bitmap texture, Canvas canvas) {
		Path path = new Path();
		path.moveTo(points.get(0).x, points.get(0).y);
		for (int i = 1; i < points.size(); ++i) {
			path.lineTo(points.get(i).x, points.get(i).y);
		}
		
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		BitmapShader shader = new BitmapShader(texture, TileMode.REPEAT, TileMode.REPEAT);
		paint.setShader(shader);
		
		canvas.drawPath(path, paint);
	}
	
	public void Fill(Bitmap texture, Canvas canvas, TileMode tileModeX, TileMode tileModeY) {
		Path path = new Path();
		path.moveTo(points.get(0).x, points.get(0).y);
		for (int i = 1; i < points.size(); ++i) {
			path.lineTo(points.get(i).x, points.get(i).y);
		}
		
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		BitmapShader shader = new BitmapShader(texture, tileModeX, tileModeY);
		paint.setShader(shader);
		
		canvas.drawPath(path, paint);
	}

	public void FillWithImage(Bitmap bmp, Canvas canvas) {
		Rect boundRect = getBoundRect();
		DynamicBitmap dbmp = new DynamicBitmap(bmp, new Point(boundRect.left,
				boundRect.top), 0, boundRect.width(), boundRect.height());
		dbmp.show(canvas);
	}

	public Rect getBoundRect() {
		Point topLeft = new Point(99999, 99999);
		Point bottomRight = new Point(0, 0);

		for (int i = 0; i < points.size(); ++i) {
			if (points.get(i).x < topLeft.x)
				topLeft.x = points.get(i).x;
			if (points.get(i).y < topLeft.y)
				topLeft.y = points.get(i).y;
			if (points.get(i).x > bottomRight.x)
				bottomRight.x = points.get(i).x;
			if (points.get(i).y > bottomRight.y)
				bottomRight.y = points.get(i).y;
		}

		return new Rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
	}
}
