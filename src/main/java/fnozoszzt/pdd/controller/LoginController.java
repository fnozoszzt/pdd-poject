package fnozoszzt.pdd.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.List;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger("login");

    @RequestMapping("/logon")
    public void login (HttpServletRequest request, HttpServletResponse response, String code) throws Exception {


        if (code == null) {
            
        }
        //logger.info("= = =");
    }
}
