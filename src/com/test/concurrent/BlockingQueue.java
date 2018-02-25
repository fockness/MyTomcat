package com.test.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import sun.misc.Unsafe;

/*BlockingQueue�ļ�ʵ��
 *ʹ����������������ģ�� 
 */
public class BlockingQueue<T> {
	
	//�����������еײ��������ṹ
	private List<Object> queue = new LinkedList<Object>();
	
	//queue������
	private int limit = 0;
	
	//Ĭ��ɾ���Ķ���Ԫ��
	private volatile int index = 0;
	
	public BlockingQueue(int limit){
		this.limit = limit;
	}
	
	public boolean enqueue(Object item) throws InterruptedException{
		synchronized(this){
			while(this.queue.size() == this.limit){
				wait();
			}
			if(this.queue.size() == 0) notifyAll();
			return queue.add(item);
		}
	}
	
	public  Object dequeue() throws InterruptedException{
		synchronized(this){
			while(this.queue.size() == 0){
				wait();
			}
			if(this.queue.size() == this.limit) notifyAll();
			return queue.remove(0);
		}
	}
	
	public boolean reduceQueue(){
		synchronized (this) {
			return queue.remove(index) != null;
		}
	}
}
