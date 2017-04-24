package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3Lodging extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private Date arriveDate;
	private Date departureDate;
	private String folioNum;
	private String propertyPhoneNum;
	private String customerServiceNum;
	private KualiDecimal prePaidAmt;
	private KualiDecimal roomRate;
	private KualiDecimal roomTax;
	private String programCode;
	private KualiDecimal callCharges;
	private KualiDecimal foodSvcCharges;
	private KualiDecimal miniBarCharges;
	private KualiDecimal giftShopCharges;
	private KualiDecimal laundryCharges;
	private KualiDecimal healthClubCharges;
	private KualiDecimal movieCharges;
	private KualiDecimal busCtrCharges;
	private KualiDecimal parkingCharges;
	private String otherCode;
	private KualiDecimal otherCharges;
	private KualiDecimal adjustmentAmount;
	
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
	
	public Date getArriveDate() {
		return arriveDate;
	}
	
	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}
	
	public Date getDepartureDate() {
		return departureDate;
	}
	
	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}
	
	public String getFolioNum() {
		return folioNum;
	}
	
	public void setFolioNum(String folioNum) {
		this.folioNum = folioNum;
	}
	
	public String getPropertyPhoneNum() {
		return propertyPhoneNum;
	}
	
	public void setPropertyPhoneNum(String propertyPhoneNum) {
		this.propertyPhoneNum = propertyPhoneNum;
	}
	
	public String getCustomerServiceNum() {
		return customerServiceNum;
	}
	
	public void setCustomerServiceNum(String customerServiceNum) {
		this.customerServiceNum = customerServiceNum;
	}
	
	public KualiDecimal getPrePaidAmt() {
		return prePaidAmt;
	}
	
	public void setPrePaidAmt(KualiDecimal prePaidAmt) {
		this.prePaidAmt = prePaidAmt;
	}
	
	public KualiDecimal getRoomRate() {
		return roomRate;
	}
	
	public void setRoomRate(KualiDecimal roomRate) {
		this.roomRate = roomRate;
	}
	
	public KualiDecimal getRoomTax() {
		return roomTax;
	}
	
	public void setRoomTax(KualiDecimal roomTax) {
		this.roomTax = roomTax;
	}
	
	public String getProgramCode() {
		return programCode;
	}
	
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	
	public KualiDecimal getCallCharges() {
		return callCharges;
	}
	
	public void setCallCharges(KualiDecimal callCharges) {
		this.callCharges = callCharges;
	}
	
	public KualiDecimal getFoodSvcCharges() {
		return foodSvcCharges;
	}
	
	public void setFoodSvcCharges(KualiDecimal foodSvcCharges) {
		this.foodSvcCharges = foodSvcCharges;
	}
	
	public KualiDecimal getMiniBarCharges() {
		return miniBarCharges;
	}
	
	public void setMiniBarCharges(KualiDecimal miniBarCharges) {
		this.miniBarCharges = miniBarCharges;
	}
	
	public KualiDecimal getGiftShopCharges() {
		return giftShopCharges;
	}
	
	public void setGiftShopCharges(KualiDecimal giftShopCharges) {
		this.giftShopCharges = giftShopCharges;
	}
	
	public KualiDecimal getLaundryCharges() {
		return laundryCharges;
	}
	
	public void setLaundryCharges(KualiDecimal laundryCharges) {
		this.laundryCharges = laundryCharges;
	}
	
	public KualiDecimal getHealthClubCharges() {
		return healthClubCharges;
	}
	
	public void setHealthClubCharges(KualiDecimal healthClubCharges) {
		this.healthClubCharges = healthClubCharges;
	}
	
	public KualiDecimal getMovieCharges() {
		return movieCharges;
	}
	
	public void setMovieCharges(KualiDecimal movieCharges) {
		this.movieCharges = movieCharges;
	}
	
	public KualiDecimal getBusCtrCharges() {
		return busCtrCharges;
	}
	
	public void setBusCtrCharges(KualiDecimal busCtrCharges) {
		this.busCtrCharges = busCtrCharges;
	}
	
	public KualiDecimal getParkingCharges() {
		return parkingCharges;
	}
	
	public void setParkingCharges(KualiDecimal parkingCharges) {
		this.parkingCharges = parkingCharges;
	}
	
	public String getOtherCode() {
		return otherCode;
	}
	
	public void setOtherCode(String otherCode) {
		this.otherCode = otherCode;
	}
	
	public KualiDecimal getOtherCharges() {
		return otherCharges;
	}
	
	public void setOtherCharges(KualiDecimal otherCharges) {
		this.otherCharges = otherCharges;
	}
	
	public KualiDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}
	
	public void setAdjustmentAmount(KualiDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
