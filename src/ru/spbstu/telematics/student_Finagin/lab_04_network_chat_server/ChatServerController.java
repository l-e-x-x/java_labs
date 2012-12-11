package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.concurrent.locks.ReentrantLock;

public class ChatServerController implements Runnable
{
	/*	// набор ников зарегеных клиентов <Id обработчика, ник>
	private HashMap<Integer, String> regClientsNicknames_;
		// lock для доступа к никам клиентов 
	private ReentrantLock clientNicknamesLock_; 
	*/
	
	private Integer clientsCount_ = new Integer(0);
	private ArrayList <ReadyToSendMessage> readyToSendMessages_;
	private ReentrantLock readyToSendMessagesLock_;
	ServerSocket clientsAccepterSocket_;
	
	public ChatServerController()
	{
		readyToSendMessagesLock_ = new ReentrantLock();
		readyToSendMessages_ = new ArrayList<ReadyToSendMessage>();
		try
			{clientsAccepterSocket_ = new ServerSocket(3000);} 
		catch (IOException e)
			{e.printStackTrace();}
	}
	
	
	void addNewMessageToSend(ReadyToSendMessage msg)
	{
		readyToSendMessagesLock_.lock();
		try
			{readyToSendMessages_.add(msg);}
		finally
			{readyToSendMessagesLock_.unlock();}
	}
	
	ReadyToSendMessage getMessageToSend(Long handlerId)
	{
		ReadyToSendMessage newMsgToSend = null;
		readyToSendMessagesLock_.lock();
		try
		{
			if (!readyToSendMessages_.isEmpty())
			{
				if (!readyToSendMessages_.get(0).hasBeenSendedBy(handlerId))	
				{	// если сообщение не было отправлено от handlerId
					newMsgToSend=readyToSendMessages_.get(0); 			 // берем последнее сообщение
					readyToSendMessages_.get(0).addNewSender(handlerId); // добавляем отправителя
					if (readyToSendMessages_.get(0).getNumOfSenders() == clientsCount_)
						readyToSendMessages_.remove(0); // если все отправители забрали сообщение - удаляем
				}
			}
		}
		finally
			{readyToSendMessagesLock_.unlock();}
		return newMsgToSend;
	}
	
	@Override
	public void run() 
	{
		Socket newClient = null;
		while (true)
		{
			try
				{newClient = clientsAccepterSocket_.accept();}
			catch (IOException e)
				{e.printStackTrace();}
			new Thread(new ClientHandler(newClient, this)).start();
			clientsCount_++;
			System.out.println("new client connected");
		}
	}

}
