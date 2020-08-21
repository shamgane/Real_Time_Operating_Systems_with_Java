package edu.amrita.cb.cen.mtech2019.rtos.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;

public class Client implements Runnable {

	Socket client;
	String msg;

	public Client(String ip, int port, String m) {
		try {
			client = new Socket(ip, port);
			msg = m;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			Random r = new Random(7);
			PrintStream ot = new PrintStream(client.getOutputStream());
			Thread.sleep(r.nextInt(500));
			ot.println(msg);

			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			String msg = in.readLine();
			
			System.out.println("received :" + msg);

			ot.close();
			in.close();
			client.close();
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		
		
		
		for(int i = 0; i< 10000; i++) {
			Client c = new Client("127.0.0.1", 9876, String.valueOf(i));
			(new Thread(c)).start();
			
			
		}
		Client c = new Client("127.0.0.1", 9876, "stop");
		(new Thread(c)).start();
	}

}
