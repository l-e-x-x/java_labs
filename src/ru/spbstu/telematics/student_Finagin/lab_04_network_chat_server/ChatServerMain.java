package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServerMain 
{
	public static ChatServerController serverController_= new ChatServerController();
	
	public static void main(String[] args) throws IOException 
		{(new Thread(serverController_)).start();}
}