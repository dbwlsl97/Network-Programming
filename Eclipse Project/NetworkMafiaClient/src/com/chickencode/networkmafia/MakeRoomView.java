package com.chickencode.networkmafia;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MakeRoomView extends JPanel
{
	private static MakeRoomView instance = null;
	static public MakeRoomView getInstance()	//SingleTon
	{
		if(instance == null)
			instance = new MakeRoomView();
		return instance;
	}
	
	JLabel labelState;
	JTextField inputName;
	JPasswordField inputPassword;
	JButton btnMakeRoom;
	private MakeRoomView()
	{
		this.setBounds(0,0,540,960);
		this.setBackground(new Color(0x22,0x22,0x22));
		
		
		inputName = new JTextField();
		inputName.setBounds(50, 230, 440, 70);
		inputName.setBorder(new EmptyBorder(0, 0, 0, 0));
		inputName.setFont(new Font("∏º¿∫ ∞ÌµÒ" , Font.PLAIN , 35));
		this.add(inputName);
		
		labelState = new JLabel();
		labelState.setBounds(10,10,300,50);
		labelState.setForeground(new Color(0xff,0x66,0x66));
		this.add(labelState);
		
		btnMakeRoom = new JButton();
		btnMakeRoom.setBounds(50, 600,440, 100);
		btnMakeRoom.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnMakeRoom.setFont(new Font("∏º¿∫ ∞ÌµÒ" , Font.PLAIN , 30));
		btnMakeRoom.setBackground(new Color(0xFF,0xCC,0x44));
		btnMakeRoom.setText("πÊ ∏∏µÈ±‚");
		btnMakeRoom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
				
					Socket client = DataBase.getDataBase().connectToLobbyServer();
					BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
					BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					String info = null;
					output.write("makeroom:" +  inputName.getText());
					output.newLine();
					output.flush();
					while((info = input.readLine()) == null);
					String args[] = info.split(":");
					System.out.println("πﬁ¿∫ ¡§∫∏ : " + info);
					if(args[0].equals("1"))
					{
						GameRoomView.getInstance().setPort(Integer.parseInt(args[1]));
						MainFrame.getInstance().changeView(GameRoomView.getInstance());
						GameRoomView.getInstance().game.initGame();
					}
					else if(args[0].equals("0"))
					{
						labelState.setText("πÊ∏∏µÈ±‚ Ω«∆–¿‘¥œ¥Ÿ");
					}
					
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		this.add(btnMakeRoom);
	}
	public void paint(Graphics g)
	{
		super.paint(g);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("∏º¿∫ ∞ÌµÒ" , Font.PLAIN , 40));
		g.drawString("πÊ ¿Ã∏ß" ,200, 190);
		
	}
}
