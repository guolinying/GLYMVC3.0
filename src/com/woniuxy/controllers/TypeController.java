package com.woniuxy.controllers;

import java.util.List;
import java.util.Map;

import com.woniuxy.annotations.GLYAutowired;
import com.woniuxy.annotations.GLYController;
import com.woniuxy.annotations.GLYRequestMapping;
import com.woniuxy.daos.TypeDao;
import com.woniuxy.entitys.Type;

@GLYController
@GLYRequestMapping("/type")
public class TypeController {

	@GLYAutowired
	private TypeDao typeDao;
	
	public void setTypeDao(TypeDao typeDao) {
		this.typeDao = typeDao;
	}
	
	@GLYRequestMapping("/list.do")
	public String getAll(Map<String, Object> dataModel){
		
		List<Type> l=typeDao.getAll();
		dataModel.put("types", l);
		return "forward:/type.jsp";
	}
	
}
