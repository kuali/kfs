package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3AddUser extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private Date userEffectiveDate;
	private KualiDecimal userAmount;
	
	public String getDocumentNumber() {
		return documentNumber;
	}
	
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	
	public Integer getFinancialDocumentTransactionLineNumber() {
		return financialDocumentTransactionLineNumber;
	}
	
	public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
		this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
	}
	
	public Date getUserEffectiveDate() {
		return userEffectiveDate;
	}
	
	public void setUserEffectiveDate(Date userEffectiveDate) {
		this.userEffectiveDate = userEffectiveDate;
	}
	
	public KualiDecimal getUserAmount() {
		return userAmount;
	}
	
	public void setUserAmount(KualiDecimal userAmount) {
		this.userAmount = userAmount;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
