package com.woniuxy.daos;

import java.util.ArrayList;
import java.util.List;

import com.woniuxy.annotations.GLYRepository;
import com.woniuxy.entitys.Type;

@GLYRepository
public class TypeDao {

	
	public List<Type> getAll(){
		
		List<Type> l=new ArrayList<Type>();
		l.add(new Type(1,"001","ˮ��"));
		l.add(new Type(2,"002","ˮ��"));
		l.add(new Type(3,"003","ˮ��"));
		l.add(new Type(4,"004","ˮ��"));
		return l;
		
	}
}
