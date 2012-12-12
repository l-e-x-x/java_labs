package ru.spbstu.telematics.student_Finagin.lab_04_network_chat_client;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import sun.misc.Cleaner;

public class ClientGUI
{
	private Font font = new Font("Helvetica", Font.PLAIN, 11);
	private ChatClient chatClient_;
	public RegistrationFrame registrationFrame_ = new RegistrationFrame();
	public MainChatFrame mainChatFrame_ = new MainChatFrame();
	
	public ClientGUI(ChatClient client)
		{chatClient_=client;}
	
	class RegistrationFrame extends JFrame
	{
		public RegistrationFrame()
		{
			setTitle("Java Chat Room [registration]");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	
		private JLabel registrationInvitationLabel_ = new JLabel();
		private JLabel registrationFailLabel_ = new JLabel();
		private JTextField registrationNicknameInput_ = new JTextField();
		private JButton registrationOkButton_ = new JButton("Ok");
		
		public void initRegistrationLayout()
		{
			setResizable(false);
			setBounds(100, 100, 500, 100);
			setLayout(null);
			registrationInvitationLabel_.setBounds(40, 20, 150, 20);
			registrationInvitationLabel_.setFont(font);
			registrationInvitationLabel_.setText("Enter your Nickname: ");
			
			registrationFailLabel_.setBounds(170, 45, 200, 20);
			registrationFailLabel_.setText("This nick is in use");
			registrationFailLabel_.setFont(font);
			registrationFailLabel_.setForeground(Color.RED);
			registrationFailLabel_.setVisible(false);
			
			registrationNicknameInput_.setBounds(170, 20, 200, 20);
			registrationNicknameInput_.setFont(font);
			registrationNicknameInput_.setToolTipText("Enter nick here");
			registrationNicknameInput_.addKeyListener(new KeyboardEventAdapter());
			
			registrationOkButton_.setBounds(390, 20, 70, 20);
			registrationOkButton_.setFont(font);
			registrationOkButton_.addActionListener(new ButtonEventListener());
					
			add(registrationInvitationLabel_);
			add(registrationFailLabel_);
			add(registrationNicknameInput_);
			add(registrationOkButton_);
			setVisible(true);
		}
		
		private void registerFailAction()
		{	// я твой окно шатал xD
			registrationFailLabel_.setVisible(true);
			Point startPosition=getLocationOnScreen();
			int yPosition=startPosition.y;
			int amplitude=25;
			while (amplitude > 0)
			{
				while (getLocationOnScreen().x < startPosition.x+amplitude)
				{
					setLocation(getLocationOnScreen().x+5, yPosition);
					try
						{Thread.sleep(8);}
					catch (InterruptedException e)
						{e.printStackTrace();}
				}
				while (getLocationOnScreen().x > startPosition.x-amplitude)
				{
					setLocation(getLocationOnScreen().x-5, yPosition);
					try
						{Thread.sleep(8);}
					catch (InterruptedException e)
						{e.printStackTrace();}
				}
				amplitude-=5;
			}
		}
		
		private void registerAttempt()
		{
			String nickname=registrationNicknameInput_.getText();
			if (nickname.isEmpty())
				 registrationNicknameInput_.setBackground(Color.RED);
			else
			{
				if (!chatClient_.registerInChat(nickname))
					registerFailAction();	// шатаем форму, если неуспешна рега
				else
				{	// Если все ok - рушим форму и открываем окно чата и создаем поток для чтения 
					ClientGUI.this.registrationFrame_.dispose();
					ClientGUI.this.mainChatFrame_.initMainChatLayout();
					new Thread(chatClient_).start();
				}
			} // endElse
		}
		
		private class ButtonEventListener implements ActionListener 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{	// обработчик нажатия кнопок
				if (e.getSource() == registrationOkButton_)
					registerAttempt();	
			}
		}
		
		private class KeyboardEventAdapter extends KeyAdapter
		{
			public void keyPressed(KeyEvent event)
			{
				if (event.getKeyCode() == event.VK_ENTER)
					registerAttempt();
			}	
		}
	}
	
	class MainChatFrame extends JFrame
	{
		public JTextArea mainChatMessagesArea_= new JTextArea();
		public JTextArea mainChatOutMessageArea_ = new JTextArea();
		
		public MainChatFrame()
		{
			setTitle("Java Chat Room");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		public void initMainChatLayout()
		{
			setResizable(true);
			setBounds(100, 100, 800, 600);
			setLayout(new GridLayout(2, 1));
		
			mainChatMessagesArea_.setBorder(BorderFactory.createBevelBorder(0));
			mainChatMessagesArea_.setEditable(false);
			
			mainChatOutMessageArea_ = new JTextArea();
			mainChatOutMessageArea_.addKeyListener(new KeyboardEventAdapter());
			mainChatOutMessageArea_.setBorder(BorderFactory.createBevelBorder(0));
			
			add (mainChatOutMessageArea_);
			add (mainChatMessagesArea_);
				
			JScrollPane chatMsgScroller = new JScrollPane(mainChatMessagesArea_);
			JScrollPane outMsgScroller = new JScrollPane(mainChatOutMessageArea_);
			
			outMsgScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			chatMsgScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			add(chatMsgScroller);
			add(outMsgScroller);
			setVisible(true);
		}
	
	
		private class KeyboardEventAdapter extends KeyAdapter
		{
			public void keyPressed(KeyEvent event)
			{
				if (event.getKeyCode() == event.VK_ENTER)
				{
					if (event.isControlDown())
						mainChatOutMessageArea_.append("\n");
					else
					{
						chatClient_.sendMessage(mainChatOutMessageArea_.getText());
						mainChatOutMessageArea_.setText("");
					}
					event.consume();
				}
			}	
		}
	}	// MainChatFrameEnd
	
}


