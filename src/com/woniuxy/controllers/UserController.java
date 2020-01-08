package com.woniuxy.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.woniuxy.annotations.GLYAutowired;
import com.woniuxy.annotations.GLYController;
import com.woniuxy.annotations.GLYParamName;
import com.woniuxy.annotations.GLYRequestMapping;
import com.woniuxy.daos.UserDao;
import com.woniuxy.entitys.User;

@GLYController
@GLYRequestMapping("/user")
public class UserController {

	@GLYAutowired
	private UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@GLYRequestMapping("/login.do")
	public String login(HttpServletResponse resp,@GLYParamName("user_name") String userName,@GLYParamName("user_pwd") String userPwd,HttpServletRequest req){
	      
		User u=new User(userName,userPwd);
		
		if(userDao.isExit(u)){
			
			return "redirect:/index.jsp";//逻辑视图名称
		}
		else{
			
			return "redirect:/login.jsp";
		}
	}
	
	@GLYRequestMapping("/add.do")
	public void add(){
		
		
		System.out.println("add方法到达。。。。。");
	}
	
}
