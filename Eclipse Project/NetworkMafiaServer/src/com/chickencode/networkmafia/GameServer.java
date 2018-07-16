package com.chickencode.networkmafia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Base64.Decoder;
import java.util.Iterator;

import javax.swing.plaf.SliderUI;
public class GameServer implements Runnable
{
	GameServer instance;
	ServerSocket server;
	GameData gameData;
	Thread gameServerThread;
	Thread gameConnectThread;
	boolean endServer = false;
	GameServer(int id ,int port)
	{
		instance = this;
		gameData = new GameData();
		gameData.port = port;
		gameData.roomid = id;
		try
		{
			server = new ServerSocket(gameData.port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		startServer();
	}
	public void startServer()
	{
		gameServerThread = new Thread(this);
		gameServerThread.start();
	}
	public void run()
	{
		gameConnectThread =new Thread(new GameConnnectThread());
		gameConnectThread.start();
		new Thread(new ChatThread()).start();
		gameData.time = 1;
		while(!endServer)
		{
			try
			{
				while(gameData.players.size() != 8)
					gameServerThread.sleep(100);
				
				/*
				 * 게임이 시작 되엇습니다.
				 */
				System.out.println("[GameServer] : 게임시작");
				gameData.end = false;	// 게임 시작
				sendMessageAll("start");
				
				startGame();
				while(!gameData.end)
				{
					System.out.println("[GameServer] : 아침");
					gameData.time = 1;
					notifyTime();
					closeAct();
					thinkResult();
					if(gameData.end)
						break;
					
					
					if(!waitingTime(gameData.morning))
						break;
					System.out.println("[GameServer] : 투표");
					gameData.time = 2;
					notifyTime();
					openVote();
					if(!waitingTime(gameData.voteTime))
						break;
					System.out.println("[GameServer] : 저녁");
					gameData.time = 3;
					voteResult();
					thinkResult();
					openAct();
					notifyTime();
					if(!waitingTime(gameData.night))
						break;
				}
				for(int i = 0; i < gameData.players.size(); i++)
						gameData.players.get(i).alive = true;
				gameData.time = 1;
				System.out.println("게임이 종료되었습니다.");
				gameServerThread.sleep(gameData.waitTime);	//기다리는시간
			}catch(Exception e) {}
		
		}
	}
	public boolean waitingTime(long time) throws Exception
	{
		long bt = System.currentTimeMillis();
		while(System.currentTimeMillis() - bt <= time)
		{
			gameServerThread.sleep(10);
			if(gameData.end)
				return false;
		}
		return true;
	}
	public void createTestPlayer()
	{
		String botName[] = {"나서스_봇","문도_봇","하이머딩거_봇","애니_봇","누누_봇","가렌_봇","야스오_봇"};
		gameData.roomName = "테스트 서버입니다.";
		for(int i = 0; i < 6; i++)
		{
			PlayerData p = new PlayerData();
			p.id = botName[i];
			p.input = new BufferedReader(new InputStreamReader(System.in));
			p.output = new BufferedWriter(new OutputStreamWriter(System.out));
			gameData.players.add(p);
		}
		reviewPlayer();
	}
	public void closeThread()
	{
		gameData.end = true;
		endServer = true;
	}
	public void startGame()
	{
		for(int i = 0; i < gameData.players.size() ;i++)
			gameData.players.get(i).alive = true;
		int randomJob[] = {1,1,1,1,1,2,2,3};
		for(int i = 0; i < 1000; i++)
		{
			int a = (int) (Math.random() * 8);
			int b = (int) (Math.random() * 8);
			int t = randomJob[a];
			randomJob[a] = randomJob[b];
			randomJob[b] = t;
		}
		for(int i = 0; i < gameData.players.size() ;i++)
			gameData.players.get(i).job = randomJob[i];
		int team = -1;
		for(int i = 0; i < 8; i++)
		{
			if(randomJob[i] == 2 && team == -1)
				team = i;
			else if(randomJob[i] == 2 && team != -1)
			{
				sendMessage(team, "job:"+ randomJob[i] + ":" +i);
				sendMessage(i, "job:"+ randomJob[i] + ":" +team);
			}
			else
				sendMessage(i, "job:"+ randomJob[i]);
		}
		gameData.end = false;
		gameData.chooseMapia = -1;
		gameData.choosePolice = -1;
	}
	public void thinkResult()
	{
		int numberMapia = 0;
		int numberSimin = 0;
		for(int i = 0; i < gameData.players.size(); i++)
			if(gameData.players.get(i).alive)
			{
				if(gameData.players.get(i).job == 2)
					++numberMapia;
				else
					++numberSimin;
			}
		System.out.println("남은 마피아 " + numberMapia);
		System.out.println("남은 시민  " + numberSimin);
		if(numberMapia == numberSimin)
			winMapia();
		else if(numberMapia == 0)
			winSimin();
			
	}
	
	public void winMapia()
	{
		for(int i = 0; i < gameData.players.size(); i++)
			if(gameData.players.get(i).job == 2)
				sendMessage(i, "end:1");
			else
				sendMessage(i, "end:0");
		gameData.end = true;
	}
	public void winSimin()
	{
		for(int i = 0; i < gameData.players.size(); i++)
			if(gameData.players.get(i).job != 2)
				sendMessage(i, "end:1");
			else
				sendMessage(i, "end:0");
		gameData.end = true;
	}
	public void notifyTime()
	{
		sendMessageAll("time:" + gameData.time);
	}
	public void openVote()
	{
		for(int i = 0 ; i < gameData.votes.length; i++)
			gameData.votes[i] = -1;
	}
	public void openAct()
	{
		gameData.chooseMapia = -1;
		gameData.choosePolice = -1;
	}
	public void closeAct()
	{
		if(gameData.chooseMapia != -1)
			kill(gameData.chooseMapia);
		if(gameData.choosePolice != -1)
			prove(gameData.choosePolice);
			
	}
	public void prove(int number)
	{
		boolean isMapia = (gameData.players.get(number).job == 2); 
		for(int i = 0; i < gameData.players.size(); i++)
			if(gameData.players.get(i).job == 3)
				sendMessage(i,"prove:"+number+":"+ (isMapia ? 1 : 0));
			
	}
	public void kill(int number)
	{
		sendMessageAll("die:" + number);
		gameData.players.get(number).alive = false;
	}
	public void voteResult()
	{
		int[] index = new int[8];
		for(int i = 0; i < 8; i++)
			index[i] = 0;
		for(int i = 0; i < 8; i++)
		{
			if(gameData.votes[i] == -1)
				continue;
			index[gameData.votes[i]]++;
		}
		int maxValue = 0;
		int maxIndex = -1;
		for(int i = 0; i < 8; i++)
		{
			if(maxValue < index[i])
			{
				maxValue = index[i];
				maxIndex = i;
			}
			else if(maxValue == index[i])
				maxIndex = -1;
		}
		if(maxIndex != -1)
		{
			kill(maxIndex);
		}
	}
	public void reviewPlayer()
	{
		String playerinfo = "playerinfo";
		for(int i = 0; i < gameData.players.size(); i++)
			playerinfo += (":" + gameData.players.get(i).id);
		sendMessageAll(playerinfo);
		for(int i = 0; i < gameData.players.size(); i++)	//숫자 재활당
			gameData.players.get(i).number = i;
	}
	public void sendMessageAll(String info)
	{
		System.out.println("[GameServer] all 보냄 : " + info);
		try
		{
			for(int i = 0; i < gameData.players.size(); i++)
			{
				PlayerData player = gameData.players.get(i);
				player.output.write(info);
				player.output.newLine();
				player.output.flush();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void sendMessage(int number, String info)
	{
		System.out.println("[GameServer] 보냄 : " + info);
		try
		{
			PlayerData player = gameData.players.get(number);
			player.output.write(info);
			player.output.newLine();
			player.output.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	class ChatThread implements Runnable
	{
		Selector selector;
		ServerSocketChannel serverChannel;
		ServerSocket serverSocket;
		CharsetDecoder decoder;
		ArrayList<SocketChannel> socketList;
		ByteBuffer buf;
		public void run()
		{
			try
			{
				decoder = Charset.forName("KSC5601").newDecoder();
				selector = Selector.open();
				serverChannel = ServerSocketChannel.open();
				serverChannel.configureBlocking(false);
				serverSocket = serverChannel.socket();
				serverSocket.bind(new InetSocketAddress("localHost", gameData.port + 1));
				serverChannel.register(selector,SelectionKey.OP_ACCEPT);
				socketList = new ArrayList<>();
				buf = ByteBuffer.allocate(1024);
				while(!instance.endServer)
				{
					selector.select();
					Iterator iterator = selector.selectedKeys().iterator();
					while(iterator.hasNext())
					{
						
						SelectionKey key = (SelectionKey)iterator.next();
						if(key.isAcceptable())
							accept(key);
						else if(key.isReadable())
						{
							String info = read(key);
							String args[] = info.split(":");
							System.out.println("[Chat] : " + info);
							if(args[0].equals("exit"))
							{
								socketList.remove((SocketChannel)key.channel());
								((SocketChannel)key.channel()).close();
							}
							else if(args[0].equals("chat"))
							{
								String id = args[1];
								String context = args[2];
								buf.clear();
								buf.put(("chat:"+id+":"+context).getBytes());
								buf.flip();
								for(int i = 0; i < gameData.players.size(); i++)
									if(gameData.players.get(i).id.equals(id) && gameData.players.get(i).alive && gameData.time != 3)
										for(int j = 0; j < socketList.size(); j++)
										{
											socketList.get(j).write(buf);
											buf.rewind();
										}
							}
						}
						iterator.remove();
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		public void accept(SelectionKey key)
		{
			ServerSocketChannel serverChannel = (ServerSocketChannel)key.channel();
			SocketChannel socketChannel = null;
			try
			{
				socketChannel = serverChannel.accept();
				if(socketChannel == null)return;
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ);
				socketList.add(socketChannel);
			}catch(Exception ioe){ioe.printStackTrace(); }
			
		}
		public String read(SelectionKey key)
		{
			String message = null;
			try
			{
				SocketChannel socketChannel = (SocketChannel)key.channel();
				ByteBuffer buf = ByteBuffer.allocate(1024);
				socketChannel.read(buf);
				buf.flip();
				message = decoder.decode(buf).toString();
				buf.clear();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return message;
		}
	}
	class GameConnnectThread implements Runnable	// 플레이어 받는 그런 쓰레드
	{
		
		public void run()
		{
			try
			{
				while(!endServer)
				{
					while(gameData.players.size() == 8)	// 플레이어수가 꽉차면 무한대기
					{
						gameConnectThread.sleep(100);
					}
					Socket client = server.accept();
					System.out.println("새로운 플레이어 연결하는중....");
					PlayerData newPlayer = new PlayerData();
					newPlayer.client = client;
					newPlayer.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
					newPlayer.output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					newPlayer.connect = true;
					String info;
					while((info = newPlayer.input.readLine()) == null);
					
					System.out.println("[gameServer] 받음 : " + info);
					String args[] = info.split(":");
					newPlayer.id = args[1];
					newPlayer.runInputThread();
					newPlayer.alive = true;
					gameData.players.add(newPlayer);
					reviewPlayer();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	class GameData
	{
		final int morning = 60000;
		final int voteTime = 10000;
		final int night = 30000;
		final int waitTime = 10000;
		int chooseMapia = -1;
		int choosePolice = -1;
		int roomid;
		int port;
		int time;
		int votes[] = new int[8];
		int choose[] = new int[8];
		boolean end = true;
		String roomName;
		ArrayList<PlayerData> players;
		GameData()
		{
			players = new ArrayList<>();
		}
	}
	class PlayerData
	{
		String id;
		int job;
		boolean alive;
		Socket client;
		SocketChannel channel;
		BufferedReader input;
		BufferedWriter output;
		int number;
		boolean connect;
		Thread inputThread;
		public void runInputThread()
		{
			inputThread = new Thread(new PlayerInputThread());
			inputThread.start();
		}
		class PlayerInputThread implements Runnable
		{
			public void run()
			{
				while(connect)
				{
					try
					{
						String info;
						while((info = input.readLine()) == null);
						String args[] = info.split(":");
						System.out.println("[GameServer] 받음 : " + info);
						if(args[0].equals("vote"))
						{
							if(gameData.time == 2 && alive)
								if(gameData.votes[number] == -1)
									if(gameData.players.get(Integer.parseInt(args[1])).alive)
										gameData.votes[number] = Integer.parseInt(args[1]);
								
									
							
						}
						else if(args[0].equals("select"))
						{
							if(gameData.time == 3 && alive)
							{
								if(job == 2 && gameData.chooseMapia == -1)
								{
									gameData.chooseMapia = Integer.parseInt(args[1]);
								}
								else if(job == 3 && gameData.choosePolice == -1)
								{
									gameData.choosePolice = Integer.parseInt(args[1]);
								}
							}
						}
						else if(args[0].equals("close"))
						{
							gameData.players.get(number).client.close();
							gameData.players.get(number).connect = false;
							gameData.players.remove(number);
							gameData.end = true;
							reviewPlayer();
						}
					}catch(Exception e)	// 연결 끊어짐
					{			
						e.printStackTrace();
					}
				}
			}
		}
	}
}