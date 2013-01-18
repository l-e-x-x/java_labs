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


public class ClientGUI
{
	private Font font = new Font("Helvetica", Font.PLAIN, 11);
	private ChatClient chatClient_;
	
	public ConnectFrame connectionFrame_ = new ConnectFrame();
	public RegistrationFrame registrationFrame_ = new RegistrationFrame();
	public MainChatFrame mainChatFrame_ = new MainChatFrame();
	
	public ClientGUI(ChatClient client)
		{chatClient_=client;}
	
	class ConnectFrame extends JFrame
	{
		public ConnectFrame()
		{
			setResizable(false);
			setBounds(100, 100, 500, 100);
			setLayout(null);
			setTitle("Java Chat Room by [LeXX] .:disconnected:.");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	
		private JLabel serverAddressLabel_ = new JLabel();
		private JTextField serverAddressInput_ = new JTextField();
		private JButton connectButton_ = new JButton("Connect");
		private JLabel connectionFailLabel_ = new JLabel();
		public String connectFailMsg_ = new String();
		
		public void initConnectionLayout()
		{
			serverAddressLabel_.setBounds(20, 20, 150, 20);
			serverAddressLabel_.setFont(font);
			serverAddressLabel_.setText("Enter server address: ");
			
			connectionFailLabel_.setBounds(120, 45, 250, 20);
			connectionFailLabel_.setText("Could not connect to server");
			connectionFailLabel_.setFont(font);
			connectionFailLabel_.setForeground(Color.RED);
			connectionFailLabel_.setVisible(false);
			
			serverAddressInput_.setBounds(150, 20, 200, 20);
			serverAddressInput_.setFont(font);
			serverAddressInput_.setText("localhost");
			serverAddressInput_.setToolTipText("Enter address here");
			serverAddressInput_.addKeyListener(new KeyboardEventAdapter());
			
			connectButton_.setBounds(370, 20, 100, 20);
			connectButton_.setFont(font);
			connectButton_.addActionListener(new ButtonEventListener());
					
			add(serverAddressLabel_);
			add(connectionFailLabel_);
			add(serverAddressInput_);
			add(connectButton_);
			setVisible(true);
		}
		
		private void connectAttempt()
		{
			String serverAddress=serverAddressInput_.getText();
			if (serverAddress.isEmpty())
				 serverAddressInput_.setBackground(Color.RED);
			else
			{
				serverAddressInput_.setBackground(Color.WHITE);
				if (!chatClient_.clientConnectionInit(serverAddressInput_.getText()))
				{	// если коннект не удался - выставляем мессагу
					connectionFailLabel_.setText(connectFailMsg_);
					connectionFailLabel_.setVisible(true);
					frameFailAction(this);	// шатаем форму
				}
				else
					chatClient_.notifyMainThread(); // Если все ok - пробуждаем основной поток
			} // endElse
		}
		
		private class ButtonEventListener implements ActionListener 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{	// обработчик нажатия кнопок
				if (e.getSource() == connectButton_)
					connectAttempt();	
			}
		}
		
		private class KeyboardEventAdapter extends KeyAdapter
		{
			public void keyPressed(KeyEvent event)
			{
				if (event.getKeyCode() == event.VK_ENTER)
					connectAttempt();
			}	
		}
		
		public void setConnectionFailMsg(String failMsg)
			{connectionFailLabel_.setText(failMsg);}
		
	}
	
	class RegistrationFrame extends JFrame
	{
		public RegistrationFrame()
		{
			setResizable(false);
			setBounds(100, 100, 500, 100);
			setLayout(null);
			setTitle("Java Chat Room by [LeXX] .:registration:.");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	
		private JLabel registrationInvitationLabel_ = new JLabel();
		private JLabel registrationFailLabel_ = new JLabel();
		private JTextField registrationNicknameInput_ = new JTextField();
		private JButton registrationOkButton_ = new JButton("Ok");
		public String registrationFailMsg_ = new String();
		
		public void initRegistrationLayout()
		{
			registrationInvitationLabel_.setBounds(40, 20, 150, 20);
			registrationInvitationLabel_.setFont(font);
			registrationInvitationLabel_.setText("Enter your Nickname: ");
			
			registrationFailLabel_.setBounds(170, 45, 200, 20);
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
		
		private void registerAttempt()
		{
			String nickname=registrationNicknameInput_.getText();
			if (nickname.isEmpty())
				 registrationNicknameInput_.setBackground(Color.RED);
			else
			{
				registrationNicknameInput_.setBackground(Color.WHITE);
				if (!chatClient_.registerInChat(nickname))
				{
					registrationFailLabel_.setText(registrationFailMsg_);
					registrationFailLabel_.setVisible(true);
					frameFailAction(this);	// шатаем форму, если неуспешна рега
				}
				else
					chatClient_.notifyMainThread(); // Если все ok - пробуждаем основной поток
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
			setResizable(true);
			setBounds(100, 100, 600, 300);
			setLayout(new GridLayout(2, 1));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		public void initMainChatLayout()
		{
			setTitle("Java Chat Room by [LeXX] .:connected as "+chatClient_.nickName_+":.");
			
			mainChatMessagesArea_.setBorder(BorderFactory.createBevelBorder(0));
			mainChatMessagesArea_.setEditable(false);
			
			mainChatOutMessageArea_ = new JTextArea();
			mainChatOutMessageArea_.addKeyListener(new KeyboardEventAdapter());
			mainChatOutMessageArea_.setBorder(BorderFactory.createBevelBorder(0));
			mainChatOutMessageArea_.setToolTipText("Send message: [Enter]; New line: [Ctrl]+[Enter]");
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
						if (!mainChatOutMessageArea_.getText().isEmpty())
						{
							chatClient_.sendMessage(mainChatOutMessageArea_.getText());
							mainChatOutMessageArea_.setText("");
						}
					}
					event.consume();
				}
			}	
		}
	}	// MainChatFrameEnd
	
	private void frameFailAction(JFrame frame)
	{	// я твой окно шатал xD
		
		Point startPosition=frame.getLocationOnScreen();
		int yPosition=startPosition.y;
		int amplitude=25;
		while (amplitude > 0)
		{
			while (frame.getLocationOnScreen().x < startPosition.x+amplitude)
			{
				frame.setLocation(frame.getLocationOnScreen().x+5, yPosition);
				try
					{Thread.sleep(8);}
				catch (InterruptedException e)
					{e.printStackTrace();}
			}
			while (frame.getLocationOnScreen().x > startPosition.x-amplitude)
			{
				frame.setLocation(frame.getLocationOnScreen().x-5, yPosition);
				try
					{Thread.sleep(8);}
				catch (InterruptedException e)
					{e.printStackTrace();}
			}
			amplitude-=5;
		}
	}
	
	
}


