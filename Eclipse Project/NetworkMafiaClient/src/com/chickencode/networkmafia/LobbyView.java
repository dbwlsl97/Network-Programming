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
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.omg.Messaging.SyncScopeHelper;

public class LobbyView extends JPanel
{
	private static LobbyView instance;
	private static boolean haveInstance = false;
	private JPanel roomPane;
	private JButton btnPrev;
	private JButton btnNext;
	private JButton btnRefresh;	//����
	private JButton btnMakeRoom;	//�游���
	private JLabel labelState;
	private ArrayList<Room> roomList;
	private int page = 0;
	public static LobbyView getInstance()
	{
		if(!haveInstance)
		{
			haveInstance = true;
			instance = new LobbyView();
		}
		return instance;
	}
	
	
	private LobbyView()
	{
		this.setBounds(0, 0, 540, 960);
		this.setBackground(new Color(0X11 ,0X11, 0X11));
		roomList = new ArrayList<Room>();
		roomPane = new JPanel();
		roomPane.setBounds(0,200,560,660);
		roomPane.setBackground(new Color(0x33,0x33,0x33));
		this.add(roomPane);
		
		labelState = new JLabel();
		labelState.setForeground(new Color(0xff,0x66,0x66));
		labelState.setBounds(10,10,300,50);
		this.add(labelState);
		

		btnPrev = new JButton();
		btnPrev.setBackground(new Color(0xff,0xff,0xaa));
		btnPrev.setBounds(0, 860, 270, 100);
		btnPrev.setBorder(new EmptyBorder(0,0,0,0));
		btnPrev.setFont(new Font("���� ���" , Font.PLAIN , 100));
		btnPrev.setText("<");
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				page = Math.max(0,page - 1);
				refreshLobby();
			}
		});
		this.add(btnPrev);
		

		btnNext = new JButton();
		btnNext.setBackground(new Color(0xff,0xff,0xaa));
		btnNext.setBounds(270, 860, 270, 100);
		btnNext.setBorder(new EmptyBorder(0,0,0,0));
		btnNext.setFont(new Font("���� ���" , Font.PLAIN , 100));
		btnNext.setText(">");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				page = Math.min((roomList.size() - 1) / 6,page + 1);
				refreshLobby();
			}
		});
		this.add(btnNext);
		
		btnRefresh = new JButton();
		btnRefresh.setBackground(new Color(0xff,0x77,0x77));
		btnRefresh.setBounds(0, 100, 270, 100);
		btnRefresh.setBorder(new EmptyBorder(0,0,0,0));
		btnRefresh.setFont(new Font("���� ���" , Font.PLAIN , 40));
		btnRefresh.setText("���� ��ħ");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					Socket client = DataBase.getDataBase().connectToLobbyServer();
					BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
					BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					output.write("refresh");
					output.newLine();
					output.flush();
					String info;
					while((info = input.readLine()) == null);
					roomList = new ArrayList<>();
					String[] args = info.split(":");
					int len = (args.length - 1) / 4;
					System.out.println("[lobbyview] : " + info);
					for(int i = 0; i < len; i++)
					{
						int roomId = Integer.parseInt(args[i * 4 + 1]);
						String roomName = args[i * 4 + 2];
						int port = Integer.parseInt(args[i * 4 + 3]);
						int playerNumber = Integer.parseInt(args[i * 4 + 4]);
					    roomList.add(new Room(roomId , roomName , "port : "+ port , playerNumber));
					}
					refreshLobby();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		this.add(btnRefresh);
		
		btnMakeRoom = new JButton();
		btnMakeRoom.setBackground(new Color(0xff,0x77,0x77));
		btnMakeRoom.setBounds(270, 100, 270, 100);
		btnMakeRoom.setBorder(new EmptyBorder(0,0,0,0));
		btnMakeRoom.setFont(new Font("���� ���" , Font.PLAIN , 40));
		btnMakeRoom.setText("�� �����");
		btnMakeRoom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.getInstance().changeView(MakeRoomView.getInstance());
			}
		});
		this.add(btnMakeRoom);
		refreshLobby();
	}
	public void addLobby(Room lobby)
	{
		roomList.add(lobby);
	}
	/*
	 * 
	 * �������� �κ� ���� �ҷ����� �Լ� �߰�
	 */
	public void refreshLobby()	// �κ�� ���� �ʱ�ȭ	��Ʈ��ũ �䱸
	{
		if((roomList.size() - 1) / 6 < page)
			page = Math.max(0, (roomList.size() - 1) / 6);
		System.out.println(page);
		roomPane.removeAll();
		for(int i = page * 6; i < Math.min(roomList.size(),(page + 1) * 6); i++)
		{
			Room room = roomList.get(i);
			JButton button = new JButton()
			{
				public void paint(Graphics g)
				{
					super.paint(g);
					g.setColor(Color.white);
					g.setFont(new Font("���� ���" , Font.PLAIN , 17));
					g.drawString(room.getName(), 10, 30);
					g.drawString(room.getOwner(), 10,70);
					g.drawString(room.getNowNumber() + " / " + room.getMaxNumber(), 400, 70);
				}
			};
			button.setBounds(0,i % 6 * 110, 540, 110);
			button.setBorder(new EmptyBorder(0, 0, 0, 0));
			button.setBackground(new Color(0x22,0x22,0x22));
			button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0) 
				{
					try
					{
						Socket client = DataBase.getDataBase().connectToLobbyServer();
						BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
						BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
						output.write("join:" + room.getId());
						output.newLine();
						output.flush();
						String info;
						while((info = input.readLine()) == null);
						String args[] = info.split(":");
						if(info.startsWith("1"))
						{
							int port = Integer.parseInt(args[1]);
							//gameView�� port ���� �����
							GameRoomView.getInstance().setPort(port);
							MainFrame.getInstance().changeView(GameRoomView.getInstance());
							GameRoomView.getInstance().game.initGame();
						}
						else if(info.startsWith("2"))
						{
							labelState.setText("�÷��̾ ��á���ϴ�.");
						}
						else if(info.startsWith("3"))
						{
							labelState.setText("���� Error");
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
			});
			
			roomPane.add(button);
		}
		roomPane.repaint();
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(new Color(0xdd,0x55,0x55));
		g.setFont(new Font("���� ���" , Font.BOLD , 77));
		g.drawString("LOBBY", 140 , 77);
	}
	
}
class Room
{
	private int id;
	private String name;
	private int nowNumber;
	private int maxNumber = 8;
	private String owner;           
	Room(int id ,String name,String owner , int nowNumber)
	{
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.nowNumber = nowNumber;
		//this.maxNumber = maxNumber;
	}
	public int getId()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
	public int getNowNumber()
	{
		return nowNumber;
	}
	public int getMaxNumber()
	{
		return maxNumber;
	}
	public String getOwner()
	{
		return owner;
	}
}