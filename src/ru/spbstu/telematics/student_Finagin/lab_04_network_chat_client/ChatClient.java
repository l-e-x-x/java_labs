package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ChatClient implements Runnable
{
		/*Соединение*/
	private Socket clientSocket_;
	private int serverPort_ = 3000;
		
		/*Потоки для чтения/записи*/
	private InputStream clientInputStream_ = null;
	private ObjectInputStream clientObjInputStream_ = null;
	private OutputStream clientOutputStream_ = null;
	private ObjectOutputStream clientObjOutputStream_ = null;

		// гуй клиента
	private ClientGUI clientGUI_;
	
	public ChatClient()
	{
		if (initClient())
		{
			clientGUI_=new ClientGUI(this);
			clientGUI_.registrationFrame_.initRegistrationLayout();
			try	// инициализируем поток чтения
				{initInputStreams();}
			catch (IOException e1)
				{e1.printStackTrace();}
		}
	}
	
	private void setUpConnection() throws UnknownHostException, IOException, ConnectException
		{clientSocket_ = new Socket("localhost", serverPort_);}
	
	private void initOutputStreams() throws IOException
	{	// инициализируем выходные потоки для записи
		clientOutputStream_ = clientSocket_.getOutputStream();
		clientObjOutputStream_ = new ObjectOutputStream(clientOutputStream_);
	}
	
	private void initInputStreams() throws IOException
	{	// инициализируем входные потоки для чтения
		clientInputStream_ = clientSocket_.getInputStream();
		clientObjInputStream_ = new ObjectInputStream(clientInputStream_);
	}
	
	private boolean initClient()
	{
		try	// инициализируем соединение
			{setUpConnection();}
		catch (UnknownHostException unknownHostEx)
			{System.out.println("Unknown host"); return false;}
		catch (ConnectException connectEx) 
			{System.out.println("Server not responding!"); return false;}
		catch (IOException e1)
			{e1.printStackTrace();}
		try	// инициализируем поток для записи
			{initOutputStreams();} 
		catch (IOException e)
			{e.printStackTrace();}
		return true;
	}
	
	public boolean registerInChat(String nickname)
	{
		try	// пытаемся отправить свой ник на регу
			{clientObjOutputStream_.writeObject(nickname);}
		catch (IOException e)
			{e.printStackTrace(); return false;}
		Integer nickRegMsg = new Integer(0);
		try	// пытаемся прочитать ответ от сервера
			{nickRegMsg = (Integer) clientObjInputStream_.readObject();}
		catch (SocketException sockEx)
			{return false;} // если соединение прервалось - закрываемся
		catch (IOException e)
			{e.printStackTrace(); return false;} 
		catch (ClassNotFoundException e)
			{e.printStackTrace(); return false;}
	
		/*проверяем результат реги*/
		if (nickRegMsg == 0)
			return false; // если неудача - возвращаемся обратно
		return true;
	}
	
	public void sendMessage(String message)
	{
		try	// пытаемся отправить свой ник на регу
			{clientObjOutputStream_.writeObject(message);}
		catch (IOException e)
			{e.printStackTrace();}
	}
	
	public void recieveMessages()
	{
		String inMessage = new String();
		while(true)
		{
			try	// пытаемся прочитать ответ от сервера
				{inMessage = (String) clientObjInputStream_.readObject();}
			catch (SocketTimeoutException e)
				{continue;}	// если вывалились по таймауту, значит ничего не пришло 
			catch (SocketException sockEx)
				{break;} // если соединение прервалось - закрываемся
			catch (IOException e)
				{e.printStackTrace(); break;} 
			catch (ClassNotFoundException e)
				{e.printStackTrace(); break;}
			clientGUI_.mainChatFrame_.mainChatMessagesArea_.insert(inMessage+"\n\n", 0);
		}
	}

	@Override
	public void run()
		{recieveMessages();}
	
}
