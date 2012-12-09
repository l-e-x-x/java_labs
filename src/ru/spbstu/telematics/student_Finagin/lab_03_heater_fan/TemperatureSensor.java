package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

/*Класс датчика температуры*/
public class TemperatureSensor implements Runnable
{
	private RoomController roomCntrl_;
	private StateMonitorFrame roomStateMon_; 
	private Float currentTemperature_;
	private Float currenttemperatureSetting_;
	private Float currentTemperatureSettingPercent_;
	
	public TemperatureSensor(RoomController roomCtrl, StateMonitorFrame roomStateMon)
	{
		roomCntrl_=roomCtrl;
		roomStateMon_=roomStateMon;
	}
	
	@Override
	public void run()
	{			
		while (true)
		{
			currentTemperature_=roomCntrl_.temperatureSensorAction();
			currenttemperatureSetting_=roomCntrl_.getCurrentSettings();
			currentTemperatureSettingPercent_=currenttemperatureSetting_/100;
			roomStateMon_.temperatureLabel_.setText("Temperature: "+currentTemperature_.toString());
			roomStateMon_.temperatureSettingLabel_.setText("Temperature Set: "+currenttemperatureSetting_.toString());
			if (currentTemperature_ < currenttemperatureSetting_-currentTemperatureSettingPercent_)
			{
				roomStateMon_.heaterOnLabel_.setVisible(true);
				roomCntrl_.heaterTurnOn();
			}
			else
			{
				roomStateMon_.heaterOnLabel_.setVisible(false);
				roomCntrl_.heaterTurnOff();
			}	
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)
				{e.printStackTrace();}
		}
	}
}
