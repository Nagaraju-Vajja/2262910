package com.macys.uop.foundation.core.utils.future;

import java.util.concurrent.CompletableFuture;

/**
 * Custom runtime exception that is thrown when {@link CompletableFuture#allOf} method get completion issue.
 *
 */
public class CompletableFutureCompletionRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -7275864699761992815L;

	public CompletableFutureCompletionRuntimeException() {
		super();
	}

	public CompletableFutureCompletionRuntimeException(String s) {
		super(s);
	}

	public CompletableFutureCompletionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompletableFutureCompletionRuntimeException(Throwable cause) {
		super(cause);
	}
}
