package bytedance.send;

import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

@WebServlet(value = "/send")
public class SendServlet extends HttpServlet {
    //验证码的提交主要是表单提交，所以在doPost()方法内写
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String phone_no = request.getParameter("phone_no");
        if(phone_no == null){
            return;
        }
        //生成验证码之前先确认这个号码今天发了几次了，超过三次都不给它生成验证码，直接返回limit
        //前端收到返回为"limit"时，在页面显示今天发送的验证码超过3次，明天再试；
        String countKey="PN_CNT_"+phone_no;//在redis中存的key，表示此号码获取验证码的次数
        Jedis jedis=new Jedis("192.168.198.129",6379);
        String countStr = jedis.get(countKey);//从redis中读取当前手机号取验证码的次数
        if(countStr==null){//redis中没找打，是第一次获取
            jedis.setex(countKey,86400,"1");//设置记录此手机号请求验证码次数为1，此kv一天后清除
        }else {
            Integer count=Integer.parseInt(countStr); //redis中有，说明之前申请过
            if(count<3){//3次以内允许，超过三次明天再试
                jedis.incr(countKey);//为redis的一个数据+1
            }else{ //超过3次，直接返回limit
                response.getWriter().write("limit");
                return;
            }
        }

        String verifyCode = genCode(6);
        System.out.println(phone_no+"的验证码是"+verifyCode);

        jedis.psetex("PN_"+phone_no,10000,verifyCode);
//        jedis.set("PN_"+phone_no,verifyCode); //为每个电话号码加前缀"PN_"，作为key存入redis中
        response.getWriter().write("true");
        //务必注意！jedis要关闭
        jedis.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("到达了doGet方法");
        doPost(request,response);
    }

    //随机生成6位验证码的方法
    private String genCode(int len) {
        String code = "";
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            int rand = random.nextInt(10);
            code += rand;
        }
        return code;
    }
}
