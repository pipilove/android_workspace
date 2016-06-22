package com.example.flowgestuer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	SharedPreferences preferences; // �״������ο�

	ImageView infoOperatingIV;
	// ImageButton infoOperatingIB1;
	Animation operatingAnim, transparencyAnim, scaleAnim;

	private SoundPool sp; // ����һ��SoundPool
	private int music; // ����һ��������load������������suondID
	boolean flag = true;

	private long mExitTime = 0; // ��һ�θհ����˳�����ʱ��
	SensorRunner mSensorRunner = null;
	VeriManager mVeriManager = null;
	private boolean isSet = false;
	private Handler mHandler;
	private int status;
	private String Adr = "192.168.214.1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toastDisplay();

		musicInit();

		mSensorRunner = new SensorRunner(
				(SensorManager) getSystemService(SENSOR_SERVICE));
		mVeriManager = new VeriManager(Adr);

		infoOperatingIV = (ImageView) findViewById(R.id.imageView1); // ��תͼ
		// infoOperatingIV2 = (ImageView) findViewById(R.id.imageView2);
		// infoOperatingIB1 = (ImageButton) findViewById(R.id.imageButton1);

		// ��������
		operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
		transparencyAnim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		transparencyAnim.setInterpolator(lin);
		scaleAnim.setInterpolator(lin);

		// �ڲ�����ʼ֮ǰ����
		infoOperatingIV.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (operatingAnim != null) {
					// infoOperatingIV.startAnimation(scaleAnim);
					infoOperatingIV.startAnimation(operatingAnim);
					// infoOperatingIB1.setVisibility(-1);
					// final int
					// play(soundID,leftVolume,rightVolume,priority,loop,rate)

					mSensorRunner.openSensors(); // Open Sensors

					if (flag) {
						sp.play(music, 1.0f, 1.0f, 0, -1, 1);
						flag = false;
					}
					sp.resume(music);
				}
				return false;
			}
		});

		// �ڲ������ʱ����
		infoOperatingIV.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					// infoOperatingIB1.setVisibility(1);
					infoOperatingIV.clearAnimation();

					mSensorRunner.saveFile("Sensors.txt"); // Buffer to file
					mSensorRunner.stopSensors(); // Stop Sensors
					if (isSet == true) {
						mHandler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if (msg.what == 0x120) {
									status = Integer.parseInt(msg.obj
											.toString());
									// toastShow("Watting for Server!");
									if (status == -1) {
										toastShow("����ʧ�ܣ������¿�ʼ����");// Setting
																	// Error!
																	// Again
																	// -3");
									} else if (status == -2) {
										toastShow("�����������������");// Network
																	// Error!
																	// Again
																	// last");
									} else if (status == 1) {
										toastShow("good! ������������");// Again -2");
									} else if (status == 2) {
										toastShow("good! ���������һ��");// Again
																	// -1");
									} else if (status == 0) {
										toastShow("good! �����������óɹ���");// Setting
																		// OK!");
										isSet = false;
									}
								}
							}
						};
						mVeriManager.setting("Sensors.txt", mHandler);
					} else {
						mHandler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if (msg.what == 0x121) {
									status = Integer.parseInt(msg.obj
											.toString());
									// toastShow("Watting for Server!");
									if (status == 0) {
										toastShow("��֤ʧ�ܣ���������֤��");// Error!
																	// Again");
									} else if (status == -1) {
										toastShow("�������������룡");// Please Set");
									} else if (status == 1) {
										toastShow("��֤�ɹ�����");// Success!!");
									} else if (status == -2) {
										toastShow("�������");// NetWork Error!
															// Again");
									}
								}
							}
						};
						mVeriManager.verificating("Sensors.txt", mHandler);
					}

					if (sp != null)
						sp.pause(music);
				}
				return false;
			}
		});

	}

	private void toastShow(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	private void toastDisplay() {
		// �״�ʹ����ʾ��ʾ����
		preferences = getSharedPreferences("count", 0); // ��������������򴴽��µ�Preferences
		int count = preferences.getInt("count", 0); // ȡ������
		if (count == 0) { // �жϳ�����ڼ������У�����ǵ�һ����������ת������ҳ��
			toastInit();
			SharedPreferences.Editor editor = preferences.edit(); // ��preferences���ڱ༭״̬
			editor.putInt("count", 1); // ��������
			editor.commit(); // �ύ�޸�
		}
	}
	
	private void toastInit() {
		// ��ʼʹ����ʾtoast��ʾ��������
		Toast toast = Toast.makeText(this, "�״�ʹ����������������", Toast.LENGTH_LONG);// please
																				// set
																				// your
																				// gesture
																				// for
																				// first
																				// used
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
		toast.setMargin(0f, 0.1f);
		toast.show();
	}

	private void musicInit() {
		// ��ʼ��������
		// AudioManager audioManager = (AudioManager)
		// this.getSystemService(AUDIO_SERVICE);
		// setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sp = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0); // ��һ������Ϊͬʱ������������������10���ڶ����������ͣ�����Ϊ��������5
		music = sp.load(this, R.raw.press_music, 1); // ����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
		// setVolumeControlStream(AudioManager.STREAM_SYSTEM);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ת���ں���(������Ϊ�˲��ػ�activity)ʱ��������⣬����ת����ƫ�ƽ��
		super.onConfigurationChanged(newConfig);

		if (operatingAnim != null && infoOperatingIV != null
				&& operatingAnim.hasStarted()) {
			infoOperatingIV.clearAnimation();
			infoOperatingIV.startAnimation(operatingAnim);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// �Ѳ˵�xml��Դת��Ϊ������ӵ�menu������
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// ѡ��˵�ʱ�Ķ���
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(this, "������ʼ������������", Toast.LENGTH_LONG).show();// set
																			// gesture
																			// starting...
			isSet = true;
			// set gesture
			break;
		// other cases
		case R.id.ip_settings:
			showDialog_Layout(this);
			break;
		case R.id.gesture_reg:
			Toast.makeText(this, "������ʼʶ����������", Toast.LENGTH_LONG).show();
			isSet = false;
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// �����ؼ������˳�
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) { // ������ΰ���ʱ��������2000���룬���˳�
				Toast.makeText(this, "�ٰ�һ���˳�Flowgesture", Toast.LENGTH_SHORT)
						.show();// press again to exit
				mExitTime = System.currentTimeMillis(); // ����mExitTime
			} else {
				System.exit(0); // �����˳�����
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// ���ٵ�ʱ���ͷ�SoundPool��Դ
		if (sp != null) {
			sp.release();
			sp = null;
		}
		super.onDestroy();
	}

	private void showDialog_Layout(Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater
				.inflate(R.layout.dialogue_mes, null);
		final EditText edtInput = (EditText)

		textEntryView.findViewById(R.id.edtInput);
		final AlertDialog.Builder builder = new

		AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("�����������ip��ַ");
		builder.setView(textEntryView);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,

			int whichButton) {
				Adr = edtInput.getText().toString();
				mVeriManager = new VeriManager(Adr);
				toastShow("���óɹ���ip��ַΪ��" + Adr);
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,

			int whichButton) {
				// cancel
			}
		});
		builder.show();
	}

}
