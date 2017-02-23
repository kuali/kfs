package edu.arizona.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3TransportLeg extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private Integer sequenceNumber;
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
	
	public KualiDecimal getFees() {
		return fees;
	}
	
	public void setFees(KualiDecimal fees) {
		this.fees = fees;
	}
	
	public KualiDecimal getTaxes() {
		return taxes;
	}
	
	public void setTaxes(KualiDecimal taxes) {
		this.taxes = taxes;
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
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
