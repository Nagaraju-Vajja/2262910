package com.macys.uop.order.ordercollectorchestrator.proxy.impl;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes.LOCK_ORDER_ERROR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.LOCK_MANAGER;

import com.macys.uop.common.lock.model.LockManagerRequest;
import com.macys.uop.foundation.core.utils.exception.ExceptionHandlerUtil;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientRequest;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.order.ordercollectorchestrator.proxy.ILockmanagerProxy;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockmanagerProxyImpl implements ILockmanagerProxy, OrdercollectorchestratorUtil,
    ExceptionHandlerUtil {

    private final RestClient<LockManagerRequest, LockManagerRequest> restClient;

    @Value("${lockmanager-service.createLockTransaction.base_uri}")
    private String createLockUri;


    /**
     * Rest call for LockManager to create lock
     * @param lockManagerRequest
     * @param headers
     * @return LockManagerRequest
     */
    @Retry(name = "createLockTransaction-rt", fallbackMethod = "createLockTransactionFallback")
    public LockManagerRequest createLockTransaction(final LockManagerRequest lockManagerRequest,
        final MultiValueMap<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders(headers);
        RestClientRequest<LockManagerRequest> clientRequest =
            RestClientRequest.<LockManagerRequest>builder().withUrl(createLockUri)
                .withBody(lockManagerRequest).withMethod(HttpMethod.POST.name())
                .withHeaders(httpHeaders).build();

        RestClientResponse<LockManagerRequest> serviceResponse =
            restClient.execute(clientRequest, LockManagerRequest.class);
        return serviceResponse.getBody();
    }

   /**
     * LockManager fallback method to handle exception
     * @param lockManagerRequest
     * @param headers
     * @param e
     * @return LockManagerRequest
     */
    @Override
    public LockManagerRequest createLockTransactionFallback(LockManagerRequest lockManagerRequest, 
    		final MultiValueMap<String, String> headers, Throwable e) {
        getErrorLogMessageBuilder(LOCK_ORDER_ERROR.getCode(), LOCK_MANAGER, e,
            "Failed to create Lock for orderId : "+ lockManagerRequest.getOrderId(), log).build().logAsError();

        handleServiceFailure(e, LOCK_ORDER_ERROR.getCode(), LOCK_ORDER_ERROR.getDescription(), LOCK_MANAGER);
        return null;
    }

}
