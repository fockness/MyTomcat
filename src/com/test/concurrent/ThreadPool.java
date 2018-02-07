package com.test.concurrent;

import java.util.ArrayList;
import java.util.List;


public class ThreadPool {

	private BlockingQueue<Runnable> takeQueue = null;
	
	private List<PoolThread> threads = new ArrayList<PoolThread>();
	
	private boolean isStoped = false;
	
	public ThreadPool(int noOfThreads, int maxNoOfTasks){
		takeQueue = new BlockingQueue<Runnable>(maxNoOfTasks);
		for(int i=0; i<noOfThreads; i++){
			threads.add(new PoolThread(takeQueue));
		}
		for(PoolThread thread : threads){
			thread.start();
		}
	}
	
	public synchronized void execute(Runnable task){
		if(this.isStoped) throw new IllegalStateException();
		try {
			this.takeQueue.enqueue(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void stop(){
		this.isStoped = true;
		for(PoolThread thread : threads){
			thread.stop();
		}
	}
}

class PoolThread extends Thread{
	
	private BlockingQueue takeQueue = null;
	private boolean isStoped = false;
	
	public PoolThread(BlockingQueue queue){
		this.takeQueue = queue;
	}
	
	public void run(){
		while(!isStoped){
			Runnable runnable;
			try {
				runnable = (Runnable)takeQueue.dequeue();
				runnable.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void toStop(){
		isStoped = true;
		this.interrupt();// 打断池中线程的 dequeue() 调用.
	}
	
	public synchronized boolean isStop(){
		return isStoped;
	}
	
	
	
}
