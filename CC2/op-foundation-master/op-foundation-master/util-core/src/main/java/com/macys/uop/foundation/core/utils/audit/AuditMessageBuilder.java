package com.macys.uop.foundation.core.utils.audit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Actual audit message builder class having with* methods to populate audit information.
 * <br>
 * {@link #build()} method validates mandatory parameter values and throws {@link IllegalArgumentException} in case of empty value
 */
public class AuditMessageBuilder {
	private String auditType; // Mandatory
	private String orderId; // Mandatory
	private String subClientId;
	private String transactionId; // Mandatory
	private String transactionDesc; // Mandatory
	private String status;
	private String previousValueDetails;
	private String newValueDetails;
	private String reasonCode;
	private String reasonDesc;
	private List<AuditDetail> auditDetails=new ArrayList<>();
	private String createdBy; // Mandatory
	private String transactionTs; // Mandatory

	private String originalSaleOrderId;

	private String reservationId;
	
	/**
	 * This method constructs {@link AuditMessage} instance there by validates mandatory parameter values 
	 * and throws {@link IllegalArgumentException} in case of empty value.
	 *  
	 * @return {@Link AuditMessage} instance
	 */
	public AuditMessage build() {
		Assert.hasText(auditType, "'auditType' must not be empty");
		Assert.hasText(orderId, "'orderId' must not be empty");
		Assert.hasText(transactionId, "'transactionId' must not be empty");
		Assert.hasText(transactionDesc, "'transactionDesc' must not be empty");
		Assert.hasText(createdBy, "'createdBy' must not be empty");
		if(StringUtils.isAllBlank(transactionTs)) {
			transactionTs=Instant.now().toString();
		}
		return constructAuditMessage();
	}
	
	
	/**
	 * Creates {@link AuditMessage} instance
	 * 
	 * @return {@link AuditMessage}
	 */
	private AuditMessage constructAuditMessage() {
		AuditMessage auditMessage=new AuditMessage();
		auditMessage.setAuditType(auditType);
		auditMessage.setOrderId(orderId);
		auditMessage.setSubClientId(subClientId);
		auditMessage.setTransactionId(transactionId);
		auditMessage.setTransactionDesc(transactionDesc);
		auditMessage.setStatus(status);
		auditMessage.setPreviousValueDetails(previousValueDetails);
		auditMessage.setNewValueDetails(newValueDetails);
		auditMessage.setReasonCode(reasonCode);
		auditMessage.setReasonDesc(reasonDesc);
		auditMessage.setAuditDetails(auditDetails);
		auditMessage.setCreatedBy(createdBy);
		auditMessage.setTransactionTs(transactionTs);
		auditMessage.setReservationId(reservationId);
		auditMessage.setOriginalSaleOrderId(originalSaleOrderId);
		return auditMessage;
	}
	
	public AuditMessageBuilder withAuditType(final String auditType) {
		this.auditType = auditType;
		return this;
	}
	
	public AuditMessageBuilder withOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}
	
	public AuditMessageBuilder withSubClientId(final String subClientId) {
		this.subClientId = subClientId;
		return this;
	}
	
	public AuditMessageBuilder withTransactionId(final String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public AuditMessageBuilder withTransactionDesc(final String transactionDesc) {
		this.transactionDesc = transactionDesc;
		return this;
	}
	
	public AuditMessageBuilder withStatus(final String status) {
		this.status = status;
		return this;
	}
	
	public AuditMessageBuilder withPreviousValueDetails(final String previousValueDetails) {
		this.previousValueDetails = previousValueDetails;
		return this;
	}
	
	public AuditMessageBuilder withNewValueDetails(final String newValueDetails) {
		this.newValueDetails = newValueDetails;
		return this;
	}
	
	public AuditMessageBuilder withReasonCode(final String reasonCode) {
		this.reasonCode = reasonCode;
		return this;
	}
	
	public AuditMessageBuilder withReasonDesc(final String reasonDesc) {
		this.reasonDesc = reasonDesc;
		return this;
	}
	
	public AuditMessageBuilder withAuditDetail(final String referenceId, final String referenceType) {
		this.auditDetails.add(new AuditDetail(referenceId, referenceType));
		return this;
	}
	
	public AuditMessageBuilder withCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
		return this;
	}
	
	public AuditMessageBuilder withTransactionTs(final String transactionTs) {
		this.transactionTs = transactionTs;
		return this;
	}

	public AuditMessageBuilder withReservationId(final String reservationId) {
		this.reservationId = reservationId;
		return this;
	}
	public AuditMessageBuilder withOriginalSaleOrderId(final String originalSaleOrderId) {
		this.originalSaleOrderId = originalSaleOrderId;
		return this;
	}
}


