package com.chickencode.networkmafia;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GameRoomView extends JPanel
{;
	static private GameRoomView instance = null;
	static public GameRoomView getInstance()	//SingleTon
	{
		if(instance == null)
			instance = new GameRoomView();
		return instance;
	}
	Game game = new Game();
	JButton btnExit;
	JButton btnStartGame;
	JTextField inputChat;
	JList listChat;
	JScrollPane scrollChat;
	DefaultListModel<String> listChatModel;
	/* ���� ��� ��ư �߰� */
	
	/*
	 * Protocol
	 * 
	 * �ޱ�
	 * 
		 * ó���� ���� ���� : job : ���� :[���Ǿ��ϰ�� �� number]
		 * ä�� : chat : number :  content
		 * ���� ���� : die : number
		 * �������� : prove : number : yesorno
		 * �÷��̾� ���� playerinfo : id[�迭]	//������� number
		 * ���ӽ��� : start
		 * ���ӳ� end: victory or lose
		 * �ð� time : [1=��ħ,2=��ǥ�ð�,3=����]
		 * 
		 * ������
		 * ä�� chat : content
		 * ��ǥ vote : number
		 * ���� ���� : select : number
		 * �÷��̾� ���� : playerinfo:id
		 * �������� close
	 * 
	 * 
	 */
	private GameRoomView()
	{
		
		this.setBackground(new Color(0x22,0x22,0x22));
		this.setBounds(0,0,540,960);
		
		btnExit = new JButton("������");
		btnExit.setBackground(new Color(0xff,0x44,0x44));
		btnExit.setBorder(new EmptyBorder(0,0,0,0));
		btnExit.setFont(new Font("���� ���" , Font.PLAIN , 40)); 
		btnExit.setBounds(0,860,540,100);
		btnExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					/*
					 * ���� : exit : id
					 */
					game.output.write("close");
					game.output.newLine();
					game.output.flush();
					ByteBuffer buf = ByteBuffer.allocate(1024);;
					buf.put(("exit:" + DataBase.getDataBase().getId()).getBytes());
					buf.flip();
					game.chatChannel.write(buf);
					game.client.close();
					game.chatChannel.close();
					game.client = null;
					game.connect = false;
					MainFrame.getInstance().changeView(LobbyView.getInstance());
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
				
			}
		});
		this.add(btnExit);
		/*
		btnStartGame = new JButton("���� ����");		//������ �ƴϰų� �������̸� ȸ������ �ٲ�� �ϱ�
		btnStartGame.setBackground(new Color(0xff,0xff,0x44));
		btnStartGame.setBorder(new EmptyBorder(0,0,0,0));
		btnStartGame.setBounds(0,860,270,100);
		btnStartGame.setFont(new Font("���� ���" , Font.PLAIN , 40)); 
		this.add(btnStartGame);*/
		

		listChatModel = new DefaultListModel<>();
		listChat = new JList(listChatModel);
		listChat.setBackground(new Color(0x22,0x22,0x22));
		//listChat.setBorder(new EmptyBorder(0,0,0,0));
		listChat.setForeground(Color.white);
		//listChat.setFont(new Font("���� ���" , Font.PLAIN , 12));
		//listChat.setBounds(0,560,540,230);
		
		scrollChat  = new JScrollPane();
		scrollChat.setBorder(new LineBorder(new Color(0x33,0x33,0x33),2));
		scrollChat.setViewportView(listChat);
		scrollChat.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //���ι���å
		scrollChat.setBounds(0,560,540,230);
		this.add(scrollChat);
		
		inputChat = new JTextField();
		inputChat.setBackground(new Color(0x22,0x22,0x22));
		inputChat.setForeground(Color.white);
		inputChat.setBounds(70, 790,540, 70);
		inputChat.setBorder(new EmptyBorder(0, 00, 0, 0));
		inputChat.setFont(new Font("���� ���" , Font.PLAIN , 25));
		inputChat.addKeyListener(new KeyListener() {
			
			ByteBuffer buf = ByteBuffer.allocate(1024);
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if(inputChat.getText().equals(""))
						return;
					try
					{
						buf.clear();
						buf.put(("chat:"+DataBase.getDataBase().getId() + ":"+inputChat.getText()).getBytes());
						buf.flip();
						game.chatChannel.write(buf);
						inputChat.setText("");
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		this.add(inputChat);
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int number = 0;
				int x = e.getX();
				int y = e.getY();
				if(405 <= x && x <= 505)
					number += 4;
				if(y >= 17 && ((y - 17) % 136) <= 100)
				{
					number += (y - 17) / 136;
				}
				game.clickNumber(number);
			}
		});
	}
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(new Color(0xFF,0xCC,0x44));
		g.fillRect(0, 790, 70, 70);
		g.drawImage(DataBase.getDataBase().getImage("img_chat"),10,800,50,50,this);
		
		for(int i  = 0; i < Math.min(4, game.players.size()); i++)
		{
			if(!game.players.get(i).alive)
				continue;
			//g.fillRect(16, i * 136 + 16, 120, 120);
			g.drawString(""+i + "��", 127 , i * 136 + 17);
			g.drawString(game.players.get(i).id, 17 , i * 136 + 130);
			g.drawImage(DataBase.getDataBase().getImage("img_person"),17,i * 136 + 17 , 100 , 100,this);
		}
		for(int i  = 0; i < Math.min(4 , game.players.size() - 4); i++)
		{
			if(!game.players.get(i + 4).alive)
				continue;
			//g.fillRect(404, i * 136 + 16, 120, 120);
			g.drawString(""+(i + 4) + "��", 305 , i * 136 + 17);
			g.drawString(game.players.get(i + 4).id,405 , i * 136 + 130);
			g.drawImage(DataBase.getDataBase().getImage("img_person"),405,i * 136 + 17 , 100 , 100,this);
		}
		
		/*���߿� ���̵� �߰� */
	}
	public void setPort(int port)
	{
		this.game.port = port;
	}
	class Game
	{
		int myJob;		//[1 : �ù� 	2: ���Ǿ�   3: ����
		int time;	//[1 : �� 2 : ��ǥ�ð�  3 : ��]
		int port;
		boolean connect = true;
		ArrayList<PlayerData> players = new ArrayList<>();
		SocketChannel chatChannel = null;
		Selector selector;
		Socket client = null;
		BufferedReader input;
		BufferedWriter output;
		public void initGame()
		{
			connect = true;
			if(client == null)
			{
				try 
				{
					selector = Selector.open();
					listChatModel.clear();
					chatChannel =  SocketChannel.open(new InetSocketAddress(DataBase.getDataBase().getIP(),game.port + 1));
					chatChannel.configureBlocking(false);
					chatChannel.register(selector, SelectionKey.OP_READ);
					new Thread(new ChatThread()).start();
					client = new Socket(DataBase.getDataBase().getIP() , port);
					input = new BufferedReader(new InputStreamReader(client.getInputStream()));
					output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					output.write("playerinfo:" + DataBase.getDataBase().getId());
					output.newLine();
					output.flush();
					listChatModel.addElement("���ӿ� �����ϽŰ��� ȯ���մϴ�.");
					new Thread(new GameThread()).start();
				}catch(Exception e)
				{
					e.printStackTrace();
				} 
			}
		}
		public void clickNumber(int number)
		{
			try
			{
				if(time == 1)	// �ൿ ����
				{
					
				}
				else if(time == 2)
				{
					output.write("vote:" + number);
					output.newLine();
					output.flush();
				}
				else if(time == 3)
				{
					if(myJob != 0)	// ���Ǿ� �� ����
					{
						output.write("select:" + number);
						output.newLine();
						output.flush();
					}
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		class GameThread implements Runnable
		{
			public void run()
			{
				try
				{
					while(connect)
					{
						String info;
						while((info = input.readLine()) == null);
						String args[] = info.split(":");
						System.out.println("[GameRoomView] ���� ���� : " + info);
						if(info.startsWith("job"))
						{
							myJob = Integer.parseInt(args[1]);
							if(myJob == 1)
							{
								listChatModel.addElement("����� �ù��Դϴ�.��� ���ǾƸ� ���̼���");
							}
							else if(myJob == 2)
							{
								String teamId = players.get(Integer.parseInt(args[2])).id;
								listChatModel.addElement("����� " + teamId + "�� �Բ� ���Ǿ��Դϴ�");
								listChatModel.addElement("��� �ùε��� ���̼���");
							}
							else if(myJob == 3)
							{
								listChatModel.addElement("����� �����Դϴ�.��� ���ǾƸ� ���̼���");
							}
						}
						else if(info.startsWith("die"))
						{
							int number = Integer.parseInt(args[1]);
							players.get(number).alive = false;
							listChatModel.addElement(players.get(number).id + " ���� ����Ͽ����ϴ�.");
						}
						else if(info.startsWith("prove"))
						{
							int number = Integer.parseInt(args[1]);
							boolean isSimin = args[2].equals("0");
							if(isSimin)
								listChatModel.addElement(players.get(number).id + "�����ù��Դϴ�.");
							else
								listChatModel.addElement(players.get(number).id + "�������Ǿ��Դϴ�.");
						}
						else if(info.startsWith("playerinfo"))	//number ���������
						{
							int len = (args.length - 1);
							players.clear();
							for(int i = 0; i < len; i++)
							{
								String id = args[i + 1];
								players.add(new PlayerData(i, id));
							}
						}
						else if(info.startsWith("time"))
						{
							int newTime = Integer.parseInt(args[1]);
							time = newTime;
							if(time == 1)
								listChatModel.addElement("��ħ�� �Ǿ����ϴ�.");
							else if(time == 2)
								listChatModel.addElement("��ǥ �ð��� �Ǿ����ϴ�.");
							else if(time == 3)
								listChatModel.addElement("������ �Ǿ����ϴ�.");
						}
						else if(info.startsWith("start"))
						{
							for(int i = 0; i < game.players.size(); i++)
								game.players.get(i).alive = true;
							listChatModel.clear();
							listChatModel.addElement("������ ���۵Ǿ����ϴ�.");
						}
						else if(info.startsWith("end"))
						{
							boolean victory = args[1].equals("1");
							if(victory)
								listChatModel.addElement("���ӿ��� �¸��Ͽ����ϴ�");
							else
								listChatModel.addElement("���ӿ��� �й��Ͽ����ϴ�");
						}
						MainFrame.getInstance().repaint();
						scrollChat.getVerticalScrollBar().setValue(scrollChat.getVerticalScrollBar().getMaximum());
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	class PlayerData
	{
		int number;
		String id;
		boolean alive = true;
		PlayerData(int nubmer , String id)
		{
			this.number = number;
			this.id = id;
		}
	}
	class ChatThread implements Runnable
	{
		CharsetDecoder decoder;
		ChatThread()
		{
			try
			{
				decoder = Charset.forName("KSC5601").newDecoder();
			}
			catch(Exception e)
			{
				
			}
		}
		public void run() 
		{
			String message = null;
			String[] receivedMsg = null;
			while(game.connect)
			{
				try
				{
					game.selector.select(3000);
					Iterator iterator = game.selector.selectedKeys().iterator();
					while(iterator.hasNext())
					{
						SelectionKey key = (SelectionKey)iterator.next();
						if(key.isReadable())
						{
							message = read(key);
							System.out.println("[Chat] : " + message);
							String args[] = message.split(":");
							if(args[0].equals("chat"))
							{
								String id = args[1];
								String content = args[2];
								if(game.time != 3)
									listChatModel.addElement(id + " : " + content);	
							}
							
						}
						iterator.remove();
						scrollChat.getVerticalScrollBar().setValue(scrollChat.getVerticalScrollBar().getMaximum());
					}
					
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}
		public String read(SelectionKey key) throws Exception
		{
			SocketChannel sc = (SocketChannel)key.channel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			sc.read(buffer);
			buffer.flip();
			String message = decoder.decode(buffer).toString();
			return message;
		}
	}
}