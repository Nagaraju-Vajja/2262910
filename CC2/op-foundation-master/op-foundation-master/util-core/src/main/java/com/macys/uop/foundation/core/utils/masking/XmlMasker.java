package com.macys.uop.foundation.core.utils.masking;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zalando.problem.Status;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Data masking implementation which masks XML string.
 * <br>
 * During data masking {@link XmlMasker#maskConfigMap} is referred for masking configuration.
 * <br>
 * Refer to resource folder masking-config.json file for masking configuration details.
 */
@Slf4j
public class XmlMasker implements IDataMasker, LoggingUtil, ProblemUtil, XmlMaskerHelper {
	private Map<String, IDataMasker> maskConfigMap = new HashMap<>();

	public XmlMasker(Map<String, IDataMasker> maskConfigMap) {
		this.maskConfigMap = maskConfigMap;
	}
	
	/**
	 * Method which masks valid XML string.
	 *  
	 * {@link IDataMasker#maskData(String)} 
	 */
	@Override
	public String maskData(String xmlData) {
		if (StringUtils.isAllBlank(xmlData)) {
			return "";
		}
		if(CollectionUtils.isEmpty(maskConfigMap)) {
			return xmlData;
		}
		String result = null;
		 try {
			Document document = toXmlDocument(xmlData);
			maskElements(document.getDocumentElement());
			result = toXmlString(document);
		} catch (Exception e) {
			com.macys.uop.foundation.core.utils.exception.Error errorInfo = com.macys.uop.foundation.core.utils.exception.Error
    				.builder()
    				.withCode(CommonStatusCode.XML_PROCESSING_ERROR.getCode())
    				.withMessage(CommonStatusCode.XML_PROCESSING_ERROR.getDescription())
    				.withErrorDetail(ErrorDetail.builder()
    						.withDomain("Global")
    						.withReason("Xml Processing Error")
    						.withMessage(e.getMessage())
    						.build())
    				.build();
        	
        	new LogMessageBuilder()
        	.withContext("Xml Masking")
        	.withLogType(LogTypeEnum.ERROR)
			.withErrorCode(CommonStatusCode.XML_PROCESSING_ERROR.getCode())
			.withErrorMessage(CommonStatusCode.XML_PROCESSING_ERROR.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(e))
			.withLogger(log)
    		.buildDisableChecking()
    		.logAsError();
    		
    		throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorInfo);
		}

		return result;
	}
	
	/**
	 * Recursive method to mask XML node value.
	 * <br>
	 * This method recursively traverses all the leaf nodes and masks their data based on the {@link XmlMasker#maskConfigMap} entry.
	 * 
	 * @param node Root node
	 */
	private void maskElements(Node node) {
		NodeList nodeList = node.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			// recursively call maskElements until you find a Leaf node
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				maskElements(currentNode);
			} else if (currentNode.getNodeType() == Node.TEXT_NODE) {
				// leaf node.. apply masking logic
				String name = currentNode.getParentNode().getNodeName();
				String value = currentNode.getParentNode().getTextContent();
				if (name != null && maskConfigMap.containsKey(name)) {
					currentNode.setTextContent(maskConfigMap.get(name).maskData(value));
				}
			} 
		}
	}
}
