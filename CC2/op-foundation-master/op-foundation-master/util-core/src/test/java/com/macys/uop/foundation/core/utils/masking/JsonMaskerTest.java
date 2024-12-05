package com.macys.uop.foundation.core.utils.masking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.ThrowableProblem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { JsonMasker.class })
@Slf4j
public class JsonMaskerTest 
{
	private static String JSONDATA="{\"name\":\"test\",\"accountNumber\":\"1234567890\",\"country\":\"in\"}"; 
	private static String JSONARRAYDATA="[{\"name\":\"test1\",\"accountNumber\":\"111111111\",\"country\":\"in\"},{\"name\":\"test2\",\"accountNumber\":\"111111111\",\"country\":\"in\"}]"; 
	
	@Test
	public void testMaskData() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		
		String result=jsonMasker.maskData(JSONDATA);
		Map<String, String> resultMap=convertJsonValueToMap(result);
		
		Assert.assertEquals("******7890", resultMap.get("accountNumber"));
	}
	
	@Test
	public void testMaskDataBlank() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		String data=null;
		String result=jsonMasker.maskData(data);
		
		Assert.assertEquals("", result);
	}
	
	@Test
	public void testMaskDataConfigBlank() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		
		String result=jsonMasker.maskData(JSONDATA);
		
		Assert.assertEquals(JSONDATA, result);
	}
	
	@Test
	public void testMaskDataArray() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		
		String result=jsonMasker.maskData(JSONARRAYDATA);
		List<HashMap<String, String>> listMap=convertJsonValueToListMap(result);
		
		Assert.assertEquals("*****1111", listMap.get(0).get("accountNumber"));
	}
	
	@Test
	public void testMaskDataWithJsonProcessingException() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		try {
				jsonMasker.maskData("<abc>");
		} catch(Exception e) {
			Assert.assertTrue(e instanceof ThrowableProblem);
		}
	}
	
	
	private Map<String, String> convertJsonValueToMap(String json) {
		Map<String, String> result=null;
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
		ObjectMapper mapper=new ObjectMapper();
		try {
			return mapper.readValue(json, typeRef);
		} catch (JsonProcessingException e) {
			log.error("ObjectMapper unable to convertJsonValue", e);
		} 
		return result;
	}
	
	private List<HashMap<String, String>> convertJsonValueToListMap(String json) {
		List<HashMap<String, String>> result=null;
		TypeReference<List<HashMap<String, String>>> typeRef = new TypeReference<List<HashMap<String, String>>>() {};
		ObjectMapper mapper=new ObjectMapper();
		try {
			return mapper.readValue(json, typeRef);
		} catch (JsonProcessingException e) {
			log.error("ObjectMapper unable to convertJsonValue", e);
		} 
		return result;
	}
}
