package com.macys.uop.foundation.core.utils.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.pubsub.v1.PubsubMessage;

import lombok.RequiredArgsConstructor;

/**
 * This is the default implementation of {@link MessageValidator} provided by foundation to to wrap both message header validation and duplicate check at one place
 *
 */
@Service
@RequiredArgsConstructor
public class MessageValidatorImpl implements MessageValidator {
	
	@Value("${message.duplicate.checking.enabled:true}")
	private boolean isMessageDuplicationCheckEnabled;

	@Value("${default.mandatory.header.checking.enabled:true}")
	private boolean isDefaultMandatoryHeaderCheckingEnabled;
	
	private final IMessageDuplicationCheck messageDuplicationService;
	private final IMessageHeaderCheck messageHeaderCheckService;
	
	@Override
	public boolean validateMessage(PubsubMessage message) {
		boolean validationStatus = true;
		if(isDefaultMandatoryHeaderCheckingEnabled) {
			messageHeaderCheckService.validateMessageHeaders(message);
		}
		boolean isMessageDuplicate = isMessageDuplicationCheckEnabled
				&& messageDuplicationService.isMessageDuplicate(message);
		if (isMessageDuplicate) {
			validationStatus = false;
		}
		return validationStatus;
	}
}
