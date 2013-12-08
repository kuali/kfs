package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;

public class AwardBasisOfPaymentDTO implements Serializable {

	private static final long serialVersionUID = -7293109685985758125L;
	
	private String basisOfPaymentCode;
    private String description;
    
	public String getBasisOfPaymentCode() {
		return basisOfPaymentCode;
	}
	public void setBasisOfPaymentCode(String basisOfPaymentCode) {
		this.basisOfPaymentCode = basisOfPaymentCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
