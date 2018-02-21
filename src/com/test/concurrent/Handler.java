package com.test.concurrent;

//拒绝策略枚举类
public enum Handler{
	//抛异常
	AbortPolicy, 
	//默认将排在队列中最久的一个任务也就是队列第一个任务抛弃
	DiscardOldestPolicy
}