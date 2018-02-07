package com.test.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class MyHandler implements Runnable{

	private Socket socket;
	private Map<String, String> urlClassMap;
	
	public MyHandler(Socket socket, Map<String, String> urlClassMap){
		this.socket = socket;
		this.urlClassMap = urlClassMap;
	}
	
	@Override
	public void run() {
		try {
			System.out.println(Thread.currentThread().getName());
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			MyRequest request = new MyRequest(inputStream);
			MyResponse response = new MyResponse(outputStream);
			//请求分发
			dispatch(request, response);
			
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void dispatch(MyRequest request, MyResponse response){
		String clazz = urlClassMap.get(request.getUrl());
		//反射
		try {
			Class<MyServlet> myServletClass = (Class<MyServlet>)Class.forName(clazz);
			MyServlet myServlet = myServletClass.newInstance();
			myServlet.service(request, response);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}
	}
}
