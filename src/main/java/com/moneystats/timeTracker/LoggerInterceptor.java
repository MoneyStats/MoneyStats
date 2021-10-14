package com.moneystats.timeTracker;

import com.moneystats.generic.CorrelationIdUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;

@Aspect
@Component
public class LoggerInterceptor {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  @Autowired private HttpServletRequest request;

  @Around("@annotation(TrackTime)")
  public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
    LOG.error("Method {}", request.getMethod());
    Method method = joinPoint.getClass().getMethod(request.getMethod());
    TrackTime annotations = method.getAnnotation(TrackTime.class);

    if (annotations == null) {
      return joinPoint.proceed();
    }
    String className = method.getDeclaringClass().getName();
    String methodName = className + "." + method.getName();
    String parameters = getParameters(request);

    LogTimeTracker tracker =
        LogTimeTracker.startInvocation(
            annotations.type(),
            methodName,
            parameters,
            CorrelationIdUtils.getCorrelationIdThreadLocal());
    Object obj;
    try {
      obj = joinPoint.proceed();
    } catch (Exception ex) {
      tracker.trackFailure(LOG, ex);
      throw ex;
    }
    tracker.trackSuccess(LOG);
    return obj;
  }

  private String getParameters(HttpServletRequest request) {
    StringBuffer posted = new StringBuffer();
    Enumeration<?> e = request.getParameterNames();
    if (e != null) {
      posted.append("?");
    }
    while (e.hasMoreElements()) {
      if (posted.length() > 1) {
        posted.append("&");
      }
      String curr = (String) e.nextElement();
      posted.append(curr + "=");
      if (curr.contains("password") || curr.contains("pass") || curr.contains("pwd")) {
        posted.append("*****");
      } else {
        posted.append(request.getParameter(curr));
      }
    }
    String ip = request.getHeader("X-FORWARDED-FOR");
    String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
    if (ipAddr != null && !ipAddr.equals("")) {
      posted.append("&_psip=" + ipAddr);
    }
    return posted.toString();
  }

  private String getRemoteAddr(HttpServletRequest request) {
    String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
    if (ipFromHeader != null && ipFromHeader.length() > 0) {
      LOG.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
      return ipFromHeader;
    }
    return request.getRemoteAddr();
  }
}
