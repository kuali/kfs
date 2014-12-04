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
package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivablePredeterminedBillingSchedule;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;



/**
 * Created a Predetermined Billing Schedule maintenance Document parameter
 */
public class PredeterminedBillingSchedule extends PersistableBusinessObjectBase implements AccountsReceivablePredeterminedBillingSchedule {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PredeterminedBillingSchedule.class);

    private static final String PREDETERMINED_BILLING_SCHEDULE_INQUIRY_TITLE_PROPERTY = "message.inquiry.predetermined.billing.schedule.title";
    private Long proposalNumber;

    private String predeterminedBillingScheduleInquiryTitle;

    private List<Bill> bills;
    private ContractsAndGrantsBillingAward award;

    public PredeterminedBillingSchedule() {
        // Must use ArrayList because its get() method automatically grows the array for Struts.
        bills = new ArrayList<Bill>();
    }


    /**
     * Constructs an Milestone Schedule with parameter Award
     *
     * @param proposal
     */
    public PredeterminedBillingSchedule(ContractsAndGrantsBillingAward award) {
        this();
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
     * Gets the totalAmountScheduled attribute.
     *
     * @return Returns the totalAmountScheduled.
     */
    @Override
    public KualiDecimal getTotalAmountScheduled() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Bill bill: bills) {
            if (ObjectUtils.isNotNull(bill.getEstimatedAmount()) && bill.isActive()) {
                total = total.add(bill.getEstimatedAmount());
            }
        }
        return total;
    }

    /**
     * Gets the totalAmountRemaining attribute.
     *
     * @return Returns the totalAmountRemaining.
     */
    @Override
    public KualiDecimal getTotalAmountRemaining() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(award) && ObjectUtils.isNotNull(award.getAwardTotalAmount())) {
            total = award.getAwardTotalAmount().subtract(getTotalAmountScheduled());
        }
        return total;
    }

    /**
     * Gets the predeterminedBillingScheduleInquiryTitle attribute.
     *
     * @return Returns the predeterminedBillingScheduleInquiryTitle.
     */
    @Override
    public String getPredeterminedBillingScheduleInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PREDETERMINED_BILLING_SCHEDULE_INQUIRY_TITLE_PROPERTY);

    }


    /**
     * Sets the predeterminedBillingScheduleInquiryTitle attribute value.
     *
     * @param predeterminedBillingScheduleInquiryTitle The predeterminedBillingScheduleInquiryTitle to set.
     */
    public void setPredeterminedBillingScheduleInquiryTitle(String predeterminedBillingScheduleInquiryTitle) {
        this.predeterminedBillingScheduleInquiryTitle = predeterminedBillingScheduleInquiryTitle;
    }


    /**
     * Gets the bills attribute.
     *
     * @return Returns the bills.
     */
    public List<Bill> getBills() {
        return bills;
    }


    /**
     * Sets the bills attribute value.
     *
     * @param bills The bills to set.
     */
    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }


    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    @Override
    public ContractsAndGrantsBillingAward getAward() {
        award = SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).updateAwardIfNecessary(proposalNumber, award);
        return award;
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
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        m.put("totalAmountScheduled", getTotalAmountScheduled().toString());
        m.put("totalAmountRemaining", getTotalAmountRemaining().toString());
        return m;
    }


}
