package com.macys.uop.foundation.core.utils.web.interceptor;

import static com.macys.uop.foundation.core.utils.Constant.CALLERID_DEFAULT_VALUE;
import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.macys.uop.foundation.core.utils.Constant;

/**
 * Interface contract which helps assigning default values to incoming Http headers.
 * 
 * @see <a href="https://confluence.federated.fds/display/OCOM/UOP+Request+-+Headers+handlers+and+validations">Header Default Value Rules</a>
 */
public interface IRequestProcessor {

	/**
	 * Utility method to populate {@link HttpHeaders} from  {@link HttpServletRequest}
	 * 
	 * @param request {@link HttpServletRequest}
	 * @return {@link HttpHeaders}
	 */
	default HttpHeaders extractHeaders(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
			String name = names.nextElement();
			for (Enumeration<String> values = request.getHeaders(name); values.hasMoreElements();) {
				String value = values.nextElement();
				headers.add(name, value);
			}
		}
		return headers;
	}
	
	/**
	 * Utility method to populate query parameters inside MultiValueMap<String, String> from  {@link HttpServletRequest}
	 * 
	 * @param request {@link HttpServletRequest}
	 * 
	 * @return MultiValueMap<String, String>
	 */
	default MultiValueMap<String, String> extractQueryParameters(HttpServletRequest request) {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements();) {
			String name = names.nextElement();
			String[] paramvalues = request.getParameterValues(name);
			for (int i = 0; i < paramvalues.length; i++) {
				queryParams.add(name, paramvalues[i]);
			}
		}
		return queryParams;
	}

	/**
	 * Evaluate {@link Constant#CLIENTID_HDR} from  {@link HttpServletRequest}
	 * 
	 * @param request {@link HttpServletRequest}
	 * 
	 * @return {@link Constant#CLIENTID_HDR}
	 */
	default String getCallerId(HttpServletRequest request) {
		String callerId = null;
		String clientId = request.getHeader(CLIENTID_HDR);
		if (clientId != null) {
			callerId = clientId;
		} else {
			callerId = CALLERID_DEFAULT_VALUE;
		}
		return callerId;
	}
	
	public HttpHeaders processHeaders(HttpServletRequest request);

}
