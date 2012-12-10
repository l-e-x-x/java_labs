package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.text.*;

public class Main {
	public static float relativeWetnessFor(float temperature, float absWetness)
		{return 100*absWetness/(3*(temperature+100));}
	public static String customFormat(float value ) {
      DecimalFormat myFormatter = new DecimalFormat("###.##");
      return myFormatter.format(value);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RoomController room = new RoomController();
		room.init();
	}
}
