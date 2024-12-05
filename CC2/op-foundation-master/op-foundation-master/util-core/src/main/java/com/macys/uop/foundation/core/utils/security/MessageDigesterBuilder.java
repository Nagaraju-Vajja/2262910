package com.macys.uop.foundation.core.utils.security;

import org.springframework.util.Assert;

import com.macys.uop.foundation.core.utils.exception.ProblemUtil;


/**
 * Provides Builder Pattern to construct {@link MessageDigester} instance.
 * <br>
 * with* methods are part of building {@link MessageDigester} information.
 */
public class MessageDigesterBuilder implements ProblemUtil
{
	private static final byte[] DEFAULT_SALT="4927c774-8c55-4b50-b626-58610cdfa208".getBytes();
	private static final String DEFAULT_ALGORITHM="SHA-256";
	private String message;
	private String algorithm;
	private byte[] salt;
	
	
	/**
	 * message parameter is mandatory.
	 * <br>
	 * If algorithm value is null then default value DEFAULT_ALGORITHM is set.
	 * <br>
	 * If salt value is null then default value @see DEFAULT_SALT is set.
	 * 
	 * @return MessageDigester
	 */
	public MessageDigester build() {
		Assert.hasText(message, "'message' must not be empty");
		MessageDigester messageDigester=new MessageDigester();
		if(algorithm==null) {
			algorithm=DEFAULT_ALGORITHM;
		}
		messageDigester.setAlgorithm(algorithm);
		if(salt==null) {
			salt=DEFAULT_SALT;
		}
		messageDigester.setSalt(salt);
		messageDigester.setMessage(message);
		return messageDigester;
	}
	
	public MessageDigesterBuilder withMessage(final String message) {
		this.message = message;
		return this;
	}
	
	public MessageDigesterBuilder withAlgorithm(final String algorithm) {
		this.algorithm = algorithm;
		return this;
	}
	
	public MessageDigesterBuilder withSalt(final byte[] salt) {
		this.salt = salt;
		return this;
	}
	
	public MessageDigesterBuilder withSalt(final String salt) {
		Assert.notNull(salt, "Salt must not be null");
		this.salt = salt.getBytes();
		return this;
	}
	
}
