package com.moneystats.timeTracker;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.generic.CorrelationIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;

@Interceptor
@Logged
public class LoggerInterceptor implements Serializable {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  @Autowired private TokenService tokenService;

  @AroundInvoke
  public Object processMethod(InvocationContext context) throws Exception {
    Method method = context.getMethod();

    TrackTime annotations = method.getAnnotation(TrackTime.class);

    if (annotations == null) {
      return context.proceed();
    }

    String className = method.getDeclaringClass().getName();
    String methodName = className + "." + method.getName();
    TokenDTO tokenDTO = new TokenDTO(null);
    AuthCredentialDTO authCredentialDTO = tokenService.parseToken(tokenDTO);
    String userId = authCredentialDTO.getUsername();

    LogTimeTracker tracker =
        LogTimeTracker.startInvocation(
            annotations.type(),
            methodName,
            userId,
            CorrelationIdUtils.getCorrelationIdThreadLocal());

    Object obj;
    try {
      obj = context.proceed();
    } catch (Exception e) {
      tracker.trackFailure(LOG, e);
      throw e;
    }
    tracker.trackSuccess(LOG);

    return obj;
  }
}
