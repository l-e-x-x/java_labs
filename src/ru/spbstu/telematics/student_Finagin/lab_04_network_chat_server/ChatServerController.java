package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;

public class ChatServerController implements Runnable
{
		// набор ников зарегеных клиентов <Id обработчика, ник>
	private HashMap<Integer, String> regClientsNicknames_;
		// lock для доступа к никам клиентов 
	Lock clientNicknamesLock_; 
	
	
	@Override
	public void run() 
	{
		
	}

}
