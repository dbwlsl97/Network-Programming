package com.chickencode.networkmafia;import java.io.File;

import javax.xml.crypto.Data;

public class Main 
{
	public static void main(String args[])
	{
		if(args.length != 2)
		{
			System.out.println("java className ip password");
			return;
		}
		//System.setProperty("javax.net.debug","ssl");
		DataBase.getDataBase().setKeyStore("C:\\keystore\\clientKey");
		DataBase.getDataBase().setIP(args[0]);
		DataBase.getDataBase().setKeyPass(args[1]);
		System.setProperty("javax.net.ssl.trustStore", "C:\\keystore\\cacerts");
		System.setProperty("javax.net.ssl.keyStore", DataBase.getDataBase().getKeyStore());
		System.setProperty("javax.net.ssl.keyStorePassword", DataBase.getDataBase().getkeyPass());
		MainFrame.getInstance();

	}
}
