package com.sns.midware.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Bean attributes annotation type
 * @author yaoyao
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
	public String value();
}
