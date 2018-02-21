package com.test.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/*BlockingQueue�ļ�ʵ��
 *ʹ����������������ģ�� 
 */
public class BlockingQueue<T> {
	
	//�����������еײ��������ṹ
	private List<Object> queue = new LinkedList<Object>();
	
	//queue������
	private int limit = 0;
	
	//Ĭ��ɾ���Ķ���Ԫ��
	private int index = 0;
	
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
	
	public boolean reduceQueue(){
		return queue.remove(index) != null;
	}
}
