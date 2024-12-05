package com.macys.uop.foundation.core.utils.common;

import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UUIDUtil.class)
public class UUIDUtilsTest implements UUIDUtil, TestContextUtil {

    @Before
    public void beforeTest() {
        initContext();
    }


    @After
    public void afterTest() {
        clearContext();
    }

    @Test
    public void testGenerateType5UUID() {
        String input = "test";
        var output = generateType5UUIDWithSHA256(input);
        Assert.assertNotNull(output); // Random UUID will be generated allows

    }

    @Test
    public void testGenerateType5UUIDWrongAlgorithm() {
        try {
            String input = "test";
            generateType5UUID(input, "TESTSALT");
        }
        catch(Exception e) {
            ThrowableProblem problem=(ThrowableProblem)e;
            com.macys.uop.foundation.core.utils.exception.Error errorInfo
                    = (com.macys.uop.foundation.core.utils.exception.Error)problem.getParameters().get(PROBLEM_ERROR_KEY);
            Assert.assertEquals(CommonStatusCode.MESSAGEDIGEST_ERROR.getCode(), errorInfo.getCode());
        }
    }
}