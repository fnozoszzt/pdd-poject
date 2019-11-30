//package fnozoszzt.pdd.interceptor;
//
//import com.alibaba.fastjson.JSONObject;
//import org.apache.log4j.Logger;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.ArrayList;
//
//@Aspect
//@Component
//public class LogAspect {
//    private static Logger logger = Logger.getLogger(LogAspect.class);
//
//    @Around("execution(public * fnozoszzt.pdd.controller.*.*(..))")
//    public Object LogAspect(ProceedingJoinPoint point) throws Throwable {
//        long start = System.currentTimeMillis();
//        Object result = null;
//        Throwable th = null;
//
//        try {
//            result = point.proceed();
//        } catch (Throwable throwable) {
//            th = throwable;
//            throw th;
//        } finally {
//            addLog(point, th, (System.currentTimeMillis() - start));
//        }
//
//        return result;
//    }
//
//    private void addLog(ProceedingJoinPoint joinPoint, Throwable e, long time) {
//        String args = "";
//        try{
//            ArrayList<Object> printedArgs = new ArrayList<Object>();
//            for (Object arg: joinPoint.getArgs()){
//                if (!(arg instanceof MultipartFile ||
//                        arg instanceof HttpServletResponse ||
//                        arg instanceof HttpServletRequest)){
//                    printedArgs.add(arg);
//                }
//            }
//
//            args = JSONObject.toJSONString(printedArgs);
//        }catch(Exception e1){
//            logger.error(e.getMessage(), e);
//        }
//
//        String method = joinPoint.getSignature().getName();
//        String log = String.format("#### method = %s, args = %s" + ",cost_time=%s", method, args, time);
//        if (e == null) {
//            logger.info(log);
//        } else {
//            logger.error(log, e);
//        }
//    }
//}
