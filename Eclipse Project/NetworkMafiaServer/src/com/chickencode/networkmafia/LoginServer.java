package com.chickencode.networkmafia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class LoginServer implements Runnable
{
	public void run()
	{
		try
		{
			int port = 1115;
			String keyStore  =DataBase.getDataBase().getKeyStore();
			String keyStorePass = DataBase.getDataBase().getkeyPass();
			String keyPass = DataBase.getDataBase().getkeyPass();
		//	System.setProperty("javax.net.debug","ssl");
	    	KeyStore ks;
			KeyManagerFactory kmf;
			SSLContext sc;
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyStore), keyStorePass.toCharArray());
			kmf = KeyManagerFactory.getInstance("SunX509"); 
			kmf.init(ks, keyPass.toCharArray());
			sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);
			SSLServerSocketFactory factory = sc.getServerSocketFactory();
			SSLServerSocket serverSocket = (SSLServerSocket)factory.createServerSocket(port);
			
			while(true)
			{
				SSLSocket clientSocket = (SSLSocket)serverSocket.accept();
				ReadWatingServer ws = new ReadWatingServer(clientSocket);
				Thread threadWs = new Thread(ws);
				threadWs.start();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
class ReadWatingServer implements Runnable
{
	private SSLSocket clientSocket;
	private final long timeout = 8000;	// 3��
	ReadWatingServer(SSLSocket clientSocket)
	{
		this.clientSocket = clientSocket;
	}
	public void run()
	{
		try
		{
			long firstTime = System.currentTimeMillis();	// timeOut ����
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			String info;
			while((info = input.readLine()) == null)
			{
				if(System.currentTimeMillis() - firstTime >= timeout)
				{
					input.close();
					output.close();
					clientSocket.close();
					System.out.println("TimeOut");
					return;
				}
			}
					
					System.out.println("���� ���� : " + info);
					String args[] = info.split(":");
					/*
					 * ���̵� ��й�ȣ ó��
					 * 
					 * protocol
					 * 
					 * �α���
					 * Login:id:password ���� 1 ���� 0
					 *
					 * ȸ������
					 * SignUp:id:password ���� 1 ���� 0
					 * 
					 * ���̵�üũ
					 * Check:id	�ߺ� : 1 ���� : 0
					 */
					boolean service = true;
					if(args[0].startsWith("Login"))
					{
						String id = args[1];
						String ps = args[2];
						boolean success = DataBase.getDataBase().login(id,ps);
						if(success)
							output.write("1");
						else
							output.write("0");
					}
					else if(args[0].startsWith("SignUp"))
					{
						String id = args[1];
						String ps = args[2];
						boolean success = DataBase.getDataBase().signup(id, ps);
						if(success)
							output.write("1");
						else
							output.write("0");
					}
					else if(args[0].startsWith("Check"))
					{
						String id = args[1];
						boolean overlap = DataBase.getDataBase().checkId(id);
						if(overlap)
							output.write("1");
						else
							output.write("0");
					}
					else
						service = false;
					output.newLine();
					output.flush();
					input.close();
					output.close();
					clientSocket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}