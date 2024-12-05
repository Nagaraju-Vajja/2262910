package com.macys.uop.foundation.core.utils.audit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.macys.uop.foundation.core.utils.common.StringUtil;

import lombok.Data;

/**
 * The purpose of this class is to :
 * <ul>
 * <li>Capture audit information</li>
 * <li>Provide builder pattern to populate audit information</li>
 * <li>Produce JSON output structure by calling {@link #toString()}</li>
 * </ul> 
 * <br>
 * Refer to Confluence page <a href="https://confluence.federated.fds/display/OCOM/UOP+Order+Audit+Framework+-+Solution+Design">Audit Solution Design</a>
 * for field description. 
 */
@Data
public class AuditMessage implements StringUtil
{
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
	 * Convenient static builder method for returning {@link AuditMessageBuilder} instance
	 *  
	 * @return {@link AuditMessageBuilder}
	 */
	public static AuditMessageBuilder builder() {
		return new AuditMessageBuilder();
	}
	
	/**
	 * Instead of JsonUtils, StringBuilder is used to construct the Json.
	 * 
	 * @return Json representation of {@link AuditMessage}
	 */
	@Override
	public String toString() {
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		
		builder.append("\"").append("auditType").append("\"").append(" : ").append("\"").append(auditType).append("\"").append(" , ");;
		builder.append("\"").append("orderId").append("\"").append(" : ").append("\"").append(orderId).append("\"").append(" , ");;
		
		if(!StringUtils.isAllBlank(subClientId)) {
			builder.append("\"").append("subClientId").append("\"").append(" : ").append("\"").append(subClientId).append("\"").append(" , ");
		}
		
		builder.append("\"").append("transactionId").append("\"").append(" : ").append("\"").append(transactionId).append("\"").append(" , ");
		builder.append("\"").append("transactionDesc").append("\"").append(" : ").append("\"").append(quoteAsString(transactionDesc)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(status)) {
			builder.append("\"").append("status").append("\"").append(" : ").append("\"").append(status).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(previousValueDetails)) {
			builder.append("\"").append("previousValueDetails").append("\"").append(" : ").append("\"").append(quoteAsString(previousValueDetails)).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(newValueDetails)) {
			builder.append("\"").append("newValueDetails").append("\"").append(" : ").append("\"").append(quoteAsString(newValueDetails)).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(reasonCode)) {
			builder.append("\"").append("reasonCode").append("\"").append(" : ").append("\"").append(reasonCode).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(reasonDesc)) {
			builder.append("\"").append("reasonDesc").append("\"").append(" : ").append("\"").append(quoteAsString(reasonDesc)).append("\"").append(" , ");
		}
		
		builder.append("\"").append("transactionTs").append("\"").append(" : ").append("\"").append(quoteAsString(transactionTs)).append("\"").append(" , ");
		
		if(!auditDetails.isEmpty())
			appendAuditDetails(builder);
		
		builder.append("\"").append("createdBy").append("\"").append(" : ").append("\"").append(createdBy).append("\"");

		if(!StringUtils.isAllBlank(reservationId)) {
			builder.append(" , ").append("\"").append("reservationId").append("\"").append(" : ").append("\"").append(quoteAsString(reservationId)).append("\"");
		}
		if(!StringUtils.isAllBlank(originalSaleOrderId)) {
			builder.append(" , ").append("\"").append("originalSaleOrderId").append("\"").append(" : ").append("\"").append(quoteAsString(originalSaleOrderId)).append("\"");
		}
		
		builder.append("}");
		return builder.toString();
	}
	
	/**
	 * Append auditDetails array information in Json format 
	 * 
	 * @param builder {@link StringBuilder}
	 */
	private void appendAuditDetails(StringBuilder builder) {
		builder.append("\"").append("auditDetails").append("\"").append(" : ");
		builder.append("[ ");
		for (int i = 0; i < auditDetails.size(); i++) {
			AuditDetail auditDetail = auditDetails.get(i);
			if (i == (auditDetails.size() - 1)) {
				builder.append(getAuditDetailJsonFragment(auditDetail));
			} else {
				builder.append(getAuditDetailJsonFragment(auditDetail)).append(" , ");
			}
		}
		builder.append(" ], ");
	}
	
	/**
	 * Construct @link {@link AuditDetail} related Json information
	 * 
	 * @param auditDetail
	 * 
	 * @return Json String
	 */
	private String getAuditDetailJsonFragment(AuditDetail auditDetail) {
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		builder.append("\"").append("referenceId").append("\"").append(" : ").append("\"").append(auditDetail.getReferenceId()).append("\"").append(" , ");
		builder.append("\"").append("referenceType").append("\"").append(" : ").append("\"").append(auditDetail.getReferenceType()).append("\"");
		builder.append("}");
		return builder.toString();
	}
}
