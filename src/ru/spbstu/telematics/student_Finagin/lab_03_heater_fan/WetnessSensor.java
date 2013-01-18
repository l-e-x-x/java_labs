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
				// снимаем показатели
			currentWetness_=roomCntrl_.wetnessSensorAction();
				// читаем настройки для влажности
			currentWetnessSetting_=roomCntrl_.getCurrentWetnessSettings();
			currentWetnessSettingPercent_=currentWetnessSetting_/100;
			roomStateMon_.wetnessLabel_.setText("Wetness: "+Main.customFormat(currentWetness_)+"%");
			roomStateMon_.wetnessSettingLabel_.setText("Wetness Set: "+
														Main.customFormat(currentWetnessSetting_)+
														"±"+
														Main.customFormat(currentWetnessSettingPercent_)+
														"%");
			if (currentWetness_ > // если влажность больше чем заданная (на 1%)
				currentWetnessSetting_+currentWetnessSettingPercent_)
				roomCntrl_.heaterTurnOn(false ,true); // тогда включаем обогрев
			else
				roomCntrl_.heaterTurnOff(false ,true); // иначе - выключаем
			
			if (currentWetness_ < // если влажность меньше чем заданная (на 1%)
				currentWetnessSetting_-currentWetnessSettingPercent_)
				roomCntrl_.fanTurnOn(false ,true); // тогда включаем охлаждение
			else
				roomCntrl_.fanTurnOff(false ,true); // иначе - выключаем
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)
				{e.printStackTrace();}
		}
	}
}

