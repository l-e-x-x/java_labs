package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RoomController
{
		/*Сущности*/
		// обогреватель
	private Heater heater_;
		// вентилятор
	private Fan fan_;
		// температурный датчик
	private TemperatureSensor tSensor_;
		// датчик влажности
	private WetnessSensor wSensor_;
	
		// потоки
	private Thread heaterT_;
	private Thread fanT_;
	private Thread tSensorT_;
	private Thread wSensorT_;
	
	/*Состояние комнаты*/
		// текущая температура
	private float temperature_;
		// текущая вляжность (относительная)
	private float wetness_;
		// начальная абсолютная влажность (постоянна всегда)
	final float initialAbsWetness_=new Random().nextInt(150)+50; // 50-200
		// предпочитаемая температура и влажность
	private float temperatureSetting_;
	private float wetnessSetting_;
	
		// блокировки и условия
	private ReentrantLock settingsLock_;
	private ReentrantLock airStateLock_;
	private Condition actionNeededCondition_;
		// флаги необходимости включить обогреватель
	private boolean heaterTNeeded_=false;
	private boolean heaterWNeeded_=false;
		// флаги необходимости включить вентилятор
	private boolean fanTNeeded_=false;
	private boolean fanWNeeded_=false;

		// фрейм монитора
	private StateMonitorFrame stateMonitor_;	
	Scanner settingsScanner_ = new Scanner(System.in);
	
	public RoomController() 
	{
		settingsLock_ = new ReentrantLock();
		airStateLock_ = new ReentrantLock();
		actionNeededCondition_ = airStateLock_.newCondition();
		stateMonitor_ = new StateMonitorFrame(this);
		temperature_=new Random().nextInt(20)+10; 		 // 10-30
		wetness_= Main.relativeWetnessFor(temperature_, initialAbsWetness_);
		
		temperatureSetting_=temperature_;
		wetnessSetting_=wetness_;
		
		heater_= new Heater(this);
		fan_ = new Fan(this); 
		tSensor_ = new TemperatureSensor(this, stateMonitor_);
		wSensor_ = new WetnessSensor(this, stateMonitor_);
		
		heaterT_= new Thread(heater_);
		fanT_ = new Thread(fan_);
		tSensorT_ = new Thread(tSensor_);
		wSensorT_ = new Thread(wSensor_);
		System.out.println(initialAbsWetness_);
	}
	
	public void init()
	{
		heaterT_.start();
		fanT_.start();
		tSensorT_.start();
		wSensorT_.start();
	}
	
	public void heaterAction() throws InterruptedException
	{
		airStateLock_.lock();
		try
		{
			while ((!heaterWNeeded_)&&(!heaterTNeeded_))	
			{
				try 
					{actionNeededCondition_.await();} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			}
			stateMonitor_.heaterOnLabel_.setVisible(true);
			temperature_+= new Random().nextFloat()/10;
			wetness_=Main.relativeWetnessFor(temperature_, initialAbsWetness_);
		}
		finally 
			{airStateLock_.unlock();}
	}
	
	public void fanAction() throws InterruptedException
	{
		airStateLock_.lock();
		try
		{
			while ((!fanWNeeded_)&&(!fanTNeeded_))	
			{
				try 
					{actionNeededCondition_.await();} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			}
			stateMonitor_.fanOnLabel_.setVisible(true);
			temperature_-= new Random().nextFloat()/10;
			wetness_=Main.relativeWetnessFor(temperature_, initialAbsWetness_);
		}
		finally 
			{airStateLock_.unlock();}
	}
	
	public float temperatureSensorAction()
	{
		airStateLock_.lock();
		try
			{return temperature_;}
		finally 
			{airStateLock_.unlock();}
	}
	
	public float wetnessSensorAction()
	{
		airStateLock_.lock();
		try
			{return wetness_;}
		finally 
			{airStateLock_.unlock();}
	}
	
	public void heaterTurnOn(boolean heaterTNeeded, boolean heaterWNeeded)
	{
		if ((!heaterTNeeded_)&&(!heaterWNeeded_))
		{
			airStateLock_.lock();
			try
			{
				heaterTNeeded_=heaterTNeeded;
				heaterWNeeded_=heaterWNeeded;
				actionNeededCondition_.signalAll();	
			}
			finally
				{airStateLock_.unlock();}		
		}
	}
	
	public void heaterTurnOff(boolean heaterTNeeded, boolean heaterWNeeded)
	{
		if ((heaterWNeeded_)&&(heaterWNeeded))
		{
			heaterWNeeded_=false;
			stateMonitor_.heaterOnLabel_.setVisible(false);
		}
		if ((heaterTNeeded_)&&(heaterTNeeded))
		{
			heaterTNeeded_=false;
			stateMonitor_.heaterOnLabel_.setVisible(false);
		}
	}
	
	public void fanTurnOn(boolean fanTNeeded, boolean fanWNeeded)
	{
		if ((!fanTNeeded_)&&(!fanWNeeded_))
		{
			airStateLock_.lock();
			try
			{
				fanTNeeded_=fanTNeeded;
				fanWNeeded_=fanWNeeded;
				actionNeededCondition_.signalAll();	
			}
			finally
				{airStateLock_.unlock();}		
		}
	}
	
	public void fanTurnOff(boolean fanTNeeded, boolean fanWNeeded)
	{
		if ((fanWNeeded_)&&(fanWNeeded))
		{
			fanWNeeded_=false;
			stateMonitor_.fanOnLabel_.setVisible(false);
		}
		if ((fanTNeeded_)&&(fanTNeeded))
		{
			fanTNeeded_=false;
			stateMonitor_.fanOnLabel_.setVisible(false);
		}
	}
	
	public void updateSettings(float newTempSet, float newWetnessSet)
	{
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
	{
		settingsLock_.lock();
		try
			{temperatureSetting_=newTempSet;}
		finally
			{settingsLock_.unlock();}
	}
	
	public void updateWetnessSetting(float newWetnessSet)
	{
		settingsLock_.lock();
		try
			{wetnessSetting_=newWetnessSet;}
		finally
			{settingsLock_.unlock();}
	}
	
	public float getCurrentTemperatureSettings()
	{
		settingsLock_.lock();
		try
			{return temperatureSetting_;}
		finally
			{settingsLock_.unlock();}
	}

	public float getCurrentWetnessSettings()
	{
		settingsLock_.lock();
		try
			{return wetnessSetting_;}
		finally
			{settingsLock_.unlock();}
	}
}
