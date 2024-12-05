package com.macys.uop.foundation.core.utils.execution;

import static com.macys.uop.foundation.core.utils.Constant.OPERATION_NOT_SUPPORTED_YET;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.context.request.RequestAttributes;


/**
 * Placeholder for storing attribute objects associated with a request.
 * <br>
 * Used in Pub/Sub scenario to store context object.
 *
 */
public class CustomRequestAttributes implements RequestAttributes
{
	private Map<String,Object> attributeMap=new ConcurrentHashMap<>();
	
	/**
	 * Get attribute value for the given scope
	 */
	@Override
	public Object getAttribute(String name, int scope) {
		return attributeMap.get(name+scope);
	}

	/**
	 * Set the attribute value in given scope
	 */
	@Override
	public void setAttribute(String name, Object value, int scope) {
		attributeMap.put(name+scope,value);
	}

	/**
	 * Remove attribute for the given scope
	 */
	@Override
	public void removeAttribute(String name, int scope) {
		attributeMap.remove(name+scope);
	}

	/**
	 * Operation Currently Not Supported. Throws Exception
	 */
	@Override
	public String[] getAttributeNames(int scope) {
		throw new java.lang.UnsupportedOperationException(OPERATION_NOT_SUPPORTED_YET);
	}
	
	/**
	 * Operation Currently Not Supported. Throws Exception
	 */
	@Override
	public void registerDestructionCallback(String name, Runnable callback, int scope) {
		throw new java.lang.UnsupportedOperationException(OPERATION_NOT_SUPPORTED_YET);
	}
	
	/**
	 * Operation Currently Not Supported. Throws Exception
	 */
	@Override
	public Object resolveReference(String key) {
		throw new java.lang.UnsupportedOperationException(OPERATION_NOT_SUPPORTED_YET);
	}
	
	/**
	 * Operation Currently Not Supported. Throws Exception
	 */
	@Override
	public String getSessionId() {
		throw new java.lang.UnsupportedOperationException(OPERATION_NOT_SUPPORTED_YET);
	}
	
	/**
	 * Operation Currently Not Supported. Throws Exception
	 */
	@Override
	public Object getSessionMutex() {
		throw new java.lang.UnsupportedOperationException(OPERATION_NOT_SUPPORTED_YET);
	}

}
