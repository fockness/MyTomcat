package com.test.tomcat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(17);
//		for(int i=0; i<17; i++){
		while(true)
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						 Socket socket = new Socket("47.96.28.67",8080);
						 //构建IO
				         InputStream is = socket.getInputStream();
				         OutputStream os = socket.getOutputStream();
				         
				         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
				         //向服务器端发送一条消息
				         bw.write("GET /hello HTTP/1.1"+
				        		 "Host: localhost:8080"+
				        		 "Connection: keep-alive"+
				        		 "Cache-Control: max-age=0"+
				        		 "Upgrade-Insecure-Requests: 1"+
				        		 "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"+
				        		 "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,q=0.8"+
				        		 "Accept-Encoding: gzip, deflate, sdch, br"+
				        		 "Accept-Language: zh-CN,zh;q=0.8");
				         bw.flush();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
	}
//		}
}

