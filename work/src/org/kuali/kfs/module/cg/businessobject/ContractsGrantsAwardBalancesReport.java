/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Defines a Contracts Grants Award Balances Report object.
 */
public class ContractsGrantsAwardBalancesReport extends TransientBusinessObjectBase {

    private String awardPrimaryProjectDirectorName;
    private String awardPrimaryFundManagerName;

    private KualiDecimal totalBilledToDate;
    private KualiDecimal totalPaymentsToDate;
    private KualiDecimal amountCurrentlyDue;

    private KualiDecimal awardTotalAmountForReport;
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Award.class);
    private Long proposalNumber;
    private String awardId;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    
    protected KualiDecimal awardTotalAmount;

    private String awardStatusCode;
    private String agencyNumber;
    private String awardProjectTitle;

    private Proposal proposal;

    private Agency agency;


    private String drawNumber;

    /**
     * @return
     */
    public KualiDecimal getTotalBilledToDate() {
        return totalBilledToDate;
    }

    /**
     * @param totalBilledToDate
     */
    public void setTotalBilledToDate(KualiDecimal totalBilledToDate) {
        this.totalBilledToDate = totalBilledToDate;
    }

    /**
     * @return
     */
    public KualiDecimal getTotalPaymentsToDate() {
        return totalPaymentsToDate;
    }

    /**
     * @param totalPaymentsToDate
     */
    public void setTotalPaymentsToDate(KualiDecimal totalPaymentsToDate) {
        this.totalPaymentsToDate = totalPaymentsToDate;
    }

    /**
     * @return
     */
    public KualiDecimal getAmountCurrentlyDue() {
        return amountCurrentlyDue;
    }

    /**
     * @param amountCurrentlyDue
     */
    public void setAmountCurrentlyDue(KualiDecimal amountCurrentlyDue) {
        this.amountCurrentlyDue = amountCurrentlyDue;
    }

    /**
     * @return
     */
    public String getAwardPrimaryProjectDirectorName() {
        return awardPrimaryProjectDirectorName;
    }

    /**
     * @param awardPrimaryProjectDirectorName
     */
    public void setAwardPrimaryProjectDirectorName(String awardPrimaryProjectDirectorName) {
        this.awardPrimaryProjectDirectorName = awardPrimaryProjectDirectorName;
    }

    /**
     * @return
     */
    public String getAwardPrimaryFundManagerName() {
        return awardPrimaryFundManagerName;
    }

    /**
     * @param awardPrimaryFundManagerName
     */
    public void setAwardPrimaryFundManagerName(String awardPrimaryFundManagerName) {
        this.awardPrimaryFundManagerName = awardPrimaryFundManagerName;
    }

    /**
     * Gets the awardTotalAmountForReport attribute.
     * 
     * @return Returns the awardTotalAmountForReport.
     */
    public KualiDecimal getAwardTotalAmountForReport() {
        return awardTotalAmountForReport;
    }

    /**
     * Sets the awardTotalAmountForReport attribute value.
     * 
     * @param awardTotalAmountForReport The awardTotalAmountForReport to set.
     */
    public void setAwardTotalAmountForReport(KualiDecimal awardTotalAmountForReport) {
        this.awardTotalAmountForReport = awardTotalAmountForReport;
    }

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }

    public KualiDecimal getAwardTotalAmount() {
        return awardTotalAmount;
    }

    public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }

    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }

    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    /**
     * This method maps the proposal number into a hash map with "proposalNumber" as the identifier.
     * 
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        m.put("awardId", this.awardId);

        return m;
    }
}
