package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;

import java.util.Set;

public class ReadyToSendMessage
{
	private Set <Long> readedHandlersIdSet_;
	private long senderHandlerId_; 
	private String message_;
	
	public ReadyToSendMessage(long senderId, String msg)
	{
		setMessage_(msg);
		senderHandlerId_=senderId;
	}
	
	
	public boolean hasBeenSendedBy(Long handlerId)
	{
		if (handlerId == senderHandlerId_)
			return false;
		if (readedHandlersIdSet_.contains(handlerId))
			return false;
		return true;
	}
	
	public void addNewSender(Long handlerId)
		{readedHandlersIdSet_.add(handlerId);}

	public Integer getNumOfSenders()
		{return readedHandlersIdSet_.size();}
	
	public String getMessage_()
		{return message_;}

	public void setMessage_(String message_)
		{this.message_ = message_;}
	
}
