package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

public class Fan implements Runnable
{
	private RoomController roomCntrl_;
	public Fan(RoomController roomCtrl) 
		{roomCntrl_=roomCtrl;}
	
	@Override
	public void run() 
	{
		while (true)
		{	
			try 
			{
				roomCntrl_.fanAction();
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
				{e.printStackTrace();}
		} // endWhile		
	}
}
