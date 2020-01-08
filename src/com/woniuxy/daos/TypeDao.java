package com.woniuxy.daos;

import java.util.ArrayList;
import java.util.List;

import com.woniuxy.annotations.GLYRepository;
import com.woniuxy.entitys.Type;

@GLYRepository
public class TypeDao {

	
	public List<Type> getAll(){
		
		List<Type> l=new ArrayList<Type>();
		l.add(new Type(1,"001","水果"));
		l.add(new Type(2,"002","水果"));
		l.add(new Type(3,"003","水果"));
		l.add(new Type(4,"004","水果"));
		return l;
		
	}
}
