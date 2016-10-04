package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3Add extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private String invoiceNumber;
	private Date orderDate;
	private String purchaseTime;
	private String shipPostal;
	private String destinationPostal;
	private String destinationCountryCode;
	private KualiDecimal taxAmount;
	private KualiDecimal taxRate;
	private KualiDecimal discountAmount;
	private KualiDecimal freightAmount;
	private KualiDecimal dutyAmount;
	
	
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
	
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	public Date getOrderDate() {
		return orderDate;
	}
	
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public String getPurchaseTime() {
		return purchaseTime;
	}
	
	public void setPurchaseTime(String purchaseTime) {
		this.purchaseTime = purchaseTime;
	}
	
	public String getShipPostal() {
		return shipPostal;
	}
	
	public void setShipPostal(String shipPostal) {
		this.shipPostal = shipPostal;
	}
	
	public String getDestinationPostal() {
		return destinationPostal;
	}
	
	public void setDestinationPostal(String destinationPostal) {
		this.destinationPostal = destinationPostal;
	}
	
	public String getDestinationCountryCode() {
		return destinationCountryCode;
	}
	
	public void setDestinationCountryCode(String destinationCountryCode) {
		this.destinationCountryCode = destinationCountryCode;
	}
	
	public KualiDecimal getTaxAmount() {
		return taxAmount;
	}
	
	public void setTaxAmount(KualiDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	
	public KualiDecimal getTaxRate() {
		return taxRate;
	}
	
	public void setTaxRate(KualiDecimal taxRate) {
		this.taxRate = taxRate;
	}
	
	public KualiDecimal getDiscountAmount() {
		return discountAmount;
	}
	
	public void setDiscountAmount(KualiDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
	
	public KualiDecimal getFreightAmount() {
		return freightAmount;
	}
	
	public void setFreightAmount(KualiDecimal freightAmount) {
		this.freightAmount = freightAmount;
	}
	
	public KualiDecimal getDutyAmount() {
		return dutyAmount;
	}
	
	public void setDutyAmount(KualiDecimal dutyAmount) {
		this.dutyAmount = dutyAmount;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
	
}
