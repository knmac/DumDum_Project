package fr.eurecom.allmenus;

import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.MainActivity.StateList;
import fr.eurecom.utility.DataWriter;
import fr.eurecom.utility.MapReader;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserWriter;
import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.Paint.Align;

public class CongratBox extends BaseMenu {
	private MainActivity o;

	public CongratBox(DynamicBitmap bmpBackground, MainActivity o) {
		super(bmpBackground);
		this.o = o;
	}

	public void Show(Canvas canvas) {
		super.Show(canvas);
		// Show scores
		// User user = o.getCurrentUser();
		User user = o.getUser();

		Paint paint = new Paint();
		paint.setTextSize(30);
		paint.setColor(Color.YELLOW);
		paint.setAlpha(100);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextAlign(Align.CENTER);

		for (int i = 0; i < user.getLevelScore().size(); ++i)
			canvas.drawText(user.getLevelScore().get(i).toString(),
					Parameters.posScoreList[i].x, Parameters.posScoreList[i].y,
					paint);
	}

	@Override
	public boolean Action(Point p, Object o) {
		((MainActivity) o).setState(StateList.MAIN_MENU);

		User user = ((MainActivity) o).getUser();

		((MainActivity) o).getPauseMenu().Unzoom(o);

		// write to buffer
		// user.setCurrentLevel(1);
		// user.setCurrentScore(0);
		MapReader gameData = new MapReader(Parameters.dMapID[0]);
		// user.setCurrentPos(gameData.getHolePos());
		gameData = null;
		user.setUnlockedLevel(10);

		// write to disk
		// DataWriter.WriteData(((MainActivity) o).getUserList(),
		// Parameters.pthUserData, user.getName());
		UserWriter.writeUserData(((MainActivity) o).getUser(),
				Parameters.pthUserData);
		try {
			((MainActivity) o).updateContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		((MainActivity) o).getGame().flushData();

		((MainActivity) o).getMainView().invalidate();
		return true;
	}
}
