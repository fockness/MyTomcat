package com.test.tomcat;

public class UserServlet extends MyServlet{

	@Override
	public void doGet(MyRequest request, MyResponse response) {
		try {
			response.write("user get");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void doPost(MyRequest request, MyResponse response) {
		try {
			response.write("user post");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
