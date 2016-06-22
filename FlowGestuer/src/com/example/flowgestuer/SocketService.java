package com.example.flowgestuer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

public class SocketService {

	String IpAdr = null;
	public int status;

	public SocketService(String IpAdr) {
		super();
		this.IpAdr = IpAdr;
	}

	public char setting(File file) {
		FileInputStream reader = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		byte[] buf = null;
		char status = 'X';
		Socket statusSocket = new Socket();
		Socket fileSocket = new Socket();
		byte[] temp = new byte[1];

		try {
			statusSocket.connect(new InetSocketAddress(IpAdr, 13569), 1000);
			Log.i("Setting", "Conect");
			out = new DataOutputStream(statusSocket.getOutputStream());
			out.write(("S" + file.length()).getBytes());
			out.flush();
			Log.i("Setting", "sent S");
			statusSocket.shutdownOutput();
			in = new DataInputStream(statusSocket.getInputStream());
			in.read(temp);
			status = (char) temp[0];
			Log.i("Setting", "No1." + status);
			if (status == 'O') {
				fileSocket.connect(new InetSocketAddress(IpAdr, 13571), 1000);
				out = new DataOutputStream(fileSocket.getOutputStream());
				reader = new FileInputStream(file);
				int bufferSize = 20480;
				buf = new byte[bufferSize];
				int read = 0;
				while ((read = reader.read(buf, 0, buf.length)) != -1) {
					out.write(buf, 0, read);
				}
				out.flush();
				fileSocket.shutdownOutput();
				Log.i("setting", "No1.1 Sent File");
				in = new DataInputStream(fileSocket.getInputStream());
				in.read(temp);
				Log.i("setting", "No2." + temp);
				status = (char) temp[0];
			} else {
				status = 'X';
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("Setting", "socket£º" + e.toString());
			status = 'X';
		} finally {
			try {
				if (statusSocket != null) {
					statusSocket.close();
				}
				if (fileSocket != null) {
					fileSocket.close();
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (reader != null) {
					reader.close();
				}
				buf = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		return status;
	}

	public char verificating(File file) {
		FileInputStream reader = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		byte[] buf = null;
		char status = 'X';
		Socket statusSocket = new Socket();
		;
		Socket fileSocket = new Socket();
		byte[] temp = new byte[1];
		try {
			statusSocket.connect(new InetSocketAddress(IpAdr, 13569), 1000);
			out = new DataOutputStream(statusSocket.getOutputStream());
			out.write(("V" + file.length()).getBytes());
			out.flush();
			statusSocket.shutdownOutput();
			in = new DataInputStream(statusSocket.getInputStream());
			in.read(temp);
			status = (char) temp[0];
			Log.i("verificating", "No1." + status);
			if (status == 'O') {
				fileSocket.connect(new InetSocketAddress(IpAdr, 13571), 1000);
				out = new DataOutputStream(fileSocket.getOutputStream());
				reader = new FileInputStream(file);
				int bufferSize = 20480;
				buf = new byte[bufferSize];
				int read = 0;
				while ((read = reader.read(buf, 0, buf.length)) != -1) {
					out.write(buf, 0, read);
				}
				out.flush();
				fileSocket.shutdownOutput();
				in = new DataInputStream(fileSocket.getInputStream());

				in.read(temp);

				status = (char) temp[0];
				Log.i("verificating", "No2." + status);
			} else if (status == 'N') {

			} else {
				status = 'X';
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("Setting", "socket£º" + e.toString());
			status = 'X';
		} finally {
			try {
				if (statusSocket != null) {
					statusSocket.close();
				}
				if (fileSocket != null) {
					fileSocket.close();
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (reader != null) {
					reader.close();
				}
				buf = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		return status;
	}
}
