package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientHandler implements Runnable 
{
	private Socket clientSocket_;
	ChatServerController serverController_;
	
	public ClientHandler(Socket client, ChatServerController servCntrl)
	{
		serverController_=servCntrl;
		this.clientSocket_ = client;
		try
			{client.setSoTimeout(500);}
		catch (SocketException e)
			{e.printStackTrace();}
	}

	@Override
	public void run() 
	{
		InputStream is = null;
		OutputStream outputStream = null;
		try
			{outputStream = clientSocket_.getOutputStream();} 
		catch (IOException e2)
			{e2.printStackTrace();}
		ObjectOutputStream objOutStream = null;
		try
			{objOutStream = new ObjectOutputStream(outputStream);} 
		catch (IOException e2)
			{e2.printStackTrace();}
		
		try
			{is = clientSocket_.getInputStream();} 
		catch (IOException e1)
			{e1.printStackTrace();}
		ObjectInputStream objInStream = null;
		try
			{objInStream = new ObjectInputStream(is);} 
		catch (IOException e1)
			{e1.printStackTrace();}
		String inMsg = new String();
		ReadyToSendMessage msgToSend = null;
		while (true)
		{
			msgToSend = serverController_.getMessageToSend(Thread.currentThread().getId());
			if (msgToSend != null)
			try
				{objOutStream.writeObject(msgToSend);}
			catch (IOException e1)
				{e1.printStackTrace();}	
			
			try
				{inMsg = (String) objInStream.readObject();}
			catch (SocketTimeoutException e)
				{continue;}
			catch (IOException e)
				{e.printStackTrace();} 
			catch (ClassNotFoundException e)
				{e.printStackTrace();}
			
			ReadyToSendMessage newMsg = new ReadyToSendMessage(Thread.currentThread().getId(), inMsg);
			try
				{objOutStream.writeObject(inMsg);}
			catch (IOException e1)
				{e1.printStackTrace();}	
			serverController_.addNewMessageToSend(newMsg);
		} // endWhile
	}
}
