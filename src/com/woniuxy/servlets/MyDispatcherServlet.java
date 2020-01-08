package com.woniuxy.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.woniuxy.annotations.GLYAutowired;
import com.woniuxy.annotations.GLYController;
import com.woniuxy.annotations.GLYParamName;
import com.woniuxy.annotations.GLYRepository;
import com.woniuxy.annotations.GLYRequestMapping;

public class MyDispatcherServlet extends HttpServlet{

   //properties缓存配置文件中的信息
	private static Properties properties=new Properties();
	private static List<String> classNames=new ArrayList<String>();
	
	private static Map<String, Object> ioc=new HashMap<String,Object>();
	
	private static Map<String, Method> methodMappings=new HashMap<String,Method>();
	private static Map<String, Object> controllerMappings=new HashMap<String,Object>();
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		//读取到配置文件的名称
		String configName=config.getInitParameter("MVCConfig");
		
	    //加载配置文件
		try {
			doLoadConfig(configName);
			//com.woniuxy
			String basePkg=properties.getProperty("scanPackage");
			
			//根据包名去找包下以及子包中所有的完整类名
			doLoadClassName(basePkg);
			
			
			//实例化
			doInstance();
			
			
			//自动装配
			
			doAutoWired();
			
			
			//处理请求路径和方法以及控制器之间的映射关系
			doHandlerMapping();
			
			for(Entry<String, Object> entry:controllerMappings.entrySet()){
				System.out.println(entry.getKey()+"===="+entry.getValue());
				
			}
			
			System.out.println(configName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private void doHandlerMapping() {
		// TODO Auto-generated method stub
		//对ioc做遍历
		for(Entry<String, Object> entry:ioc.entrySet()){
			//获取到ioc中每个对象对应的Class实例
			Class clazz=entry.getValue().getClass();
			//判断类型是否有控制的注解
			if(clazz.isAnnotationPresent(GLYController.class)){
				
				//获得类型上的GLYRequestMapping注解的值
				GLYRequestMapping rm=(GLYRequestMapping)clazz.getAnnotation(GLYRequestMapping.class);
				//得到注解中配置的父路径 /user  
				String baseUrl=rm.value();
				//得到控制器中所有的方法
				Method thods[]=clazz.getDeclaredMethods();
				String subUrl="";
				//对方法做遍历
				for(Method thod:thods){
					//判断是否加了GLYRequestMapping注解
					if(thod.isAnnotationPresent(GLYRequestMapping.class)){
						//获得注解中配置的子路径  /login.do
						 subUrl=thod.getAnnotation(GLYRequestMapping.class).value();
						 // 得到完整路径  /user/login.do
						 String finalUrl=baseUrl+subUrl;
						   //将完整路径和方法的映射关系存放起来
							methodMappings.put(finalUrl,thod);
							//将完整路径和控制器的映射关系存放起来
							controllerMappings.put(finalUrl, entry.getValue());
							
						
					}
					 
				}
				
			}
			
		}
		
	}

	private void doAutoWired() throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		
		//  typeDao---new TypeDao()
	//  userDao---new UserDao()
	//  typeControler---new TypeControler()
	//  userController---new UserController()
		for(Entry<String, Object> entry:ioc.entrySet()){
			//活取到Ioc中的对象(UserController)
			Object obj=entry.getValue();
			//
			Class clazz=obj.getClass();
			//获得类中所有的属性
			Field fs[]=clazz.getDeclaredFields();
			//对所有的属性做遍历
			for(Field f:fs){
				//判断有没有加自动装配的注解
				if(f.isAnnotationPresent(GLYAutowired.class)){
					//userDao,TypeDao
					String className=toFirstLowerCase(f.getType().getSimpleName());
					//从ioc容器获得类型匹配的对象
					Object value=ioc.get(className);
					//属性为私有，不能直接赋值，需要设置为true
					f.setAccessible(true);
					//给属性赋值
					f.set(obj, value);
					
					
					
				}
				
			}
			
		}
		
	}

	private void doInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		//遍历所有的类名
		for(String className:classNames){
			
			//加载所有的类
			Class clazz=Class.forName(className);
			//判断是否加了指定的注解：GLYController|GLYRepository
			if(clazz.isAnnotationPresent(GLYController.class)||clazz.isAnnotationPresent(GLYRepository.class)){
				
				//对加了指定注解的类进行实例化
				Object obj=clazz.newInstance();
				
				//将实例化后的对象存放到ioc中，ioc是Map结构
				//存放键值对： userDao-----obj(new UserDao());
				ioc.put(toFirstLowerCase(clazz.getSimpleName()),obj);
				
			}
			
			
		}
		
		
	}
	
	public String toFirstLowerCase(String str){
		char cs[]=str.toCharArray();
		cs[0]+=32;
		return String.valueOf(cs);
	}

	private void doLoadClassName(String basePkg) {
		// TODO Auto-generated method stub
		//com.woniuxy
		
		//basePath:com/woniuxy
		String basePath=basePkg.replace(".", File.separator);
		//D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/GLYMVC3.0/WEB-INF/classes/com/woniuxy/
		// 
		String realPath=this.getClass().getResource("/"+basePath).getPath();
		
		File f=new File(realPath);
		//获取所有的子目录或者子文件
		File subFiles[]=f.listFiles();
		for(File subFile:subFiles){
			//如果是一个目录，进行递归
			if(subFile.isDirectory()){
				//参数：com.woniuxy.daos  
				doLoadClassName(basePkg+"."+subFile.getName());
				
			}
			else if(subFile.isFile()){
				//得到具体的文件名称去掉后缀，然后再加包的名称
				//com.woniuxy.daos.UserDao
				String className=basePkg+"."+subFile.getName().replace(".class", "");
				classNames.add(className);
				
			}
			
		}
		
		System.out.println(basePath);
		
	}

	private void doLoadConfig(String configName) throws Exception {
		// TODO Auto-generated method stub
		//得到类路径：D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/GLYMVC3.0/WEB-INF/classes/
		String classPath=this.getClass().getResource("/").getPath();
		//filePath配置文件的完整路径
		String filePath=classPath+configName;
		File f=new File(filePath);
		
		properties.load(new FileInputStream(f));
		
		
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			//请求到达执行doDispatcher
			doDispatcher(req,resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// TODO Auto-generated method stub
		//得到请求路径  (/user/login.do)
		String path=req.getServletPath();
		//根据请求路径去找应该执行的方法
		Method thod=methodMappings.get(path);
		//根据请求路径找对应的控制器对象
		Object controller=controllerMappings.get(path);
		//得到方法的所有参数
		Parameter ps[]=thod.getParameters();
		//将实参的值存放的paramValues数组中
		Object paramValues[]=new Object[ps.length];
		//存放用户需求带到页面的值
		Map<String, Object> dataModel=new HashMap<String,Object>();
		//确定方法的实参的值，并且存放到paramValues中
		//HttpServletResponse resp,@GLYParamName("user_name") String userName,@GLYParamName("user_pwd") String userPwd,HttpServletRequest req
		for(int i=0;i<ps.length;i++){
			//得到数组中的每一个参数
			Parameter param=ps[i];
			//如果形参的类型是HttpServletRequest类型
			if(param.getType().getSimpleName().equals("HttpServletRequest")){
				
				paramValues[i]=req;
			}
			else if(param.getType().getSimpleName().equals("HttpServletResponse")){
				
				paramValues[i]=resp;
			}
			else if(param.getType().getSimpleName().equals("String")){
				System.out.println("...........");
				//判断参数上有没有加GLYParamName注解（加了GLYParamName注解的参数就是用来接收请求参数的值）
				if(param.isAnnotationPresent(GLYParamName.class)){
					
					//得到注解中配置的请求参数的名称(user_name)
					String paramName=param.getAnnotation(GLYParamName.class).value();
					//根据请求参数的名称得到请求参数的值
					paramValues[i]=req.getParameter(paramName);
					
				}
				
			}
			else if((param.getType().getSimpleName().equals("Map"))){
				
				paramValues[i]=dataModel;
				
			}
			
			
		}
		
		
		//确定每一个实参的值
		
	     //调用控制器的对应方法（paramValues是实参的值，方法的返回值为逻辑视图名称）
	    String viewName=(String)thod.invoke(controller,paramValues);
	    
	    doViewResolver(viewName,req,resp,dataModel);
		
		
	}
    //viewName:逻辑视图名称redirect:/index.jsp
	private void doViewResolver(String viewName, HttpServletRequest req, HttpServletResponse resp,Map<String,Object> dataModel) throws IOException, ServletException {
		// TODO Auto-generated method stub
		//根据逻辑视图名称解析到真实视图名称（/index.jsp）
		String realViewName=viewName.substring(viewName.indexOf(":")+1);
		if(viewName.startsWith("redirect:")){
			
			resp.sendRedirect(req.getContextPath()+realViewName);
			
		}
		else if(viewName.startsWith("forward:")){
			//将map中的键值对原样放置到请求对象中
			for(Entry<String, Object> entry:dataModel.entrySet()){
				
				req.setAttribute(entry.getKey(),entry.getValue());
				
			}
			System.out.println(req.getContextPath()+realViewName);
			req.getRequestDispatcher(realViewName).forward(req, resp);
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);

		System.out.println("hhahhaha by yangguo");

		System.out.println("hahahahhabyguolinying");
		System.out.println("hahahahhabyguolinying--test");
		System.out.println("hahahahhabyguolinying--test1");
		System.out.println("hahahahhabyguolinying--gly");
	}
}
