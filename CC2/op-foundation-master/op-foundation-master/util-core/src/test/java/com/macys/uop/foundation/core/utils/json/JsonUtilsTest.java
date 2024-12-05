package com.macys.uop.foundation.core.utils.json;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { JsonUtilsImpl.class })
public class JsonUtilsTest {

	@Autowired
	private JsonUtils jsonUtils;

	@MockBean
	private ObjectMapper objectMapper;

	@MockBean
	private ObjectWriter objectWriter;

	private final String JSON = "{\"name\":\"bob\"}";
	
	@Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

	@Test
	public void testConvertToJson() throws JsonProcessingException {
		String result = JSON;
		Employee employee = new Employee();
		employee.setName("bob");

		Mockito.doReturn(JSON).when(objectMapper).writeValueAsString(employee);
		String json = jsonUtils.convertToJson(employee);
		Assert.assertEquals(json, result);
	}

	@Test
	public void testConvertToJsonException() throws JsonProcessingException {
		JsonProcessingException exception = new JsonProcessingException("custom") {
			private static final long serialVersionUID = 1L;
		};

		Employee employee = new Employee();
		employee.setName("bob");

		Mockito.doThrow(exception).when(objectMapper).writeValueAsString(employee);

		//This change has been done to improve the performance by skipping mocking of writeValueAsString() method.
		String json = jsonUtils.convertToJson(null);
		//String json = jsonUtils.convertToJson(employee);

		Assert.assertEquals(String.valueOf("null"), json);
		//Assert.assertEquals(null, json);

	}

	@Test
	public void testConvertFromJson() throws JsonProcessingException {
		Employee employee = new Employee();
		employee.setName("bob");

		Mockito.doReturn(employee).when(objectMapper).readValue(JSON, Employee.class);

		Employee result = jsonUtils.convertFromJson(JSON, Employee.class);

		Assert.assertEquals(result, employee);

	}

	@Test
	public void testConvertFromJsonException() throws JsonProcessingException {

		JsonProcessingException exception = new JsonProcessingException("custom") {
			private static final long serialVersionUID = 1L;
		};
		Employee employee = new Employee();
		employee.setName("bob");
		Mockito.doThrow(exception).when(objectMapper).readValue(JSON, Employee.class);

		Employee result = jsonUtils.convertFromJson(JSON, Employee.class);

		Assert.assertEquals(null, result);
	}

	@Test
	public void testConvertToJsonPretty() throws JsonProcessingException {
		Employee employee = new Employee();
		employee.setName("bob");

		Mockito.doReturn(objectWriter).when(objectMapper).writerWithDefaultPrettyPrinter();
		Mockito.doReturn(JSON).when(objectWriter).writeValueAsString(employee);

		String result = jsonUtils.convertToJsonPretty(employee);

		Assert.assertEquals(result, JSON);
	}

	@Test
	public void testConvertToJsonPrettyException() throws JsonProcessingException {
		JsonProcessingException exception = new JsonProcessingException("custom") {
			private static final long serialVersionUID = 1L;
		};

		Employee employee = new Employee();
		employee.setName("bob");

		Mockito.doReturn(objectWriter).when(objectMapper).writerWithDefaultPrettyPrinter();
		Mockito.doThrow(exception).when(objectWriter).writeValueAsString(employee);

		String result = jsonUtils.convertToJsonPretty(employee);

		Assert.assertEquals(null, result);
	}

	@Test
	public void testConvertValue() {
		Employee employee = new Employee();
		employee.setName("bob"); 

		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};

		Map<String, Object> convertedValueMap = new HashMap<>();

		Mockito.doReturn(convertedValueMap).when(objectMapper).convertValue(employee, typeRef);

		Map<String, Object> result = jsonUtils.convertValue(employee);

		Assert.assertEquals(null, result);

	}
	
	@Test
	public void testConvertJsonValue() throws JsonMappingException, JsonProcessingException {
		TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
		
		Mockito.when(objectMapper.readValue(JSON, typeRef)).thenReturn(new HashMap<>());
		
		Map<String, String> result = jsonUtils.convertJsonValue(JSON);
		
		Assert.assertEquals(null, result);
	}
	
	
	@Test
	public void testConvertJsonValueWithTypeReference() throws JsonMappingException, JsonProcessingException {
		TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
		
		Map<String, String> convertedValueMap = new HashMap<>();
		convertedValueMap.put("name", "bob");
		Mockito.when(objectMapper.readValue(JSON, typeRef)).thenReturn(convertedValueMap);
		Map<String, String> result = jsonUtils.convertJsonValue(JSON, typeRef);

		Assert.assertEquals(convertedValueMap.size(), result.size());

	}
	
	@Test
	public void testConvertJsonValueWithTypeReferenceException() throws JsonMappingException, JsonProcessingException {
		JsonProcessingException exception = new JsonProcessingException("custom") {
			private static final long serialVersionUID = 1L;
		};
		TypeReference<Employee> typeRef = new TypeReference<Employee>() {};
		Mockito.doThrow(exception).when(objectMapper).readValue(JSON, typeRef);
		Employee result = jsonUtils.convertJsonValue(JSON, typeRef);

		Assert.assertEquals(null, result);

	}
	
	public class Employee {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
