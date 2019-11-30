package fnozoszzt.pdd.controller;

import com.alibaba.fastjson.JSONObject;
import fnozoszzt.pdd.bean.User;
import fnozoszzt.pdd.common.Const;
import fnozoszzt.pdd.dao.Dao;
import fnozoszzt.pdd.util.HttpUtil;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    Dao dao;


    @RequestMapping("/logon")
    public void logon(HttpServletRequest request, HttpServletResponse response, String code, String status) throws Exception {

        logger.info("logon code {} status {}", code, status);

        if (code == null) {
            throw new Exception("code null");
        }

        try {
            HashMap<String, String> head = new HashMap<>();
            head.put("Content-Type", "application/json");

            JSONObject body = new JSONObject();
            body.put("client_id", Const.CLIENT_ID);
            body.put("code", code);
            body.put("grant_type", "authorization_code");
            body.put("client_secret", Const.CLIENT_SECRET);

            String res = HttpUtil.doPost(Const.OAUTH_HOST, Const.OAUTH_PATH, head, new HashMap<>(), body.toJSONString().getBytes());

            logger.info("res {}", res);

            JSONObject json = JSONObject.parseObject(res);

            String owner_name = json.getString("owner_name");
            String access_token = json.getString("access_token");
            String refresh_token = json.getString("refresh_token");
            String owner_id = json.getString("owner_id");


            Date login_time = new Date();
            long login_timestamp = System.currentTimeMillis() / 1000;


            User user = new User();
            user.setOwner_name(owner_name);
            user.setAccess_token(access_token);
            user.setLogin_timestamp(login_timestamp);
            user.setOwner_id(owner_id);
            user.setRefresh_token(refresh_token);
            user.setStr(res);

            User user_ = dao.getUser(user);
            logger.info("user {}", user_);
            if (user_ == null) {
                dao.insertUser(user);
            } else {
                dao.updateUser(user);
            }
            dao.insertLoginHistory(user);


            request.getSession().setAttribute(Const.REDIS_KEY_USER, user);

        } catch (Exception e) {
            logger.error("", e);
        }





        //logger.info("= = =");
    }

    @RequestMapping("/out")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

    }
}