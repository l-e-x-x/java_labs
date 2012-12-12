package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;

import java.util.ArrayList;

	/* Описывает сообщение, готовое к рассылке клиентам  */
	/* Cоздатель такого сообщения - обслуживающий поток, */
	/*   который получил от своего клиента сообщение     */
public class ReadyToSendMessage
{
		// набор обслуживающих потоков, уже отославших сообщение
	private ArrayList <Long> readedHandlersIdSet_;
		// само сообщение
	private String message_;
	
	public ReadyToSendMessage(long senderId, String msg)
	{	// в список отославших сразу же добавляется поток-создатель  
		readedHandlersIdSet_ = new ArrayList<Long>();
		readedHandlersIdSet_.add(senderId);
		setMessage_(msg);
	}
	
	public boolean hasBeenSendedBy(Long handlerId)
	{	// возвращает true, если обслуживающий поток handlerId уже отослал сообщение своему клиенту 
		if (readedHandlersIdSet_.contains(handlerId))
			return true;
		return false;
	}
	
	public void addNewSender(Long handlerId)
		{readedHandlersIdSet_.add(handlerId);}
	
	public void deleteSender(Long handlerId)
		{readedHandlersIdSet_.remove(handlerId);}
	
	public int getNumOfSenders()
		{return readedHandlersIdSet_.size();}
	
	public String getMessage_()
		{return message_;}

	public void setMessage_(String message_)
		{this.message_ = message_;}
}
