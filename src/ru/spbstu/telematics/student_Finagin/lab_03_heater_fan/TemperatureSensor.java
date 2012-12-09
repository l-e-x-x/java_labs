package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

public class TemperatureSensor implements Runnable
{
	public TemperatureSensor(RoomSensorControlled room)
		{room_=room;}

	private RoomSensorControlled room_;

	
	@Override
	public void run()
	{			
		float temperature = 0;
		while (true)
		{
			temperature=room_.temperatureSensorAction();
			System.out.print("Sensor [Temperature]: ");
			System.out.println(temperature);
			if (temperature < 15)
			{
				System.out.println("Heater ON\n");
				room_.heaterOn();
			}
			else
				room_.heaterOff();
				
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)
				{e.printStackTrace();}
		}
	}
}