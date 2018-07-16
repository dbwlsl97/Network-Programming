package com.chickencode.networkmafia;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DataBase
{
	private String sshKeyPassword;
	private HashMap<String, String> loginMap;
	private HashSet<String> loginUser;
	private HashMap<String, PlayerInfo> players;	// id  , PlayerInfo
	private HashMap<Integer, GameServer> gameservers;
	private String keyStore = "";
	private String keyPass = "";
	private int nextPort = 2000;
	private int nextRoomId = 1;
	public static DataBase instance;
	public void setKeyStore(String keyStore)
	{
		this.keyStore = keyStore;
	}
	public void setKeyPass(String keyPass)
	{
		this.keyPass = keyPass;
	}
	public String getKeyStore()
	{
		return keyStore;
	}
	public String getkeyPass()
	{
		return keyPass;
	}
	static public DataBase getDataBase()
	{
		if(instance == null)
			instance = new DataBase();
		return instance;
	}
	private DataBase()
	{
		/*
		 * 아이디 비밀번호 가져오는 소스
		 */
		loginMap = new HashMap<>();
		loginUser = new HashSet<>();
		players = new HashMap<>();
		gameservers = new HashMap<>();
	}
	public boolean login(String id, String password)
	{
		String ps = loginMap.get(id);
		if(ps != null && ps.equals(password))
		{
			loginUser.add(id);
			return true;
		}
		return false;
	}
	public boolean signup(String id,String password)
	{
		if(loginMap.containsKey(id))
			return false;
		loginMap.put(id, password);
		return true;
	}
	public boolean checkId(String id)
	{
		return loginMap.containsKey(id);
	}
	
	public void putPlayer(PlayerInfo info)
	{
		players.put(info.playerId, info);
	}
	public PlayerInfo getPlayer(String id)
	{
		return players.get(id);
	}
	public void removePlayer(String info)
	{
		players.remove(info);
	}
	public void putGameServer(int id , GameServer server)
	{
		gameservers.put(id, server);
	}
	public GameServer getGameServer(int id)
	{
		return gameservers.get(id);
	}
	public void removeGameServer(int id)
	{
		gameservers.remove(id);
	}
	public ArrayList<GameServer> getAllGameServer()
	{
		ArrayList<GameServer> r = new ArrayList<>();
		Iterator<Integer> keyIt = gameservers.keySet().iterator();
		while(keyIt.hasNext())
			r.add(gameservers.get(keyIt.next()));
		return r;
	}
	public int getNextRoomId()
	{
		return nextRoomId++;
	}
	public int getNextPort()
	{
		nextPort += 2;
		return nextPort - 2;
	}
}
class PlayerInfo
{
	public String playerId;
	public SocketChannel socket;
}
