package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ProcurementCardLevel3Transport extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer financialDocumentTransactionLineNumber;
	private String passengerName;
	private Date departDate;
	private String departureCity;
	private String agencyCode;
	private String agencyName;
	private long ticketNumber;
	private String customerCode;
	private Date issueDate;
	private String issuingCarrier;
	private KualiDecimal totalFare;
	private KualiDecimal totalFees;
	private KualiDecimal totalTaxes;
	
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
	
	public String getPassengerName() {
		return passengerName;
	}
	
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	
	public Date getDepartDate() {
		return departDate;
	}
	
	public void setDepartDate(Date departDate) {
		this.departDate = departDate;
	}
	
	public String getDepartureCity() {
		return departureCity;
	}
	
	public void setDepartureCity(String departureCity) {
		this.departureCity = departureCity;
	}
	
	public String getAgencyCode() {
		return agencyCode;
	}
	
	public void setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
	}
	
	public String getAgencyName() {
		return agencyName;
	}
	
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	
	public long getTicketNumber() {
		return ticketNumber;
	}
	
	public void setTicketNumber(long ticketNumber) {
		this.ticketNumber = ticketNumber;
	}
	
	public String getCustomerCode() {
		return customerCode;
	}
	
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	public String getIssuingCarrier() {
		return issuingCarrier;
	}
	
	public void setIssuingCarrier(String issuingCarrier) {
		this.issuingCarrier = issuingCarrier;
	}
	
	public KualiDecimal getTotalFare() {
		return totalFare;
	}
	
	public void setTotalFare(KualiDecimal totalFare) {
		this.totalFare = totalFare;
	}
	
	public KualiDecimal getTotalFees() {
		return totalFees;
	}
	
	public void setTotalFees(KualiDecimal totalFees) {
		this.totalFees = totalFees;
	}
	
	public KualiDecimal getTotalTaxes() {
		return totalTaxes;
	}
	
	public void setTotalTaxes(KualiDecimal totalTaxes) {
		this.totalTaxes = totalTaxes;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("documentNumber", documentNumber);
		m.put("financialDocumentTransactionLineNumber", getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
