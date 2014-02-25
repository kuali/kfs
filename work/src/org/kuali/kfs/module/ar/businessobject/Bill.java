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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.integration.ar.AccountsReceivableBill;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * Bills to be used for Billing Schedule under Contracts and Grants
 */
public class Bill extends PersistableBusinessObjectBase implements AccountsReceivableBill, MutableInactivatable {

    private Long proposalNumber;
    private Long billIdentifier;
    private Long billNumber;
    private String billDescription;
    private Date billDate;

    private KualiDecimal estimatedAmount = KualiDecimal.ZERO;
    private boolean billedIndicator;
    private boolean active;

    private ContractsAndGrantsBillingAward award;

    /**
     * Constructs a Bills.java.
     */
    public Bill() {
        this.setBilledIndicator(false);
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    @Override
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
    @Override
    public Long getBillNumber() {
        return billNumber;
    }

    @Override
    public boolean isBilledIndicator() {
        return billedIndicator;
    }

    public void setBilledIndicator(boolean billedIndicator) {
        this.billedIndicator = billedIndicator;
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsBillingAward getAward() {
        Map<String,Object> key = new HashMap<String, Object>(1);
        key.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.getProposalNumber());
        return award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, key);

    }

    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsBillingAward award) {
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
        m.put("isBilledIndicator", this.billedIndicator);
        return m;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

}
