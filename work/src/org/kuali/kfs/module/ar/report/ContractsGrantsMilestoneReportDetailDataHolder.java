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

package org.kuali.kfs.module.ar.report;

import java.math.BigDecimal;
import java.sql.Date;

public class ContractsGrantsMilestoneReportDetailDataHolder {

    private Long proposalNumber;
    private String accountNumber;
    private Long milestoneNumber;
    private BigDecimal milestoneAmount;
    private Date milestoneExpectedCompletionDate;
    private String isItBilled;
    private String active;
    private String sortedFieldValue;
    private BigDecimal subTotal;
    public boolean displaySubtotalInd;

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the milestoneNumber attribute.
     *
     * @return Returns the milestoneNumber.
     */
    public Long getMilestoneNumber() {
        return milestoneNumber;
    }

    /**
     * Sets the milestoneNumber attribute value.
     *
     * @param milestoneNumber The milestoneNumber to set.
     */
    public void setMilestoneNumber(Long milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    /**
     * Gets the milestoneAmount attribute.
     *
     * @return Returns the milestoneAmount.
     */
    public BigDecimal getMilestoneAmount() {
        return milestoneAmount;
    }

    /**
     * Sets the milestoneAmount attribute value.
     *
     * @param milestoneAmount The milestoneAmount to set.
     */
    public void setMilestoneAmount(BigDecimal milestoneAmount) {
        this.milestoneAmount = milestoneAmount;
    }

    /**
     * Gets the milestoneExpectedCompletionDate attribute.
     *
     * @return Returns the milestoneExpectedCompletionDate.
     */
    public Date getMilestoneExpectedCompletionDate() {
        return milestoneExpectedCompletionDate;
    }

    /**
     * Sets the milestoneExpectedCompletionDate attribute value.
     *
     * @param milestoneExpectedCompletionDate The milestoneExpectedCompletionDate to set.
     */
    public void setMilestoneExpectedCompletionDate(Date milestoneExpectedCompletionDate) {
        this.milestoneExpectedCompletionDate = milestoneExpectedCompletionDate;
    }

    /**
     * Gets the isItBilled attribute.
     *
     * @return Returns the isItBilled.
     */
    public String getIsItBilled() {
        return isItBilled;
    }

    /**
     * Sets the isItBilled attribute value.
     *
     * @param isItBilled The isItBilled to set.
     */
    public void setIsItBilled(String isItBilled) {
        this.isItBilled = isItBilled;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    /**
     * Gets the sortedFieldValue attribute.
     *
     * @return Returns the sortedFieldValue.
     */
    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    /**
     * Sets the sortedFieldValue attribute value.
     *
     * @param sortedFieldValue The sortedFieldValue to set.
     */
    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    /**
     * Gets the subTotal attribute.
     *
     * @return Returns the subTotal.
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * Sets the subTotal attribute value.
     *
     * @param subTotal The subTotal to set.
     */
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * Gets the displaySubtotalInd attribute.
     *
     * @return Returns the displaySubtotalInd.
     */
    public boolean isDisplaySubtotalInd() {
        return displaySubtotalInd;
    }

    /**
     * Sets the displaySubtotalInd attribute value.
     *
     * @param displaySubtotalInd The displaySubtotalInd to set.
     */
    public void setDisplaySubtotalInd(boolean displaySubtotalInd) {
        this.displaySubtotalInd = displaySubtotalInd;
    }


}
