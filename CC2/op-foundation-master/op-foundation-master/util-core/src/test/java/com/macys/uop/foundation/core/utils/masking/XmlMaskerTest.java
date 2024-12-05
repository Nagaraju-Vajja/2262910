package com.macys.uop.foundation.core.utils.masking;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.ThrowableProblem;

@RunWith(SpringRunner.class)
public class XmlMaskerTest 
{
	private static String XMLDATA="<Company><Employee><FirstName>Tanmay</FirstName><LastName>Patil</LastName><ContactNo>1234567890</ContactNo></Employee></Company>"; 
	
	@Test
	public void testMaskData() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker xmlMasker=new XmlMasker(maskConfigMap);
		String result=xmlMasker.maskData(XMLDATA);
		
		Assert.assertEquals("******7890", getDataBetweenTwoStrings(result, "<ContactNo>", "</ContactNo>"));
	}
	
	@Test
	public void testMaskDataBlank() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker xmlMasker=new XmlMasker(maskConfigMap);
		String data=null;
		String result=xmlMasker.maskData(data);
		
		Assert.assertEquals("", result);
	}
	
	@Test
	public void testMaskDataConfigBlank() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		IDataMasker xmlMasker=new XmlMasker(maskConfigMap);
		String result=xmlMasker.maskData(XMLDATA);
		
		Assert.assertEquals(XMLDATA, result);
	}
	
	@Test
	public void testMaskDataException() {
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker xmlMasker=new XmlMasker(maskConfigMap);
		
		try {
			xmlMasker.maskData("<abc>");
		} catch(Exception e) {
			Assert.assertTrue(e instanceof ThrowableProblem);
		}
	}
	
	private String getDataBetweenTwoStrings(String text, String pattern1, String pattern2) {
		String result="";
		Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
		Matcher m = p.matcher(text);
		while (m.find()) {
			result=m.group(1);
		}
		return result;
	}
}
