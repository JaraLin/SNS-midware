package com.sns.midware.pool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.springframework.beans.BeanUtils;

import com.sns.midware.model.BasedSNSProcesser;
import com.sns.midware.model.SNSRequester;

public class SNSProcesserFactory extends BaseKeyedPoolableObjectFactory {
	private Map<String,SNSProcesserDefine> processDefines = new HashMap<String,SNSProcesserDefine>();;

	public SNSProcesserFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object makeObject(Object key) throws Exception {
		SNSProcesserDefine define = this.processDefines.get(key);
		
		Class<?> processClz =define.getProcessClass();
		Class<?> requestClz =define.getRequestClass();
		
		SNSRequester requester = (SNSRequester)BeanUtils.instantiateClass(requestClz);
		
		BasedSNSProcesser processer= (BasedSNSProcesser)BeanUtils.instantiateClass(processClz);
		processer.setName((String)key);
		processer.setRequester(requester);
		return processer;
	}

	public void setProcessDefines(List<SNSProcesserDefine> processDefines) {
		for (SNSProcesserDefine define : processDefines) {
			this.processDefines.put(define.getName(),define);
		}
	}

}
