package fnozoszzt.pdd.interceptor;

import com.netflix.ribbon.proxy.annotation.Http;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@WebFilter(filterName="RequestsLogFilter",urlPatterns="/*")
@Order(1)
public class RequestsLogFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger("RequestsLogFilter");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        //logger.info("servletRequest getCharacterEncoding {}", servletRequest.getCharacterEncoding());
        //logger.info("servletRequest map {}", servletRequest.getParameterMap());


        logger.info("RequestsLogFilter");

        HttpServletRequest request = (HttpServletRequest)servletRequest;

        ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(request);


        StringBuilder parameters = new StringBuilder();
        for (Map.Entry<String, String[]> entry: requestWrapper.getParameterMap().entrySet()) {
            parameters.append(entry.getKey());
            parameters.append(" : [");
            parameters.append(StringUtils.join(entry.getValue(), " & "));
            parameters.append("]; ");
        }
        logger.info("< {} {} {}", request.getMethod(), request.getRequestURI(), parameters);


        if (request.getMethod().equals(Http.HttpMethod.POST.toString())) {


            //JSONObject body = JSONObject.parseObject(((BodyReaderHttpServletRequestWrapper) requestWrapper).getBody());

            logger.info("{} >", ((BodyReaderHttpServletRequestWrapper) requestWrapper).getBody());

            /*productKey = body.getString("productKey");
            personalKey = body.getString("personalKey");
            email = body.getString("email");
            product = body.getString("product");*/
        } else {

            /*productKey = servletRequest.getParameter("productKey");
            personalKey = servletRequest.getParameter("personalKey");
            email = servletRequest.getParameter("email");
            product = servletRequest.getParameter("product");*/
            logger.info(">");
        }




        /*if (StringUtils.isEmpty(productKey) || StringUtils.isEmpty(personalKey) ||
                StringUtils.isEmpty(email) || StringUtils.isEmpty(product)) {

            servletResponse.setCharacterEncoding("utf-8");
            servletResponse.getWriter().write(
                    ResponseHelper.genErrorResult(
                            ErrorCode.SESSION_INVALID, ErrorMessage.AUTH_FAILED).toJSONString());
            return;
        }*/

        /*productKey =  URLDecoder.decode(productKey,"utf-8");
        personalKey =  URLDecoder.decode(personalKey,"utf-8");

        try {
            JSONObject result = dsMetaInvoker.getDSApiMeta(productKey, personalKey).getJSONObject("data");
            String retProduct = result.getString("product");
            String retEmail = result.getString("email");
            if (email.equals("grp.ndp@corp.netease.com")) {
                retProduct = product;
            }
            if (!StringUtils.equals(product, retProduct) || !StringUtils.equals(email, retEmail)) {
                servletResponse.setCharacterEncoding("utf-8");
                servletResponse.getWriter().write(
                        ResponseHelper.genErrorResult(
                                ErrorCode.SESSION_INVALID, ErrorMessage.AUTH_FAILED).toJSONString());
                return;
            }

            filterChain.doFilter(requestWrapper == null ? servletRequest : requestWrapper, servletResponse);
        }catch (Exception e){
            servletResponse.setCharacterEncoding("utf-8");
            servletResponse.getWriter().write(
                    ResponseHelper.genErrorResult(
                            ErrorCode.SESSION_INVALID, ErrorMessage.AUTH_FAILED).toJSONString());
        }*/
        try {
            filterChain.doFilter(requestWrapper == null ? servletRequest : requestWrapper, servletResponse);
        } catch (Exception e) {
            logger.error("", e);
        }
        HttpServletResponse response = (HttpServletResponse)servletResponse;


        logger.info("response {}", response.getStatus());
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
