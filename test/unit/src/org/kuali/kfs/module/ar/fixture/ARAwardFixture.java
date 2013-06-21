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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.businessobject.Award;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for CG Award
 */
public enum ARAwardFixture {

    CG_AWARD1(new Long(111), "111", "2011-10-01", "2011-09-22", null, null, null, null, false, null, true, null),


    CG_AWARD2(new Long(11), null, "1968-07-01", "1969-06-30", Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, null, true, null), CG_AWARD3(new Long(1234), "1234", "2011-01-01", "2011-09-22", Date.valueOf("2011-10-01"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, null, true, null),

    CG_AWARD_INV_AWARD(new Long(111), "111", "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), null, null, null, false, ArPropertyConstants.INV_AWARD, true, null), CG_AWARD_INV_ACCOUNT(new Long(111), "111", "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), null, null, null, false, ArPropertyConstants.INCOME_ACCOUNT, true, ArPropertyConstants.MONTHLY_BILLING_SCHEDULE_CODE), CG_AWARD_INV_CCA(new Long(111), "111", "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), null, null, null, false, ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT, true, null),

    CG_AWARD_MONTHLY_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.MONTHLY_BILLING_SCHEDULE_CODE), CG_AWARD_MILESTONE_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE), CG_AWARD_PREDETERMINED_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE), CG_AWARD_QUAR_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true,
            ArPropertyConstants.QUATERLY_BILLING_SCHEDULE_CODE), CG_AWARD_SEMI_ANN_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE), CG_AWARD_ANNUAL_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.ANNUALLY_BILLING_SCHEDULE_CODE), CG_AWARD_LOCB_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE),

    CG_AWARD_MONTHLY_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.MONTHLY_BILLING_SCHEDULE_CODE), CG_AWARD_MILESTONE_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE), CG_AWARD_PREDETERMINED_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE), CG_AWARD_QUAR_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-06-30"), new KualiDecimal(0), new KualiDecimal(0),
            new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.QUATERLY_BILLING_SCHEDULE_CODE), CG_AWARD_SEMI_ANN_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE), CG_AWARD_ANNUAL_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2010-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.ANNUALLY_BILLING_SCHEDULE_CODE), CG_AWARD_LOCB_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2010-12-13"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE);

    private Long proposalNumber;
    private String awardId;
    private String awardBeginningDate;
    private String awardEndingDate;
    private Date lastBilledDate;


    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal minInvoiceAmount;
    private boolean suspendInvoicingIndicator;
    private String invoicingOptions;
    private boolean active;
    private String preferredBillingFrequency;


    private ARAwardFixture(Long proposalNumber, String awardId, String awardBeginningDate, String awardEndingDate, Date lastBilledDate, KualiDecimal awardDirectCostAmount, KualiDecimal awardIndirectCostAmount, KualiDecimal minInvoiceAmount, boolean suspendInvoicingIndicator, String invoicingOptions, boolean active, String preferredBillingFrequency) {

        this.proposalNumber = proposalNumber;
        this.awardId = awardId;
        this.awardBeginningDate = awardBeginningDate;
        this.awardEndingDate = awardEndingDate;
        this.lastBilledDate = lastBilledDate;
        this.awardDirectCostAmount = awardDirectCostAmount;
        this.awardIndirectCostAmount = awardIndirectCostAmount;
        this.minInvoiceAmount = minInvoiceAmount;
        this.suspendInvoicingIndicator = suspendInvoicingIndicator;
        this.invoicingOptions = invoicingOptions;
        this.active = active;
        this.preferredBillingFrequency = preferredBillingFrequency;

    }

    public Award createAward() {
        Award award = new Award();
        award.setProposalNumber(this.proposalNumber);
        award.setAwardId(this.awardId);
        award.setAwardBeginningDate(Date.valueOf(this.awardBeginningDate));
        award.setAwardEndingDate(Date.valueOf(this.awardEndingDate));
        award.setLastBilledDate(this.lastBilledDate);
        award.setAwardDirectCostAmount(this.awardDirectCostAmount);
        award.setAwardIndirectCostAmount(this.awardIndirectCostAmount);
        award.setMinInvoiceAmount(this.minInvoiceAmount);
        award.setSuspendInvoicingIndicator(this.suspendInvoicingIndicator);
        award.setInvoicingOptions(this.invoicingOptions);
        award.setActive(this.active);
        award.setPreferredBillingFrequency(this.preferredBillingFrequency);

        return award;
    }

    public Award setAgencyFromFixture(Award award) {
        ContractsAndGrantsCGBAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();
        award.setAgency(agency);
        return award;
    }

    public Award setDunningCampaignFromFixture(Award award) {
        DunningCampaign dunningCampaign = DunningCampaignFixture.AR_DUNC1.createDunningCampaign();
        award.setDunningCampaign(dunningCampaign.getCampaignID());
        return award;
    }
}
