package com.test.tomcat;

public class HelloServlet extends MyServlet{

	@Override
	public void doGet(MyRequest request, MyResponse response) {
		try {
			response.write("hello get");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void doPost(MyRequest request, MyResponse response) {
		try {
			response.write("hello post");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
