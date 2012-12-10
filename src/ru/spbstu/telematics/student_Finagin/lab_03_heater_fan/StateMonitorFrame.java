package ru.spbstu.telematics.student_Finagin.lab_03_heater_fan;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class StateMonitorFrame extends JFrame
{
	public JLabel settingsHeaderLabel_ = new JLabel();
	public JLabel temperatureLabel_ = new JLabel();
	public JLabel temperatureSettingLabel_ = new JLabel();
	private JTextField temperatureSettingInput_ = new JTextField("", 5);
	public JLabel temperatureSettingInputLabel_ = new JLabel();
	
	public JLabel wetnessLabel_ = new JLabel();
	public JLabel wetnessSettingLabel_ = new JLabel();
	private JTextField wetnessSettingInput_ = new JTextField("", 5);
	public JLabel wetnessSettingInputLabel_ = new JLabel();
	
	
	public JLabel heaterOnLabel_ = new JLabel();
	public JLabel fanOnLabel_ = new JLabel();
	
	private JButton temperatureSettingUpdateButton = new JButton("Update");
	private JButton wetnessSettingUpdateButton = new JButton("Update");
	private JButton settingUpdateButton = new JButton("All");
	
	private FloatInputVerifier inputVerifier_ = new FloatInputVerifier();
	
	public RoomController roomCntrl_;
	
	public StateMonitorFrame(RoomController roomCtrl)
	{
		setTitle("Room State Monitor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setSize(400, 200);
		setLayout(null);
		roomCntrl_=roomCtrl;
		settingsHeaderLabel_.setBounds(150, 75, 100, 20);
		settingsHeaderLabel_.setText("..::SETTINGS::..");
		temperatureLabel_.setBounds(50, 10, 130, 20);
		temperatureSettingLabel_.setBounds(180, 10, 200, 20);
		temperatureSettingInput_.setBounds(90, 100, 100, 20); 
		temperatureSettingInputLabel_.setBounds(5, 100, 100, 20);
		temperatureSettingInputLabel_.setText("Temperature: ");
		
		wetnessLabel_.setBounds(50, 30, 130, 20);
		wetnessSettingLabel_.setBounds(180, 30, 200, 20);
		wetnessSettingInput_.setBounds(90, 120, 100, 20);
		wetnessSettingInputLabel_.setBounds(5, 120, 100, 20);
		wetnessSettingInputLabel_.setText("Wetness: ");
		
		temperatureSettingUpdateButton.setBounds(200, 100, 100, 20);
		temperatureSettingUpdateButton.addActionListener(new ButtonEventListener());
		wetnessSettingUpdateButton.setBounds(200, 120, 100, 20);
		wetnessSettingUpdateButton.addActionListener(new ButtonEventListener());
		settingUpdateButton.setBounds(310, 100, 70, 40);
		settingUpdateButton.addActionListener(new ButtonEventListener());
		
		heaterOnLabel_.setBounds(100, 50, 70, 20);
		heaterOnLabel_.setText("Heater ON");
		heaterOnLabel_.setForeground(Color.RED);
		heaterOnLabel_.setVisible(false);
		fanOnLabel_.setBounds(180, 50, 50, 20);
		fanOnLabel_.setText("Fan ON");
		fanOnLabel_.setForeground(Color.BLUE);
		fanOnLabel_.setVisible(false);
		add(settingsHeaderLabel_);
		add(temperatureLabel_);
		add(temperatureSettingLabel_);
		add (temperatureSettingInput_);
		add(temperatureSettingInputLabel_);
		
		add(wetnessLabel_);
		add(wetnessSettingLabel_);
		add(wetnessSettingInput_);
		add(wetnessSettingInputLabel_);
		
		add(temperatureSettingUpdateButton);
		add(wetnessSettingUpdateButton);
		add(settingUpdateButton);
		
		add(heaterOnLabel_);
		add(fanOnLabel_);
		
		temperatureSettingInput_.setInputVerifier(inputVerifier_);
		wetnessSettingInput_.setInputVerifier(inputVerifier_);
	}
	
		/*Верификатор значений*/
	class FloatInputVerifier extends InputVerifier
	{
		@Override 
		public boolean verify(JComponent input) {
             JTextField textField = ((JTextField) input);
             try {
                 Float isFloat = Float.valueOf(textField.getText());
                 textField.setBackground(Color.WHITE);
                 return true;
             } catch (NumberFormatException e) {
                 textField.setBackground(Color.RED);
                 return false;
             }
         }
	}
	
	class ButtonEventListener implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{	// обработчик нажатия кнопок обновления
			if (e.getSource() == temperatureSettingUpdateButton)
			{	// если нажата кнопка обновления настроек температуры 
				if (inputVerifier_.verify(temperatureSettingInput_))
				{	// если значение верифицировано - обновляем
					float newTemperature=Float.valueOf(temperatureSettingInput_.getText());
					roomCntrl_.updateTemperatureSetting(newTemperature);
				}
			}
			if (e.getSource() == wetnessSettingUpdateButton)
			{	// если нажата кнопка обновления настроек влажности
				if (inputVerifier_.verify(wetnessSettingInput_))
				{	// если значение верифицировано - обновляем
					float newWetness=Float.valueOf(wetnessSettingInput_.getText());
					roomCntrl_.updateWetnessSetting(newWetness);
				}
			}
			if (e.getSource() == settingUpdateButton)
			{	// если нажата кнопка обновления всех настроек
				if ((inputVerifier_.verify(temperatureSettingInput_))&&
					(inputVerifier_.verify(wetnessSettingInput_)))
				{	// если значения верифицированы - обновляем
					float newTemperature=Float.valueOf(temperatureSettingInput_.getText());
					float newWetness=Float.valueOf(wetnessSettingInput_.getText());
					roomCntrl_.updateSettings(newTemperature, newWetness);
				}
				
			}
		}
	}
}