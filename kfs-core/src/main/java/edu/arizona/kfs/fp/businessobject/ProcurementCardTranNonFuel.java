package edu.arizona.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardTranNonFuel extends PersistableBusinessObjectBase {

	private Integer transactionSequenceRowNumber;
	private Integer sequenceRowNumber;
	private String nonFuelProductCode;
	private String itemDesc;
	private String itemQuant;
	private String itemUnitOfMeasure;
	private String taxRateApplied;
	private KualiDecimal itemTaxAmt;
	private String alternateTaxId;
	private KualiDecimal itemDiscountAmt;
	private KualiDecimal itemTotal;
	
	public Integer getTransactionSequenceRowNumber() {
		return transactionSequenceRowNumber;
	}
	
	public void setTransactionSequenceRowNumber(Integer transactionSequenceRowNumber) {
		this.transactionSequenceRowNumber = transactionSequenceRowNumber;
	}
	
	public Integer getSequenceRowNumber() {
		return sequenceRowNumber;
	}
	
	public void setSequenceRowNumber(Integer sequenceRowNumber) {
		this.sequenceRowNumber = sequenceRowNumber;
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
	
	public void setItemTaxAmt(String itemTaxAmt) {
		if(StringUtils.isNotBlank(itemTaxAmt)) {
			this.itemTaxAmt = new KualiDecimal(itemTaxAmt);
		} else {
			this.itemTaxAmt = KualiDecimal.ZERO;
		}
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
	
	public void setItemDiscountAmt(String itemDiscountAmt) {
		if(StringUtils.isNotBlank(itemDiscountAmt)) {
			this.itemDiscountAmt = new KualiDecimal(itemDiscountAmt);
		} else {
			this.itemDiscountAmt = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getItemTotal() {
		return itemTotal;
	}
	
	public void setItemTotal(KualiDecimal itemTotal) {
		this.itemTotal = itemTotal;
	}
	
	public void setItemTotal(String itemTotal) {
		if(StringUtils.isNotBlank(itemTotal)) {
			this.itemTotal = new KualiDecimal(itemTotal);
		} else {
			this.itemTotal = KualiDecimal.ZERO;
		}
	}
	
	protected LinkedHashMap<String, Integer> toStringMapper() {
		LinkedHashMap<String, Integer> m = new LinkedHashMap<String, Integer>();
		m.put("transactionSequenceRowNumber", getTransactionSequenceRowNumber());
		m.put("sequenceRowNumber", getSequenceRowNumber());
		return m;
	}
}
