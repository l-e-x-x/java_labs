package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

	/*Класс датчика температуры*/
public class TemperatureSensor implements Runnable
{
	private RoomController roomCntrl_; // контроллер состояния комнаты
	private StateMonitorFrame roomStateMon_; // монитор состояния комнаты
	private Float currentTemperature_;	// текущая температура
	private Float currenttemperatureSetting_; // текущая настройка температуры
	private Float currentTemperatureSettingPercent_; // один процент от текущей температуры 
	
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
			currenttemperatureSetting_=roomCntrl_.getCurrentTemperatureSettings();
			currentTemperatureSettingPercent_=currenttemperatureSetting_/100;
			roomStateMon_.temperatureLabel_.setText("Temperature: "+Main.customFormat(currentTemperature_));
			roomStateMon_.temperatureSettingLabel_.setText("Temperature Set: "+
															Main.customFormat(currenttemperatureSetting_)+
															"±"+
															Main.customFormat(currentTemperatureSettingPercent_)+
															"°C");
			if (currentTemperature_ < currenttemperatureSetting_-currentTemperatureSettingPercent_)
				roomCntrl_.heaterTurnOn(true, false);
			else
				roomCntrl_.heaterTurnOff(true, false);
			
			if (currentTemperature_ > currenttemperatureSetting_+currentTemperatureSettingPercent_)
				roomCntrl_.fanTurnOn(true, false);
			else
				roomCntrl_.fanTurnOff(true, false);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)
				{e.printStackTrace();}
		}
	}
}
