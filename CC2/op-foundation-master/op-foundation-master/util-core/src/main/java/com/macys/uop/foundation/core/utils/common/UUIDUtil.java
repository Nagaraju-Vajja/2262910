package com.macys.uop.foundation.core.utils.common;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.zalando.problem.Status;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Class used for generating UUID based on input source
 */
public interface UUIDUtil extends ProblemUtil {

    /**
     * Default algorithm SHA-256 to generate UUID
     *
     * @param source input String
     * @return UUID
     */
    default UUID generateType5UUIDWithSHA256(String source) {
        return generateType5UUID(source, "SHA-256");
    }

    /**
     * Static method to generate Type 5 UUID based on source
     *
     * @param source input string
     * @return UUID
     */
    default UUID generateType5UUID(String source, String algorithm) {
        final Integer EIGHT = 8;
        final Integer SIXTEEN = 16;

        byte[] input = source.getBytes(StandardCharsets.UTF_8);
        var secureRandom = new SecureRandom();
        var salt = new byte[SIXTEEN];
        secureRandom.nextBytes(salt);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(salt);
        } catch (NoSuchAlgorithmException e) {
            com.macys.uop.foundation.core.utils.exception.Error errorInfo = com.macys.uop.foundation.core.utils.exception.Error
                    .builder()
                    .withCode(CommonStatusCode.MESSAGEDIGEST_ERROR.getCode())
                    .withMessage(CommonStatusCode.MESSAGEDIGEST_ERROR.getDescription())
                    .withErrorDetail(ErrorDetail.builder()
                            .withDomain("Global")
                            .withReason("UUID Generation failure")
                            .withMessage(e.getMessage())
                            .build())
                    .build();

            new LogMessageBuilder()
                    .withContext("Message digest creation")
                    .withLogType(LogTypeEnum.ERROR)
                    .withErrorCode(CommonStatusCode.MESSAGEDIGEST_ERROR.getCode())
                    .withErrorMessage(CommonStatusCode.MESSAGEDIGEST_ERROR.getDescription())
                    .withStackTrace(ExceptionUtils.getStackTrace(e))
                    .withAdditionalInfo("Error creating UUID")
                    .withLogger(LogHolder.LOGGER)
                    .buildDisableChecking()
                    .logAsError();

            throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorInfo);
        }
        final byte[] bytes = md.digest(input);
        bytes[6] &= 0x0f; /* clear version        */
        bytes[6] |= 0x50; /* set to version 5     */
        bytes[EIGHT] &= 0x3f; /* clear variant        */
        bytes[EIGHT] |= 0x80; /* set to IETF variant  */
        long msb = 0;
        long lsb = 0;
        assert bytes.length >= SIXTEEN : "Data must be 16 bytes in length";

        for (var i = 0; i < EIGHT; i++) {
            msb = (msb << EIGHT) | (bytes[i] & 0xff);
        }

        for (int i = EIGHT; i < SIXTEEN; i++) {
            lsb = (lsb << EIGHT) | (bytes[i] & 0xff);
        }
        return new UUID(msb, lsb);
    }

    static final class LogHolder {
        private static final Logger LOGGER = getLogger(UUIDUtil.class);

        private LogHolder() {
        }
    }

}