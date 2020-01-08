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

   //properties���������ļ��е���Ϣ
	private static Properties properties=new Properties();
	private static List<String> classNames=new ArrayList<String>();
	
	private static Map<String, Object> ioc=new HashMap<String,Object>();
	
	private static Map<String, Method> methodMappings=new HashMap<String,Method>();
	private static Map<String, Object> controllerMappings=new HashMap<String,Object>();
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		//��ȡ�������ļ�������
		String configName=config.getInitParameter("MVCConfig");
		
	    //���������ļ�
		try {
			doLoadConfig(configName);
			//com.woniuxy
			String basePkg=properties.getProperty("scanPackage");
			
			//���ݰ���ȥ�Ұ����Լ��Ӱ������е���������
			doLoadClassName(basePkg);
			
			
			//ʵ����
			doInstance();
			
			
			//�Զ�װ��
			
			doAutoWired();
			
			
			//��������·���ͷ����Լ�������֮���ӳ���ϵ
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
		//��ioc������
		for(Entry<String, Object> entry:ioc.entrySet()){
			//��ȡ��ioc��ÿ�������Ӧ��Classʵ��
			Class clazz=entry.getValue().getClass();
			//�ж������Ƿ��п��Ƶ�ע��
			if(clazz.isAnnotationPresent(GLYController.class)){
				
				//��������ϵ�GLYRequestMappingע���ֵ
				GLYRequestMapping rm=(GLYRequestMapping)clazz.getAnnotation(GLYRequestMapping.class);
				//�õ�ע�������õĸ�·�� /user  
				String baseUrl=rm.value();
				//�õ������������еķ���
				Method thods[]=clazz.getDeclaredMethods();
				String subUrl="";
				//�Է���������
				for(Method thod:thods){
					//�ж��Ƿ����GLYRequestMappingע��
					if(thod.isAnnotationPresent(GLYRequestMapping.class)){
						//���ע�������õ���·��  /login.do
						 subUrl=thod.getAnnotation(GLYRequestMapping.class).value();
						 // �õ�����·��  /user/login.do
						 String finalUrl=baseUrl+subUrl;
						   //������·���ͷ�����ӳ���ϵ�������
							methodMappings.put(finalUrl,thod);
							//������·���Ϳ�������ӳ���ϵ�������
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
			//��ȡ��Ioc�еĶ���(UserController)
			Object obj=entry.getValue();
			//
			Class clazz=obj.getClass();
			//����������е�����
			Field fs[]=clazz.getDeclaredFields();
			//�����е�����������
			for(Field f:fs){
				//�ж���û�м��Զ�װ���ע��
				if(f.isAnnotationPresent(GLYAutowired.class)){
					//userDao,TypeDao
					String className=toFirstLowerCase(f.getType().getSimpleName());
					//��ioc�����������ƥ��Ķ���
					Object value=ioc.get(className);
					//����Ϊ˽�У�����ֱ�Ӹ�ֵ����Ҫ����Ϊtrue
					f.setAccessible(true);
					//�����Ը�ֵ
					f.set(obj, value);
					
					
					
				}
				
			}
			
		}
		
	}

	private void doInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		//�������е�����
		for(String className:classNames){
			
			//�������е���
			Class clazz=Class.forName(className);
			//�ж��Ƿ����ָ����ע�⣺GLYController|GLYRepository
			if(clazz.isAnnotationPresent(GLYController.class)||clazz.isAnnotationPresent(GLYRepository.class)){
				
				//�Լ���ָ��ע��������ʵ����
				Object obj=clazz.newInstance();
				
				//��ʵ������Ķ����ŵ�ioc�У�ioc��Map�ṹ
				//��ż�ֵ�ԣ� userDao-----obj(new UserDao());
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
		//��ȡ���е���Ŀ¼�������ļ�
		File subFiles[]=f.listFiles();
		for(File subFile:subFiles){
			//�����һ��Ŀ¼�����еݹ�
			if(subFile.isDirectory()){
				//������com.woniuxy.daos  
				doLoadClassName(basePkg+"."+subFile.getName());
				
			}
			else if(subFile.isFile()){
				//�õ�������ļ�����ȥ����׺��Ȼ���ټӰ�������
				//com.woniuxy.daos.UserDao
				String className=basePkg+"."+subFile.getName().replace(".class", "");
				classNames.add(className);
				
			}
			
		}
		
		System.out.println(basePath);
		
	}

	private void doLoadConfig(String configName) throws Exception {
		// TODO Auto-generated method stub
		//�õ���·����D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/GLYMVC3.0/WEB-INF/classes/
		String classPath=this.getClass().getResource("/").getPath();
		//filePath�����ļ�������·��
		String filePath=classPath+configName;
		File f=new File(filePath);
		
		properties.load(new FileInputStream(f));
		
		
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			//���󵽴�ִ��doDispatcher
			doDispatcher(req,resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// TODO Auto-generated method stub
		//�õ�����·��  (/user/login.do)
		String path=req.getServletPath();
		//��������·��ȥ��Ӧ��ִ�еķ���
		Method thod=methodMappings.get(path);
		//��������·���Ҷ�Ӧ�Ŀ���������
		Object controller=controllerMappings.get(path);
		//�õ����������в���
		Parameter ps[]=thod.getParameters();
		//��ʵ�ε�ֵ��ŵ�paramValues������
		Object paramValues[]=new Object[ps.length];
		//����û��������ҳ���ֵ
		Map<String, Object> dataModel=new HashMap<String,Object>();
		//ȷ��������ʵ�ε�ֵ�����Ҵ�ŵ�paramValues��
		//HttpServletResponse resp,@GLYParamName("user_name") String userName,@GLYParamName("user_pwd") String userPwd,HttpServletRequest req
		for(int i=0;i<ps.length;i++){
			//�õ������е�ÿһ������
			Parameter param=ps[i];
			//����βε�������HttpServletRequest����
			if(param.getType().getSimpleName().equals("HttpServletRequest")){
				
				paramValues[i]=req;
			}
			else if(param.getType().getSimpleName().equals("HttpServletResponse")){
				
				paramValues[i]=resp;
			}
			else if(param.getType().getSimpleName().equals("String")){
				System.out.println("...........");
				//�жϲ�������û�м�GLYParamNameע�⣨����GLYParamNameע��Ĳ������������������������ֵ��
				if(param.isAnnotationPresent(GLYParamName.class)){
					
					//�õ�ע�������õ��������������(user_name)
					String paramName=param.getAnnotation(GLYParamName.class).value();
					//����������������Ƶõ����������ֵ
					paramValues[i]=req.getParameter(paramName);
					
				}
				
			}
			else if((param.getType().getSimpleName().equals("Map"))){
				
				paramValues[i]=dataModel;
				
			}
			
			
		}
		
		
		//ȷ��ÿһ��ʵ�ε�ֵ
		
	     //���ÿ������Ķ�Ӧ������paramValues��ʵ�ε�ֵ�������ķ���ֵΪ�߼���ͼ���ƣ�
	    String viewName=(String)thod.invoke(controller,paramValues);
	    
	    doViewResolver(viewName,req,resp,dataModel);
		
		
	}
    //viewName:�߼���ͼ����redirect:/index.jsp
	private void doViewResolver(String viewName, HttpServletRequest req, HttpServletResponse resp,Map<String,Object> dataModel) throws IOException, ServletException {
		// TODO Auto-generated method stub
		//�����߼���ͼ���ƽ�������ʵ��ͼ���ƣ�/index.jsp��
		String realViewName=viewName.substring(viewName.indexOf(":")+1);
		if(viewName.startsWith("redirect:")){
			
			resp.sendRedirect(req.getContextPath()+realViewName);
			
		}
		else if(viewName.startsWith("forward:")){
			//��map�еļ�ֵ��ԭ�����õ����������
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
