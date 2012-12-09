package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
   
public class RoomController implements Runnable
{
	private Thread heaterT_;
	private Thread tsensorT_;
	private Heater heater_;
	private TemperatureSensor tSensor_;
	private boolean heaterNeeded_=false;
	private float temperature_;
	float initialAbsWetness_;
	
	private float temperatureSetting_;
	private ReentrantLock settingsLock_;
	private ReentrantLock airStateLock_;
	private Condition actionNeededCondition_;
	
	private StateMonFrame stateMonFrame_ = new StateMonFrame();	
	Scanner settingsScanner_ = new Scanner(System.in);
	
	public RoomController() 
	{
		settingsLock_ = new ReentrantLock();
		airStateLock_ = new ReentrantLock();
		actionNeededCondition_ = airStateLock_.newCondition();
		
		temperature_=new Random().nextInt(20)+10; 		 // 10-30
		initialAbsWetness_=new Random().nextInt(150)+50; // 50-200
		temperatureSetting_=temperature_;
		
		stateMonFrame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		stateMonFrame_.setVisible(true);
		
		heater_= new Heater();
		tSensor_ = new TemperatureSensor();
		heaterT_= new Thread(heater_);
		tsensorT_ = new Thread(tSensor_);
	}
	
	public void init()
	{
		heaterT_.start();
		tsensorT_.start();
	}
	
	private class StateMonFrame extends JFrame
	{
		public StateMonFrame()
		{
			setTitle("Room State Monitor");
			setSize(500, 200);
			setLayout(null);
			temperatureLabel_.setBounds(0, 0, 130, 20);
			temperatureSettingLabel_.setBounds(140, 0, 200, 20);
			wetnessLabel_.setBounds(0, 20, 130, 20);
			heaterOnLabel_.setBounds(0, 20, 70, 20);
			heaterOnLabel_.setText("Heater ON");
			heaterOnLabel_.setVisible(false);
			fanOnLabel_.setBounds(75, 20, 50, 20);
			fanOnLabel_.setText("Fan ON");
			fanOnLabel_.setVisible(false);
			add(temperatureLabel_);
			add(temperatureSettingLabel_);
			add(wetnessLabel_);
			add(heaterOnLabel_);
			add(fanOnLabel_);
		}
		public JLabel temperatureLabel_ = new JLabel();
		public JLabel wetnessLabel_ = new JLabel();
		public JLabel heaterOnLabel_ = new JLabel();
		public JLabel fanOnLabel_ = new JLabel();
		public JLabel temperatureSettingLabel_ = new JLabel();
		
	}
	
	
	private class TemperatureSensor implements Runnable
	{
		@Override
		public void run()
		{			
			Float temperature;
			Float temperatureSetting;
			Float temperatureSettingPercent;
			while (true)
			{
				temperature=RoomController.this.temperatureSensorAction();
				temperatureSetting=RoomController.this.readSettings();
				temperatureSettingPercent=temperatureSetting/100;
				RoomController.this.stateMonFrame_.temperatureLabel_.setText("Temperature: "+temperature.toString());
				RoomController.this.stateMonFrame_.temperatureSettingLabel_.setText("Temperature Set: "+temperatureSetting.toString());
				if (temperature < temperatureSetting-temperatureSettingPercent)
				{
					RoomController.this.stateMonFrame_.heaterOnLabel_.setVisible(true);
					RoomController.this.heaterOn();
				}
				else
				{
					RoomController.this.stateMonFrame_.heaterOnLabel_.setVisible(false);
					RoomController.this.heaterOff();
				}	
				try {
					Thread.sleep(500);
				} catch (InterruptedException e)
					{e.printStackTrace();}
			}
		}
	}
	

	private class Heater implements Runnable
	{
		@Override
		public void run() 
		{
			while (true)
			{	
				try 
				{
					RoomController.this.heaterAction();
					Thread.sleep(500);
				} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			} // endWhile		
		}
	}

	
	private void heaterAction() throws InterruptedException
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
	
	private float temperatureSensorAction()
	{
		airStateLock_.lock();
		try
			{return temperature_;}
		finally 
			{airStateLock_.unlock();}
	}
	
	private void heaterOn()
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
	
	private void heaterOff()
	{
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
	
	private float readSettings()
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
