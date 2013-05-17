package com.sns.midware.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.sns.midware.config.ResponseCodes;
import com.sns.midware.data.response.ResponseMessage;
import com.sns.midware.exceptions.InvalidAccessTokenException;
import com.sns.midware.exceptions.SNSMidwareBaseException;
import com.sns.midware.model.ISNSProcesser;
import com.sns.midware.model.SNSProcesserManager;
import com.sun.jersey.api.spring.Autowire;

@Path("/")
@Autowire
public class ServiceResource {
	Logger log =  Logger.getLogger(getClass());
	
	@Autowired
	private SNSProcesserManager processerManager;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("service")
	// @QueryParam("name") String name,
	// @QueryParam("emmlId") int emmlId, @QueryParam("parameters")
	// List<ServiceParameter> parameters
	public Object publishService(JSONObject request) {
		// TODO save to database;
//		LoginRequest c = jaxbService.getValue();
		ISNSProcesser processer=null;
		try {
			log.info("request:" + request);
			processer = processerManager.getSNSProcesser(request);
			Object result = processer.request();
			log.info("response:" + result);
			//TODO some exception handle needed here.
			return result;
		} catch (InvalidAccessTokenException e) {
			//error code is 401;
			log.info(e.getMessage());
			return new ResponseMessage(e.getErrorCode(),e.getMessage());
		} catch (SNSMidwareBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("error occur", e);
			return new ResponseMessage(e.getErrorCode(),e.getMessage());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("error occur", e);
			String message = StringUtils.isBlank(e.getMessage())?"Unknow error occur":e.getMessage();
			return new ResponseMessage(ResponseCodes.UnknowError,message);
		}finally{
			if(processer!=null){
				processerManager.returnProcesser(processer);
			}
		}
		
	}

/*	@GET
	@Produces({MediaType.APPLICATION_XML ,MediaType.APPLICATION_JSON})
	@Path("delete/{id}")
	public ResponseStatus deleteService(@PathParam("id") int serviceId) {
		// TODO save to database;
		httpServiceDao.deleteService(serviceId);
		httpSvrParameterDao.deleteParameterByServiceId(serviceId);
		ResponseStatus status = new ResponseStatus("0000", "Delete service successfully.");
		return status;
	}

	@GET
	@Produces({MediaType.APPLICATION_XML ,MediaType.APPLICATION_JSON})
	@Path("changestatus/{id}")
	public ResponseStatus changeStatus(@PathParam("id") String serviceId,
			@QueryParam("statusCode") int statusCode) {
		// TODO save to database;

		ResponseStatus status = new ResponseStatus("0001", "Don't provided!");
		return status;
	}

	@GET
	@Produces({MediaType.APPLICATION_XML ,MediaType.APPLICATION_JSON})
	@Path("query/{id}")
	public Object queryService(@PathParam("id") int serviceId) {
		// TODO delete from database;
		if (test!=null){
			test.test();
		}
		EN_HttpService service=	httpServiceDao.getServiceById(serviceId);
		
		if (service ==null){
			ResponseStatus status = new ResponseStatus("0001",
					"Don't find any service!");
			return status;
		}

	    return PO2VO(service);
	}
	

	@GET
	@Produces({MediaType.APPLICATION_XML ,MediaType.APPLICATION_JSON})
	@Path("list")
	public Object listService(@DefaultValue("1") @QueryParam("page") int page,
			@DefaultValue("10") @QueryParam("numOfPage") int numOfPage) {
		if (page<0){
			return new ResponseStatus("1002",
					"page can't be less then 0");
		}
		
		if (numOfPage<=0){
			return new ResponseStatus("1002",
					"numOfPage must be larger than 0");
		}
		
		int start =  (page-1) * numOfPage;
		List<EN_HttpService> services=	httpServiceDao.listServices(start,numOfPage);
		
		if (CollectionUtils.isEmpty(services)){
			ResponseStatus status = new ResponseStatus("0001",
					"Don't find any service!");
			return status;
		}
		
		List<HttpService> result = new ArrayList<HttpService>();
		for (EN_HttpService e_Service : services) {
			HttpService s = PO2VO(e_Service);
			result.add(s);
		}
		return  new GenericEntity<List<HttpService>>(result) {};
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML ,MediaType.APPLICATION_JSON})
	@Path("search")
	public Object listService(@DefaultValue("") @QueryParam("q") String keyword) {
		if (StringUtils.isEmpty(keyword)){
			return new ResponseStatus("1002",
					"The keyword is necessary");
		}

		List<EN_HttpService> services=	httpServiceDao.searchService(keyword);
		
		if (CollectionUtils.isEmpty(services)){
			ResponseStatus status = new ResponseStatus("0001",
					"Don't find any service!");
			return status;
		}
		
		List<HttpService> result = new ArrayList<HttpService>();
		for (EN_HttpService e_Service : services) {
			HttpService s = PO2VO(e_Service);
			result.add(s);
		}
		return  new GenericEntity<List<HttpService>>(result) {};
	}

	private HttpService PO2VO(EN_HttpService e_Service) {
		HttpService s = new HttpService();
		s.setEmmlId(e_Service.getEmmlId());
		s.setName(e_Service.getName());
		s.setSid(e_Service.getSid());
		s.setDescription(e_Service.getDesc());
//		List<EN_ServiceParameter> temp =  httpSvrParameterDao.getParameters(e_Service.getSid());
		List<ServiceParameter> parameters = toVOList(e_Service.getParameters());
		s.setParameters(parameters );
		return s;
	}

	private List<ServiceParameter> toVOList(List<EN_ServiceParameter> temp) {
		 List<ServiceParameter> result = new ArrayList<ServiceParameter>();
		for (EN_ServiceParameter en_sp : temp) {
			ServiceParameter ssp = new ServiceParameter();
			ssp.setName(en_sp.getName());
			ssp.setDescription(en_sp.getDescription()); 
			ssp.setDefaultValue(en_sp.getDefaultvalue());
			ssp.setRequired(en_sp.getRequired());
			ssp.setType(en_sp.getPtype());
			ssp.setPid(en_sp.getPid());
			result.add(ssp);
		}
		return result;
	}
	
	@POST
	@Path("postCall")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML}) 
	@Produces({MediaType.APPLICATION_XML ,MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Object processRequset(JSONObject t) {
		String serviceId="";
		Properties pdata = new Properties();
		try {
			serviceId = t.getString("serviceId");
		
			if (StringUtils.isEmpty(serviceId)){
				return errorFeedback("0001","serviceId is needed!");
			}
		
			if (t.has("parameters")) {
				JSONObject paras =  t.getJSONObject("parameters");
				
				for (Iterator iterator = paras.keys(); iterator.hasNext();) {
					String name = (String) iterator.next();
					String value = paras.getString(name);
					pdata.put(name, value);
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return executeService(serviceId, pdata);
	}

	private Object executeService(String serviceId, Properties pdata) {
		String responseStr="";
		try {
			EN_HttpService service=	httpServiceDao.getServiceById(Integer.valueOf(serviceId));
			if (service == null){
				return errorFeedback("0001","The service dosen't exist!");
			}
			int emmlId= service.getEmmlId();
					
			EN_EMMLItmeWithBLOBs en_script = emmlScriptDao.getScriptById(emmlId);
			if (en_script == null) {
				return errorFeedback("0001","The emml script of the serivie dosen't exist!");
			}
			String endurl= this.settingDao.getOptionValue("endpoint");
			
			Netway net = new Netway(endurl);
			String emml = en_script.getInput();
			
			//			pdata.put("zip", "31092");
			pdata.put("script", emml );
			
			ResponseStatus checkParameters = this.checkParameters(PO2VO(service).getParameters(),pdata);
			if(checkParameters!=null){
				return checkParameters;
			}
			responseStr = net.postPage("emml/execute", pdata );
			if (StringUtils.isEmpty(responseStr)){
				responseStr="Error!";
			}
			return responseStr;
		} catch (Exception e) {
			e.printStackTrace();
			errorFeedback("1000","UnkownException:" + e.getMessage());
		}
		return "error occur!";
	}
	
	
	@GET
	@Path("getCall")
	@Produces({MediaType.APPLICATION_XML ,MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Object find( @Context UriInfo allUri ) {
	    MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
	    String serviceId = mpAllQueParams.getFirst("serviceId");
		if (StringUtils.isEmpty(serviceId)){
			return errorFeedback("0001","serviceId is needed!");
		}
	    
	    mpAllQueParams.remove("serviceId");
		Properties pdata = new Properties();
		
		for (Object kv : mpAllQueParams.entrySet()) {
			Map.Entry<String,?> entry = (Map.Entry<String,?>)kv;
			
			Object value = entry.getValue();
			if (value instanceof String[]){
				value = ((String[])value)[0];
			} else 		if (value instanceof List){
				value = ((List<?>)value).get(0);
			}
			pdata.put(entry.getKey(), value);
		}
	    return executeService(serviceId,pdata);
	}


	
	private ResponseStatus errorFeedback(String code,String msg){
		ResponseStatus status = new ResponseStatus(code,
				msg);
		return status;
	}
	
	private ResponseStatus checkParameters(List<ServiceParameter> parameters, Properties pdata) throws IOException {

		List<ServiceParameter> missed = new ArrayList<ServiceParameter>();
		List<ServiceParameter> erroyType = new ArrayList<ServiceParameter>();
		for (ServiceParameter sp : parameters) {
			String name = sp.getName();
			String type = sp.getType();
			boolean isRequired= sp.isRequired();
			if (isRequired && !pdata.containsKey(name)){
				missed.add(sp);
				continue;
			}
		}
		
		if(missed.size()==0){
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("Missed parameters:");
		for (ServiceParameter missedSp : missed) {
			sb.append(missedSp.getName()).append(";");
		}
		return errorFeedback("0003",sb.toString());
		
	}
*/

}
