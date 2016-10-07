package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3Rental extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private Date checkOutDate;
	private String rentalAgreementNum;
	private String renterName;
	private String returnCity;
	private String returnState;
	private String returnCountry;
	private Date returnDate;
	private String returnLocation;
	private String customerSvcNum;
	private String rentalClass;
	private KualiDecimal dailyRate;
	private KualiDecimal weeklyRate;
	private KualiDecimal ratePerMile;
	private Integer maxFreeMiles;
	private Integer totalMiles;
	private KualiDecimal oneWayCharges;
	private KualiDecimal insuranceCharges;
	private KualiDecimal regularCharges;
	private KualiDecimal towingCharges;
	private KualiDecimal extraCharges;
	private KualiDecimal lateReturnFee;
	private String adjustCode;
	private KualiDecimal adjustAmount;
	private String progCode;
	private KualiDecimal phoneCharges;
	private KualiDecimal othrCharges;
	private KualiDecimal totalTaxAmount;
	
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
	
	public Date getCheckOutDate() {
		return checkOutDate;
	}
	
	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	
	public String getRentalAgreementNum() {
		return rentalAgreementNum;
	}
	
	public void setRentalAgreementNum(String rentalAgreementNum) {
		this.rentalAgreementNum = rentalAgreementNum;
	}
	
	public String getRenterName() {
		return renterName;
	}
	
	public void setRenterName(String renterName) {
		this.renterName = renterName;
	}
	
	public String getReturnCity() {
		return returnCity;
	}
	
	public void setReturnCity(String returnCity) {
		this.returnCity = returnCity;
	}
	
	public String getReturnState() {
		return returnState;
	}
	
	public void setReturnState(String returnState) {
		this.returnState = returnState;
	}
	
	public String getReturnCountry() {
		return returnCountry;
	}
	
	public void setReturnCountry(String returnCountry) {
		this.returnCountry = returnCountry;
	}
	
	public Date getReturnDate() {
		return returnDate;
	}
	
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	
	public String getReturnLocation() {
		return returnLocation;
	}
	
	public void setReturnLocation(String returnLocation) {
		this.returnLocation = returnLocation;
	}
	
	public String getCustomerSvcNum() {
		return customerSvcNum;
	}
	
	public void setCustomerSvcNum(String customerSvcNum) {
		this.customerSvcNum = customerSvcNum;
	}
	
	public String getRentalClass() {
		return rentalClass;
	}
	
	public void setRentalClass(String rentalClass) {
		this.rentalClass = rentalClass;
	}
	
	public KualiDecimal getDailyRate() {
		return dailyRate;
	}
	
	public void setDailyRate(KualiDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}
	
	public KualiDecimal getWeeklyRate() {
		return weeklyRate;
	}
	
	public void setWeeklyRate(KualiDecimal weeklyRate) {
		this.weeklyRate = weeklyRate;
	}
	
	public KualiDecimal getRatePerMile() {
		return ratePerMile;
	}
	
	public void setRatePerMile(KualiDecimal ratePerMile) {
		this.ratePerMile = ratePerMile;
	}
	
	public Integer getMaxFreeMiles() {
		return maxFreeMiles;
	}
	
	public void setMaxFreeMiles(Integer maxFreeMiles) {
		this.maxFreeMiles = maxFreeMiles;
	}
	
	public Integer getTotalMiles() {
		return totalMiles;
	}
	
	public void setTotalMiles(Integer totalMiles) {
		this.totalMiles = totalMiles;
	}
	
	public KualiDecimal getOneWayCharges() {
		return oneWayCharges;
	}
	
	public void setOneWayCharges(KualiDecimal oneWayCharges) {
		this.oneWayCharges = oneWayCharges;
	}
	
	public KualiDecimal getInsuranceCharges() {
		return insuranceCharges;
	}
	
	public void setInsuranceCharges(KualiDecimal insuranceCharges) {
		this.insuranceCharges = insuranceCharges;
	}
	
	public KualiDecimal getRegularCharges() {
		return regularCharges;
	}
	
	public void setRegularCharges(KualiDecimal regularCharges) {
		this.regularCharges = regularCharges;
	}
	
	public KualiDecimal getTowingCharges() {
		return towingCharges;
	}
	
	public void setTowingCharges(KualiDecimal towingCharges) {
		this.towingCharges = towingCharges;
	}
	
	public KualiDecimal getExtraCharges() {
		return extraCharges;
	}
	
	public void setExtraCharges(KualiDecimal extraCharges) {
		this.extraCharges = extraCharges;
	}
	
	public KualiDecimal getLateReturnFee() {
		return lateReturnFee;
	}
	
	public void setLateReturnFee(KualiDecimal lateReturnFee) {
		this.lateReturnFee = lateReturnFee;
	}
	
	public String getAdjustCode() {
		return adjustCode;
	}
	
	public void setAdjustCode(String adjustCode) {
		this.adjustCode = adjustCode;
	}
	
	public KualiDecimal getAdjustAmount() {
		return adjustAmount;
	}
	
	public void setAdjustAmount(KualiDecimal adjustAmount) {
		this.adjustAmount = adjustAmount;
	}
	
	public String getProgCode() {
		return progCode;
	}
	
	public void setProgCode(String progCode) {
		this.progCode = progCode;
	}
	
	public KualiDecimal getPhoneCharges() {
		return phoneCharges;
	}
	
	public void setPhoneCharges(KualiDecimal phoneCharges) {
		this.phoneCharges = phoneCharges;
	}
	
	public KualiDecimal getOthrCharges() {
		return othrCharges;
	}
	
	public void setOthrCharges(KualiDecimal othrCharges) {
		this.othrCharges = othrCharges;
	}
	
	public KualiDecimal getTotalTaxAmount() {
		return totalTaxAmount;
	}
	
	public void setTotalTaxAmount(KualiDecimal totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
