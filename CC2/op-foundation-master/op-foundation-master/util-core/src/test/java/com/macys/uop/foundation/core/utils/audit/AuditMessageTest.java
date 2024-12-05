package com.macys.uop.foundation.core.utils.audit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.junit4.SpringRunner;
import org.threeten.bp.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@Slf4j
public class AuditMessageTest {

	@Test
	public void testBuildFullAuditMessage() throws JsonProcessingException {
		AuditMessage message=AuditMessage.builder()
				.withAuditType("order").withOrderId("12345").withSubClientId("3456")
				.withTransactionId("5555").withTransactionDesc("Data Saved Successfully").withStatus("OK")
				.withPreviousValueDetails("1234").withNewValueDetails("5678")
				.withReasonCode("RC1289").withReasonDesc("updated")
				.withAuditDetail("123", "1236")
				.withAuditDetail("aaa", "bbb")
				.withCreatedBy("test").build();

		Assert.assertEquals("RC1289", message.getReasonCode());

		String jsonAuditMessage=message.toString();

		log.info(jsonAuditMessage);

		ObjectMapper mapper=new ObjectMapper();
		AuditMessage reconstructedAuditMessage=mapper.readValue(jsonAuditMessage, AuditMessage.class);

		Assert.assertEquals(reconstructedAuditMessage.getTransactionDesc(), message.getTransactionDesc());
	}

	@Test
	public void testBuildOnlyMandatoryAuditMessage() throws JsonProcessingException {
		AuditMessage message=AuditMessage.builder()
				.withAuditType("order").withOrderId("12345")
				.withTransactionId("5555").withTransactionDesc("Data Saved Successfully")
				.withCreatedBy("test")
				.build();

		String jsonAuditMessage=message.toString();

		log.info(jsonAuditMessage);

		ObjectMapper mapper=new ObjectMapper();
		AuditMessage reconstructedAuditMessage=mapper.readValue(jsonAuditMessage, AuditMessage.class);

		Assert.assertEquals(reconstructedAuditMessage.getOrderId(), message.getOrderId());
	}

	@Test
	public void testAuditMessageMandatoryParamException() {
		try {
			AuditMessage.builder()
					.withOrderId("12345")
					.withTransactionId("5555")
					.withTransactionDesc("Data Saved Successfully")
					.withCreatedBy("test")
					.build();
		}
		catch(IllegalArgumentException ex) {
			Assert.assertEquals("'auditType' must not be empty", ex.getMessage());
		}

		try {
			AuditMessage.builder()
					.withAuditType("order")
					.withTransactionId("5555")
					.withTransactionDesc("Data Saved Successfully")
					.withCreatedBy("test")
					.build();
		}
		catch(IllegalArgumentException ex) {
			Assert.assertEquals("'orderId' must not be empty", ex.getMessage());
		}

		try {
			AuditMessage.builder()
					.withAuditType("order")
					.withOrderId("12345")
					.withTransactionDesc("Data Saved Successfully")
					.withCreatedBy("test")
					.build();
		}
		catch(IllegalArgumentException ex) {
			Assert.assertEquals("'transactionId' must not be empty", ex.getMessage());
		}

		try {
			AuditMessage.builder()
					.withAuditType("order")
					.withOrderId("12345")
					.withTransactionId("5555")
					.withCreatedBy("test")
					.build();
		}
		catch(IllegalArgumentException ex) {
			Assert.assertEquals("'transactionDesc' must not be empty", ex.getMessage());
		}

		try {
			AuditMessage.builder()
					.withAuditType("order")
					.withOrderId("12345")
					.withTransactionId("5555")
					.withTransactionDesc("Data Saved Successfully")
					.withCreatedBy("test")
					.build();
		}
		catch(IllegalArgumentException ex) {
			Assert.assertEquals("'transactionDesc' must not be empty", ex.getMessage());
		}

	}
	
	@Test
	public void testBuildAuditMessageWithTransactionTs() throws JsonProcessingException {
		String transactionTs=Instant.now().toString();
		AuditMessage message=AuditMessage.builder()
				.withAuditType("order").withOrderId("12345").withSubClientId("3456")
				.withTransactionId("5555").withTransactionDesc("Data Saved Successfully").withStatus("OK")
				.withPreviousValueDetails("1234").withNewValueDetails("5678")
				.withReasonCode("RC1289").withReasonDesc("updated")
				.withAuditDetail("123", "1236")
				.withAuditDetail("aaa", "bbb")
				.withCreatedBy("test")
				.withTransactionTs(transactionTs)
				.build();

		String jsonAuditMessage=message.toString();

		log.info(jsonAuditMessage);

		ObjectMapper mapper=new ObjectMapper();
		AuditMessage reconstructedAuditMessage=mapper.readValue(jsonAuditMessage, AuditMessage.class);

		Assert.assertEquals(reconstructedAuditMessage.getTransactionTs(), message.getTransactionTs());
	}
}
