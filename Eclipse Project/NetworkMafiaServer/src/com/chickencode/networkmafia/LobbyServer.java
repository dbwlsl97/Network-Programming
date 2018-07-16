package com.chickencode.networkmafia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

import javax.swing.table.TableColumn;
import javax.xml.crypto.Data;

public class LobbyServer implements Runnable
{
	public void run()
	{
		try 
		{
			ServerSocket server = new ServerSocket(1116);
			while(true)
			{
				System.out.println("Debug : Server");
				Socket client = server.accept();
				new LobbyServiceThread(client).run();
			}
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
class LobbyServiceThread implements Runnable
{
	Socket client;
	LobbyServiceThread(Socket client)
	{
		this.client = client;
	}
	public void run()
	{
		try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			String info;
			while((info = input.readLine()) == null);
			System.out.println("[LobbyServer] 받은정보 : " + info);
			String args[] = info.split(":");
			if(info.startsWith("refresh"))
			{
				ArrayList<GameServer> gameServers = DataBase.getDataBase().getAllGameServer();	//방아이디:방이름:port:플레이어수
				output.write("refresh");
				for(int i = 0; i < gameServers.size(); i++)
				{
					GameServer gs = gameServers.get(i);
					int playerNumbrer = gs.gameData.players.size();
					int roomid = gs.gameData.roomid;
					int port = gs.gameData.port;
					String roomName = gs.gameData.roomName;
					output.write(":" + roomid + ":" + roomName + ":" + port +":" + playerNumbrer);
				}
				output.newLine();
				output.flush();
			}
			else if(info.startsWith("join"))	//join:roomid		return 1:port 성공  2 : 플레이어 꽉참 3 : 서버에러
			{
				/*
				 * 게임서버랑 연결작업
				 */
				int roomid = Integer.parseInt(args[1]);	
				GameServer gameServer  = DataBase.getDataBase().getGameServer(roomid);
				if(gameServer == null)
					output.write("3");
				else if(gameServer.gameData.players.size() == 8)
					output.write("2");
				else
					output.write("1:"+gameServer.gameData.port);
				output.newLine();
				output.flush();
			}
			else if(info.startsWith("makeroom"))	//makeroom:roomname
			{
				String roomName = args[1];
				int port = DataBase.getDataBase().getNextPort();
				int id = DataBase.getDataBase().getNextRoomId();
				GameServer server = new GameServer(id,port);
				server.gameData.roomName = roomName;
				DataBase.getDataBase().putGameServer(id, server);
				output.write("1:"+port);
				output.newLine();
				output.flush();
			}
			client.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}