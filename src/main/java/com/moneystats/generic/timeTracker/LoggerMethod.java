package com.moneystats.generic.timeTracker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.moneystats.generic.timeTracker.LogTimeTracker.ActionType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoggerMethod {

  ActionType type();
}
