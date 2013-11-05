package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "awardDTO", propOrder = {
    "awardId",
    "proposal",
    "awardStartDate",
    "awardEndDate",
    "awardTotalAmount",
    "awardDocumentNumber",
    "awardLastUpdateDate",
    "awardDirectCostAmount",
    "awardIndirectCostAmount",
    "awardCreateTimestamp",
    "proposalAwardTypeCode",
    "awardStatusCode",
    "sponsorCode",
    "title",
    "awardCommentText",
    "sponsor",
    "principalInvestigatorId",
    "unitNumber",
    "commentText"
})
public class AwardDTO implements Serializable {

	private static final long serialVersionUID = -7830094624716529740L;
	
	private String awardId;
    private ProposalDTO proposal;	
	private Date awardStartDate;
	private Date awardEndDate;
	private KualiDecimal awardTotalAmount;
    private String awardDocumentNumber;
    private Timestamp awardLastUpdateDate;
    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private Timestamp awardCreateTimestamp;
    private String proposalAwardTypeCode;
    private String awardStatusCode;
    private String sponsorCode;
    private String title;
    private String awardCommentText;
    private SponsorDTO sponsor;
    private String principalInvestigatorId;
    private String unitNumber;
    private String commentText;
    
	public String getAwardId() {
		return awardId;
	}
	public void setAwardId(String awardId) {
		this.awardId = awardId;
	}
	public ProposalDTO getProposal() {
		return proposal;
	}
	public void setProposal(ProposalDTO proposal) {
		this.proposal = proposal;
	}
	public Date getAwardStartDate() {
		return awardStartDate;
	}
	public void setAwardStartDate(Date awardStartDate) {
		this.awardStartDate = awardStartDate;
	}
	public Date getAwardEndDate() {
		return awardEndDate;
	}
	public void setAwardEndDate(Date awardEndDate) {
		this.awardEndDate = awardEndDate;
	}
	public KualiDecimal getAwardTotalAmount() {
		return awardTotalAmount;
	}
	public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
		this.awardTotalAmount = awardTotalAmount;
	}
	public String getAwardDocumentNumber() {
		return awardDocumentNumber;
	}
	public void setAwardDocumentNumber(String awardDocumentNumber) {
		this.awardDocumentNumber = awardDocumentNumber;
	}
	public Timestamp getAwardLastUpdateDate() {
		return awardLastUpdateDate;
	}
	public void setAwardLastUpdateDate(Timestamp awardLastUpdateDate) {
		this.awardLastUpdateDate = awardLastUpdateDate;
	}
	public KualiDecimal getAwardDirectCostAmount() {
		return awardDirectCostAmount;
	}
	public void setAwardDirectCostAmount(KualiDecimal awardDirectCostAmount) {
		this.awardDirectCostAmount = awardDirectCostAmount;
	}
	public KualiDecimal getAwardIndirectCostAmount() {
		return awardIndirectCostAmount;
	}
	public void setAwardIndirectCostAmount(KualiDecimal awardIndirectCostAmount) {
		this.awardIndirectCostAmount = awardIndirectCostAmount;
	}
	public Timestamp getAwardCreateTimestamp() {
		return awardCreateTimestamp;
	}
	public void setAwardCreateTimestamp(Timestamp awardCreateTimestamp) {
		this.awardCreateTimestamp = awardCreateTimestamp;
	}
	public String getProposalAwardTypeCode() {
		return proposalAwardTypeCode;
	}
	public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
		this.proposalAwardTypeCode = proposalAwardTypeCode;
	}
	public String getAwardStatusCode() {
		return awardStatusCode;
	}
	public void setAwardStatusCode(String awardStatusCode) {
		this.awardStatusCode = awardStatusCode;
	}
	public String getUnitNumber() {
		return unitNumber;
	}
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public String getSponsorCode() {
		return sponsorCode;
	}
	public void setSponsorCode(String sponsorCode) {
		this.sponsorCode = sponsorCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAwardCommentText() {
		return awardCommentText;
	}
	public void setAwardCommentText(String awardCommentText) {
		this.awardCommentText = awardCommentText;
	}
	public SponsorDTO getSponsor() {
		return sponsor;
	}
	public void setSponsor(SponsorDTO sponsor) {
		this.sponsor = sponsor;
	}
	public String getPrincipalInvestigatorId() {
		return principalInvestigatorId;
	}
	public void setPrincipalInvestigatorId(String principalInvestigatorId) {
		this.principalInvestigatorId = principalInvestigatorId;
	}
}
