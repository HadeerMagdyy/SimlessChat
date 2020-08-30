package com.example.simlesschat.Connection;

import android.content.Context;
import android.os.AsyncTask;

import com.example.simlesschat.Classes.Message;
import com.example.simlesschat.Classes.ServerInit;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendMessageServer extends AsyncTask<Message, Message, Message>{
	private static final String TAG = "SendMessageServer";
	private Context mContext;
	private static final int SERVER_PORT = 4446;


	public SendMessageServer(Context context){
		mContext = context;
	}
	
	@Override
	protected Message doInBackground(Message... msg) {

		try {
			    InetAddress addr= ServerInit.client;

				if(msg[0].getSenderAddress()!=null && addr.getHostAddress().equals(msg[0].getSenderAddress().getHostAddress())){
					return msg[0];
				}

				Socket socket = new Socket();
				socket.setReuseAddress(true);
				socket.bind(null);
				socket.connect(new InetSocketAddress(addr, SERVER_PORT));
				OutputStream outputStream = socket.getOutputStream();
				new ObjectOutputStream(outputStream).writeObject(msg[0]);
			    socket.close();
			}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return msg[0];
	}

	@Override
	protected void onProgressUpdate(Message... values) {

	}

	@Override
	protected void onPostExecute(Message result) {
		super.onPostExecute(result);
	}
	

}