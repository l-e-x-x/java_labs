package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RoomSensorControlled
{
	public RoomSensorControlled() 
	{
		roomStateLock_ = new ReentrantReadWriteLock(); 
	}
	
	public synchronized void heaterAction() throws InterruptedException
	{
		while (!heaterNeeded_)
		{
			try 
				{wait();} 
			catch (InterruptedException e) 
				{e.printStackTrace();}
		}
		temperature_+=0.1;
	}
	
	public synchronized float temperatureSensorAction()
	{
		return temperature_;
	}
	
	public synchronized void heaterOn()
	{
		heaterNeeded_=true;
		notifyAll();
	}
	
	public synchronized void heaterOff()
	{
		heaterNeeded_=false;
	}
	
	
	public ReentrantReadWriteLock roomStateLock_;
	public boolean heaterNeeded_=false;
	public float temperature_=14;
}
