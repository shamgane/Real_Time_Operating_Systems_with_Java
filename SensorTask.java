package edu.amrita.cb.cen.mtech2019.rtos;

import java.util.Random;

import edu.amrita.cb.cen.mtech2019.rtos.events.Listener;
import edu.amrita.cb.cen.mtech2019.rtos.events.ReadData;
import edu.amrita.cb.cen.mtech2019.rtos.events.ReadListener;
import edu.amrita.cb.cen.mtech2019.rtos.task.Task;

/* absentees 5,6 hrs on 18th oct 2019 fri.
	5, 6,7,1
*/
public class SensorTask implements Task {

	double startTime;
	double endTime;
	double deadLine;
	
	Thread thread;
	int id;
	String name;
	boolean stop = false;
	
	Random r;
	
	ReadListener[] registry;
	int listnerCount;
	
	public SensorTask(double dL) {
		deadLine = dL;
		registry = new ReadListener[100];
		listnerCount = -1;
		id = java.util.UUID.randomUUID().variant() ;
		name = "Thread"+id;
		r = new Random(7);
	}
	
	
	public void start() {
		System.out.println("Sensor:Initiated");
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		int v = 24;
		
		while(!stop) {
			startTime = System.currentTimeMillis()/1000.0;
			int flutter = r.nextInt(10);	
			int d = r.nextInt(2);
	
			if(d == 0) v = v+flutter;
			else v = v-flutter;
	
			fireReadEvent(v);
			
			endTime = System.currentTimeMillis()/1000.0;
			
			if(endTime - startTime >= deadLine) 
				System.err.println("Sensor:TimeOut "+v);
			
			try {  Thread.sleep(200);} 
			catch (InterruptedException e) { }
		}
	}
	
	private void fireReadEvent(double v) {
		for(int i=0; i<=listnerCount;i++ ) 
			registry[i].readRespose(new ReadData(v,0));
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
		registry[++listnerCount] = (ReadListener)e;	
	}

	
	public static void main(String[] args) {
		Task st = new SensorTask(0.000055);
		A r1 = new A();
		B r2 = new B();
		C r3 = new C();
		
		st.registerListener(r1);
		st.registerListener(r2);
		st.registerListener(r3);
		
		
		st.start();

	}
}

class A implements ReadListener {

	@Override
	public void readRespose(ReadData r) {
		System.out.println("A:"+r.tempVal);
	}
	
}

class B implements ReadListener {

	@Override
	public void readRespose(ReadData r) {
		System.out.println("B:"+r.tempVal);
	}
	
}

class C implements ReadListener {

	@Override
	public void readRespose(ReadData r) {
		System.out.println("C:"+r.tempVal);
	}
	
}
