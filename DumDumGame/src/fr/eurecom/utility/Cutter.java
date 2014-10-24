package fr.eurecom.utility;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Cutter {
	public enum CutStyle {
		HORIZONTAL, VERTICAL
	}

	public static Bitmap[] cutBitmap(Bitmap bitmap, int numOfPieces,
			CutStyle cutStyle) throws Exception
	// ATTENTION: for performance purposes of your program, remember to dispose
	// the return array
	// whenever it's no longer needed. Don't worry, it's safe to do so.
	{
		Bitmap[] result = new Bitmap[numOfPieces];

		if (cutStyle == CutStyle.HORIZONTAL) {
			int deltaHeight = bitmap.getHeight() / numOfPieces;
			for (int i = 0; i < numOfPieces; ++i) {
				result[i] = Bitmap.createBitmap(bitmap.getWidth(), deltaHeight,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(result[i]);
				canvas.drawBitmap(bitmap, 0, -deltaHeight * i, new Paint());
			}
		} else if (cutStyle == CutStyle.VERTICAL) {
			int deltaWidth = bitmap.getWidth() / numOfPieces;
			for (int i = 0; i < numOfPieces; ++i) {
				result[i] = Bitmap.createBitmap(deltaWidth, bitmap.getHeight(),
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(result[i]);
				canvas.drawBitmap(bitmap, -deltaWidth * i, 0, new Paint());
			}
		} else
			throw new Exception(
					"Cutter.cutBitmap(Bitmap bitmap, int numOfPieces, CutStyle cutStyle) encounters an error!");

		return result;
	}
}
