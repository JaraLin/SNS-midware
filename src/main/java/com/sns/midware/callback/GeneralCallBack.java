package com.sns.midware.callback;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GeneralCallBack extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req,HttpServletResponse resp){
		String verifyCode=req.getParameter("verifyCode");
		System.out.println("verifyCode is " + verifyCode);
	}

}
