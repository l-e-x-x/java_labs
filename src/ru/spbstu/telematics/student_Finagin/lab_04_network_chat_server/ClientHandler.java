package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientHandler implements Runnable 
{
	private Socket clientSocket_;

	public ClientHandler(Socket client)
	{
		this.clientSocket_ = client;
	}

	@Override
	public void run() 
	{
		InputStream is = null;
		try
			{is = clientSocket_.getInputStream();} 
		catch (IOException e1)
			{e1.printStackTrace();}
		ObjectInputStream objInStream = null;
		try
			{objInStream = new ObjectInputStream(is);} 
		catch (IOException e1)
			{e1.printStackTrace();}
		String msg = new String();
		while (true)
		{
			try
				{msg = (String) objInStream.readObject();}
			catch (IOException e)
				{e.printStackTrace();} 
			catch (ClassNotFoundException e)
				{e.printStackTrace();}
			System.out.println("Client socket: "+clientSocket_.toString() + " "+msg);
		} // endWhile
	}
}
