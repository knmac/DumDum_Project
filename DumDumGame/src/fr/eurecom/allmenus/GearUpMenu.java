package fr.eurecom.allmenus;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.text.TextPaint;
import android.widget.Toast;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.engine.Character;
import fr.eurecom.engine.Character.gearState;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserWriter;

public class GearUpMenu extends BaseMenu {

	public GearUpMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		Bitmap bmp;
		Button btn;
		int w, h, dist;
		Point pos;
		Point center = new Point(Parameters.dMaxWidth / 2,
				Parameters.dMaxHeight / 2);
		dist = 5 * Parameters.dZoomParam;
		int tmp = (int) (dist / Math.sqrt(2.0));

		// normal DumDum (x, y-d)
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_normal_big);
		h = 3 * Parameters.dZoomParam;
		w = h * bmp.getWidth() / bmp.getHeight();
		pos = new Point(center.x - w / 2, center.y - dist - h / 2);
		btn = new Button(Character.gearState.NORMAL, bmp, pos, w, h);
		AddButton(btn);

		// helmet (x+d/sqrt(2), y-d/sqrt(2))
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_helmet_big);
		pos = new Point(center.x + tmp - w / 2, center.y - tmp - h / 2);
		btn = new Button(Character.gearState.HELMET, bmp, pos, w, h);
		AddButton(btn);

		// drill (x+d, y)
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_drill_big);
		pos = new Point(center.x + dist - w / 2, center.y - h / 2);
		btn = new Button(Character.gearState.DRILL, bmp, pos, w, h);
		AddButton(btn);

		// scholar (x+d/sqrt(2), y+d/sqrt(2))
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_tracingray_big);
		pos = new Point(center.x + tmp - w / 2, center.y + tmp - h / 2);
		btn = new Button(Character.gearState.SCHOLAR, bmp, pos, w, h);
		AddButton(btn);

		// time (x, y+d)
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_timedelay_big);
		pos = new Point(center.x - w / 2, center.y + dist - h / 2);
		btn = new Button(Character.gearState.TIME, bmp, pos, w, h);
		AddButton(btn);

		// feeder (x-d/sqrt(2), y+d/sqrt(2))
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_hungryfeeder_big);
		pos = new Point(center.x - tmp - w / 2, center.y + tmp - h / 2);
		btn = new Button(Character.gearState.FEEDER, bmp, pos, w, h);
		AddButton(btn);

		// ninja (x-d, y)
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_ninja_big);
		pos = new Point(center.x - dist - w / 2, center.y - h / 2);
		btn = new Button(Character.gearState.NINJA, bmp, pos, w, h);
		AddButton(btn);

		// angel (x-d/sqrt(2), y-d/sqrt(2))
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.dumdum_angel_big);
		pos = new Point(center.x - tmp - w / 2, center.y - tmp - h / 2);
		btn = new Button(Character.gearState.ANGEL, bmp, pos, w, h);
		AddButton(btn);
	}

	public void Show(Canvas canvas) {
		if (GameManager.screenShot != null) {
			canvas.drawBitmap(GameManager.screenShot, 0, 0, new Paint());
			canvas.drawARGB(80, 0, 0, 0);
		}

		super.Show(canvas);

		// show gear amount
		LinkedList<Integer> gearAmount = GameManager.user.getGearAmount();

		Paint paint = new TextPaint();
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.WHITE);
		paint.setTextSize(Parameters.dZoomParam / 2);

		Point center = new Point(Parameters.dMaxWidth / 2,
				Parameters.dMaxHeight / 2);
		int dist = 3 * Parameters.dZoomParam;
		int tmp = (int) (dist / Math.sqrt(2.0));

		int[] listX = new int[] { center.x, center.x + tmp, center.x + dist,
				center.x + tmp, center.x, center.x - tmp, center.x - dist,
				center.x - tmp };
		int[] listY = new int[] { center.y - dist, center.y - tmp, center.y,
				center.y + tmp, center.y + dist, center.y + tmp, center.y,
				center.y - tmp };
		for (int i = 1; i < listX.length; i++) {
			canvas.drawText("x" + Integer.toString(gearAmount.get(i)),
					listX[i], listY[i], paint);
		}
	}

	@Override
	public boolean Action(Point p, Object o) {
		Character.gearState gear = (Character.gearState) ClickedButton(p);

		if (gear == null)
			return false;

		// decrease gear
		// TODO: need to polish
		LinkedList<Integer> gearAmount = GameManager.user.getGearAmount();
		int idx = 0;
		switch (gear) {
		case NORMAL:idx = 0;break;
		case HELMET:idx = 1;break;
		case DRILL:idx = 2;break;
		case SCHOLAR:idx = 3;break;
		case TIME:idx = 4;break;
		case FEEDER:idx = 5;break;
		case NINJA:idx = 6;break;
		case ANGEL:idx = 7;break;
		default:break;
		}
		
		if (idx != 0) {
			int tmp = gearAmount.get(idx);
			if (tmp != 0) { // still usable
				gearAmount.set(idx, tmp - 1);
				GameManager.user.setGearAmount(gearAmount);
				UserWriter.writeUserData(GameManager.user, Parameters.pthUserData);
			} else { // no more to use
				gear = gearState.NORMAL; // reset to normal
				Toast.makeText(App.getMyContext(), "Insufficient amount. Please buy more to use!",
						Toast.LENGTH_SHORT).show();
			}
		}

		// change gear
		GameManager.game.changeGear(gear);
		CallResume();
		return true;
	}

	private void CallResume() {
		GameManager.setCurrentState(GameManager.GameState.GAME);
		GameManager.game.resume();
		GameManager.mainView.invalidate();
	}
}
