package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

public class Main {
	static float relativeWetnessFor(float temperature, float absWetness)
		{return absWetness/(3*(temperature+100));}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoomController room = new RoomController();
		room.init();
		Thread roomCntr = new Thread(room);
		roomCntr.start();
	}
}
