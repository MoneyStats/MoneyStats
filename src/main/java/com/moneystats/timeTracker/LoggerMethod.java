package com.moneystats.timeTracker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.moneystats.timeTracker.LogTimeTracker.ActionType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoggerMethod {

  ActionType type();
}
