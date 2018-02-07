package com.test.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.test.concurrent.ThreadPool;

/*
 * ���ܹ�����Tomcat�Ĵ������̣���URL��Ӧ�����Servlet��ϵ�γɣ�����HTTPЭ�飬��װ����/��Ӧ�������÷���ʵ���������Servlet���д����ɡ�
 * 
 * ��д���̳߳غ󲢷����ʲ���
 */
public class MyTomcat {

	//tomcat����Ĭ�϶˿ں�
	private int port = 8080;
	//�̳߳��ڳ�ʼ�����̵߳���Ŀ
	private static final int POOL_SIZE = 5;
	//�̳߳��ڳ�ʼ�������г���
	private static final int maxNoOfTasks = 5;
	
	//url---class(�����������ɹ�������WEB.XML�ļ����������hashmap��,�����ͻ����з���ʱȡ������·��ȥhashmap��Ѱ�Ҷ�Ӧ��class���䴴��,������ת����ȥ)
	private Map<String, String> urlClassMap = new HashMap<String, String>();
	private ThreadPool pool = new ThreadPool(POOL_SIZE, maxNoOfTasks);
	
	public MyTomcat(int port){
		this.port = port;
	}
	
	public void start(){
		/*	
		 * ��ʼ��url���Ӧ�����servlet�Ĺ�ϵ
		 * ���������������XML����ӳ���ϵ����hashmap��
		 */
		initServletMapping();
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("MyTomcat is start");
			while(true){
				Socket socket = serverSocket.accept();
				pool.execute(new MyHandler(socket, urlClassMap));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(serverSocket != null){
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void initServletMapping(){
		for(ServletMapping servletMapping : ServletContainer.servletMappingList){
			urlClassMap.put(servletMapping.getUrl(), servletMapping.getClazz());
		}
	}
	
	public static void main(String[] args) {
		new MyTomcat(8080).start();
	}
}
