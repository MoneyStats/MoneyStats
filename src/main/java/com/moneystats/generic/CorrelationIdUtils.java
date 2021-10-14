package com.moneystats.generic;

import java.util.UUID;

public class CorrelationIdUtils {
  public static String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }

  private static final ThreadLocal<String> correlationIdThreadLocal =
      new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
          return null;
        }
      };

  public static void setCorrelationIdThreadLocal(String correlationId) {
    correlationIdThreadLocal.set(correlationId);
  }

  public static String getCorrelationIdThreadLocal() {
    return correlationIdThreadLocal.get();
  }
}
