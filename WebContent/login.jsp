<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
<style type="text/css">

#form-info{
	width: 400px;
	margin:100px auto;
} 
</style>

</head>   
<body>
<%
  Cookie cs[]=request.getCookies();
String userName="";
if(cs!=null){
	 for(Cookie c:cs){
		 out.print(c.getName()+":"+c.getValue());
		 if(c.getName().equals("uname")){
			 
			 userName=c.getValue();
			 
		 }
		 
		 
	 }
}

%>
<div class="container-fluid">
		<div class="row" style="background-color: black;color: white;height: 100px">
		 		<div  class="col-md-12">
		 			<h1 align="center">仓储管理系统</h1>
		 		</div> 
				
		</div>
		<div class="row" style="height:550px">
		 		<div  class="col-md-12">
		 			    <form class="form-horizontal" action="user/login.do" method="post" id="form-info"> 
							<div class="form-group">
								<label class="col-md-4 col-sm-4 control-label">用户名：</label>
								<div class="col-md-6 ">
									<input type="text" class="form-control"  required="required" name="user_name" value="<%=userName%>">
								
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-4 col-sm-4 control-label">密码：</label>
								<div class="col-md-6 ">
									<input type="password" class="form-control"  required="required" name="user_pwd">
								
								</div>
							</div>
							
					
							<div class="form-group">
								<label class="col-md-4 control-label"></label>
								<div class="col-md-6">
								    
									     	<input type="submit" class="btn btn-info" value="登录"/>
									    	<input type="reset" class="btn btn-info" value="取消"/>
								
								</div> 
							</div>
						</form>
		 		</div> 
				 
		</div>
		
		<div class="row" style="background-color: #eee;height: 100px">
		 		<div  class="col-md-12" style="text-align: center;line-height: 100px">
		 		
		 			&copy;版权信息。。。。。。。
		 		</div>
				
		</div>		

</div>
  


<script type="text/javascript" src="js/jquery-1.12.3.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
</body>
</html>