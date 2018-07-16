package com.chickencode.networkmafia;

import com.chickencode.networkmafia.GameServer.PlayerData;

public class Main
{
	static public void main(String args[])
	{
		if(args.length != 1)
		{
			System.out.println("java className keypass");
			return;
		}
		DataBase.getDataBase().setKeyStore("C:\\keystore\\ServerKey");
		DataBase.getDataBase().setKeyPass(args[0]);
		System.setProperty("javax.net.ssl.trustStore", "C:\\keystore\\cacerts");
		LoginServer server = new LoginServer();
		new Thread(server).start();
		new Thread(new LobbyServer()).start();;
		GameServer testServer = new GameServer(99, 7000);
		DataBase.getDataBase().putGameServer(99, testServer);
		testServer.createTestPlayer();
	}
}
