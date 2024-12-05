package com.macys.uop.foundation.core.utils.validation;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SERVER_HEADER_VALIDATION_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.zalando.problem.Status;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.common.URIUtil;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the default implementation of {@link IHeaderCheck} provided by foundation which validates only {@link Constant#CORRELATIONID_HDR} header.
 * <br>
 * REST header validation is only applied on valid URL patterns {@link com.macys.uop.foundation.core.utils.common.URIUtil#isURIInExclusionList(String)}
 * <br>
 * @see <a href="https://confluence.federated.fds/display/OCOM/UOP+Request+-+Headers+handlers+and+validations">Header Validation Rules</a>
 */
@Service
@Slf4j
public class HeaderCheckServiceImpl implements IHeaderCheck, ServiceContextUtil, ProblemUtil, URIUtil {
	
	@Override
	public void validateHeaders(HttpServletRequest request) {
		String requestURI = getRequestURI();
		if (!isURIInExclusionList(requestURI)) {
			HttpHeaders headers=getHttpHeaders();
			boolean correlationIdHdrExists=checkHeaderAndValueExists(headers, CORRELATIONID_HDR);  
			if (!correlationIdHdrExists) {
				com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
						.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
						.withErrorDetail(ErrorDetail.builder().withDomain(getAppName()).withReason(HDR_REQUIRED_DEFAULT_REASON)
								.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation(getRequestURL())
								.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
						.build();
				
				new LogMessageBuilder()
				.withClientId(getClientId())
				.withMessageId(getMessageId())
				.withOrderId(getOrderId())
				.withCorrelationId(getCorrelationId())
				.withAppName(getAppName())
				.withCallerId(getCallerId())
				.withLogType(LogTypeEnum.ERROR)
				.withErrorCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withErrorMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withContext(CONTEXT_SERVER_HEADER_VALIDATION_LOGGING)
				.withEndpointUrl(getRequestURL())
				.withHeaderAttribute(getSingleValueHttpHeaders())
				.withLogger(log)
				.buildDisableChecking()
				.logAsError();
				
				
				throw createProblem(Status.BAD_REQUEST.getStatusCode(), error);
			}
		}
	}
	
	/**
	 * Check to see if header exists. If exists then check for header values.
	 * 
	 * @param headers {@link HttpHeaders}
	 * 
	 * @param headerName Name of header
	 * 
	 * @return return false if header does not exists or header values are not available.
	 */
	private Boolean checkHeaderAndValueExists(HttpHeaders headers, String headerName) {
		if (headers.containsKey(headerName)) {
			List<String> headervalues = headers.get(headerName);
			if (headervalues != null && !headervalues.isEmpty()) {
				return headervalues.stream().noneMatch(StringUtils::isAllBlank);
			} else {
				return Boolean.FALSE;
			}
		} else {
			return Boolean.FALSE;
		}
	}
}
