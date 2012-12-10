package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RoomController
{
	/*--Сущности--*/
		// обогреватель
	private Heater heater_;
		// вентилятор
	private Fan fan_;
		// температурный датчик
	private TemperatureSensor tSensor_;
		// датчик влажности
	private WetnessSensor wSensor_;
	
		// Потоки
	private Thread heaterT_;
	private Thread fanT_;
	private Thread tSensorT_;
	private Thread wSensorT_;
	
	/*--Состояние комнаты--*/
		// текущая температура
	private float temperature_;
		// текущая вляжность (относительная)
	private float wetness_;
		// начальная абсолютная влажность (постоянна всегда)
	final float initialAbsWetness_=new Random().nextInt(150)+50; // 50-200
		// предпочитаемая температура и влажность (настройки)
	private float temperatureSetting_;
	private float wetnessSetting_;
	
	/*--Блокировки и Условия--*/
		// блокировка на настройки
	private ReentrantLock settingsLock_;
		// блокировка на состояние воздуха в комнате (температура и влажность)
	private ReentrantLock airStateLock_;
		// условие на предпринятие действия (включить обогреватель/вентилятор)
	private Condition actionNeededCondition_;
	
	/*Флаги*/
		// флаги необходимости включить обогреватель
	private boolean heaterTNeeded_=false;
	private boolean heaterWNeeded_=false;
		// флаги необходимости включить вентилятор
	private boolean fanTNeeded_=false;
	private boolean fanWNeeded_=false;

		// фрейм монитора
	private StateMonitorFrame stateMonitor_;	
		
		// номер комнаты
	public int roomNumber_;
	
	public RoomController(int roomNumber) 
	{
		roomNumber_=roomNumber;
			// создаем блокировки и условия
		settingsLock_ = new ReentrantLock();
		airStateLock_ = new ReentrantLock();
		actionNeededCondition_ = airStateLock_.newCondition();
		stateMonitor_ = new StateMonitorFrame(this);
		temperature_=new Random().nextInt(20)+10; 	// начальная температура 10-30
		wetness_= Main.relativeWetnessFor(temperature_, initialAbsWetness_);
		
			// выставляем настройки такими же, как и состояния
		temperatureSetting_=temperature_;
		wetnessSetting_=wetness_;
		
			// создаем сущности
		heater_= new Heater(this);
		fan_ = new Fan(this); 
		tSensor_ = new TemperatureSensor(this, stateMonitor_);
		wSensor_ = new WetnessSensor(this, stateMonitor_);
			// создаем потоки
		heaterT_= new Thread(heater_);
		fanT_ = new Thread(fan_);
		tSensorT_ = new Thread(tSensor_);
		wSensorT_ = new Thread(wSensor_);
		System.out.println("Absolute wetness in room #"+roomNumber_+": "+initialAbsWetness_+" g/m³");
	}
	
	
	public void init()
	{	// инициализация (запуск приборов)
		heaterT_.start();
		fanT_.start();
		tSensorT_.start();
		wSensorT_.start();
	}
	
	
	public void heaterAction() throws InterruptedException
	{	// работа обогревателя
		airStateLock_.lock();
		try
		{
			while ((!heaterWNeeded_)&&(!heaterTNeeded_))	
			{	// пока обогреватель не требуется - ждем-с
				try 
					{actionNeededCondition_.await();} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			}
				// греем, если надо
			stateMonitor_.heaterOnLabel_.setVisible(true);
			temperature_+= new Random().nextFloat()/10; //  мощность: от 0 до 0.1 градуса за такт
			wetness_=Main.relativeWetnessFor(temperature_, initialAbsWetness_);
		}
		finally 
			{airStateLock_.unlock();}
	}
	
	public void fanAction() throws InterruptedException
	{	// работа вентилятора
		airStateLock_.lock();
		try
		{
			while ((!fanWNeeded_)&&(!fanTNeeded_))	
			{	// пока вентилятор не требуется - ждем-с
				try 
					{actionNeededCondition_.await();} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			}
				// если надо - охлаждаем
			stateMonitor_.fanOnLabel_.setVisible(true);
			temperature_-= new Random().nextFloat()/10; //  мощность: от 0 до 0.1 градуса за такт
			wetness_=Main.relativeWetnessFor(temperature_, initialAbsWetness_);
		}
		finally 
			{airStateLock_.unlock();}
	}
	
	public float temperatureSensorAction()
	{	// работа температурного датчика
		airStateLock_.lock();
		try
			{return temperature_;}
		finally 
			{airStateLock_.unlock();}
	}
	
	public float wetnessSensorAction()
	{	// работа датчика влажности
		airStateLock_.lock();
		try
			{return wetness_;}
		finally 
			{airStateLock_.unlock();}
	}
	
	public void heaterTurnOn(boolean heaterTNeeded, boolean heaterWNeeded)
	{	// включение обогревателя
		if ((!heaterTNeeded_)&&(!heaterWNeeded_))
		{	// включаем, если он выключен (причина включения в параметрах)
			airStateLock_.lock();
			try
			{
				heaterTNeeded_=heaterTNeeded;
				heaterWNeeded_=heaterWNeeded;
					// тыркаем все потоки
				actionNeededCondition_.signalAll();	
			}
			finally
				{airStateLock_.unlock();}		
		}
	}
	
	public void heaterTurnOff(boolean heaterTNeeded, boolean heaterWNeeded)
	{	// выключение нагревателя
		if ((heaterWNeeded_)&&(heaterWNeeded))
		{	// если работали на понижение влажности и больше не нужно греть - выключаем
			heaterWNeeded_=false;
			stateMonitor_.heaterOnLabel_.setVisible(false);
		}
		if ((heaterTNeeded_)&&(heaterTNeeded))
		{	// если работали для повышения температуры и больше не нужно греть - выключаем
			heaterTNeeded_=false;
			stateMonitor_.heaterOnLabel_.setVisible(false);
		}
	}
	
	public void fanTurnOn(boolean fanTNeeded, boolean fanWNeeded)
	{	// включение вентилятора
		if ((!fanTNeeded_)&&(!fanWNeeded_))
		{	// включаем, если он выключен (причина включения в параметрах)
			airStateLock_.lock();
			try
			{
				fanTNeeded_=fanTNeeded;
				fanWNeeded_=fanWNeeded;
					// тыркаем все потоки
				actionNeededCondition_.signalAll();	
			}
			finally
				{airStateLock_.unlock();}		
		}
	}
	
	public void fanTurnOff(boolean fanTNeeded, boolean fanWNeeded)
	{	// выключение вентилятора
		if ((fanWNeeded_)&&(fanWNeeded))
		{	// если работали на повышение влажности и больше не нужно охлаждать - выключаем
			fanWNeeded_=false;
			stateMonitor_.fanOnLabel_.setVisible(false);
		}
		if ((fanTNeeded_)&&(fanTNeeded))
		{	// если работали для понижения температуры и больше не нужно охлаждать - выключаем
			fanTNeeded_=false;
			stateMonitor_.fanOnLabel_.setVisible(false);
		}
	}
	
	public void updateSettings(float newTempSet, float newWetnessSet)
	{	// обновление настроек предпочитаемой температуры и влажности
		settingsLock_.lock();
		try
		{
			temperatureSetting_=newTempSet;
			wetnessSetting_ = newWetnessSet;
		}
		finally
			{settingsLock_.unlock();}
	}
	
	
	public void updateTemperatureSetting(float newTempSet)
	{	// обновление настроек предпочитаемой температуры
		settingsLock_.lock();
		try
			{temperatureSetting_=newTempSet;}
		finally
			{settingsLock_.unlock();}
	}
	
	public void updateWetnessSetting(float newWetnessSet)
	{	// обновление настроек предпочитаемой влажности
		settingsLock_.lock();
		try
			{wetnessSetting_=newWetnessSet;}
		finally
			{settingsLock_.unlock();}
	}
	
	public float getCurrentTemperatureSettings()
	{	// возвращает настройки предпочитаемой температуры
		settingsLock_.lock();
		try
			{return temperatureSetting_;}
		finally
			{settingsLock_.unlock();}
	}

	public float getCurrentWetnessSettings()
	{	// возвращает настройки предпочитаемой влажности
		settingsLock_.lock();
		try
			{return wetnessSetting_;}
		finally
			{settingsLock_.unlock();}
	}
}
