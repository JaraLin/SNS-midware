package com.sz.util.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

public class ObjectAttributesAnnotationScanner {

	private static Logger log = Logger
			.getLogger(ObjectAttributesAnnotationScanner.class);

	public Map<String, FieldStructsBean> generalObjectAttributesMapping(
			Class objClass) throws JsonConvertException {
		Map<String, FieldStructsBean> fieldMapping = scanClassAttribute(objClass);
		Class supperClass = objClass.getSuperclass();
		if (supperClass != Object.class) {
			Map<String, FieldStructsBean> supperFieldMapping = generalObjectAttributesMapping(supperClass);

			return mergeSupperClass(fieldMapping, supperFieldMapping);
		}
		return fieldMapping;
	}

	/**
	 * Only scan public get method and set method
	 * 
	 * @param obj
	 * @return
	 * @throws JsonConvertException
	 */
	private Map<String, FieldStructsBean> scanClassAttribute(Class objClass)
			throws JsonConvertException {
		if (Collection.class.isAssignableFrom(objClass)
				|| Map.class.isAssignableFrom(objClass)
				|| !isNotJavaClass(objClass))
			return null;
		Map<String, FieldStructsBean> fieldNameMapping = new Hashtable<String, FieldStructsBean>();
		log.debug("Scanning Fields annotation[" + objClass + "]");
		Field fields[] = objClass.getDeclaredFields();
		// set access permission
		Field.setAccessible(fields, true);
		for (Field field : fields) {
			String fieldName = field.getName();
			Alias aliasObj = field.getAnnotation(Alias.class);
			String alias = fieldName;
			if (null != aliasObj) {
				alias = aliasObj.value();
			}
			// log.info("Field \"" + fieldName + "\" -> \"" + alias + "\"");

			FieldStructsBean aliasBean = new FieldStructsBean();
			aliasBean.setOriginalFieldName(fieldName);
			aliasBean.setAliasFieldName(alias);

			String getMethodPrefix = field.getType() == boolean.class ? fieldName
					.matches("[i][s][A-Z][\\S]*") ? "" : "is" : "get";
			String getMethodName = getMethodPrefix
					+ (field.getType() == boolean.class
							&& fieldName.matches("[i][s][A-Z][\\S]*") ? fieldName
							: fieldName.substring(0, 1).toUpperCase()
									+ fieldName.substring(1));

			String setMethodName = "set"
					+ (field.getType() == boolean.class
							&& fieldName.matches("[i][s][A-Z][\\S]*") ? fieldName
							.substring(2) : fieldName.substring(0, 1)
							.toUpperCase() + fieldName.substring(1));

			try {
				Method setMethod = objClass.getMethod(setMethodName,
						field.getType());
				Method getMethod = objClass.getMethod(getMethodName);
				if (null == setMethod)
					throw new Exception(setMethodName + " in class " + objClass
							+ " not found!");
				if (null == getMethod)
					throw new Exception(getMethodName + " in class " + objClass
							+ " not found!");
			} catch (Exception e) {
				throw new JsonConvertException(e);
			}

			aliasBean.setFieldSetMethodName(setMethodName);
			aliasBean.setFieldGetMethodName(getMethodName);
			aliasBean.setFieldType(field.getType());
			if (Collection.class.isAssignableFrom(field.getType())
					|| Map.class.isAssignableFrom(field.getType())) {
				Type fieldGenericType = field.getGenericType();
				if (!(fieldGenericType instanceof ParameterizedType)
						&& !Properties.class.isAssignableFrom(field.getType())) {
					throw new JsonConvertException(
							"Must assign Generic Type to this attribute ["
									+ field.getType() + " "
									+ objClass.getName() + "." + fieldName
									+ "]");
				}
				// Class genericClazz = (Class) pt.getActualTypeArguments()[0];
				// if(isNotJavaClass(genericClazz.getName())){
				// aliasBean.setReferanceTypeAttributeMapping(scanClassAttribute(genericClazz));
				// }
				if (Map.class.isAssignableFrom(field.getType())
						&& !Properties.class.isAssignableFrom(field.getType())) {
					ParameterizedType pt = (ParameterizedType) fieldGenericType;
					Class mapKeyClazz = (Class) pt.getActualTypeArguments()[0];
					if (mapKeyClazz != String.class)
						throw new JsonConvertException(
								"The key Generic Type of Map must be String !");

					// aliasBean.setMapValueGenericMapping(scanClassAttribute(mapValueClazz));
				}

			}
			aliasBean.setOriginalField(field);

			// try {
			// System.out.println("class : "+field.getType().toString());
			// if(isNotJavaClass(field.getType().getName())){
			// aliasBean.setReferanceTypeAttributeMapping(scanClassAttribute(aliasBean.getFieldType()));
			// }
			// } catch (Exception e) {
			// throw new TN3270Exception(e);
			// }

			fieldNameMapping.put(fieldName, aliasBean);
		}

		log.debug("End Scanning Fields annotation[" + objClass + "]");

		return fieldNameMapping;
	}

	private Map<String, FieldStructsBean> mergeSupperClass(
			Map<String, FieldStructsBean> subFieldMapping,
			Map<String, FieldStructsBean> supperFieldMapping) {
		if (null == subFieldMapping || subFieldMapping.size() == 0)
			return supperFieldMapping;
		if (null == supperFieldMapping || supperFieldMapping.size() == 0)
			return subFieldMapping;
		Set<String> keySet = supperFieldMapping.keySet();
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			String fieldName = (String) iterator.next();
			if (subFieldMapping.containsKey(fieldName))
				continue;
			subFieldMapping.put(fieldName, supperFieldMapping.get(fieldName));
		}
		return subFieldMapping;
	}

	public boolean isNotJavaClass(Class clazz) {
		return !(clazz.getName().startsWith("java.lang") || clazz.isPrimitive())
				&& !clazz.isArray();
	}

	public boolean isNotJavaClass(String clazzStr)
			throws ClassNotFoundException {
		Class clazz = Class.forName(clazzStr);
		return isNotJavaClass(clazz);
	}

}
