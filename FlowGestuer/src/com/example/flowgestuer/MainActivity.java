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
	
	SharedPreferences preferences; // 首次启动参考

	ImageView infoOperatingIV;
	// ImageButton infoOperatingIB1;
	Animation operatingAnim, transparencyAnim, scaleAnim;

	private SoundPool sp; // 声明一个SoundPool
	private int music; // 定义一个整型用load（）；来设置suondID
	boolean flag = true;

	private long mExitTime = 0; // 第一次刚按下退出键的时间
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

		infoOperatingIV = (ImageView) findViewById(R.id.imageView1); // 旋转图
		// infoOperatingIV2 = (ImageView) findViewById(R.id.imageView2);
		// infoOperatingIB1 = (ImageButton) findViewById(R.id.imageButton1);

		// 动作载入
		operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
		transparencyAnim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		transparencyAnim.setInterpolator(lin);
		scaleAnim.setInterpolator(lin);

		// 在操作开始之前调用
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

		// 在操作完成时调用
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
										toastShow("设置失败，请重新开始设置");// Setting
																	// Error!
																	// Again
																	// -3");
									} else if (status == -2) {
										toastShow("网络错误，请重新输入");// Network
																	// Error!
																	// Again
																	// last");
									} else if (status == 1) {
										toastShow("good! 请再输入两次");// Again -2");
									} else if (status == 2) {
										toastShow("good! 请输入最后一次");// Again
																	// -1");
									} else if (status == 0) {
										toastShow("good! 手势密码设置成功！");// Setting
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
										toastShow("验证失败，请重新验证！");// Error!
																	// Again");
									} else if (status == -1) {
										toastShow("请设置手势密码！");// Please Set");
									} else if (status == 1) {
										toastShow("验证成功！！");// Success!!");
									} else if (status == -2) {
										toastShow("网络错误！");// NetWork Error!
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
		// 首次使用显示提示控制
		preferences = getSharedPreferences("count", 0); // 存在则打开它，否则创建新的Preferences
		int count = preferences.getInt("count", 0); // 取出数据
		if (count == 0) { // 判断程序与第几次运行，如果是第一次运行则跳转到引导页面
			toastInit();
			SharedPreferences.Editor editor = preferences.edit(); // 让preferences处于编辑状态
			editor.putInt("count", 1); // 存入数据
			editor.commit(); // 提交修改
		}
	}
	
	private void toastInit() {
		// 初始使用显示toast提示设置手势
		Toast toast = Toast.makeText(this, "首次使用请设置手势密码", Toast.LENGTH_LONG);// please
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
		// 初始载入音乐
		// AudioManager audioManager = (AudioManager)
		// this.getSystemService(AUDIO_SERVICE);
		// setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sp = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0); // 第一个参数为同时播放数据流的最大个数10，第二数据流类型，第三为声音质量5
		music = sp.load(this, R.raw.press_music, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		// setVolumeControlStream(AudioManager.STREAM_SYSTEM);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 转动在横屏(被设置为了不重绘activity)时会出现问题，即旋转中心偏移解决
		super.onConfigurationChanged(newConfig);

		if (operatingAnim != null && infoOperatingIV != null
				&& operatingAnim.hasStarted()) {
			infoOperatingIV.clearAnimation();
			infoOperatingIV.startAnimation(operatingAnim);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 把菜单xml资源转换为对象并添加到menu对象中
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 选择菜单时的动作
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(this, "长按开始设置手势密码", Toast.LENGTH_LONG).show();// set
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
			Toast.makeText(this, "长按开始识别手势密码", Toast.LENGTH_LONG).show();
			isSet = false;
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按返回键两次退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) { // 如果两次按键时间间隔大于2000毫秒，则不退出
				Toast.makeText(this, "再按一次退出Flowgesture", Toast.LENGTH_SHORT)
						.show();// press again to exit
				mExitTime = System.currentTimeMillis(); // 更新mExitTime
			} else {
				System.exit(0); // 否则退出程序
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// 销毁的时候释放SoundPool资源
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
		builder.setTitle("请输入服务器ip地址");
		builder.setView(textEntryView);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,

			int whichButton) {
				Adr = edtInput.getText().toString();
				mVeriManager = new VeriManager(Adr);
				toastShow("设置成功！ip地址为：" + Adr);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,

			int whichButton) {
				// cancel
			}
		});
		builder.show();
	}

}
