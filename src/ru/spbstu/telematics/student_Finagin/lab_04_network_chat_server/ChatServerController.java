package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.concurrent.locks.ReentrantLock;

public class ChatServerController implements Runnable
{
	/*Сетевые настройки сервера*/
		// сокет, принимающий новые подключения
	private ServerSocket clientsAccepterSocket_;

	/*Клиенты*/
		// набор ников зарегеных клиентов 
	private ArrayList<String> regClientsNicknames_;
		// блокировка для доступа к никам клиентов 
	private ReentrantLock clientNicknamesLock_; 
		// текущее число зарегистрированных клиентов
	private int registeredClientsCount_;
		//	блокировка на счетчик зарегистрированых клиентов
	private ReentrantLock registeredClientsCounterLock_;
	
	/*Сообщения*/
		// список сообщений, готовых к отправке клиентам
	private ArrayList <ReadyToSendMessage> readyToSendMessages_;
		// блокировка для доступа к списку readyToSendMessages
	private ReentrantLock readyToSendMessagesLock_;
		// список всех сообщений
	private ArrayList<String> allMessagesList_;
		// блокировка для доступа к списку всех сообщений
	private ReentrantLock allMessagesListLock_;
		// лимит числа последних сообщений (из списка allMessages)
	final private int lastMessagesCountLimit_=100;
	
	public ChatServerController()
	{
		readyToSendMessagesLock_ = new ReentrantLock();
		readyToSendMessages_ = new ArrayList<ReadyToSendMessage>();
		allMessagesList_ = new ArrayList<String>();
		allMessagesListLock_ = new ReentrantLock();
		regClientsNicknames_ = new ArrayList<String>();
		clientNicknamesLock_ = new ReentrantLock();
		
		registeredClientsCounterLock_ = new ReentrantLock();
		try
			{clientsAccepterSocket_ = new ServerSocket(3000);} 
		catch (IOException e)
			{e.printStackTrace();}
	}
	
		/*Методы для работы с сообщениями*/
	void addNewMessageToSend(ReadyToSendMessage msg)
	{	// добавление сообщения к списку на отправку другим клиентам
		readyToSendMessagesLock_.lock();
		try	// если список не заблочен - добавляем
			{readyToSendMessages_.add(msg);}
		finally  // снимаем блокировку
			{readyToSendMessagesLock_.unlock();}
	}
	
	ReadyToSendMessage getMessageToSend(Long handlerId)
	{	// запрос сообщения на отправку клиенту от его обслуживающего потока (handlerId - Id потока)
		ReadyToSendMessage newMsgToSend = null;
		readyToSendMessagesLock_.lock();
		try
		{	// если список не заблочен - пытаемся получить сообщение
			if (!readyToSendMessages_.isEmpty())
			{	// если список не пуст - выбираем сообщение
				if (!readyToSendMessages_.get(0).hasBeenSendedBy(handlerId))	
				{	// если сообщение еще не было отправлено от текущего потока (handlerId)
					newMsgToSend=readyToSendMessages_.get(0); 			 // берем первое сообщение в списке
					readyToSendMessages_.get(0).addNewSender(handlerId); // добавляем отправителя handlerId
				}
				tryToRemoveFromReadyToSendMsgList(); // пытаемся удалить сообщение из рассылки
			}
		}
		finally	// снимаем блокировку
			{readyToSendMessagesLock_.unlock();}
		return newMsgToSend;	// возвращаем сообщение или null (если уже отправлено)
	}
	
	void delSenderFromReadyToSendMsgList(Long handlerId)
	{	// удаление обслуживающего потока-отправителя из списка рассылки
		readyToSendMessagesLock_.lock();
		try
		{	// если список не заблочен - пытаемся удалить из списка рассылки
			for (ReadyToSendMessage msg : readyToSendMessages_)
				msg.deleteSender(handlerId);
		}
		finally	// снимаем блокировку
			{readyToSendMessagesLock_.unlock();}
	}

	public void tryToRemoveFromReadyToSendMsgList()
	{	// удаление сообщения из общей рассылки
		readyToSendMessagesLock_.lock();
		try
		{	// если список не заблочен - пытаемся получить количество отправителей сообщения
			if (!readyToSendMessages_.isEmpty())
			{	// если список не пуст - выбираем сообщение
				if (readyToSendMessages_.get(0).getNumOfSenders() == getRegisteredClientsCount())
				{	// если все обслуживающие потоки-отправители забрали сообщение - удаляем
					addMessageToAllMsgList(readyToSendMessages_.get(0).getMessage_());
					readyToSendMessages_.remove(0); 
				}
			}
		}
		finally	// снимаем блокировку
			{readyToSendMessagesLock_.unlock();}
	}
	
	public void addMessageToAllMsgList(String msg)
	{	// добавление сообщения к общему списку сообщений
		allMessagesListLock_.lock();
		try	// пытаемся добавить
		{
			allMessagesList_.add(msg);
			if (allMessagesList_.size() > lastMessagesCountLimit_)
				allMessagesList_.remove(0); // удаляем, если превышен лимит
		}
		finally 	// снимаем блокировку
			{allMessagesListLock_.unlock();}
	}
	
	public ArrayList<String> getLastMessagesFromAllMsgList()
	{	// запрос списка последних сообщений из общего списка
		allMessagesListLock_.lock();
		try	// пытаемся запросить сообщения
		{
			if (!allMessagesList_.isEmpty())
				return (allMessagesList_);
			return null;
		}
		finally 	// снимаем блокировку
			{allMessagesListLock_.unlock();}
	}
	
	
	/*Методы для работы с клиентами*/
	
	public boolean registerNewClientNickname(String newNick)
	{	// регистрация нового ника в системе
		registeredClientsCounterLock_.lock();
		try
		{	// пытаемся зарегить ник
			if (regClientsNicknames_.contains(newNick))
				return false;	// если такой ник уже есть - отказ
			regClientsNicknames_.add(newNick);
			return true;
		}
		finally  // снимаем блокировку
			{registeredClientsCounterLock_.unlock();}
	}
	
	public void unregisterClientNickname(String nickname)
	{	// удаление ника из системы
		registeredClientsCounterLock_.lock();
		try	// пытаемся удалить ник
			{regClientsNicknames_.remove(nickname);}
		finally
			{registeredClientsCounterLock_.unlock();}
	} 
		
	public int getRegisteredClientsCount()
	{
		registeredClientsCounterLock_.lock();
		try	// пытаемся прочитать и вернуть значение счетчика
			{return registeredClientsCount_;}
		finally // снимаем блокировку
			{registeredClientsCounterLock_.unlock();}
	}

	public void setRegisteredClientsCount(int registeredClientsCount)
	{
		registeredClientsCounterLock_.lock();
		try	// пытаемся изменить значение счетчика
			{this.registeredClientsCount_ = registeredClientsCount;}
		finally
			{registeredClientsCounterLock_.unlock();}
	}
	
	public void changeRegisteredClientsCountBy(int num)
	{	// изменение значения счетчика на num единиц
		registeredClientsCounterLock_.lock();
		try	// пытаемся изменить значение счетчика (увеличить/уменьшить на num)
			{this.registeredClientsCount_+=num;}
		finally
			{registeredClientsCounterLock_.unlock();}
	}

	@Override
	public void run() 
	{	// основной метод чат-сервера
		Socket newClientSocket = null;
		while (true)
		{
			try	// принимаем новое подключение
				{newClientSocket = clientsAccepterSocket_.accept();}
			catch (IOException e)
				{e.printStackTrace();}
			new Thread(new ChatServerClientHandler(newClientSocket, 50, this)).start();
		}
	}
}
