package com.macys.uop.foundation.core.utils.json;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link JsonUtils} Default Implementation.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtilsImpl implements JsonUtils {
	private final ObjectMapper objectMapper;

	/**
	 * {@link JsonUtils#convertToJson(Object)}
	 */
	@Override
	public String convertToJson(Object obj) {
		ObjectMapper mapper=new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("ObjectMapper unable to writeValueAsString", e);
		}
		finally {
			mapper = null;
		}
		return json;
	}
	
	/**
	 * {@link JsonUtils#convertFromJson(String json, Class<T> requiredType)}
	 */
	@Override
	public <T> T convertFromJson(String json, Class<T> requiredType) {
		ObjectMapper mapper = new ObjectMapper();
		T result = null;
		try {
			result = objectMapper.readValue(json, requiredType);
		} catch (JsonProcessingException e) {
			log.error("ObjectMapper unable to readValue", e);
		} finally {
			mapper = null;
		}
		return result;
	}

	/**
	 * {@link JsonUtils#convertToJsonPretty(Object obj)}
	 */
	@Override
	public String convertToJsonPretty(Object obj) {
		String json = null;
		try {
			json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("ObjectMapper unable to writeValueAsString", e);
		}
		return json;
	}

	/**
	 * {@link JsonUtils#convertValue(Object fromValue)}
	 */
	@Override
	public Map<String, Object> convertValue(Object fromValue) {
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
		return objectMapper.convertValue(fromValue, typeRef);
	}
	
	/**
	 * {@link JsonUtils#convertJsonValue(Object fromValue)}
	 */
	@Override
	public Map<String, String> convertJsonValue(String json) {
		Map<String, String> result=null;
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
		try {
			return objectMapper.readValue(json, typeRef);
		} catch (JsonProcessingException e) {
			log.error("ObjectMapper unable to convertJsonValue", e);
		} 
		return result;
	}
	
	/**
	 * {@link JsonUtils#convertJsonValue(Object fromValue)}
	 */
	@Override
	public <T> T convertJsonValue(String json, TypeReference<T> valueTypeRef) {
		T result = null;
		try {
			ObjectMapper mapper=new ObjectMapper();
			return mapper.readValue(json, valueTypeRef);
		} catch (JsonProcessingException e) {
			log.error("ObjectMapper unable to readValue", e);
		}
		return result;
	} 
}
