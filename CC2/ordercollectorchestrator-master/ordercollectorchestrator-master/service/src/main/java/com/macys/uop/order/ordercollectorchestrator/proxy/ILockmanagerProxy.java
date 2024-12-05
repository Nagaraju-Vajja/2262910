package com.macys.uop.order.ordercollectorchestrator.proxy;

import com.macys.uop.common.lock.model.LockManagerRequest;
import org.springframework.util.MultiValueMap;

public interface ILockmanagerProxy {

	LockManagerRequest createLockTransaction(final LockManagerRequest lockManagerRequest,
			final MultiValueMap<String, String> headers);

	LockManagerRequest createLockTransactionFallback(LockManagerRequest lockManagerRequest,
			final MultiValueMap<String, String> headers, Throwable e);
}
