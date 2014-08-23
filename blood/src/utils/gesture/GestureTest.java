package utils.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/8/15.
 */
public class GestureTest extends GestureDetector.SimpleOnGestureListener {
	private Context context;

	public GestureTest(Context context) {
		this.context = context;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Toast.makeText(context, "single-tap-up  " + e.getAction(), Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Toast.makeText(context, "long press  " + e.getAction(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		//Toast.makeText(context,"scroll e1" + e1.getAction()+"\ne2"+e2.getAction() + "\nxy " + distanceX + "-" + distanceY,Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//据说是这段

		if (e1.getX() - e2.getX() > 20 && Math.abs(velocityX) > 2) {
			//Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
			// 切换Activity
			//Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
			//startActivity(intent);

		} else if (e2.getX() - e1.getX() > 20 && Math.abs(velocityX) > 2) {

			// 切换Activity
			// Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
			// startActivity(intent);


			Toast.makeText(context, "Fling e1" + e1.getAction() + "\ne2" + e2.getAction() + "\nxy " + velocityX + "-" + velocityY, Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		Toast.makeText(context, "show  " + e.getAction(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Toast.makeText(context, "down  " + e.getAction(), Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Toast.makeText(context, "double  " + e.getAction(), Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Toast.makeText(context, "double event  " + e.getAction(), Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Toast.makeText(context, "single conf  " + e.getAction(), Toast.LENGTH_SHORT).show();
		return false;
	}
}
