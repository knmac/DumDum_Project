package fr.eurecom.allmenus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.engine.Character;
import fr.eurecom.utility.Parameters;

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
	}

	@Override
	public boolean Action(Point p, Object o) {
		Character.gearState gear = (Character.gearState) ClickedButton(p);

		if (gear == null)
			return false;

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
