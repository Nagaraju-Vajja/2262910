package com.macys.uop.foundation.core.utils.common;

import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = URIUtil.class)
public class URIUtilTest implements URIUtil, TestContextUtil {

    @MockBean
    @Qualifier("uriUtil")
    private URIUtil uriUtil;

    @Before
    public void beforeTest() {

        initContext();
    }

    @After
    public void afterTest(){
        clearContext();
    }

    @Test
    public void testIsURIInExclusionListContainsActuator(){
        String input = "url://sample/actuator";
        boolean output = isURIInExclusionList(input);
        Assert.assertTrue(output);
    }

    @Test
    public void testIsURIInExclusionListContainsApiDocs(){
        String input = "url://sample/api-docs";
        boolean output = isURIInExclusionList(input);
        Assert.assertTrue(output);
    }

    @Test
    public void testIsURIInExclusionListContainsError(){
        String input = "url://sample/error";
        boolean output = isURIInExclusionList(input);
        Assert.assertTrue(output);
    }

    @Test
    public void testIsURIInExclusionListContainsSwaggerResources(){
        String input = "url://sample/swagger-resources";
        boolean output = isURIInExclusionList(input);
        Assert.assertTrue(output);
    }

    @Test
    public void testIsURIInExclusionListContainsSwaggerUI(){
        String input = "url://sample/swagger-ui";
        boolean output = isURIInExclusionList(input);
        Assert.assertTrue(output);
    }

    @Test
    public void testIsURIInExclusionListWithoutExclusions(){
        String input = "url://sample";
        boolean output = isURIInExclusionList(input);
        Assert.assertFalse(output);
    }

}