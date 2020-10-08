package bytedance.send;

import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/verify")
public class VerifyServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String verify_code = request.getParameter("verify_code");
        String phone_no = request.getParameter("phone_no");

        System.out.println("获取用户提交的验证码是"+verify_code);
        if (verify_code == null){
            return;
        }
        Jedis jedis=new Jedis("192.168.198.129",6379);
        //从redis中获取此电话号码的验证码
        String s = jedis.get("PN_" + phone_no);
        System.out.println("从redis获取的'PN_'"+phone_no+"的验证码是"+s);
        if (verify_code.equals(s)){
            response.getWriter().write("true");
        } else {
            response.getWriter().write("false");
        }
        //务必注意！jedis要关闭
        jedis.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("到达了doGet方法");
        doPost(request,response);
    }
}
