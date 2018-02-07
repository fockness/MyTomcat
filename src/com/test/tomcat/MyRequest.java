package com.test.tomcat;

import java.io.IOException;
import java.io.InputStream;

/*
 * 你可以清楚的看到，我们通过输入流，对HTTP协议进行解析，拿到了HTTP请求头的方法以及URL。
 */
public class MyRequest {
	
	private String url;
	private String method;
	
	public MyRequest(InputStream inputStream) throws IOException{
		String httpRequest = "";
		byte[] httpRequestByte = new byte[1024];
		int length = 0;
		if((length = inputStream.read(httpRequestByte)) > 0){
			httpRequest = new String(httpRequestByte, 0, length);
		}
		
		/*
		 * HTTP请求协议
		 * GET /favicon.icon HTTP/1.1
		 */
		String httpHead = httpRequest.split("\n")[0];
		url = httpHead.split("\\s")[1];
		method = httpHead.split("\\s")[0];
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
