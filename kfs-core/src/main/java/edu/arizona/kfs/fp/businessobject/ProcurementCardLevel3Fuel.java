package edu.arizona.kfs.fp.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3Fuel extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private String oilBrandName;
	private String odometerReading;
	private String fleetId;
	private String messageId;
	private String usage;
	private String fuelServiceType;
	private String fuelProductCd;
	private String productTypeCd;
	private BigDecimal fuelQuantity;
	private Integer fuelUnitOfMeasure;
	private KualiDecimal fuelUnitPrice;
	private KualiDecimal fuelSaleAmount;
	private KualiDecimal fuelDiscountAmount;
	private KualiDecimal taxAmount1;
	private KualiDecimal taxAmount2;
	private KualiDecimal totalAmount;
	
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
	
	public String getOilBrandName() {
		return oilBrandName;
	}
	
	public void setOilBrandName(String oilBrandName) {
		this.oilBrandName = oilBrandName;
	}
	
	public String getOdometerReading() {
		return odometerReading;
	}
	
	public void setOdometerReading(String odometerReading) {
		this.odometerReading = odometerReading;
	}
	
	public String getFleetId() {
		return fleetId;
	}
	
	public void setFleetId(String fleetId) {
		this.fleetId = fleetId;
	}
	
	public String getMessageId() {
		return messageId;
	}
	
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	public String getFuelServiceType() {
		return fuelServiceType;
	}
	
	public void setFuelServiceType(String fuelServiceType) {
		this.fuelServiceType = fuelServiceType;
	}
	
	public String getFuelProductCd() {
		return fuelProductCd;
	}
	
	public void setFuelProductCd(String fuelProductCd) {
		this.fuelProductCd = fuelProductCd;
	}
	
	public String getProductTypeCd() {
		return productTypeCd;
	}
	
	public void setProductTypeCd(String productTypeCd) {
		this.productTypeCd = productTypeCd;
	}
	
	public BigDecimal getFuelQuantity() {
		return fuelQuantity;
	}
	
	public void setFuelQuantity(BigDecimal fuelQuantity) {
		this.fuelQuantity = fuelQuantity;
	}
	
	public Integer getFuelUnitOfMeasure() {
		return fuelUnitOfMeasure;
	}
	
	public void setFuelUnitOfMeasure(Integer fuelUnitOfMeasure) {
		this.fuelUnitOfMeasure = fuelUnitOfMeasure;
	}
	
	public KualiDecimal getFuelUnitPrice() {
		return fuelUnitPrice;
	}
	
	public void setFuelUnitPrice(KualiDecimal fuelUnitPrice) {
		this.fuelUnitPrice = fuelUnitPrice;
	}
	
	public KualiDecimal getFuelSaleAmount() {
		return fuelSaleAmount;
	}
	
	public void setFuelSaleAmount(KualiDecimal fuelSaleAmount) {
		this.fuelSaleAmount = fuelSaleAmount;
	}
	
	public KualiDecimal getFuelDiscountAmount() {
		return fuelDiscountAmount;
	}
	
	public void setFuelDiscountAmount(KualiDecimal fuelDiscountAmount) {
		this.fuelDiscountAmount = fuelDiscountAmount;
	}
	
	public KualiDecimal getTaxAmount1() {
		return taxAmount1;
	}
	
	public void setTaxAmount1(KualiDecimal taxAmount1) {
		this.taxAmount1 = taxAmount1;
	}
	
	public KualiDecimal getTaxAmount2() {
		return taxAmount2;
	}
	
	public void setTaxAmount2(KualiDecimal taxAmount2) {
		this.taxAmount2 = taxAmount2;
	}
	
	public KualiDecimal getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(KualiDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
