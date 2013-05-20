/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cg.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBill;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Bills to be used for Billing Schedule under Contracts and Grants
 */
public class Bill extends PersistableBusinessObjectBase implements ContractsAndGrantsBill {


    private Long proposalNumber;
    private Long billNumber;
    private String billDescription;
    private Long billIdentifier;
    private Date billDate;

    private KualiDecimal estimatedAmount = KualiDecimal.ZERO;
    private String isItBilled;


    private Award award;

    /**
     * Constructs a Bills.java.
     */
    public Bill() {

    }

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
     * Gets the billNumber attribute.
     * 
     * @return Returns the billNumber.
     */
    public Long getBillNumber() {
        return billNumber;
    }

    /**
     * Sets the billNumber attribute value.
     * 
     * @param billNumber The billNumber to set.
     */
    public void setBillNumber(Long billNumber) {
        this.billNumber = billNumber;
    }

    /**
     * Gets the billDescription attribute.
     * 
     * @return Returns the billDescription.
     */
    public String getBillDescription() {
        return billDescription;
    }

    /**
     * Sets the billDescription attribute value.
     * 
     * @param billDescription The billDescription to set.
     */
    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    /**
     * Gets the billIdentifier attribute.
     * 
     * @return Returns the billIdentifier.
     */
    public Long getBillIdentifier() {
        return billIdentifier;
    }

    /**
     * Sets the billIdentifier attribute value.
     * 
     * @param billIdentifier The billIdentifier to set.
     */
    public void setBillIdentifier(Long billIdentifier) {
        this.billIdentifier = billIdentifier;
    }

    /**
     * Gets the billDate attribute.
     * 
     * @return Returns the billDate.
     */
    public Date getBillDate() {
        return billDate;
    }

    /**
     * Sets the billDate attribute value.
     * 
     * @param billDate The billDate to set.
     */
    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    /**
     * Gets the estimatedAmount attribute.
     * 
     * @return Returns the estimatedAmount.
     */
    public KualiDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    /**
     * Sets the estimatedAmount attribute value.
     * 
     * @param estimatedAmount The estimatedAmount to set.
     */
    public void setEstimatedAmount(KualiDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
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


    /**
     * Gets the award attribute.
     * 
     * @return Returns the award.
     */
    public Award getAward() {
        return award;
    }

    /**
     * Sets the award attribute value.
     * 
     * @param award The award to set.
     */
    public void setAward(Award award) {
        this.award = award;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber);
        m.put("billNumber", this.billNumber);
        m.put("billDescription", this.billDescription);
        m.put("billIdentifier", this.billIdentifier);
        m.put("billDate", this.billDate.toString());
        m.put("estimatedAmount", this.estimatedAmount.toString());
        m.put("isItBilled", this.isItBilled);
        return m;
    }

}
