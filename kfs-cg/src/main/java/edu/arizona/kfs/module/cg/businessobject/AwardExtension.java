package edu.arizona.kfs.module.cg.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

public class AwardExtension extends PersistableBusinessObjectExtensionBase {
	private Long proposalNumber;
	private String federalPassThroughPrimeAwardNumber;

	public Long getProposalNumber() {
		return proposalNumber;
	}

	public void setProposalNumber(Long proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public String getFederalPassThroughPrimeAwardNumber() {
		return federalPassThroughPrimeAwardNumber;
	}

	public void setFederalPassThroughPrimeAwardNumber(String federalPassThroughPrimeAwardNumber) {
		this.federalPassThroughPrimeAwardNumber = federalPassThroughPrimeAwardNumber;
	}

}
