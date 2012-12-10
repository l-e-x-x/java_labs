package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

public class WetnessSensor implements Runnable
{
	private RoomController roomCntrl_; // контроллер состояния комнаты
	private StateMonitorFrame roomStateMon_; // монитор состояния комнаты
	private Float currentWetness_;	// текущая влажность
	private Float currentWetnessSetting_; // текущая настройка влажности
	private Float currentWetnessSettingPercent_; // один процент от текущей влажности 
	
	public WetnessSensor(RoomController roomCtrl, StateMonitorFrame roomStateMon)
	{	
		roomCntrl_=roomCtrl;
		roomStateMon_=roomStateMon;
	}
	
	@Override
	public void run()
	{			
		while (true)
		{
			currentWetness_=roomCntrl_.wetnessSensorAction();
			currentWetnessSetting_=roomCntrl_.getCurrentWetnessSettings();
			currentWetnessSettingPercent_=currentWetnessSetting_/100;
			roomStateMon_.wetnessLabel_.setText("Wetness: "+Main.customFormat(currentWetness_)+"%");
			roomStateMon_.wetnessSettingLabel_.setText("Wetness Set: "+
														Main.customFormat(currentWetnessSetting_)+
														"±"+
														Main.customFormat(currentWetnessSettingPercent_)+
														"%");
			if (currentWetness_ > currentWetnessSetting_+currentWetnessSettingPercent_)
				roomCntrl_.heaterTurnOn(false ,true);
			else
				roomCntrl_.heaterTurnOff(false ,true);
			
			if (currentWetness_ < currentWetnessSetting_-currentWetnessSettingPercent_)
				roomCntrl_.fanTurnOn(false ,true);
			else
				roomCntrl_.fanTurnOff(false ,true);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)
				{e.printStackTrace();}
		}
	}
}

