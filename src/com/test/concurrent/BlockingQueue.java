package com.test.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/*BlockingQueue的简单实现
 *使用了生产者消费者模型 
 */
public class BlockingQueue<T> {
	
	//任务阻塞队列底层采用链表结构
	private List<Object> queue = new LinkedList<Object>();
	
	//queue的容量
	private int limit = 0;
	
	//默认删除的队列元数
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
