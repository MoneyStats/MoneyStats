package com.moneystats.generic.timeTracker;

import org.slf4j.Logger;

import java.time.Instant;

public class LogTimeTracker {
  private ActionType actionType;
  private String methodName;
  private String parameters;
  private String correlationId;
  private long start;

  public LogTimeTracker(
      ActionType actionType, String methodName, String parameters, String correlationId) {
    super();
    this.actionType = actionType;
    this.methodName = methodName;
    this.parameters = parameters;
    this.correlationId = correlationId;
    this.start = Instant.now().toEpochMilli();
  }

  public void trackFailure(Logger LOG, Exception e) {
    LOG.error(
        "ACTION_TYPE: {}, METHOD: {}, CORRELATION_ID: {}, TIME_PROCESS: {}, STATUS: KO, EXCEPTION: {}, DESCRIPTION: {}",
        this.actionType,
        this.methodName,
        this.correlationId,
        getDeltaInMilli(),
        e.getClass().getName(),
        e.getLocalizedMessage());
  }

  public void trackSuccess(Logger LOG) {
    LOG.info(
        "ACTION_TYPE: {}, METHOD: {}, CORRELATION_ID: {}, TIME_PROCESS: {}, STATUS: OK, PARAMETERS: {}",
        this.actionType,
        this.methodName,
        this.correlationId,
        getDeltaInMilli(),
        this.parameters);
  }

  private long getDeltaInMilli() {
    return Instant.now().toEpochMilli() - this.start;
  }

  public static LogTimeTracker startInvocation(
      ActionType type, String methodName, String correlationId, String parameters) {
    return new LogTimeTracker(type, methodName, correlationId, parameters);
  }

  public static enum ActionType {
    APP_DATABASE_ENDPOINT,
    APP_SERVICE_LOGIC,
    APP_WEB_SERVICE,
    APP_TOKEN_SERVICE,
    APP_VALIDATOR,
  }
}
