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

import javax.net.ssl.SSLSocket;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.xml.crypto.Data;

public class SignUpView extends JPanel
{
	static private SignUpView instance = null;
	private Color colorBackground = new Color(0X11 ,0X11, 0X11);
	
	private JTextField inputId;
	private JPasswordField inputPassword;
	private JPasswordField inputRecheckPassword;
	
	private JButton btnCheckIdOverlap;
	private JButton btnSignUp;
	private JLabel labelState;
	private SignUpView()
	{
		this.setBounds(0,0,540,960);
		this.setLayout(null); 
		
		labelState = new JLabel();
		labelState.setBounds(10, 10, 300, 40);
		labelState.setForeground(new Color(0xff,0x66,0x66));
		this.add(labelState);
		
		inputId = new JTextField();
		inputId.setBounds(150,300,240,70);
		inputId.setBorder(new EmptyBorder(0, 0, 0, 0));
		inputId.setFont(new Font("¸¼Àº °íµñ" , Font.PLAIN , 30));
		this.add(inputId);
		
		inputPassword = new JPasswordField();
		inputPassword.setBounds(150,400,340,70);
		inputPassword.setBorder(new EmptyBorder(0, 0, 0, 0));
		inputPassword.setFont(new Font("¸¼Àº °íµñ" , Font.PLAIN , 30));
		this.add(inputPassword);
		
		inputRecheckPassword = new JPasswordField();
		inputRecheckPassword.setBounds(150,500,340,70);
		inputRecheckPassword.setBorder(new EmptyBorder(0, 0, 0, 0));
		inputRecheckPassword.setFont(new Font("¸¼Àº °íµñ" , Font.PLAIN , 30));
		this.add(inputRecheckPassword);
		
		btnCheckIdOverlap = new JButton();
		btnCheckIdOverlap.setBounds(390, 300 ,100, 70);
		btnCheckIdOverlap.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnCheckIdOverlap.setFont(new Font("¸¼Àº °íµñ" , Font.PLAIN , 30));
		btnCheckIdOverlap.setBackground(new Color(0xFF,0xCC,0x44));
		btnCheckIdOverlap.setText("°Ë»ç");
		btnCheckIdOverlap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					SSLSocket socket = DataBase.getDataBase().connectToLoginServer();
					BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					output.write("Check"+":"+inputId.getText());
					output.newLine();
					output.flush();
					String getLine;
					if((getLine = input.readLine()) == null);
					if(getLine.equals("1"))
						labelState.setText("Áßº¹ÀÔ´Ï´Ù.");
					else if(getLine.equals("0"))
						labelState.setText("Áßº¹ÀÌ ¾Æ´Õ´Ï´Ù.");
							
							
				} 
				catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		this.add(btnCheckIdOverlap);
		
		btnSignUp = new JButton();
		btnSignUp.setBounds(50,750 ,440,120);
		btnSignUp.setFont(new Font("¸¼Àº °íµñ" , Font.PLAIN , 40));
		btnSignUp.setBackground(new Color(0xFF,0xCC,0x44));
		btnSignUp.setText("È¸¿ø °¡ÀÔ");
		btnSignUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(!inputPassword.getText().equals(inputRecheckPassword.getText()))
					labelState.setText("ºñ¹Ð¹øÈ£¸¦ ´Ù½Ã È®ÀÎÇØÁÖ¼¼¿ä!");
				else
				{
					try 
					{
						SSLSocket socket = DataBase.getDataBase().connectToLoginServer();
						BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						output.write("SignUp"+":"+inputId.getText()+":"+inputPassword.getText());
						output.newLine();
						output.flush();
						String getLine;
						if((getLine = input.readLine()) == null);
						if(getLine.equals("1"))
							MainFrame.getInstance().changeView(LoginView.getInstance());
						else if(getLine.equals("0"))
							labelState.setText("È¸¿ø°¡ÀÔ¿¡ ½ÇÆÐÇÏ¿³½À´Ï´Ù.");
								
								
					} 
					catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		this.add(btnSignUp);
	}
	static public SignUpView getInstance()
	{
		if(instance == null)
			instance = new SignUpView();
		return instance;
	}
	
	public void paint(Graphics g)
	{
		g.setColor(colorBackground);
		g.fillRect(0, 0, this.getWidth(),this.getHeight());
		
		g.setColor(Color.white);
		g.setFont(new Font("¸¼Àº °íµñ" ,Font.PLAIN , 100));
		//g.drawString("È¸¿ø °¡ÀÔ",50,100);
		
		g.setFont(new Font("¸¼Àº °íµñ" ,Font.PLAIN , 30));
		g.drawString("¾ÆÀÌµð" , 20,345);
		g.setFont(new Font("¸¼Àº °íµñ" ,Font.PLAIN , 30));
		g.drawString("ºñ¹Ð¹øÈ£" , 20,445);
		g.setFont(new Font("¸¼Àº °íµñ" ,Font.PLAIN , 30));
		g.drawString("ºñ¹Ð¹øÈ£" ,20,530);
		g.drawString("È®ÀÎ" ,20,570);
		this.paintComponents(g);
	}
	
}

