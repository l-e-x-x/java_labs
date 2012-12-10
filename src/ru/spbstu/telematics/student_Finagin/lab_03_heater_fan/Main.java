package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.text.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static float relativeWetnessFor(float temperature, float absWetness)
	{	// возвращает отн. влажность для указанной t и абс. влажн.
		return 100*absWetness/(3*(temperature+100));
	}	
	
	public static String customFormat(float value)
	{	// форматирует вывод float
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		return myFormatter.format(value);
	}

	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Rooms Number: ");
		int roomsNum=scan.nextInt();
		ArrayList <RoomController> rooms = new ArrayList<RoomController>();
		for (int i=0 ; i < roomsNum; i++) 
		{
			RoomController room = new RoomController(i+1);
			rooms.add(room);
			room.init();
		}
	}
}
