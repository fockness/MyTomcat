package com.test.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import sun.misc.Unsafe;

/*BlockingQueue的简单实现
 *使用了生产者消费者模型 
 */
public class BlockingQueue<T> {
	
	//任务阻塞队列底层采用链表结构
	private List<Object> queue = new LinkedList<Object>();
	
	//queue的容量
	private int limit = 0;
	
	//默认删除的队列元数
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
