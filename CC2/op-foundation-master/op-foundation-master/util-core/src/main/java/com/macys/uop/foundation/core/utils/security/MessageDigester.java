package com.macys.uop.foundation.core.utils.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Purpose of this class is to generate a String digest using algorithm "MD5,SHA-1,SHA-256,SHA-512" and salt.
 * <br>
 * It supports builder pattern.
 * <br>
 * Usage : MessageDigester.builder().withMessage("Hello World!").build().generateDigest()
 */
@Data
@Slf4j
public class MessageDigester implements LoggingUtil, ProblemUtil {
	private String message;
	private String algorithm;
	private byte[] salt;
	
	public static MessageDigesterBuilder builder() {
		return new MessageDigesterBuilder();
	}
	
	/**
	 * Generates the message digest. Each time a new {@link MessageDigest} instance is created. 
	 * <br>
	 * Refer to : https://stackoverflow.com/questions/17554998/need-thread-safe-messagedigest-in-java
	 * 
	 * @return Digested Message. Throws {@link ThrowableProblem} in case of {@link NoSuchAlgorithmException}
	 */
	public String generateDigest() {
		String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(salt);
            byte[] bytes = md.digest(message.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
        	com.macys.uop.foundation.core.utils.exception.Error errorInfo = com.macys.uop.foundation.core.utils.exception.Error
    				.builder()
    				.withCode(CommonStatusCode.MESSAGEDIGEST_ERROR.getCode())
    				.withMessage(CommonStatusCode.MESSAGEDIGEST_ERROR.getDescription())
    				.withErrorDetail(ErrorDetail.builder()
    						.withDomain("Global")
    						.withReason("Digest creation failure")
    						.withMessage(e.getMessage())
    						.build())
    				.build();
        	
        	new LogMessageBuilder()
        	.withContext("Message digest creation")
        	.withLogType(LogTypeEnum.ERROR)
			.withErrorCode(CommonStatusCode.MESSAGEDIGEST_ERROR.getCode())
			.withErrorMessage(CommonStatusCode.MESSAGEDIGEST_ERROR.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(e))
			.withAdditionalInfo("Error creating message digest")
			.withLogger(log)
    		.buildDisableChecking()
    		.logAsError();
    		
    		throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorInfo);
        }
        return generatedPassword;
	}
	
}
