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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ar.AccountsReceivablePredeterminedBillingSchedule;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;



/**
 * Created a Predetermined Billing Schedule maintenance Document parameter
 */
public class PredeterminedBillingSchedule extends PersistableBusinessObjectBase implements AccountsReceivablePredeterminedBillingSchedule {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PredeterminedBillingSchedule.class);

    private static final String PREDETERMINED_BILLING_SCHEDULE_INQUIRY_TITLE_PROPERTY = "message.inquiry.predetermined.billing.schedule.title";
    private Long proposalNumber;

    private KualiDecimal totalAmountScheduled;
    private KualiDecimal totalAmountRemaining;
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
     * Sets the totalAmountScheduled attribute value.
     *
     * @param totalAmountScheduled The totalAmountScheduled to set.
     */
    @Deprecated
    public void setTotalAmountScheduled(KualiDecimal totalAmountScheduled) {
        this.totalAmountScheduled = totalAmountScheduled;
    }

    /**
     * Gets the totalAmountRemaining attribute.
     *
     * @return Returns the totalAmountRemaining.
     */
    @Override
    public KualiDecimal getTotalAmountRemaining() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (award != null) {
            total = award.getAwardTotalAmount().subtract(getTotalAmountScheduled());
        }
        return total;
    }

    /**
     * Sets the totalAmountRemaining attribute value.
     *
     * @param totalAmountRemaining The totalAmountRemaining to set.
     */
    @Deprecated
    public void setTotalAmountRemaining(KualiDecimal totalAmountRemaining) {
        this.totalAmountRemaining = totalAmountRemaining;
    }

    /**
     * OJB calls this method as the first operation before this BO is inserted into the database. The field is read-only in the data
     * dictionary and so the value does not persist in the DB. So this method makes sure that the values are stored in the DB.
     *
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.prePersist();
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */

    @Override protected void prePersist() {
        super.prePersist();
        totalAmountScheduled = getTotalAmountScheduled();
        totalAmountRemaining = getTotalAmountRemaining();
    }

    /**
     * OJB calls this method as the first operation before this BO is updated to the database. The field is read-only in the data
     * dictionary and so the value does not persist in the DB. So this method makes sure that the values are stored in the DB.
     *
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.preUpdate();
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override protected void preUpdate() {
        super.preUpdate();
        totalAmountScheduled = getTotalAmountScheduled();
        totalAmountRemaining = getTotalAmountRemaining();
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
        if (award == null && proposalNumber != null) {
            Map<String, Object> map = new HashMap<String, Object> ();
            map.put("proposalNumber", this.proposalNumber);
            award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
        }
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
        m.put("totalAmountScheduled", this.totalAmountScheduled.toString());
        m.put("totalAmountRemaining", this.totalAmountRemaining.toString());
        return m;
    }


}
