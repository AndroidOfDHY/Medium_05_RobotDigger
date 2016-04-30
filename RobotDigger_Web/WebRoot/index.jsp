<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>RobotDigger - Download</title>
<%
	String username = (String) session.getAttribute("RobotUsername");
	if (username == null)
		username = "";
%>
<script language="javascript" type="text/javascript" src="js/jq.js"></script>
<script language="javascript" type="text/javascript" src="js/all-js.js"></script>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

.container {
	position: relative;
	width: 700px;
	height: 600px;
	margin: 0 auto;
}

.container img {
	position: absolute;
	font-size: 16px;
}
</style>
<link rel="stylesheet" rev="stylesheet" type="text/css" media="all"
	href="css/index.css" />
</head>
<body>
	<div id="name_log">
		<input type="hidden" value="<%=username%>" class="user_name" />
	</div>
	<div>
		<div style="background: #4280b9;height:70px">
			<a href="/RobotDigger_Web/"><img class="logo"
				src="image/logo1.png"
				style="height:70px; float:left;margin-left:150px;" /> </a> <img
				src="image/qqq.png" class="downapk"
				style="margin-right:150px;margin-top:20px;float:right;z-index:6;cursor: pointer;" />
			<img class="downapk" src="image/1121.png"
				style="margin-right:-20px;margin-top:-20px;float:right;cursor: pointer;" />
			
			<%
				if (username != "") {
			%>
			<p class="userid" id="userid">
				欢迎回来：<%=username%></p>
			<%
				}
			%>
			
		</div>
		<img class="2weima" src="image/2weima.jpg"
				style="margin-right:-425px;margin-top:-55px;float:right;cursor: pointer;" heigth=40px; width=40px; />
		<div>
			<div id="lift">
				<img src="image/tu.png" width="350" height="551">
			</div>
			<div class="container">
				<img class="down" src="image/download.png"
					style="margin-left:0px;margin-top:100px;cursor: pointer;" /> <img
					class="login" src="image/login.png"
					style="margin-left:400px;margin-top:100px;cursor: pointer;" /> <img
					class="outlogin" src="image/outlogin.png"
					style="margin-left:400px;margin-top:100px;cursor: pointer;" /> <img
					class="set" src="image/setting.png"
					style="margin-left:200px;margin-top:100px;cursor: pointer;" />
			</div>
		</div>
	</div>
	<div class="dan" id="gdan_layer" style="display:none;z-index:8888;heigth=100%;width=100%;background:#000;"></div>
	<div class="weima" style="display:none;">
		<a class="b_clo" style="cursor:pointer;" title="关闭"></a>
		<div class="box_con">
			<img src="image/2weima.jpg"/>
		</div>
	</div>
	<div class="download_box" style="display:none;">
		<a class="b_clo" style="cursor:pointer;" title="关闭"></a>
		<div class="box_con">
			<h3>
				请输入提取码<span class="yzm_tip" style="color:red;display:none;">不好意思
					提取码输入错误 =. =!!!</span>
			</h3>
			<div class="dr_code">
				<form action="downloadfile" method="post" id="downloadfile">
					<input class="dr_addr tqm" name="filenum" type="text"> <input
						class="dr_btn tqm_ok" type="button" value="提取">
				</form>
			</div>
			<p>例如 提取码为：123456</p>
		</div>
	</div>
	<div class="down_beifen_box" style="display:none;">
		<a class="b_clo" style="cursor:pointer;" title="关闭"></a>
		<div class="box_con">
			<div class="dr_code">
				<h3>提取您的备份文件</h3>
				<input class="dr_btn tqbf_ok" name="" type="button" value="提取备份">
				<form action="downloadBackup" method="post" id="downloadBackup">
					<input type="hidden" name="username" value="<%=username%>" />
				</form>
			</div>
		</div>
	</div>
	<div class="nologin_box" style="display:none;">
		<a class="b_clo" style="cursor:pointer;" title="关闭"></a>
		<div class="p_con1">
			<form action="nologin" id="nologin" method="post">
				<h3>
					<span class="yzm_tip" style="color:red;">您还未登录，请先登录后，继续使用！</span>
				</h3>
				<div class="mod_info">
					<div class="mod_ip name" style="position:relative;z-index:22;">
						<label>用户名：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1 loginname2" id="logname"
								name="username2" type="text" value="">
						</div>
					</div>
					<div class="mod_ip password" style="position:relative;z-index:22;">
						<label>密码：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1 loginpass2" id="logpass"
								name="password2" type="password" value="">
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="p_btn">
			<a title="登录" style="cursor:pointer;" class="login_ok">登录</a><a
				title="取消" style="cursor:pointer;" class="cancel">取消</a>
		</div>
	</div>
	<div class="login_box" style="display:none;">

		<a class="b_clo" style="cursor:pointer;" title="关闭"></a>
		<div class="p_con1">

			<div class="mod_info">
				<form action="login" id="login" method="post">
					<div class="mod_ip name" style="position:relative;z-index:22;">
						<label>用户名：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1 loginname" id="logname" name="username"
								type="text" />
						</div>
					</div>
					<div class="mod_ip password" style="position:relative;z-index:22;">
						<label>密码：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1 loginpass" name="password"
								type="password" />
						</div>
					</div>
				</form>
			</div>
		</div>
		<div class="p_btn">
			<a title="登录" style="cursor:pointer;" class="login_ok">登录</a><a
				title="取消" style="cursor:pointer;" class="cancel">取消</a>
		</div>
	</div>
	<div class="set_box" style="display:none;">
		<a class="b_clo" style="cursor:pointer;" title="关闭"></a>
		<table id="set_box_select" border="0">
			<tr>
				<th width="250" align="center"><p class="select_email"
						style="cursor:pointer;border-right-style:solid;border-right-color:#DDDDDD;border-right-size:1px;border-bottom-style:solid;border-bottom-color:#DDDDDD;border-bottom-size:1px;">邮箱设置</p>
				</th>
				<th width="250" align="center"><p class="select_pass"
						style="cursor:pointer;border-bottom-style:solid;border-bottom-color:#DDDDDD;border-bottom-size:1px;">登录密码设置</p>
				</th>
			</tr>
		</table>
		<div class="p_con1 set1">
			<form action="updateEmail" method="post" id="updateEmail">
				<div class="mod_info">
					<div class="mod_ip name" style="position:relative;z-index:22;">
						<label>邮箱：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1 email" id="email" name="email" type="text"
								value="">
						</div>
					</div>
					<div class="mod_ip password" style="position:relative;z-index:22;">
						<label>密码：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1" id="password2" name="password"
								type="password" value="" />
						</div>
					</div>
				</div>
				<div class="p_btn">
					<a title="确认" style="cursor:pointer;" class="set_ok updateEmail">确认</a><a
						title="取消" style="cursor:pointer;" class="cancel">取消</a>
				</div>
			</form>
		</div>
		<div class="p_con1 set2" style="display:none;">
			<form action="updatePassword" method="post" id="updatePassword">
				<div class="mod_info">
					<div class="mod_ip name" style="position:relative;z-index:22;">
						<label>原密码：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1" id="oldpass" name="password"
								type="password" value="">
						</div>
					</div>
					<div class="mod_ip password" style="position:relative;z-index:22;">
						<label>新密码：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1" id="newpass" name="passwordNew"
								type="password" value="" />
						</div>
					</div>
					<div class="mod_ip password" style="position:relative;z-index:22;">
						<label>重复密码：</label>
						<div class="ip_sel">
							<input class="s_ip s_ip1" id="cfnewpass" name="passwordRepeat"
								type="password" value="" />
						</div>
					</div>
				</div>
				<div class="p_btn">
					<a title="确认" style="cursor:pointer;" class="set_ok passwordRepeat">确认</a><a
						title="取消" style="cursor:pointer;" class="cancel">取消</a>
				</div>
			</form>
		</div>
	</div>
	${message}
</body>
</html>
