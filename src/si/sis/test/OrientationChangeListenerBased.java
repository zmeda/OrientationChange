package si.sis.test;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class OrientationChangeListenerBased extends Activity {
	private static final boolean DEBUG = true;
	private static final String TAG = "OrientationChangeListenerBased";

	private static final int ROTATION_THRESHOLD = 30;

	private OrientationEventListener mMyOrientationEventListener;
	private TextView mMainText;
	private int mCurrentOrientation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mMyOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int orientation) {
				int newScreenOrientation = getScreenOrientation(orientation);
				if (DEBUG) {
					Log.d(TAG, "Orientation: " + orientation + ", newScreenOrientation: " + newScreenOrientation + ", currentScreenOrientation: "
							+ mCurrentOrientation);
				}
				if (newScreenOrientation != mCurrentOrientation) {
					if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
						RotateAnimation rotateAnimation = new RotateAnimation(0, 90, mMainText.getWidth() / 2, mMainText.getHeight() / 2);
						rotateAnimation.setDuration(500);
						rotateAnimation.setFillAfter(true);
						mMainText.startAnimation(rotateAnimation);
					} else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
						RotateAnimation rotateAnimation = new RotateAnimation(90, 0, mMainText.getWidth() / 2, mMainText.getHeight() / 2);
						rotateAnimation.setDuration(500);
						rotateAnimation.setFillAfter(true);
						mMainText.startAnimation(rotateAnimation);
					}
					mCurrentOrientation = newScreenOrientation;
				}
			}
		};

		mMainText = (TextView) findViewById(R.id.mainText);

		mCurrentOrientation = getInitialScreenOrientation();
		if (DEBUG) {
			Log.d(TAG, "initialScreenOrientation: " + mCurrentOrientation);
		}
	}

	@Override
	protected void onPause() {
		if (DEBUG) {
			Log.d(TAG, "onPause()");
		}
		super.onPause();
		mMyOrientationEventListener.disable();
	}

	@Override
	protected void onResume() {
		if (DEBUG) {
			Log.d(TAG, "onResume()");
		}
		super.onResume();
		if (mMyOrientationEventListener.canDetectOrientation()) {
			Toast.makeText(this, "Can DetectOrientation = Enabling Listener", Toast.LENGTH_LONG).show();
			mMyOrientationEventListener.enable();
		} else {
			Toast.makeText(this, "Can't DetectOrientation", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private int getInitialScreenOrientation() {
		Display display = getWindowManager().getDefaultDisplay();
		int orientation = display.getOrientation();

		if (orientation == Configuration.ORIENTATION_UNDEFINED) {
			orientation = getResources().getConfiguration().orientation;

			if (orientation == Configuration.ORIENTATION_UNDEFINED) {
				if (display.getWidth() == display.getHeight()) {
					orientation = Configuration.ORIENTATION_SQUARE;
				} else if (display.getWidth() < display.getHeight()) {
					orientation = Configuration.ORIENTATION_PORTRAIT;
				} else {
					orientation = Configuration.ORIENTATION_LANDSCAPE;
				}
			}
		}
		return orientation;
	}

	private int getScreenOrientation(int orientationInDegrees) {
		int newOrientation = mCurrentOrientation;
		if (orientationInDegrees != OrientationEventListener.ORIENTATION_UNKNOWN) {
			if (orientationInDegrees < 270 + ROTATION_THRESHOLD && orientationInDegrees > 270 - ROTATION_THRESHOLD) {
				newOrientation = Configuration.ORIENTATION_LANDSCAPE;
			} else if (orientationInDegrees < 0 + ROTATION_THRESHOLD || orientationInDegrees > 360 - ROTATION_THRESHOLD) {
				newOrientation = Configuration.ORIENTATION_PORTRAIT;
			}
		}
		return newOrientation;
	}
}
