package edu.arizona.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3NonFuel extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private Integer sequenceNumber;
	private String nonFuelProductCode;
	private String itemDesc;
	private String itemQuant;
	private String itemUnitOfMeasure;
	private String taxRateApplied;
	private KualiDecimal itemTaxAmt;
	private String alternateTaxId;
	private KualiDecimal itemDiscountAmt;
	private KualiDecimal itemTotal;
	
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
	
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public String getNonFuelProductCode() {
		return nonFuelProductCode;
	}
	
	public void setNonFuelProductCode(String nonFuelProductCode) {
		this.nonFuelProductCode = nonFuelProductCode;
	}
	
	public String getItemDesc() {
		return itemDesc;
	}
	
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	
	public String getItemQuant() {
		return itemQuant;
	}
	
	public void setItemQuant(String itemQuant) {
		this.itemQuant = itemQuant;
	}
	
	public String getItemUnitOfMeasure() {
		return itemUnitOfMeasure;
	}
	
	public void setItemUnitOfMeasure(String itemUnitOfMeasure) {
		this.itemUnitOfMeasure = itemUnitOfMeasure;
	}
	
	public String getTaxRateApplied() {
		return taxRateApplied;
	}
	
	public void setTaxRateApplied(String taxRateApplied) {
		this.taxRateApplied = taxRateApplied;
	}
	
	public KualiDecimal getItemTaxAmt() {
		return itemTaxAmt;
	}
	
	public void setItemTaxAmt(KualiDecimal itemTaxAmt) {
		this.itemTaxAmt = itemTaxAmt;
	}
	
	public String getAlternateTaxId() {
		return alternateTaxId;
	}
	
	public void setAlternateTaxId(String alternateTaxId) {
		this.alternateTaxId = alternateTaxId;
	}
	
	public KualiDecimal getItemDiscountAmt() {
		return itemDiscountAmt;
	}
	
	public void setItemDiscountAmt(KualiDecimal itemDiscountAmt) {
		this.itemDiscountAmt = itemDiscountAmt;
	}
	
	public KualiDecimal getItemTotal() {
		return itemTotal;
	}
	
	public void setItemTotal(KualiDecimal itemTotal) {
		this.itemTotal = itemTotal;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
