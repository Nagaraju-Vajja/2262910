package com.macys.uop.order.ordercollectorchestrator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.uop.common.lock.model.LockManagerRequest;
import com.macys.uop.fraudprocessor.model.FraudServiceResponse;
import com.macys.uop.order.model.Lock;
import com.macys.uop.order.model.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {

	public static Order getOrderReqest() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(getFileAsString("OrderRequest_Profile.json"), Order.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	public static Order getOrderReqestWithEnrichDisable() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("OrderRequest_ProfileEnrich.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Order getOrderReqestWithPaymentDisable() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("OrderRequest_ProfilePayment.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Order getOrderCollectResponse() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("order-collector-response.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Order getOrderEnrichResponse() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("order-enrich-response.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Order getOrderProfileResponse() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("profile-manager-response.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static LockManagerRequest getCreateLockReqest() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("lockmanager_createlock_request.json"), LockManagerRequest.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Order getOrderProfileResponse2() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("profile-manager-response2.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Order getStreamReqest() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(getFileAsString("StreamRequest.json"), Order.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	public static FraudServiceResponse getStreamReqest_Fraud() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(getFileAsString("StreamRequest.json"),FraudServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	public static Lock getCreateLockReqest1() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("lockmanager_createlock_request.json"), Lock.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Order getOrderReqestForMissingFields() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("OrderRequest_MissingOptionalFields.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Order getOrderReqestForMissingFieldsOutPut() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(getFileAsString("collectorder_MissingOptionalFieldsoutput.json"), Order.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFileAsString(String filename) throws IOException {
		Resource resource = new ClassPathResource(filename);
		InputStream inputStream = resource.getInputStream();
		byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
		String data = new String(bdata, StandardCharsets.UTF_8);
		return data;
	}

}
