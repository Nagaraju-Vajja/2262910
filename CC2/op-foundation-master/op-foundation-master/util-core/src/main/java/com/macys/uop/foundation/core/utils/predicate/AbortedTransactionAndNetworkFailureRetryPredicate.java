package com.macys.uop.foundation.core.utils.predicate;

/**
 * Predicate to retry requests on failure due to aborted transaction, extends
 * network retry predicate so would retry on network failures as well
 * 
 * @author 229719
 */
public class AbortedTransactionAndNetworkFailureRetryPredicate extends DefaultNetworkFailureRetryPredicate {

	public boolean test(Throwable e) {
		boolean shouldRetry = false;

		shouldRetry = super.test(e);
		if (!shouldRetry) {
			if (e.getMessage() != null && e.getMessage().contains("UOP-GEN-E05048")) { // Code for aborted transaction error
				shouldRetry = true;
			}
		}
		return shouldRetry;
	}
}