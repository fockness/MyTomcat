package com.test.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.management.RuntimeErrorException;


public class ThreadPool {

	private BlockingQueue<Runnable> takeQueue = null;
	
	private List<PoolThread> threads = new ArrayList<PoolThread>();
	
	private boolean isStoped = false;
	
	private int MAX_THREADS = 100;//默认的tomcat最大并发量
	
	private static int INIT_THREADS = 10;//默认的tomcat初始化线程量
	
	private static int INIT_QUEUE_NUMS = 10;//默认的任务队列初始化长度
	
	private static int CURRENT_THREADS = 0;//当前的线程池内的线程数
	
	private static Handler handler = null;//拒绝策略
	
	public ThreadPool(){
		//拒绝策略默认采用AbortPolicy
		this(INIT_THREADS, INIT_QUEUE_NUMS, Handler.AbortPolicy);
	}
	
	public ThreadPool(int noOfThreads, int maxNoOfTasks, Handler handler){
		this.INIT_THREADS = noOfThreads;
		this.INIT_QUEUE_NUMS = maxNoOfTasks;
		this.handler = handler;
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
		
		//当前的线程数大于线程池允许的最大数量，默认采用AbortPolicy()策略
		if(CURRENT_THREADS >= MAX_THREADS && handler());
		
		try {
			CURRENT_THREADS++;
			this.takeQueue.enqueue(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean handler(){
		switch (handler) {
		case DiscardOldestPolicy:
			return discardOldestPolicy();
		default:
			return abortPolicy();
		}
	}
	
	//抛异常
	private boolean abortPolicy() {
		throw new RuntimeException("线程池数量已满");
	}

	//丢弃队列中最老的任务
	private boolean discardOldestPolicy() {
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
		this.interrupt();// 打断池中线程的 dequeue() 调用.
	}
	
	public synchronized boolean isStop(){
		return isStoped;
	}
	
	
	
}
