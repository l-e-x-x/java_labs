package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenServer 
{
	public ChatServerController serverController_= new ChatServerController();
	
	public static void main(String[] args) throws IOException 
	{
		ServerSocket server = new ServerSocket(3000);
		while (!Thread.currentThread().isInterrupted()) 
		{
			Socket client = server.accept();
			new Thread(new ClientHandler(client)).start();
		}
	}
}