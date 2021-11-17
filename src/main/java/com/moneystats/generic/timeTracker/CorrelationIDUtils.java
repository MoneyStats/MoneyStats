package com.moneystats.generic.timeTracker;

import java.util.UUID;

public class CorrelationIDUtils {
  public static String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }

  private static final ThreadLocal<String> correlationIdThreadLocal =
      new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
          return generateCorrelationId();
        }
      };

  public static void setCorrelationId(String correlationId) {
    correlationIdThreadLocal.set(correlationId);
  }

  public static String getCorrelationId() {
    return correlationIdThreadLocal.get();
  }
}
