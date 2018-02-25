package com.test.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.management.RuntimeErrorException;


public class ThreadPool {

	private BlockingQueue<Runnable> takeQueue = null;
	
	private List<WorkThread> threads = new ArrayList<WorkThread>();
	
	private boolean isStoped = false;
	
	private int MAX_THREADS = 100;//Ĭ�ϵ�tomcat��󲢷���
	
	private static int INIT_THREADS = 10;//Ĭ�ϵ�tomcat��ʼ���߳���,��corePoolSize
	
	private static int INIT_QUEUE_NUMS = 10;//Ĭ�ϵ�������г�ʼ������
	
	private static int CURRENT_THREADS = 0;//��ǰ���̳߳��ڵ��߳���
	
	private static Handler handler = null;//�ܾ�����
	
	private static ThreadPool pool = null;
	
	//�����̰߳�ȫ����������ģʽ��ȡ�̳߳�ʵ��
	public static ThreadPool newInstance(){
		return newInstance(INIT_THREADS, INIT_QUEUE_NUMS, Handler.AbortPolicy);
	}
	
	public static ThreadPool newInstance(int noOfThreads, int maxNoOfTasks, Handler handler){
		synchronized (ThreadPool.class) {
			if(pool == null){
				pool = new ThreadPool(noOfThreads, maxNoOfTasks, handler); 
			}
		}
		return pool;
	}
	
//	private ThreadPool(){
//		//�ܾ�����Ĭ�ϲ���AbortPolicy
//		this(INIT_THREADS, INIT_QUEUE_NUMS, Handler.AbortPolicy);
//	}
	
	private ThreadPool(int noOfThreads, int maxNoOfTasks, Handler handler){
		this.INIT_THREADS = noOfThreads;
		this.INIT_QUEUE_NUMS = maxNoOfTasks;
		this.handler = handler;
		takeQueue = new BlockingQueue<Runnable>(INIT_QUEUE_NUMS);
		for(int i=0; i<INIT_THREADS; i++){
			threads.add(new WorkThread(takeQueue));
			CURRENT_THREADS++;
		}
		for(WorkThread thread : threads){
			thread.start();
		}
	}
	
	public synchronized void execute(Runnable task){
		if(this.isStoped) throw new IllegalStateException();
		//��ǰ�߳���С�ں����߳��������ٴ���һ���߳�
		if(CURRENT_THREADS < INIT_THREADS && addOneThread());
	    //��ǰ�߳������ں����߳�����������ᱻ�����������еȴ�
		if(CURRENT_THREADS == INIT_THREADS && addTaskToQueue(task));
		//��ǰ���߳��������̳߳���������������Ĭ�ϲ���AbortPolicy()����
		if(CURRENT_THREADS >= MAX_THREADS && handler());
	}
	
	public boolean handler(){
		switch (handler) {
		case DiscardOldestPolicy:
			return discardOldestPolicy();
		default:
			return abortPolicy();
		}
	}
	
	//���쳣
	private boolean abortPolicy() {
		throw new RuntimeException("�̳߳���������");
	}

	//�������������ϵ�����
	private boolean discardOldestPolicy() {
		return this.takeQueue.reduceQueue();
	}

	public synchronized void stop(){
		this.isStoped = true;
		for(WorkThread thread : threads){
			thread.stop();
		}
	}
	
	//�ٴ���һ�������߳�
	private synchronized boolean addOneThread(){
		return threads.add(new WorkThread(takeQueue));
	}
	
	//������Ž���������
	private boolean addTaskToQueue(Runnable task){
		boolean addSuccess = true;
		synchronized (this) {
			try {
				addSuccess = this.takeQueue.enqueue(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally{
				return addSuccess;
			}
		}
	}
}

class WorkThread extends Thread{
	
	private BlockingQueue takeQueue = null;
	private boolean isStoped = false;
	
	public WorkThread(BlockingQueue queue){
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
