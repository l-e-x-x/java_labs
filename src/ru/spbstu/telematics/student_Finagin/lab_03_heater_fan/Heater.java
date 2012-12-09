package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

public class Heater implements Runnable
{
	public Heater(RoomSensorControlled room)
		{room_=room;}
	
	private RoomSensorControlled room_;
	
	@Override
	public void run() 
	{
		while (true)
		{	
			try 
			{
				room_.heaterAction();
				Thread.sleep(500);
			} 
			catch (InterruptedException e) 
				{e.printStackTrace();}
		}
			
	}
	
}
