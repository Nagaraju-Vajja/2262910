package com.macys.uop.foundation.core.utils.xml;

import javax.annotation.PostConstruct;

import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation for {@link XmlUtils}. This implementation internally uses {@link MappingJackson2XmlHttpMessageConverter} for conversion.
 * 
 * @see <a href="https://github.com/FasterXML/jackson-dataformat-xml">Jackson Data Formal XML</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XmlUtilsImpl implements XmlUtils {

	private ObjectMapper xmlMapper;
	
	private final MappingJackson2XmlHttpMessageConverter xmlConverter;
	
	/**
	 * Spring Bean PostConstruct method to initialize @{link MappingJackson2XmlHttpMessageConverter} and {@link ObjectMapper}
	 */
	@PostConstruct
	public void init() {
		xmlMapper = xmlConverter.getObjectMapper();
		xmlMapper.registerModule(new JaxbAnnotationModule());
	}

	@Override
	public String convertToXml(Object obj) {
		String xml = null;
		try {
			xml = xmlMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("XmlMapper unable to writeValueAsString", e);
		}
		return xml;
	}

	@Override
	public <T> T convertFromXml(String xml, Class<T> requiredType) {
		T result = null;
		try {
			result = xmlMapper.readValue(xml, requiredType);
		} catch (JsonProcessingException e) {
			log.error("XmlMapper unable to readValue", e);
		}
		return result;
	}
}
