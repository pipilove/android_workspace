package com.example.flowgestuer;

import java.io.File;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class VeriManager {
	private int SettingCount = 0;
	private SocketService mSocketService = null;
	private char status;
	private ManageThread mManageThread;
	int code;

	VeriManager(String Adr) {
		mSocketService = new SocketService(Adr);
	}

	int setting(String filename, Handler mHandler) {
		mManageThread = new ManageThread(filename, 0, mHandler);
		mManageThread.start();
		return code;
	}

	int verificating(String filename, Handler mHandler) {
		mManageThread = new ManageThread(filename, 1, mHandler);
		mManageThread.start();
		return code;
	}

	private void error(String str) {
		Log.i("Manager", str.toString());
	}

	private void success() {
		Log.i("Manager", "success");
	}

	private class ManageThread extends Thread {
		String filename;
		public int Status;
		int type;
		Handler mHandler;

		ManageThread(String filename, int type, Handler mHandler) {
			this.mHandler = mHandler;
			this.filename = filename;
			this.type = type;
		}

		@Override
		public void run() {
			Message message = new Message();
			if (type == 0) {
				Status = setting();
				message.what = 0x120;
			} else {
				Status = verificating();
				message.what = 0x121;
			}
			message.obj = Status;
			mHandler.sendMessage(message);
		}

		int setting() {
			File file = new File(
					android.os.Environment.getExternalStorageDirectory() + "/"
							+ "Acc" + filename);
			if (!file.exists()) {
				error("File Not Find! Try again");
				return -1;
			}
			if (SettingCount != 2) {
				status = mSocketService.setting(file);
				SettingCount++;
				if (status == 'X') {
					SettingCount--;
					return -2;
				}
			} else {
				status = mSocketService.setting(file);
				if (status == 'C') {
					error("Can not setting, Try again");
					SettingCount = 0;
					return -1;
				} else if (status == 'F') {
					success();
					SettingCount = 0;
				} else if (status == 'X') {
					Log.i("VManager", "Xreturn");
					return -2;
				} else {
					return -1;
				}
			}
			return SettingCount;
		}

		int verificating() {
			File file = new File(
					android.os.Environment.getExternalStorageDirectory() + "/"
							+ "Acc" + filename);
			if (!file.exists()) {
				error("File Not Find! Try again");
				return 0;
			}
			status = mSocketService.verificating(file);
			if (status == 'N') {
				error("Can not find setting, Please set");
				return -1;
			} else if (status == 'A') {
				success();
				return 1;
			} else if (status == 'D') {
				error("Can not get access to verification!");
				return 0;
			} else if (status == 'X') {
				return -2;
			}
			return 0;
		}
	}
}
