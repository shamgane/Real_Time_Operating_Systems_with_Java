package edu.amrita.cb.cen.mtech2019.rtos.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server implements Runnable {

	static int PORT = 9876;	
	static ServerSocket server;
	static boolean stop = false;
	
	Socket child;
	
	public Server(Socket c) {
		child = c;		
		(new Thread(this)).start();
	}
	
	public static void listen() {
		System.out.println("Server started ... Listing...");
		
		try {
			if (server == null)
				server  = new ServerSocket(PORT);
			
			while(!stop) {
				Socket c = server.accept();
				new Server(c);
			}
				
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	public void run() {
		try {
			
			System.out.println("Handling : "+child.getPort());
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							child.getInputStream() 
					) 
			);
			
			String msg = in.readLine();
			System.out.println("received :"+msg);
			
			if(msg.compareTo("stop") == 0) {
				System.out.println("Stoping ...");
				stop = true;
				server.close();
			}
			
			PrintStream ot = new PrintStream(
					child.getOutputStream() 
			);
			
			ot.println("Have a nice day!");
			
			ot.close();
			in.close();
			child.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
public static void main(String[] args) {
		Server.listen();
	}

	
}
