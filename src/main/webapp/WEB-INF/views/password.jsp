<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset=utf-8>
    <title>忘记密码 在浙里 分享你我的社交</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.css" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/zaizheli-base.css" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/zaizheli-theme.css" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/validationEngine.bootstrap.css" />" />
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.1.7.1.js" />" ></script>
	<script type="text/javascript" src="<c:url value="/resources/languages/zh-cn/jquery.validationEngine.lang.js" />" ></script>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.validationEngine.js" />" ></script>
</head>
<body>	
    <div class="container">
		<div class="row mt-30" >
			<div class="span8 offset2 mt-20">
				<div class="hero-unit board-2">
				<form:form id="passForm" cssClass="sign-in mt-30" modelAttribute="forgetPasswordVo" method="post">
					<img src="<c:url value="/resources/img/zaizheli-icon.png" />" /><hr>					
						<div class="row-fluid ">
						<div class="control-group">
							<div class="controls">
								<div class="input-prepend">
									<span class="add-on"><i class="icon-envelope"></i></span>
									<div class="f-l"><input id="user-name" class="validate[required,custom[email]] input-xlarge" name="email" type="email" placeholder="在浙里注册邮箱" autocomplete="off" value="<c:out value="${forgetPasswordVo.email}" />" /></div>
									<div class="f-l hint" ><form:errors path="email" cssClass="alert alert-error" style="padding-right:0px; margin-top:5px;"/></div>
								</div>
							</div>
						</div>
						</div>						
						<div class="row-fluid mt-10">
							 <div class="span6">
							 	<button type="submit" class="btn btn-primary btn-large">提交</button>
							 	<a class="btn btn-large ml-10" href="<c:url value="/" />" />返回</a>
							</div>
						</div>
					</form:form>
					<p><a href="<c:url value="/signup" />">还没有在浙里的账号？立即注册！</a></p>
				</div>
			</div>
		</div>
	</div>		
<script type="text/javascript" src="<c:url value="/resources/js/ga.js" />" ></script>
<script type="text/javascript">
	$("#passForm").validationEngine('attach', {promptPosition : "bottomRight", scroll: false});	
</script>
</body>
</html>