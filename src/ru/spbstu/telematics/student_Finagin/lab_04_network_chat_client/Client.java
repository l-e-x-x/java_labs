package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;


public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException 
	{
		Integer clientId = (new Random().nextInt(100)); 
		Socket clientSocket_ = new Socket("localhost", 3000);
		try
			{clientSocket_.setSoTimeout(500);}
		catch (SocketException e)
			{e.printStackTrace();}
		
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
		String data="Hello from "+clientId;
		while (true)
		{
			objOutStream.writeObject(data);
			try
				{Thread.sleep(2000);} 
			catch (InterruptedException e)
				{e.printStackTrace();}
			
			try
				{inMsg = (String) objInStream.readObject();}
			catch (SocketTimeoutException e)
				{continue;}
			catch (IOException e)
				{e.printStackTrace();} 
			catch (ClassNotFoundException e)
				{e.printStackTrace();}
			System.out.println(inMsg);
		}
	}
}