package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException 
	{
		Socket s = new Socket("localhost", 3000);
		OutputStream outputStream = s.getOutputStream();
		ObjectOutputStream objOutStream = new ObjectOutputStream(outputStream);
		while (true)
		{
			String data="Hello";
			System.out.println(data);
			objOutStream.writeObject(data);
			try
				{Thread.sleep(500);} 
			catch (InterruptedException e)
				{e.printStackTrace();}
		}
	}
}