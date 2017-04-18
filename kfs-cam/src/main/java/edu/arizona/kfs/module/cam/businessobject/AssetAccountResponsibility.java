package edu.arizona.kfs.module.cam.businessobject;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class AssetAccountResponsibility extends PersistableBusinessObjectBase {
	private static final long serialVersionUID = 1L;
    private Long capitalAssetNumber;
    private Long accountResponsibilityId;
    private String sequenceId;
    private String accountNumber;
    private String agencyNumber;
    private String grantNumber;
    private Date effectiveDate;
    
    private ContractsAndGrantsAgency agency;
    
	public Long getCapitalAssetNumber() {
		return capitalAssetNumber;
	}
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}
	public Long getAccountResponsibilityId() {
		return accountResponsibilityId;
	}
	public void setAccountResponsibilityId(Long accountResponsibilityId) {
		this.accountResponsibilityId = accountResponsibilityId;
	}
	public String getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAgencyNumber() {
		return agencyNumber;
	}
	public void setAgencyNumber(String agencyNumber) {
		this.agencyNumber = agencyNumber;
	}
	public String getGrantNumber() {
		return grantNumber;
	}
	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public ContractsAndGrantsAgency getAgency() {
		return agency;
	}
	public void setAgency(ContractsAndGrantsAgency agency) {
		this.agency = agency;
	}

}
