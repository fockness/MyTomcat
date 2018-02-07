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
 * 你能够看到Tomcat的处理流程：把URL对应处理的Servlet关系形成，解析HTTP协议，封装请求/响应对象，利用反射实例化具体的Servlet进行处理即可。
 * 
 * 手写完线程池后并发访问测试
 */
public class MyTomcat {

	//tomcat启动默认端口号
	private int port = 8080;
	//线程池内初始工作线程的数目
	private static final int POOL_SIZE = 5;
	//线程池内初始阻塞队列长度
	private static final int maxNoOfTasks = 5;
	
	//url---class(服务器启动成功后会解析WEB.XML文件并将其放入hashmap中,待到客户端有访问时取出访问路径去hashmap中寻找对应的class反射创建,将请求转发过去)
	private Map<String, String> urlClassMap = new HashMap<String, String>();
	private ThreadPool pool = new ThreadPool(POOL_SIZE, maxNoOfTasks);
	
	public MyTomcat(int port){
		this.port = port;
	}
	
	public void start(){
		/*	
		 * 初始化url与对应处理的servlet的关系
		 * 这个方法用来解析XML并将映射关系放入hashmap中
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
