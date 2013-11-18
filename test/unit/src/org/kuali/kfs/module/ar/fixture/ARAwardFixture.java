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
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.BillingFrequency;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Fixture class for CG Award
 */
public enum ARAwardFixture {

    CG_AWARD1(new Long(11), "111", "2011-10-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, null, true, null),
    CG_AWARD2(new Long(11), null, "1968-07-01", "1969-06-30", Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, null, true, null),
    CG_AWARD3(new Long(1234), "1234", "2011-01-01", "2011-09-22", Date.valueOf("2011-10-01"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, null, true, null),
    CG_AWARD_INV_AWARD(new Long(11), "111", "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, null),
    CG_AWARD_INV_ACCOUNT(new Long(11), "111", "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INCOME_ACCOUNT, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD_INV_CCA(new Long(111), "111", "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), null, null, null, false, ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT, true, null),
    CG_AWARD_MONTHLY_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD_MILESTONE_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MS.createBillingFrequency()),
    CG_AWARD_PREDETERMINED_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency()),
    CG_AWARD_QUAR_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_QUAR.createBillingFrequency()),
    CG_AWARD_SEMI_ANN_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_SEMI_ANN.createBillingFrequency()),
    CG_AWARD_ANNUAL_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_ANNUALLY.createBillingFrequency()),
    CG_AWARD_LOCB_BILLED_DATE_NULL(new Long(111), "111", "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency()),
    CG_AWARD_MONTHLY_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD_MILESTONE_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MS.createBillingFrequency()),
    CG_AWARD_PREDETERMINED_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency()),
    CG_AWARD_QUAR_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_QUAR.createBillingFrequency()),
    CG_AWARD_SEMI_ANN_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2011-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_SEMI_ANN.createBillingFrequency()),
    CG_AWARD_ANNUAL_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2010-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_ANNUALLY.createBillingFrequency()),
    CG_AWARD_LOCB_BILLED_DATE_VALID(new Long(111), "111", "2011-01-01", "2011-09-22", Date.valueOf("2010-12-13"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArPropertyConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency());

    private Long proposalNumber;
    private String awardId;
    private String awardBeginningDate;
    private String awardEndingDate;
    private Date lastBilledDate;

    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal minInvoiceAmount;
    private KualiDecimal awardTotalAmount;
    private boolean suspendInvoicingIndicator;
    private String invoicingOptions;
    private boolean active;
    private BillingFrequency billingFrequency;

    private ARAwardFixture(Long proposalNumber, String awardId, String awardBeginningDate, String awardEndingDate, Date lastBilledDate, KualiDecimal awardDirectCostAmount, KualiDecimal awardIndirectCostAmount, KualiDecimal minInvoiceAmount, boolean suspendInvoicingIndicator, String invoicingOptions, boolean active, BillingFrequency billingFrequency) {

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
        this.billingFrequency = billingFrequency;

    }

    public ContractsAndGrantsBillingAward createAward() {
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
        award.setBillingFrequency(billingFrequency);
        if (ObjectUtils.isNotNull(billingFrequency)) {
            award.setPreferredBillingFrequency(billingFrequency.getFrequency());
        }

        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_2.createAwardAccount();

        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        awardAccounts.add(awardAccount_2);
        award.setAwardAccounts(awardAccounts);

        BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency();

        return award;
    }

    public Award setAgencyFromFixture(Award award) {
        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();
        award.setAgency((Agency)agency);
        return award;
    }

    public Award setDunningCampaignFromFixture(Award award) {
        DunningCampaign dunningCampaign = DunningCampaignFixture.AR_DUNC1.createDunningCampaign();
        award.setDunningCampaign(dunningCampaign.getCampaignID());
        return award;
    }
}
