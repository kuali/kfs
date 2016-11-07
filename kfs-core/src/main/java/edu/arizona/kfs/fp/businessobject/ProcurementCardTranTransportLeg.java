package edu.arizona.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardTranTransportLeg extends PersistableBusinessObjectBase {

	private Integer transactionSequenceRowNumber;
	private Integer sequenceRowNumber;
	private String carrierCode;
	private String serviceClass;
	private String departCity;
	private String conjunctionTicket;
	private String exchangeTicket;
	private String destinationCity;
	private String fareBasisCode;
	private String flightNumber;
	private String departTime;
	private String departTimeSegment;
	private String arriveTime;
	private String arriveTimeSegment;
	private KualiDecimal fare;
	private KualiDecimal fees;
	private KualiDecimal taxes;
	private String endorsements;
	private String controlCode;
	
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
	
	public String getCarrierCode() {
		return carrierCode;
	}
	
	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}
	
	public String getServiceClass() {
		return serviceClass;
	}
	
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	
	public String getDepartCity() {
		return departCity;
	}
	
	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}
	
	public String getConjunctionTicket() {
		return conjunctionTicket;
	}
	
	public void setConjunctionTicket(String conjunctionTicket) {
		this.conjunctionTicket = conjunctionTicket;
	}
	
	public String getExchangeTicket() {
		return exchangeTicket;
	}
	
	public void setExchangeTicket(String exchangeTicket) {
		this.exchangeTicket = exchangeTicket;
	}
	
	public String getDestinationCity() {
		return destinationCity;
	}
	
	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}
	
	public String getFareBasisCode() {
		return fareBasisCode;
	}
	
	public void setFareBasisCode(String fareBasisCode) {
		this.fareBasisCode = fareBasisCode;
	}
	
	public String getFlightNumber() {
		return flightNumber;
	}
	
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	
	public String getDepartTime() {
		return departTime;
	}
	
	public void setDepartTime(String departTime) {
		this.departTime = departTime;
	}
	
	public String getDepartTimeSegment() {
		return departTimeSegment;
	}
	
	public void setDepartTimeSegment(String departTimeSegment) {
		this.departTimeSegment = departTimeSegment;
	}
	
	public String getArriveTime() {
		return arriveTime;
	}
	
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	
	public String getArriveTimeSegment() {
		return arriveTimeSegment;
	}
	
	public void setArriveTimeSegment(String arriveTimeSegment) {
		this.arriveTimeSegment = arriveTimeSegment;
	}
	
	public KualiDecimal getFare() {
		return fare;
	}
	
	public void setFare(KualiDecimal fare) {
		this.fare = fare;
	}
	
	public void setFare(String fare) {
		if(StringUtils.isNotBlank(fare)) {
			this.fare = new KualiDecimal(fare);
		} else {
			this.fare = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getFees() {
		return fees;
	}
	
	public void setFees(KualiDecimal fees) {
		this.fees = fees;
	}
	
	public void setFees(String fees) {
		if(StringUtils.isNotBlank(fees)) {
			this.fees = new KualiDecimal(fees);
		} else {
			this.fees = KualiDecimal.ZERO;
		}
	}
	
	public KualiDecimal getTaxes() {
		return taxes;
	}
	
	public void setTaxes(KualiDecimal taxes) {
		this.taxes = taxes;
	}
	
	public void setTaxes(String taxes) {
		if(StringUtils.isNotBlank(taxes)) {
			this.taxes = new KualiDecimal(taxes);
		} else {
			this.taxes = KualiDecimal.ZERO;
		}
	}
	
	public String getEndorsements() {
		return endorsements;
	}
	
	public void setEndorsements(String endorsements) {
		this.endorsements = endorsements;
	}
	
	public String getControlCode() {
		return controlCode;
	}
	
	public void setControlCode(String controlCode) {
		this.controlCode = controlCode;
	}
	
	protected LinkedHashMap<String, Integer> toStringMapper() {
		LinkedHashMap<String, Integer> m = new LinkedHashMap<String, Integer>();
		m.put("transactionSequenceRowNumber", getTransactionSequenceRowNumber());
		m.put("sequenceRowNumber", getSequenceRowNumber());
		return m;
	}
}
