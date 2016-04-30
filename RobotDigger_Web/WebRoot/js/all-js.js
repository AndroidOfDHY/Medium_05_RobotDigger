$(document).ready(function(){
		var name=$(".user_name").val();
		if(name.length != 0){$(".login").hide();}else{$(".outlogin").hide();}
		$(".container img").hover(function(){$(this).css({"heigth":195,"width":195});},function(){$(this).css({"heigth":200,"width":200});});
		$(".down").click(function(){
			 $(".container img").animate({top:"250"});
			 $(".login_box, .set_box, .nologin_box").fadeOut();
			 if(name.length != 0){
				setTimeout(function() {$(".login_box, .set_box, .nologin_box").hide();$(".download_box, .down_beifen_box").fadeIn();$(".set_box .dr_addr").val("").focus();}, 500);
			}else{
				setTimeout(function() {$(".login_box, .set_box, .nologin_box").hide();$(".download_box").fadeIn();$(".set_box .dr_addr").val("").focus();}, 500);
			}
		});
		$(".set").click(function(){
			$(".container img").animate({top:"250"});
			$(".download_box, .login_box, .down_beifen_box").fadeOut();
			if(name.length != 0){
				setTimeout(function() {$(".download_box, .login_box, .down_beifen_box").hide();$(".set_box").fadeIn();$(".set_box .dr_addr").val("").focus();}, 500);
			}else{
				setTimeout(function() {$(".download_box, .login_box, .down_beifen_box").hide();$(".nologin_box").fadeIn();$(".set_box .dr_addr").val("").focus();}, 500);
			}
			 
		});
		$(".select_email").click(function(){
				$(".set2").fadeOut();
				setTimeout(function() {$(".set2").hide();$(".set1").fadeIn();}, 500); 
		});
		$(".select_pass").click(function(){
				$(".set1").fadeOut();
				setTimeout(function() {$(".set1").hide();$(".set2").fadeIn();}, 500);
		});
		$(".login").click(function(){
			$(".container img").animate({top:"250"});
			$(".download_box, .set_box, .nologin_box, .down_beifen_box").fadeOut();
			 setTimeout(function() {$(".download_box, .set_box, .nologin_box, .down_beifen_box").hide();$(".login_box").fadeIn();$(".login_box .dr_addr").val("").focus();}, 500);
		});
		$(".outlogin").click(function(){
			window.location.href = "outlogin";
		});
		$(".2weima").click(function(){
			$(".dan, .weima").fadeIn();$(".login_box, .download_box, .set_box, .nologin_box, .down_beifen_box").fadeOut();
			$(".container img").animate({top:"0"});
		});
		$(".b_clo, .cancel").click(function(){$(".dan").fadeOut();
									$(".login_box, .download_box, .set_box, .nologin_box, .down_beifen_box, .weima").fadeOut();
									setTimeout(function() {$(".container img").animate({top:"0"});}, 500);     });
		$(".tqm_ok").click(function(){
			var a=$.trim($(".box_con .dr_addr").val().toString());
			if(a.length==0){
				$(".yzm_tip").hide();
				return;
			}
			else if(a.length==6){
				$(".yzm_tip").hide();
				document.getElementById("downloadfile").submit();
				$(".box_con .dr_addr").val("");
			}
			else{
				$(".box_con .dr_addr").val("");
				$(".yzm_tip").show();
			}
		});
		$(".passwordRepeat").click(function(){
			document.getElementById("updatePassword").submit();
		});
		$(".updateEmail").click(function(){
			var patn_email =/^((([_a-zA-Z0-9\-]{3,})+(\.[_a-zA-Z0-9\-]*)*@[a-zA-Z0-9\-]+([\.][a-zA-Z0-9\-]+)+))$/; 
			var val=$(".email").val();
			if(!patn_email.test(val))
				alert("邮件格式不对");
			else
			document.getElementById("updateEmail").submit();
		});
		$(".tqbf_ok").click(function(){
			document.getElementById("downloadBackup").submit();
		});
		$(".login_ok").click(function(){ //登录
			document.getElementById("login").submit();
		});
		$(".nologin_box .login_ok").click(function(){ //登录
			document.getElementById("nologin").submit();
		});
		$(".downapk").click(function(){
			document.getElementById("downloadApk").submit();
		});
	});
