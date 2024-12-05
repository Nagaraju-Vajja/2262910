package com.macys.uop.foundation.businessconfigmanager.utils;

/**
 * This class is to specify the error codes for Businessconfigmanager library. 
 * Error code format is UOP-GEN-E05XXX. For this library, the error code starts from 155 till 159. 
 * We can add new error codes based on the new business functionality.
 */

public enum BusinessconfigmanagerStatusCode {
	DB_GET_ERROR("UOP-GEN-E05155", "Error while fetching Businessconfig details from firestore DB"),
	DB_DATA_ERROR("UOP-GEN-E05156", "Requested Business Configuration Details not available in firestore DB");
	
	private String code;
	private String description;

	private BusinessconfigmanagerStatusCode(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}
}
