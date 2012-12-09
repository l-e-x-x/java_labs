package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoomSensorControlled room = new RoomSensorControlled();
		Heater heater = new Heater(room);
		TemperatureSensor tSensor = new TemperatureSensor(room);
		Thread heaterT = new Thread(heater);
		Thread tSensorT = new Thread(tSensor);
		heaterT.start();
		tSensorT.start();
		
	}
}
