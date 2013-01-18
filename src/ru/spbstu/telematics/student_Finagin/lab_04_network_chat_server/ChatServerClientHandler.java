package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

public class ChatServerClientHandler implements Runnable 
{
	/*Сетевые настройки*/
		// сокет обслуживающего потока
	private Socket handlerSocket_;
		// таймаут сокета в мсек
	private int socketTimeout_;	
	
	/*Общие параметры*/
		// Id обслуживающего потока
	private Long handlerThreadId_;
		// контроллер чата
	private ChatServerController serverController_;
		// никнейм обслуживаемого клиента
	private String clientNickName_;
	
		/*Потоки для чтения/записи*/
	private InputStream handlerInputStream_ = null;
	private ObjectInputStream handlerObjInputStream_ = null;
	private OutputStream handlerOutputStream_ = null;
	private ObjectOutputStream handlerObjOutputStream_ = null;
		
	public ChatServerClientHandler(Socket clientSocket, int sockTimeout, ChatServerController servCntrl)
	{
		serverController_=servCntrl;
		this.handlerSocket_ = clientSocket;
		socketTimeout_ = sockTimeout;
		clientNickName_ = new String();
	}

	private void initStreams() throws IOException
	{	// инициализируем входные потоки для чтения
		handlerInputStream_ = handlerSocket_.getInputStream();
		handlerObjInputStream_ = new ObjectInputStream(handlerInputStream_);
			// инициализируем выходные потоки для записи
		handlerOutputStream_ = handlerSocket_.getOutputStream();
		handlerObjOutputStream_ = new ObjectOutputStream(handlerOutputStream_);
	}
	
	private void setSocketTimeout() throws SocketException 
	{	// устанавливаем таймаут для чтения из сокета
		handlerSocket_.setSoTimeout(socketTimeout_);
	}
	
		/*--Работа с клиентами--*/
	private boolean clientRegistration()
	{	// регистрация клиента в системе
		String nickRegMsg = new String();
		do
		{
			try	// пытаемся прочитать сообщение от клиента
				{nickRegMsg = (String) handlerObjInputStream_.readObject();}
			catch (EOFException EOFEx)
				{return false;} // если соединение прерывалось - закрываемся
			catch (IOException e)
				{e.printStackTrace(); return false;} 
			catch (ClassNotFoundException e)
				{e.printStackTrace(); return false;}
			if (!serverController_.registerNewClientNickname(nickRegMsg))
			{	// если рега не удалась
				try	// пытаемся отправить сообщение об ошибке обратно клиенту
					{handlerObjOutputStream_.writeObject(new Integer(0));}
				catch (IOException e)
					{e.printStackTrace(); return false;}		
			}
			else	// если рега прошла - перестаем мучить клиента
				break; 
		} while (true);
		
		try	// пытаемся отправить сообщение об успехе клиенту
			{handlerObjOutputStream_.writeObject(new Integer(1));}
		catch (IOException e)
			{e.printStackTrace(); return false;}
			/*all is ok*/
		clientNickName_=nickRegMsg;	// сохраняем ник клиента
		serverController_.changeRegisteredClientsCountBy(1);  // обновляем счетчик	
			// добавляем сообщение с приветствием в рассылку
		ReadyToSendMessage newGreetingsMsg = new ReadyToSendMessage(handlerThreadId_, "[SERVER] Greetings to "+clientNickName_+"!");
		serverController_.addNewMessageToSend(newGreetingsMsg);
		sendLastChatMessages(); // отправляем клиенту последние сообщения
		sendMessage("[SERVER] Welcome to the chat, "+clientNickName_+"! Current chatters count: "+serverController_.getRegisteredClientsCount()); // отправляем сообщение с приветствием клиенту
		return true;
	}
	
	private void closeConnection()
	{	// закрытие соединения
			// добавляем сообщение с прощанием в рассылку
		ReadyToSendMessage goodByMsg = new ReadyToSendMessage(handlerThreadId_, "[SERVER] "+ clientNickName_+" has left us!");
		serverController_.addNewMessageToSend(goodByMsg);
			// удаляемся из списка рассылки
		serverController_.delSenderFromReadyToSendMsgList(handlerThreadId_);
			// удаляем ник из системы
		serverController_.unregisterClientNickname(clientNickName_);
			// уменьшаем счетчик зарегистрированных клиентов
		serverController_.changeRegisteredClientsCountBy(-1);
			// пытаемся удалить последнее сообщение из рассылки
		serverController_.tryToRemoveFromReadyToSendMsgList();
	}
	
	private boolean sendMessage(String msg) 
	{
		try	// пытаемся отправить сообщение клиенту
			{handlerObjOutputStream_.writeObject(msg);}
		catch (SocketException sockEx)
			{return false;} // если соединение прервалось
		catch (IOException e1)
			{e1.printStackTrace(); return false;}	
		return true;
	}
	
	private void sendLastChatMessages()
	{
		ArrayList<String> lastMsgList;
		lastMsgList=serverController_.getLastMessagesFromAllMsgList();
		if (lastMsgList != null)
		{
			for (String msg : lastMsgList)
				sendMessage(msg);
		}
	}
	
	private void serveTheClient()
	{	// обслуживание клиента (прием/отправка сообщений)
		String inMsg = new String();
		ReadyToSendMessage msgToSend = null;
		while (true)
		{
				/*Отправка сообщения из общей рассылки чата*/
			msgToSend = serverController_.getMessageToSend(handlerThreadId_);
			if (msgToSend != null)
			{	// если есть сообщение из рассылки для отправки обслуживаемому клиенту
				if (!sendMessage(msgToSend.getMessage_())) // отправляем сообщение
					return;  // если соединение прервалось - закрываемся
			}
			
				/*Прием сообщения от обслуживаемого клиента*/
			try	// пытаемся прочитать сообщение от клиента
				{inMsg = (String) handlerObjInputStream_.readObject();}
			catch (SocketTimeoutException sockTimeoutEx)
				{continue;}	// если вывалились по таймауту, значит ничего не пришло 
			catch (SocketException sockEx)
				{return;} // если соединение прервалось - закрываемся
			catch (EOFException EOFEx)
				{return;} // если соединение прервалось - закрываемся
			catch (IOException e)
				{e.printStackTrace(); return;} 
			catch (ClassNotFoundException e)
				{e.printStackTrace(); return;}
			
			/* Если пришло сообщение от клиента*/
				// добавляем пришедшее от клиента сообщение в список рассылки
			String compiledMsg = compileMessageToSend(inMsg);
			ReadyToSendMessage newMsg = new ReadyToSendMessage(handlerThreadId_, compiledMsg);
			serverController_.addNewMessageToSend(newMsg);
			if (!sendMessage(compiledMsg)) // отправляем сообщение обратно клиенту
				return;  // если соединение прервалось - закрываемся
		} // endWhile
	}
	
	private String compileMessageToSend(String msg)
		{return new Date().toString()+"\n["+clientNickName_+"] says: "+msg;}
	
	@Override
	public void run() 
	{
		handlerThreadId_ = Thread.currentThread().getId(); // получаем id потока
		try	// инициализируем потоки чтения/записи
			{initStreams();}
		catch (IOException e)
			{e.printStackTrace();}
		if (clientRegistration()) // регистрируем клиента
		{	// если клиент не дропнулся во время реги
			try // устанавливаем таймаут на сокет
				{setSocketTimeout();} 
			catch (SocketException e)
				{e.printStackTrace();}   
			serveTheClient();	  // обслуживаем клиента
		}
		closeConnection();    // закрываемся при потере соединения
	} 
}

