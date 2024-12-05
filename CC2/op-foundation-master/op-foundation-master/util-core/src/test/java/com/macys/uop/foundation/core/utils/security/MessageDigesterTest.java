package com.macys.uop.foundation.core.utils.security;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;

import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class MessageDigesterTest 
{
	@Test
	public void testGenerateDigest() {
		//In line usage
		String firstDigest=MessageDigester.builder().withMessage("Hello World!").build().generateDigest();
		log.info("Generated First Digest ::: "+firstDigest);
		Assert.assertNotNull(firstDigest);
		
		// Separate instance and digest generation
		MessageDigester digester=MessageDigester.builder().withMessage("Hello World!").build();
		String secondDigest=digester.generateDigest();
		log.info("Generated Second Digest ::: "+secondDigest);
		Assert.assertNotNull(secondDigest);
		
		Assert.assertEquals(firstDigest, secondDigest);
	}
	
	@Test
	public void testGenerateDigestWithAlgorithm() {
		String firstDigest=MessageDigester.builder().withMessage("Hello World!").withAlgorithm("SHA-512").build().generateDigest();
		log.info("Generated First Digest ::: "+firstDigest);
		Assert.assertNotNull(firstDigest);
		
		MessageDigester digester=MessageDigester.builder().withMessage("Hello World!").withAlgorithm("SHA-512").build();
		String secondDigest=digester.generateDigest();
		log.info("Generated Second Digest ::: "+secondDigest);
		Assert.assertNotNull(secondDigest);
		
		Assert.assertEquals(firstDigest, secondDigest);
	}
	
	@Test
	public void testGenerateDigestWithSalt() {
		String firstDigest=MessageDigester.builder().withMessage("Hello World!").withSalt("SOMESALT").build().generateDigest();
		log.info("Generated First Digest ::: "+firstDigest);
		Assert.assertNotNull(firstDigest);
		
		MessageDigester digester=MessageDigester.builder().withMessage("Hello World!").withSalt("SOMESALT".getBytes()).build();
		String secondDigest=digester.generateDigest();
		log.info("Generated Second Digest ::: "+secondDigest);
		Assert.assertNotNull(secondDigest);
		
		Assert.assertEquals(firstDigest, secondDigest);
	}
	
	@Test
	public void testGenerateDigestMandatoryCheck() {
		try {
			MessageDigester.builder().withSalt("SOMESALT").build().generateDigest();
		}
		catch(Exception e) {
			IllegalArgumentException ex=(IllegalArgumentException)e;
			Assert.assertEquals("'message' must not be empty", ex.getMessage());
		}
	}
	
	@Test
	public void testGenerateDigestWrongAlgorithm() {
		try {
			MessageDigester.builder().withMessage("Hello World!").withAlgorithm("SHA-1024").build().generateDigest();
		}
		catch(Exception e) {
			ThrowableProblem problem=(ThrowableProblem)e;
			com.macys.uop.foundation.core.utils.exception.Error errorInfo 
				= (com.macys.uop.foundation.core.utils.exception.Error)problem.getParameters().get(PROBLEM_ERROR_KEY);
			Assert.assertEquals(CommonStatusCode.MESSAGEDIGEST_ERROR.getCode(), errorInfo.getCode());
		}
	}
}
