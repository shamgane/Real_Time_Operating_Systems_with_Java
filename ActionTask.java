package edu.amrita.cb.cen.mtech2019.rtos;

import java.util.Random;

import edu.amrita.cb.cen.mtech2019.rtos.events.ActionData;
import edu.amrita.cb.cen.mtech2019.rtos.events.ActionListener;
import edu.amrita.cb.cen.mtech2019.rtos.events.Listener;
import edu.amrita.cb.cen.mtech2019.rtos.events.ReadData;
import edu.amrita.cb.cen.mtech2019.rtos.events.ReadListener;
import edu.amrita.cb.cen.mtech2019.rtos.task.Task;

public class ActionTask implements Task, ReadListener {

	double startTime;
	double endTime;
	double deadLine;
	
	Thread thread;
	int id;
	String name;
	boolean stop = false;
	
	Random r;
	
	ActionListener[] registry;
	int listnerCount;
	
	private double[] temps;
	private int valCount; 
	
	public ActionTask (double dL, int ws) {
		deadLine = dL;
		registry = new ActionListener[100];
		listnerCount = -1;
		id = java.util.UUID.randomUUID().variant() ;
		name = "Thread"+id;
		temps = new double[ws];
		valCount = 0;
	}
	
	@Override
	public void run() {
		
		while(!stop) {
			
			startTime = System.currentTimeMillis()/1000.0;
			double v = avgTemp();
			
			String act = (v > 45.0)?"Hot":(v < 20.0)?"Cold":"Norm"; 
			fireActionEvent(v, act);
			
			endTime = System.currentTimeMillis()/1000.0;
			
			if(endTime - startTime >= deadLine) 
				System.err.println("Action:TimeOut "+act);
			
			try {  Thread.sleep(3000);} 
			catch (InterruptedException e) { }
		}

	}
	
	private double avgTemp() {
		double sum = 0;
		
		for(int i =0;i<temps.length; i++) {
			sum+=temps[i];
		}
		
		return sum/temps.length;
	}

	private void fireActionEvent(double t,String act) {
		for(int i=0; i<=listnerCount;i++ ) 
			registry[i].actionResponse(
					new ActionData(t,act));
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Thread currentThread() {
		return thread;
	}

	@Override
	public double getStartTime() {
		return startTime;
	}

	@Override
	public double getLastEndTime() {
		return endTime;
	}

	@Override
	public double getdeadLine() {
		return deadLine;
	}

	@Override
	public void registerListener(Listener e) {
		registry[++listnerCount] = (ActionListener)e;	
	}


	@Override
	public void start() { 
		System.out.println("Action:Initiated");
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void readRespose(ReadData r) {
		temps[valCount] = r.tempVal;
		valCount = (valCount+1) % temps.length;
		System.out.println("Action:recv "+r.tempVal);
	}

	
	public static void main(String [] args) throws InterruptedException {
		Task sensor = new SensorTask(0.000055);
		Task action = new ActionTask(0.000055,20);
	
		A1 aListnr = new A1();
		
		sensor.registerListener((ReadListener)action);
		action.registerListener(aListnr);
		
		sensor.start();
		
		// Action Delay Constraint ...
		Thread.sleep(16000);
		
		action.start();
	}
}

class A1 implements ActionListener {

	@Override
	public void actionResponse(ActionData r) {
		System.out.println("A1:"+r.actStr);
	}
	
}
