<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Insert title here</title>
  <script  src="/phoneVerify/static/jquery/jquery-3.1.0.js" ></script>
  <link href="/phoneVerify/static/bs/css/bootstrap.min.css" rel="stylesheet" />
  <script  src="/phoneVerify/static/bs/js/bootstrap.min.js"   ></script>
  <%--  这里需要修改为当前的项目名--%>

</head>
<body>
<div class="container">
  <div class="row">
    <div id="alertdiv" class="col-md-12">
      <form class="navbar-form navbar-left" role="search" id="codeform">
        <div class="form-group">
          <input type="text" class="form-control" placeholder="填写手机号" name="phone_no" value="">
          <button type="button" class="btn btn-default" id="sendCode">发送验证码</button><br>
          <font id="countdown" color="red" ></font>
          <br>
          <input type="text" class="form-control" placeholder="填写验证码" name="verify_code">
          <button type="button" class="btn btn-default" id="verifyCode">确定</button>
          <font id="result" color="green" ></font><font id="error" color="red" ></font>
        </div>
      </form>
    </div>
  </div>
</div>
</body>


<script type="text/javascript">
    var t=10;//设定倒计时的时间
    var interval;
    function refer(){
      $("#countdown").text("请于"+t+"秒内填写验证码 "); // 显示倒计时
      t--; // 计数器递减
      if(t<=0){
        clearInterval(interval);
        $("#countdown").text("验证码已失效，请重新获取新验证码！ ");
      }
    }

    $("#sendCode").click( function () {
      $.post("/VerifyCode/send",$("#codeform").serialize(),function(data){
        if(data=="true"){ //代表服务器端接收成功，已发送验证码，所以开启验证码倒计时
          t=10; //初始化参数，接下来开启读秒倒计时
          clearInterval(interval); //将当前的读秒状态清除
          interval= setInterval("refer()",1000);//每一秒调用一次refer()函数，直到遇到clearInterval(xx)才停止
        }else if (data=="limit"){
          clearInterval(interval);
          $("#countdown").text("单日发送超过次数！ ")
        }
      });
    });

    $("#verifyCode").click( function () {
      $.post("/VerifyCode/verify",$("#codeform").serialize(),function(data){
        if(data=="true"){
          $("#result").attr("color","green");
          $("#result").text("验证成功");
          clearInterval(interval); //验证成功则停止倒计时读秒
          $("#countdown").text("")
        }else{
          $("#result").attr("color","red");
          $("#result").text("验证失败");
        }
      });
    });

</script>
</html>