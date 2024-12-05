package com.macys.uop.foundation.core.utils.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that captures audit detail information.
 * <br>
 * Refer to Confluence page <a href="https://confluence.federated.fds/display/OCOM/UOP+Order+Audit+Framework+-+Solution+Design">Audit Solution Design</a>
 * for field description. 
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditDetail {
	private String referenceId;
	private String referenceType;
}
