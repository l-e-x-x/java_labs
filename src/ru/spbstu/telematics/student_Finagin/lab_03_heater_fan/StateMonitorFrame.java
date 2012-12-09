package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class StateMonitorFrame extends JFrame
{
	public StateMonitorFrame()
	{
		setTitle("Room State Monitor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
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