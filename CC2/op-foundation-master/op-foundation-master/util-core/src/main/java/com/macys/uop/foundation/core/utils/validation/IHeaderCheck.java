package com.macys.uop.foundation.core.utils.validation;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface contract for validating {@link HttpServletRequest} headers.
 * 
 * @see {@link com.macys.uop.foundation.core.utils.validation.HeaderCheckServiceImpl}
 */
public interface IHeaderCheck {
	/**
	 * Method to be implemented by {@link IHeaderCheck} implementation classes to validate {@link HttpServletRequest} headers.
	 * <br>
	 * In case of validation failure, this method should :
	 * <ul>
	 * <li>Populate {@link com.macys.uop.foundation.core.utils.exception.Error} information</li>
	 * <li>Log error</li>
	 * <li>Create and throw {@link org.zalando.problem.ThrowableProblem}</li>
	 * </ul> 
	 * @param request {@link HttpServletRequest}
	 */
	void validateHeaders(HttpServletRequest request);
}
