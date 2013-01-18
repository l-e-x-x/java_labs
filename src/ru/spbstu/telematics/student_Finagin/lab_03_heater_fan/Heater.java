package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

public class Heater implements Runnable
{
	private RoomController roomCntrl_;
	public Heater(RoomController roomCtrl) 
		{roomCntrl_=roomCtrl;}
	
	@Override
	public void run() 
	{
		while (true)
		{	
			try 
			{
				roomCntrl_.heaterAction();
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
				{e.printStackTrace();}
		} // endWhile		
	}
}
