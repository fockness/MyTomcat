package com.test.concurrent;

import java.util.LinkedList;
import java.util.List;

//BlockingQueue�ļ�ʵ��
public class BlockingQueue<T> {
	
	private List<Object> queue = new LinkedList<Object>();
	
	private int limit = 0;//queue������
	
	public BlockingQueue(int limit){
		this.limit = limit;
	}
	
	public synchronized void enqueue(Object item) throws InterruptedException{
		while(this.queue.size() == this.limit){
			wait();
		}
		if(this.queue.size() == 0) notifyAll();
		queue.add(item);
	}
	
	public synchronized Object dequeue() throws InterruptedException{
		while(this.queue.size() == 0){
			wait();
		}
		if(this.queue.size() == this.limit) notifyAll();
		return queue.remove(0);
	}
}