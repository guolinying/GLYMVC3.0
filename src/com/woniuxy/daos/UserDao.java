package com.woniuxy.daos;

import com.woniuxy.annotations.GLYRepository;
import com.woniuxy.entitys.User;

@GLYRepository
public class UserDao {

	
	public boolean  isExit(User u){
		
		if(u.getUserName().equals("admin")&&u.getUserPwd().equals("123456")){
			
			return true;
		}
		else{
			return false;
		}
		
	}
}
