package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3ShipSvc extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private Integer sequenceNumber;
	private String courierName;
	private String trackingNumber;
	private String serviceDescription;
	private Date pickupDate;
	private String originZip;
	private String originCountryCd;
	private String destinationZip;
	private String destinationCountryCd;
	private KualiDecimal netAmount;
	private KualiDecimal taxAmt;
	private KualiDecimal discAmt;
	private Integer numberOfPackages;
	private String weight;
	private String unitsOfMeasure;
	private String senderName;
	private String senderAddress;
	private String receiverName;
	private String receiverAddress;
	private String customerRefNumber;
	
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
	
	public String getCourierName() {
		return courierName;
	}
	
	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}
	
	public String getTrackingNumber() {
		return trackingNumber;
	}
	
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	public String getServiceDescription() {
		return serviceDescription;
	}
	
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	
	public Date getPickupDate() {
		return pickupDate;
	}
	
	public void setPickupDate(Date pickupDate) {
		this.pickupDate = pickupDate;
	}
	
	public String getOriginZip() {
		return originZip;
	}
	
	public void setOriginZip(String originZip) {
		this.originZip = originZip;
	}
	
	public String getOriginCountryCd() {
		return originCountryCd;
	}
	
	public void setOriginCountryCd(String originCountryCd) {
		this.originCountryCd = originCountryCd;
	}
	
	public String getDestinationZip() {
		return destinationZip;
	}
	
	public void setDestinationZip(String destinationZip) {
		this.destinationZip = destinationZip;
	}
	
	public String getDestinationCountryCd() {
		return destinationCountryCd;
	}
	
	public void setDestinationCountryCd(String destinationCountryCd) {
		this.destinationCountryCd = destinationCountryCd;
	}
	
	public KualiDecimal getNetAmount() {
		return netAmount;
	}
	
	public void setNetAmount(KualiDecimal netAmount) {
		this.netAmount = netAmount;
	}
	
	public KualiDecimal getTaxAmt() {
		return taxAmt;
	}
	
	public void setTaxAmt(KualiDecimal taxAmt) {
		this.taxAmt = taxAmt;
	}
	
	public KualiDecimal getDiscAmt() {
		return discAmt;
	}
	
	public void setDiscAmt(KualiDecimal discAmt) {
		this.discAmt = discAmt;
	}
	
	public Integer getNumberOfPackages() {
		return numberOfPackages;
	}
	
	public void setNumberOfPackages(Integer numberOfPackages) {
		this.numberOfPackages = numberOfPackages;
	}
	
	public String getWeight() {
		return weight;
	}
	
	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public String getUnitsOfMeasure() {
		return unitsOfMeasure;
	}
	
	public void setUnitsOfMeasure(String unitsOfMeasure) {
		this.unitsOfMeasure = unitsOfMeasure;
	}
	
	public String getSenderName() {
		return senderName;
	}
	
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public String getSenderAddress() {
		return senderAddress;
	}
	
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	
	public String getReceiverName() {
		return receiverName;
	}
	
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	
	public String getReceiverAddress() {
		return receiverAddress;
	}
	
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	
	public String getCustomerRefNumber() {
		return customerRefNumber;
	}
	
	public void setCustomerRefNumber(String customerRefNumber) {
		this.customerRefNumber = customerRefNumber;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
