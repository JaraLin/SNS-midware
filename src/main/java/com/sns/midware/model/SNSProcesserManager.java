package com.sns.midware.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sns.midware.config.ConstValues;
import com.sns.midware.config.ResponseCodes;
import com.sns.midware.exceptions.InvalidRequestException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.exceptions.UnknownExceptioin;
import com.sz.util.json.JsonConvertException;
import com.sz.util.json.JsonConverter;

public class SNSProcesserManager {
	Logger log =  Logger.getLogger(getClass());
	
	public static Map<String, Method> actionHandlers = new HashMap<String, Method>();
	static {
		actionHandlers=getActionsMethodsByClass(SNSRequester.class);
	}
	//snsType=>(sessionId=>SNSProcesser);
	private Map<String,Map<String,ISNSProcesser>> activeProcesserList=new HashMap<String,Map<String,ISNSProcesser>>();
	//for one session Mode, will always fetch the first session for a specified snstype, and needn't sessionId.
	private boolean oneSessionMode=true;
	
	// will use object pool to manager all SNSprocessers.
	private KeyedObjectPool pool;
	

	public SNSProcesserManager() {
		// TODO Auto-generated constructor stub
	}

	private ISNSProcesser getSNSProcesserFromPool(String snsName) throws SNSMidwareBaseException {
		try {
			return (ISNSProcesser)getPool().borrowObject(snsName);
		} catch (NoSuchElementException e) {
			log.error("Get processer fail!", e);
		} catch (IllegalStateException e) {
			log.error("Get processer fail!", e);
		} catch (Exception e) {
			log.error("Get processer fail!", e);
		}
		throw new SNSMidwareBaseException(ResponseCodes.AuthFail,"Can't get a Processer to handle the request for "+ snsName);
	}

	public ISNSProcesser getSNSProcesser(JSONObject request)
			throws SNSMidwareBaseException {

		try {
			String action = request.getString(ConstValues.REQUEST_METHOD_KEY);
			if(StringUtils.isEmpty(action)){
				throw new InvalidRequestException("Miss the field method.");
			}
			String snstype = request.getString(ConstValues.REQUEST_SNS_TYPE_KEY);
			
			if(StringUtils.isEmpty(snstype)){
				throw new InvalidRequestException("Miss the field type.");
			}
			
			String sessionId = request.getString(ConstValues.MESSAGE_SESSIONID);
			if (StringUtils.isEmpty(sessionId)){
				//Create a new one,
				sessionId = this.createSessinId();
			}
			
			
			ISNSProcesser processer = this.getActiveProcesser(snstype, sessionId);
			if (processer == null) {
				processer = this.getSNSProcesserFromPool(snstype);
				this.addToActiveList(snstype, sessionId, processer);
			}
			Method m = actionHandlers.get(action);
			if(m==null){
				//Try to get a method from the specified requester
				 Map<String, Method> handles = getActionsMethodsByClass(processer.getRequester().getClass());
				 m = handles.get(action);
			}
			if(m==null){
				throw new InvalidRequestException("The method " + action
				+ " is not supported!"); 
			}
			Class<?> parameter = m.getParameterTypes()[0];
			Object messsage = null;
			if (parameter.isInstance(request)){
				messsage = request;
			}else{
				messsage = JsonConverter.json2Object(request, (Class<?>)parameter);
			}
//			String method = messsage.getMethod();
			processer.setAction(m);
//			processer.setRawRequest(request);
			processer.setRequestMessage(messsage);
			
			return processer;
		} catch (JsonConvertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		throw new UnknownExceptioin("Unkonow Error occur!"); 
	}
	
	private void addToActiveList(String snstype, String sessionId,
			ISNSProcesser processer) {
		Map<String, ISNSProcesser> map = activeProcesserList.get(snstype);
		if (map == null) {
			map =new HashMap<String, ISNSProcesser>();
		}
		map.put(sessionId, processer);
		
	}

	private ISNSProcesser getActiveProcesser(String snstype, String sessionId) {
		Map<String, ISNSProcesser> map = activeProcesserList.get(snstype);
		if (map == null) {
			return null;
		}
		if (this.isOneSessionMode()) {
			for (ISNSProcesser processer : map.values()) {
				return processer;
			}
		}
		return map.get(sessionId);
	}

	private String createSessinId() {
		return UUID.randomUUID().toString();
	}

	public void returnProcesser(ISNSProcesser processer){
		if (processer !=null){
			if(!processer.isReadyToPool()){
				log.info("Can't return it to pool!");
				return;
			}
			try {
				Object snsName = processer.getName();
				pool.returnObject(snsName , processer);
			} catch (Exception e) {
				log.error("return processer to pool fail!", e);
			}
		}
	}

	public KeyedObjectPool getPool() {
		return pool;
	}

	public void setPool(KeyedObjectPool pool) {
		this.pool = pool;
	}
	
	private static Map<String, Method> getActionsMethodsByClass(Class<?> clazz) {
		Map<String, Method> actionHandlers = new HashMap<String, Method>();
		Method[] methods=clazz.getMethods();
		for (Method method : methods) {
			boolean isAction=method.isAnnotationPresent(Action.class);
			if (!isAction){
				continue;
			}
			Action action=	method.getAnnotation(Action.class);
			String actionName = action.value();
			actionHandlers.put(actionName, method);
		}
		return actionHandlers;
	}

	public boolean isOneSessionMode() {
		return oneSessionMode;
	}

	public void setOneSessionMode(boolean oneSessionMode) {
		this.oneSessionMode = oneSessionMode;
	}

}
