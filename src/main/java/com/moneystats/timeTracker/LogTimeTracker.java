package com.moneystats.timeTracker;

import com.moneystats.authentication.DTO.TokenDTO;
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
        "action_type: {}, method: {}, parameters: {}, correlation_id: {}, time_tracker: {}, status: KO, exception: {}, description: {}",
        this.actionType,
        this.methodName,
        this.parameters,
        this.correlationId,
        getDeltaInMilli(),
        e.getClass().getName(),
        e.getMessage());
  }

  public void trackSuccess(Logger LOG) {
    LOG.info(
        "action_type: {}, method: {}, parameters: {}, correlation_id: {}, time_tracker: {}, status: OK",
        this.actionType,
        this.methodName,
        this.parameters,
        this.correlationId,
        getDeltaInMilli());
  }

  private long getDeltaInMilli() {
    return Instant.now().toEpochMilli() - this.start;
  }

  public static LogTimeTracker startInvocation(
      ActionType type, String methodName, String parameters, String correlationId) {
    return new LogTimeTracker(type, methodName, parameters, correlationId);
  }

  public static enum ActionType {
    APP_ENDPOINT,
    APP_LOGIC,
    APP_EXTERNAL
  }
}
