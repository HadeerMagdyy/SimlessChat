package com.example.simlesschat.Classes;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerInit extends Thread{
	private static final String TAG = "ServerInit";
	private static final int SERVER_PORT = 8888;
	public  static InetAddress client;
	private ServerSocket serverSocket;
	public ServerInit(){
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			// Collect client ip's
		    while(true) {
		       Socket clientSocket = serverSocket.accept();
		       client=clientSocket.getInetAddress();
		       clientSocket.close();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			serverSocket.close();
			Log.v(TAG, "Server init process interrupted");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
