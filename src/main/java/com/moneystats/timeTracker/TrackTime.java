package com.moneystats.timeTracker;

import com.moneystats.authentication.DTO.TokenDTO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.moneystats.timeTracker.LogTimeTracker.ActionType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TrackTime {

  ActionType type();
}
