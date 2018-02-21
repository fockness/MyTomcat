package com.test.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;


public class ThreadPool {

	private BlockingQueue<Runnable> takeQueue = null;
	
	private List<PoolThread> threads = new ArrayList<PoolThread>();
	
	private boolean isStoped = false;
	
	private int MAX_THREADS = 100;//Ĭ�ϵ�tomcat��󲢷���
	
	private static int INIT_THREADS = 10;//Ĭ�ϵ�tomcat��ʼ���߳���
	
	private static int INIT_QUEUE_NUMS = 10;//Ĭ�ϵ�������г�ʼ������
	
	private static int CURRENT_THREADS = 0;//��ǰ���̳߳��ڵ��߳���
	
	public ThreadPool(){
		this(INIT_THREADS, INIT_QUEUE_NUMS);
	}
	
	public ThreadPool(int noOfThreads, int maxNoOfTasks){
		this.INIT_THREADS = noOfThreads;
		this.INIT_QUEUE_NUMS = maxNoOfTasks;
		takeQueue = new BlockingQueue<Runnable>(INIT_QUEUE_NUMS);
		for(int i=0; i<INIT_THREADS; i++){
			threads.add(new PoolThread(takeQueue));
		}
		for(PoolThread thread : threads){
			thread.start();
		}
	}
	
	public synchronized void execute(Runnable task){
		if(this.isStoped) throw new IllegalStateException();
		
		//��ǰ���߳��������̳߳��������������������AbortPolicy()����
		if(CURRENT_THREADS >= MAX_THREADS && AbortPolicy());
		
		try {
			CURRENT_THREADS++;
			this.takeQueue.enqueue(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean AbortPolicy() {
		return this.takeQueue.reduceQueue();
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
		this.interrupt();// ��ϳ����̵߳� dequeue() ����.
	}
	
	public synchronized boolean isStop(){
		return isStoped;
	}
	
	
	
}
