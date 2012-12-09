package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
   
public class RoomController implements Runnable
{
		/*Сущности*/
		// обогреватель
	private Heater heater_;
		// температурный датчик
	private TemperatureSensor tSensor_;
		// потоки
	private Thread heaterT_;
	private Thread tsensorT_;
		
	/*Состояние комнаты*/
		// текущая температура
	private float temperature_;
		// начальная абсолютная влажность (постоянна всегда)
	final float initialAbsWetness_=new Random().nextInt(150)+50; // 50-200
		// предпочитаемая температура
	private float temperatureSetting_;
	
	
		// блокировки и условия
	private ReentrantLock settingsLock_;
	private ReentrantLock airStateLock_;
	private Condition actionNeededCondition_;
		// флаг необходимости включить обогреватель
	private boolean heaterNeeded_=false;

	// фрейм монитора
	private StateMonitorFrame stateMonitor_;	
	Scanner settingsScanner_ = new Scanner(System.in);
	
	public RoomController() 
	{
		settingsLock_ = new ReentrantLock();
		airStateLock_ = new ReentrantLock();
		actionNeededCondition_ = airStateLock_.newCondition();
		stateMonitor_ = new StateMonitorFrame();
		temperature_=new Random().nextInt(20)+10; 		 // 10-30
		temperatureSetting_=temperature_;
		
		
		heater_= new Heater(this);
		tSensor_ = new TemperatureSensor(this, stateMonitor_);
		heaterT_= new Thread(heater_);
		tsensorT_ = new Thread(tSensor_);
	}
	
	public void init()
	{
		heaterT_.start();
		tsensorT_.start();
	}
	
	public void heaterAction() throws InterruptedException
	{
		airStateLock_.lock();
		try
		{
			while (!heaterNeeded_)	
			{
				try 
					{actionNeededCondition_.await();} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			}
			temperature_+=0.1;
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
	
	public void heaterTurnOn()
	{
		if (!heaterNeeded_)
		{
			airStateLock_.lock();
			try
			{
				heaterNeeded_=true;
				actionNeededCondition_.signalAll();	
			}
			finally
				{airStateLock_.unlock();}		
		}
	}
	
	public void heaterTurnOff()
	{
		if (heaterNeeded_)
			heaterNeeded_=false;
	}
	
	private void changeSettings(float newTempSet)
	{
		settingsLock_.lock();
		try
			{temperatureSetting_=newTempSet;}
		finally
			{settingsLock_.unlock();}
	}
	
	public float getCurrentSettings()
	{
		settingsLock_.lock();
		try
			{return temperatureSetting_;}
		finally
			{settingsLock_.unlock();}
	}
	
	@Override
	public void run() 
	{
		float settingTemper,
			  settingWetness;
		while (true)
		{	
			System.out.print("New temperature setting: ");
			settingTemper=settingsScanner_.nextFloat();
			changeSettings(settingTemper);
			//System.out.print("New wetness setting: ");
			//settingWetness=settingsScanner_.nextFloat();
		} // endWhile
	}
}
