package com.chickencode.networkmafia;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.View;

public class MainFrame extends JFrame
{
	private JPanel visiableView;
	private static MainFrame instance = null;
	
	static public MainFrame getInstance()
	{
		if(instance == null)
			instance = new MainFrame();
		return instance;
	}
	private MainFrame()
	{
		this.setResizable(false);
		this.setSize(540, 989);
		this.getContentPane().setBounds(0,0,540,960);
		this.getContentPane().setLayout(null);
		this.setVisible(true);
		visiableView = LoginView.getInstance();
		this.add(visiableView);
		this.repaint();
	}
	public void changeView(JPanel view)
	{
		 this.remove(visiableView);
		 visiableView = view;
		 this.add(view);
		 this.repaint();
	}
}
 