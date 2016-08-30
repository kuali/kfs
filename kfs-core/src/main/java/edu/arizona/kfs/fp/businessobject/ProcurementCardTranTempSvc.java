package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardTranTempSvc extends PersistableBusinessObjectBase {

	private Integer transactionSequenceRowNumber;
	private Integer sequenceRowNumber;
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
	
	public Date getWeekEndingDate() {
		return weekEndingDate;
	}
	
	public void setWeekEndingDate(Date weekEndingDate) {
		this.weekEndingDate = weekEndingDate;
	}
	
	public void setWeekEndingDate(String weekEndingDate) {
		if(StringUtils.isNotBlank(weekEndingDate)) {
			this.weekEndingDate = (Date) (new SqlDateConverter()).convert(Date.class, weekEndingDate);
		}
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
	
	public void setRegularRate(String regularRate) {
		if(StringUtils.isNotBlank(regularRate)) {
			this.regularRate = new KualiDecimal(regularRate);
		} else {
			this.regularRate = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getRegularHours() {
		return regularHours;
	}
	
	public void setRegularHours(KualiDecimal regularHours) {
		this.regularHours = regularHours;
	}
	
	public void setRegularHours(String regularHours) {
		if(StringUtils.isNotBlank(regularHours)) {
			this.regularHours = new KualiDecimal(regularHours);
		} else {
			this.regularHours = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getOvertimeRate() {
		return overtimeRate;
	}
	
	public void setOvertimeRate(KualiDecimal overtimeRate) {
		this.overtimeRate = overtimeRate;
	}
	
	public void setOvertimeRate(String overtimeRate) {
		if(StringUtils.isNotBlank(overtimeRate)) {
			this.overtimeRate = new KualiDecimal(overtimeRate);
		} else {
			this.overtimeRate = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getOvertimeHours() {
		return overtimeHours;
	}
	
	public void setOvertimeHours(KualiDecimal overtimeHours) {
		this.overtimeHours = overtimeHours;
	}
	
	public void setOvertimeHours(String overtimeHours) {
		if(StringUtils.isNotBlank(overtimeHours)) {
			this.overtimeHours = new KualiDecimal(overtimeHours);
		} else {
			this.overtimeHours = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getMiscExpenseAmt() {
		return miscExpenseAmt;
	}
	
	public void setMiscExpenseAmt(KualiDecimal miscExpenseAmt) {
		this.miscExpenseAmt = miscExpenseAmt;
	}
	
	public void setMiscExpenseAmt(String miscExpenseAmt) {
		if(StringUtils.isNotBlank(miscExpenseAmt)) {
			this.miscExpenseAmt = new KualiDecimal(miscExpenseAmt);
		} else {
			this.miscExpenseAmt = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getSubTotalAmt() {
		return subTotalAmt;
	}
	
	public void setSubTotalAmt(KualiDecimal subTotalAmt) {
		this.subTotalAmt = subTotalAmt;
	}
	
	public void setSubTotalAmt(String subTotalAmt) {
		if(StringUtils.isNotBlank(subTotalAmt)) {
			this.subTotalAmt = new KualiDecimal(subTotalAmt);
		} else {
			this.subTotalAmt = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getSalesTaxAmt() {
		return salesTaxAmt;
	}
	
	public void setSalesTaxAmt(KualiDecimal salesTaxAmt) {
		this.salesTaxAmt = salesTaxAmt;
	}
	
	public void setSalesTaxAmount(String salesTaxAmt) {
		if(StringUtils.isNotBlank(salesTaxAmt)) {
			this.salesTaxAmt = new KualiDecimal(salesTaxAmt);
		} else {
			this.salesTaxAmt = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getDiscountAmt() {
		return discountAmt;
	}
	
	public void setDiscountAmt(KualiDecimal discountAmt) {
		this.discountAmt = discountAmt;
	}
	
	public void setDiscountAmt(String discountAmt) {
		if(StringUtils.isNotBlank(discountAmt)) {
			this.discountAmt = new KualiDecimal(discountAmt);
		} else {
			this.discountAmt = KualiDecimal.ZERO;
		}
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setStartDate(String startDate) {
		if(StringUtils.isNotBlank(startDate)) {
			this.startDate = (Date) (new SqlDateConverter()).convert(Date.class, startDate);
		}
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
	
	protected LinkedHashMap<String, Integer> toStringMapper() {
		LinkedHashMap<String, Integer> m = new LinkedHashMap<String, Integer>();
		m.put("transactionSequenceRowNumber", getTransactionSequenceRowNumber());
		m.put("sequenceRowNumber", getSequenceRowNumber());
		return m;
	}
}
