package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

import com.macys.uop.common.lock.model.LockManagerRequest;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.order.model.Lock;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.ILockmanagerProxy;
import com.macys.uop.order.ordercollectorchestrator.service.ILockmanagerService;
import com.macys.uop.order.ordercollectorchestrator.utils.BusinessConfigManagerUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.ValidatorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockmanagerServiceImpl implements ILockmanagerService, OrdercollectorchestratorUtil {

	private final ILockmanagerProxy lockProxy;
	private final ValidatorUtil validatorUtil;
	private final BusinessConfigManagerUtil businessConfigManagerUtil;
	private final JsonUtils jsonUtils;

	/**
	 * Method to Create Lock
	 * @param order
	 * @param httpHeaders
	 */
	@Override
	public void lockOrder(Order order, HttpHeaders httpHeaders) {
		LockManagerRequest lockManagerRequest = null;
		if ((validatorUtil.isProfileFeatureActive(order, FRAUD_VALIDATION)) == TRUE_VALUE) {
			lockManagerRequest=new LockManagerRequest();
			lockManagerRequest.setCreatedBy(SERVICENAME);
			lockManagerRequest.setLastUpdatedBy(SERVICENAME);
			lockManagerRequest.setLockedEntity(ORDER);
			lockManagerRequest.setLockedEntityId(order.getOrderId());
			lockManagerRequest.setLockId(UUID.randomUUID().toString());
			lockManagerRequest.setModifiedSystem(UOP);
			lockManagerRequest.setOrderId(order.getOrderId());
			if (StringUtils.isNotEmpty(order.getSourceSystem()) && NEW_SOURCE_SYSTEMS.contains(order.getSourceSystem())) {
				lockManagerRequest.setReasonCode(FRAUD_LOCK_CODE_NOT_INTIATED);
				lockManagerRequest.setReasonDesc(FRAUD_LOCK_CODE_NOT_INTIATED_DESC);
			} else {
				lockManagerRequest.setReasonCode(FRAUD_LOCK_CODE);
				lockManagerRequest.setReasonDesc(FRAUD_LOCK_REASON);
			}
			lockManagerRequest.setLockType(FRAUD_LOCK_TYPE);
			lockManagerRequest.setSourceSystem(order.getSourceSystem());
			lockManagerRequest.setTransactionId(LOCK_CREATE);
			lockManagerRequest.setReservationId(order.getOrderLines().get(0).getReservationId());
			lockManagerRequest.setOriginalSaleOrderId(order.getOriginalSaleOrderId());
			lockManagerRequest=lockProxy.createLockTransaction(lockManagerRequest, httpHeaders);
		} else
			{ boolean createPaymentLock = validatorUtil.getBusinessConfigForOrder(order.getOrderPurpose(),PAYMENT_LOCK);
				if (createPaymentLock) {
					lockManagerRequest=new LockManagerRequest();
					lockManagerRequest.setCreatedBy(SERVICENAME);
					lockManagerRequest.setLastUpdatedBy(SERVICENAME);
					lockManagerRequest.setLockedEntity(ORDER);
					lockManagerRequest.setLockedEntityId(order.getOrderId());
					lockManagerRequest.setLockId(UUID.randomUUID().toString());
					lockManagerRequest.setModifiedSystem(UOP);
					lockManagerRequest.setOrderId(order.getOrderId());
					lockManagerRequest.setReasonCode(PAYMENT_LOCK_CODE);
					lockManagerRequest.setReasonDesc(PAYMENT_LOCK_REASON);
					lockManagerRequest.setLockType(PAYMENT_LOCK_TYPE);
					lockManagerRequest.setSourceSystem(order.getSourceSystem());
					lockManagerRequest.setTransactionId(LOCK_CREATE);
					lockManagerRequest.setReservationId(order.getOrderLines().get(0).getReservationId());
					lockManagerRequest.setOriginalSaleOrderId(order.getOriginalSaleOrderId());
					lockManagerRequest=lockProxy.createLockTransaction(lockManagerRequest, httpHeaders);
				}
			}
		if(null != lockManagerRequest) {
			Lock lockDetails = jsonUtils.convertFromJson(jsonUtils.convertToJsonPretty(lockManagerRequest), Lock.class);
			List<Lock> locks = new ArrayList<>();
			if (null != lockDetails) {
				locks.add(lockDetails);
				order.setLocks(locks);
			}
		}
	}

		/**
		 * Method to Partner Fulfillment id lock.
		 * 
		 * @param order
		 * @param httpHeaders
		 */
		@Override
		public void lockOrderByPartnerFulfillmentId(Order order, HttpHeaders httpHeaders) {
			boolean fulfillmentOrderIdLock = validatorUtil.getBusinessConfigForOrder(ORDER_LOCK_MIRAKL_CONFIG,
					PARTNER_FULFILLMENT_ID_LOCK_ATTRIBUTE);
			if (fulfillmentOrderIdLock) {
				var lockManagerRequest = new LockManagerRequest();
				lockManagerRequest.setCreatedBy(SERVICENAME);
				lockManagerRequest.setLastUpdatedBy(SERVICENAME);
				lockManagerRequest.setLockedEntity(PARTNER_FULFILLMENT_ID_LOCKED_ENTITY);
				lockManagerRequest.setLockedEntityId(order.getOrderId());
				lockManagerRequest.setLockId(UUID.randomUUID().toString());
				lockManagerRequest.setModifiedSystem(UOP);
				lockManagerRequest.setOrderId(order.getOrderId());
				lockManagerRequest.setReasonCode(PARTNER_FULFILLMENT_ID_LOCK_CODE);
				lockManagerRequest.setReasonDesc(PARTNER_FULFILLMENT_ID_LOCK_REASON);
				lockManagerRequest.setLockType(PARTNER_FULFILLMENT_ID_LOCK_TYPE);
				lockManagerRequest.setSourceSystem(order.getSourceSystem());
				lockManagerRequest.setTransactionId(LOCK_CREATE);
				lockManagerRequest.setReservationId(order.getOrderLines().get(0).getReservationId());
				lockManagerRequest.setOriginalSaleOrderId(order.getOriginalSaleOrderId());
				lockProxy.createLockTransaction(lockManagerRequest, httpHeaders);
			}
		}
}
