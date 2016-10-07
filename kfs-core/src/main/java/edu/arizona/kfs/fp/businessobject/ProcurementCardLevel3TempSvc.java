package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3TempSvc extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private Integer sequenceNumber;
	private Date weekEndingDate;
	private String serviceDesc;
	private String employeeName;
	private KualiDecimal regularRate;
	private KualiDecimal regularHours;
	private KualiDecimal overtimeRate;
	private KualiDecimal overtimeHours;
	private KualiDecimal miscExpenseAmt;
	private KualiDecimal subTotalAmt;
	private KualiDecimal salesTaxAmt;
	private KualiDecimal discountAmt;
	private Date startDate;
	private String supervisor;
	private String timeSheetNumber;
	private String commodityCode;
	private String jobCode;
	
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
	
	public Date getWeekEndingDate() {
		return weekEndingDate;
	}
	
	public void setWeekEndingDate(Date weekEndingDate) {
		this.weekEndingDate = weekEndingDate;
	}
	
	public String getServiceDesc() {
		return serviceDesc;
	}
	
	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public KualiDecimal getRegularRate() {
		return regularRate;
	}
	
	public void setRegularRate(KualiDecimal regularRate) {
		this.regularRate = regularRate;
	}
	
	public KualiDecimal getRegularHours() {
		return regularHours;
	}
	
	public void setRegularHours(KualiDecimal regularHours) {
		this.regularHours = regularHours;
	}
	
	public KualiDecimal getOvertimeRate() {
		return overtimeRate;
	}
	
	public void setOvertimeRate(KualiDecimal overtimeRate) {
		this.overtimeRate = overtimeRate;
	}
	
	public KualiDecimal getOvertimeHours() {
		return overtimeHours;
	}
	
	public void setOvertimeHours(KualiDecimal overtimeHours) {
		this.overtimeHours = overtimeHours;
	}
	
	public KualiDecimal getMiscExpenseAmt() {
		return miscExpenseAmt;
	}
	
	public void setMiscExpenseAmt(KualiDecimal miscExpenseAmt) {
		this.miscExpenseAmt = miscExpenseAmt;
	}
	
	public KualiDecimal getSubTotalAmt() {
		return subTotalAmt;
	}
	
	public void setSubTotalAmt(KualiDecimal subTotalAmt) {
		this.subTotalAmt = subTotalAmt;
	}
	
	public KualiDecimal getSalesTaxAmt() {
		return salesTaxAmt;
	}
	
	public void setSalesTaxAmt(KualiDecimal salesTaxAmt) {
		this.salesTaxAmt = salesTaxAmt;
	}
	
	public KualiDecimal getDiscountAmt() {
		return discountAmt;
	}
	
	public void setDiscountAmt(KualiDecimal discountAmt) {
		this.discountAmt = discountAmt;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public String getSupervisor() {
		return supervisor;
	}
	
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	
	public String getTimeSheetNumber() {
		return timeSheetNumber;
	}
	
	public void setTimeSheetNumber(String timeSheetNumber) {
		this.timeSheetNumber = timeSheetNumber;
	}
	
	public String getCommodityCode() {
		return commodityCode;
	}
	
	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}
	
	public String getJobCode() {
		return jobCode;
	}
	
	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
