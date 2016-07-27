package edu.arizona.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ErrorCertification extends PersistableBusinessObjectBase {

	private Integer errorCertID;
	private String expenditureDescription;
	private String expenditureProjectBenefit;
	private String errorDescription;
	private String errorCorrectionReason;
	
	public Integer getErrorCertID() {
		return errorCertID;
	}
	
	public void setErrorCertID(Integer errorCertID) {
		this.errorCertID = errorCertID;
	}
	
	public String getExpenditureDescription() {
		return expenditureDescription;
	}
	
	public void setExpenditureDescription(String expenditureDescription) {
		this.expenditureDescription = expenditureDescription;
	}
	
	public String getExpenditureProjectBenefit() {
		return expenditureProjectBenefit;
	}
	
	public void setExpenditureProjectBenefit(String expenditureProjectBenefit) {
		this.expenditureProjectBenefit = expenditureProjectBenefit;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
	public String getErrorCorrectionReason() {
		return errorCorrectionReason;
	}
	
	public void setErrorCorrectionReason(String errorCorrectionReason) {
		this.errorCorrectionReason = errorCorrectionReason;
	}
	
	protected LinkedHashMap<String, Object> toStringMapper() {
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		
		m.put("errorCertID", getErrorCertID());
		m.put("expenditureDescription", getExpenditureDescription());
		m.put("expenditureProjectBenefit", getExpenditureProjectBenefit());
		m.put("errorDescription", getErrorDescription());
		m.put("errorCorrectionReason", getErrorCorrectionReason());
		
		return m;
	}
	
}
