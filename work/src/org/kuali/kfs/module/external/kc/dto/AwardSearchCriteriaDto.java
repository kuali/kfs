package org.kuali.kfs.module.external.kc.dto;

public class AwardSearchCriteriaDto {

	private Long awardId;
	private String awardNumber;
	private String accountNumber;
	private String chartOfAccounts;
	private String principalInvestigatorId;
	private String sponsorCode;
	private String startDate;
	private String endDate;
	private String billingFrequency;
	private String awardTotal;

	public Long getAwardId() {
		return awardId;
	}
	public void setAwardId(Long awardId) {
		this.awardId = awardId;
	}
	public String getAwardNumber() {
		return awardNumber;
	}
	public void setAwardNumber(String awardNumber) {
		this.awardNumber = awardNumber;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getChartOfAccounts() {
		return chartOfAccounts;
	}
	public void setChartOfAccounts(String chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}
    public String getPrincipalInvestigatorId() {
        return principalInvestigatorId;
    }
    public void setPrincipalInvestigatorId(String principalInvestigatorId) {
        this.principalInvestigatorId = principalInvestigatorId;
    }
	public String getSponsorCode() {
		return sponsorCode;
	}
	public void setSponsorCode(String sponsorCode) {
		this.sponsorCode = sponsorCode;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getBillingFrequency() {
		return billingFrequency;
	}
	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}
	public String getAwardTotal() {
		return awardTotal;
	}
	public void setAwardTotal(String awardTotal) {
		this.awardTotal = awardTotal;
	}
}
