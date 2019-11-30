package fnozoszzt.pdd.interceptor;

import fnozoszzt.pdd.bean.User;
import fnozoszzt.pdd.common.Const;
import fnozoszzt.pdd.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@WebFilter(filterName="LoginFilter",urlPatterns="/*")
@Order(3)
public class LoginFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger("login");

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("RequestsLogFilter");

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
        String uri = request.getRequestURI();
        if (uri.startsWith("/login") || uri.startsWith("/logon") || uri.startsWith("/logout")) {
            logger.info("skip check");
            filterChain.doFilter(requestWrapper, servletResponse);
            return;
        }

        HttpSession httpSession = requestWrapper.getSession();
        String session = httpSession.getId();
        logger.info("session {}", session);

        User user = (User) httpSession.getAttribute("user");

        logger.info("{}", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/");

        if (user == null) {
            logger.info("sendRedirect");
            response.setHeader("Cache-Control", "no-cache");
            response.sendRedirect("https://mms.pinduoduo.com/open.html?response_type=code&client_id=fe1143f5f21a41e2980bad386b4bc2dc&redirect_uri=http://127.0.0.1:8888/logon&state=1212");
        } else {
            filterChain.doFilter(requestWrapper, servletResponse);
        }

    }

    @Override
    public void destroy() {

    }

    static class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final String body;

        public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            body = getBodyString(request.getReader());
        }

        private static String getBodyString(BufferedReader br) {
            String inputLine;
            String str = "";
            try {
                while ((inputLine = br.readLine()) != null) {
                    str += inputLine;
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e);
            }
            return str;
        }

        public String getBody() {
            return body;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return true;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {

                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }
    }

}
