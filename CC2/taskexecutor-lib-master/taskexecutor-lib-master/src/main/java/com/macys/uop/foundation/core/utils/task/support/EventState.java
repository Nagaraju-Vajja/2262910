package com.macys.uop.foundation.core.utils.task.support;

import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "EventState")
public class EventState {

	@PrimaryKey(keyOrder = 1)
	@Column(nullable = false, name = "eventStatePk")
	private String eventStatePk;

	@Column(nullable = false, name = "correlationId")
	private String correlationId;

	@Column(nullable = false, name = "serviceName")
	private String serviceName;

	@Column(nullable = false, name = "completedSteps")
	private String completedSteps;

	@Column(nullable = false, name = "failedStep")
	private String failedStep;

	@Column(nullable = false, name = "status")
	private String status;

	@Column(nullable = false, name = "transactionId")
	private String transactionId;

	@Column(nullable = false, name = "createdBy")
	private String createdBy;

	@Column(nullable = false, name = "createdTs")
	private Timestamp createdTs;

	@Column(nullable = false, name = "lastUpdatedBy")
	private String lastUpdatedBy;

	@Column(nullable = false, name = "lastUpdatedTs", spannerCommitTimestamp = true)
	private Timestamp lastUpdatedTs;
	
	@Column(nullable = false, name = "process")
	private String process;
	
	@Column(nullable = false, name = "payload")
	private String payload;
	
	@Column(nullable = false, name = "orderId")
	private String orderId;
	
	@Column(nullable = false, name = "recordId")
	private String recordId;

}