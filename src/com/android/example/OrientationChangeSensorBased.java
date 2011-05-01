package com.android.example;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

public class OrientationChangeSensorBased extends Activity implements SensorEventListener {
	// This is an implementation of sensor based orientation change
	private static final boolean DEBUG = true;
	private static final String TAG = "OrientationChangeSensorBased";

	private SensorManager mSensorManager;
	private Sensor mAccelerometerSensor;
	private float[] mAccelerometerSensorValues;
	private int mOrientation;
	private TextView mMainText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mOrientation = getInitialScreenOrientation();

		mMainText = (TextView) findViewById(R.id.mainText);
	}

	@Override
	protected void onPause() {
		if (DEBUG) {
			Log.d(TAG, "onPause()");
		}
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		if (DEBUG) {
			Log.d(TAG, "onResume()");
		}
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (DEBUG) {
			Log.d(TAG, "onAccuracyChanged()");
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (DEBUG) {
			Log.d(TAG, "onSensorChanged()");
		}
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mAccelerometerSensorValues = event.values;
			if (DEBUG) {
				Log.d(TAG, "Gx = " + mAccelerometerSensorValues[0]);
				Log.d(TAG, "Gy = " + mAccelerometerSensorValues[1]);
				Log.d(TAG, "Gz = " + mAccelerometerSensorValues[2]);
			}

			float gx = mAccelerometerSensorValues[0];
			float gy = mAccelerometerSensorValues[1];

			if (gx > 6) {
				if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					if (DEBUG) {
						Log.d(TAG, "orientation = " + mOrientation);
					}
					RotateAnimation rotateAnimation = new RotateAnimation(0, 90, mMainText.getWidth() / 2, mMainText.getHeight() / 2);
					rotateAnimation.setDuration(500);
					rotateAnimation.setFillAfter(true);
					mMainText.startAnimation(rotateAnimation);
				}
			} else if (gy > 6) {
				if (mOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					if (DEBUG) {
						Log.d(TAG, "orientation = " + mOrientation);
					}
					RotateAnimation rotateAnimation = new RotateAnimation(90, 0, mMainText.getWidth() / 2, mMainText.getHeight() / 2);
					rotateAnimation.setDuration(500);
					rotateAnimation.setFillAfter(true);
					mMainText.startAnimation(rotateAnimation);
				}
			}
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
		return orientation - 1;
	}
}