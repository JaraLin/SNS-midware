package com.sns.midware.pool;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import com.sns.midware.model.BasedSNSProcesser;

public class SNSProcesserDefine {
	Logger log =  Logger.getLogger(getClass());
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getProcessClass() {
		return processClazz;
	}

	public void setProcessClazz(String Clazz) {
		this.processClazz = setClazz(Clazz);
	}

	public Class<?> getRequestClass() {
		return requestClazz;
	}

	public void setRequestClazz(String Clazz) {
		this.requestClazz = setClazz(Clazz);
	}

	private String name;
	private Class<?> processClazz = BasedSNSProcesser.class;
	private Class<?> requestClazz;

	public SNSProcesserDefine() {
		// TODO Auto-generated constructor stub
	}
	
	private Class<?> setClazz(String clz) {
		try {
			return  ClassUtils.getClass(clz);
		} catch (ClassNotFoundException e) {
			log.fatal("Can't find the class " + clz);
		}
		return null;
	}

}
