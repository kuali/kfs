package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardTranShipSvc extends PersistableBusinessObjectBase {

	private Integer transactionSequenceRowNumber;
	private Integer sequenceRowNumber;
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
	
	public void setPickupDate(String pickupDate) {
		if(StringUtils.isNotBlank(pickupDate)) {
			this.pickupDate = (Date) (new SqlDateConverter()).convert(Date.class, pickupDate);
		}
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
	
	public void setNetAmount(String netAmount) {
		if(StringUtils.isNotBlank(netAmount)) {
			this.netAmount = new KualiDecimal(netAmount);
		} else {
			this.netAmount = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTaxAmt() {
		return taxAmt;
	}
	
	public void setTaxAmt(KualiDecimal taxAmt) {
		this.taxAmt = taxAmt;
	}
	
	public void setTaxAmt(String taxAmt) {
		if(StringUtils.isNotBlank(taxAmt)) {
			this.taxAmt = new KualiDecimal(taxAmt);
		} else {
			this.taxAmt = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getDiscAmt() {
		return discAmt;
	}
	
	public void setDiscAmt(KualiDecimal discAmt) {
		this.discAmt = discAmt;
	}
	
	public void setDiscAmt(String discAmt) {
		if(StringUtils.isNotBlank(discAmt)) {
			this.discAmt = new KualiDecimal(discAmt);
		} else {
			this.discAmt = KualiDecimal.ZERO;
		}
	}
	
	public Integer getNumberOfPackages() {
		return numberOfPackages;
	}
	
	public void setNumberOfPackages(Integer numberOfPackages) {
		this.numberOfPackages = numberOfPackages;
	}
	
	public void setNumberOfPackages(String numberOfPackages) {
		if(StringUtils.isNotBlank(numberOfPackages)) {
			this.numberOfPackages = new Integer(numberOfPackages);
		} else {
			this.numberOfPackages = 0;
		}
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
	
	protected LinkedHashMap<String, Integer> toStringMapper() {
		LinkedHashMap<String, Integer> m = new LinkedHashMap<String, Integer>();
		m.put("transactionSequenceRowNumber", getTransactionSequenceRowNumber());
		m.put("sequenceRowNumber", getSequenceRowNumber());
		return m;
	}
}
