/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Defines a Contracts & Grants Award Balances Report object.
 */
public class ContractsGrantsAwardBalancesReport extends TransientBusinessObjectBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAwardBalancesReport.class);

    private String awardPrimaryProjectDirectorName;
    private String awardPrimaryFundManagerName;

    private KualiDecimal totalBilledToDate;
    private KualiDecimal totalPaymentsToDate;
    private KualiDecimal amountCurrentlyDue;

    private KualiDecimal awardTotalAmountForReport;
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";
    private Long proposalNumber;
    private Date awardBeginningDate;
    private Date awardEndingDate;

    protected KualiDecimal awardTotalAmount;

    private String awardStatusCode;
    private String agencyNumber;
    private String awardProjectTitle;

    private Proposal proposal;

    private Agency agency;


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

        return m;
    }
}
