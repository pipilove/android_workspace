package com.example.flowgestuer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorRunner {

	public SensorManager mSensorManager = null;
	private SensorThread accSensorThread = null;
	private SensorThread gyrSensorThread = null;
	public StringBuffer sbAcc = null;
	public StringBuffer sbGyr = null;

	private float[] accDataTemp = { 0, 0, 0 };
	private float[] gyrDataTemp = { 0, 0, 0 };
	private long lastTime = 0;
	private long delayTime = 10000000;

	private final int SENSOR_ACC = 0;
	private final int SENSOR_GYR = 1;

	private boolean STATE = false;
	private boolean TestSensor = false;

	public SensorRunner(SensorManager mSensorManager) {
		this.mSensorManager = mSensorManager;
		sbAcc = new StringBuffer();
		sbGyr = new StringBuffer();
		accSensorThread = new SensorThread(SENSOR_ACC);
		gyrSensorThread = new SensorThread(SENSOR_GYR);
		accSensorThread.start();
		gyrSensorThread.start();
	}

	public void openSensors() {
		if (!STATE) {
			accSensorThread.init();
			gyrSensorThread.init();
			STATE = true;
		}
	}

	public void stopSensors() {
		sbAcc.delete(0, sbAcc.length());
		sbGyr.delete(0, sbGyr.length());
		accSensorThread.cancel();
		gyrSensorThread.cancel();
		STATE = false;
	}

	public void saveFile(String filename) {
		try {
			File file1 = new File(
					android.os.Environment.getExternalStorageDirectory() + "/"
							+ "Acc" + filename);
			if (file1.exists()) {
				file1.delete();
			}
			FileOutputStream foutacc = new FileOutputStream(
					android.os.Environment.getExternalStorageDirectory() + "/"
							+ "Acc" + filename, true);
			foutacc.write(sbAcc.toString().getBytes());
			Log.i("FILE", "WriteFileAcc: " + filename);
			foutacc.close();
			File file2 = new File(
					android.os.Environment.getExternalStorageDirectory() + "/"
							+ "Gyr" + filename);
			if (file2.exists()) {
				file2.delete();
			}
			FileOutputStream foutgyr = new FileOutputStream(
					android.os.Environment.getExternalStorageDirectory() + "/"
							+ "Gyr" + filename, true);
			foutgyr.write(sbGyr.toString().getBytes());
			Log.i("FILE", "WriteFileGyr: " + filename);
			foutgyr.close();
		} catch (FileNotFoundException E) {
			Log.i("FILE", E.toString());
		} catch (Exception E) {
			Log.i("FILE", E.toString());
		}

		sbAcc.delete(0, sbAcc.length());
		sbGyr.delete(0, sbGyr.length());
	}

	private class SensorThread extends Thread implements SensorEventListener {
		int sensorIndex;

		SensorThread(int index) {
			sensorIndex = index;
		}

		public void init() {
			if (SENSOR_ACC == sensorIndex) {
				TestSensor = mSensorManager
						.registerListener(
								this,
								mSensorManager
										.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
								SensorManager.SENSOR_DELAY_FASTEST);
			} else if (SENSOR_GYR == sensorIndex) {
				TestSensor = mSensorManager.registerListener(this,
						mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
						SensorManager.SENSOR_DELAY_FASTEST);
			}
			if (!TestSensor) {
				Log.i("Sensor", "Error!");
			}
		}

		public void cancel() {
			mSensorManager.unregisterListener(this);
		}

		@Override
		public void run() {
		}

		@Override
		public void onAccuracyChanged(android.hardware.Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
			synchronized (this) {
				if (arg0.timestamp >= (lastTime + delayTime)) {
					switch (arg0.sensor.getType()) {
					case Sensor.TYPE_LINEAR_ACCELERATION:
						accDataTemp[0] = arg0.values[0];
						accDataTemp[1] = arg0.values[1];
						accDataTemp[2] = arg0.values[2];
						sbAcc.append(accDataTemp[0] + " " + accDataTemp[1]
								+ " " + accDataTemp[2] + " " + arg0.timestamp
								+ "\r\n");
						break;
					case Sensor.TYPE_GYROSCOPE:
						gyrDataTemp[0] = arg0.values[0];
						gyrDataTemp[1] = arg0.values[1];
						gyrDataTemp[2] = arg0.values[2];
						sbGyr.append(gyrDataTemp[0] + " " + gyrDataTemp[1]
								+ " " + gyrDataTemp[2] + " " + arg0.timestamp
								+ "\r\n");
						break;
					}
					lastTime = arg0.timestamp;
				}
			}
		}
	}
}