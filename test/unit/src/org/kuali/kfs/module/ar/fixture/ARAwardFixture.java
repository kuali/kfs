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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.module.cg.businessobject.BillingFrequency;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Fixture class for CG Award
 */
public enum ARAwardFixture {

    CG_AWARD1(new Long(11), "2011-10-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, null, true, null),
    CG_AWARD2(new Long(11), "2011-07-01", "1969-06-30", Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, null, true, null),
    CG_AWARD3(new Long(1234), "2011-01-01", "2011-09-22", Date.valueOf("2011-10-01"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, null, true, null),
    CG_AWARD4(new Long(11), "2011-07-01", new Date(System.currentTimeMillis() + 604800000).toString(), Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD5(new Long(11), "2011-07-01", "1969-06-30", Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD6(new Long(11), "2011-07-01", new Date(System.currentTimeMillis() + 604800000).toString(), Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), new KualiDecimal(1000000.00), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD7(new Long(11), "2011-07-01", new Date(System.currentTimeMillis() + 604800000).toString(), Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), null, false, null, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD8(new Long(11), "2011-07-01", "1969-06-30", Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, ArConstants.INV_AWARD, true, null),
    CG_AWARD9(new Long(11), "2011-07-01", new Date(System.currentTimeMillis() + 604800000).toString(), Date.valueOf("1969-01-01"), new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency()),
    CG_AWARD10(new Long(11), "2011-07-01", new Date(System.currentTimeMillis() + 604800000).toString(), Date.valueOf("1969-01-01"), new KualiDecimal(100000.00), KualiDecimal.ZERO, KualiDecimal.ZERO, false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency()),
    CG_AWARD_INV_AWARD(new Long(11), "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, null),
    CG_AWARD_INV_ACCOUNT(new Long(11), "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INCOME_ACCOUNT, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD_INV_CCA(new Long(11), "2011-10-01", "2011-09-22", Date.valueOf("2011-10-01"), null, null, null, false, ArConstants.INV_CONTRACT_CONTROL_ACCOUNT, true, null),
    CG_AWARD_MONTHLY_BILLED_DATE_NULL(new Long(11), "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD_MILESTONE_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MS.createBillingFrequency()),
    CG_AWARD_PREDETERMINED_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency()),
    CG_AWARD_QUAR_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_QUAR.createBillingFrequency()),
    CG_AWARD_SEMI_ANN_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_SEMI_ANN.createBillingFrequency()),
    CG_AWARD_ANNUAL_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_ANNUALLY.createBillingFrequency()),
    CG_AWARD_LOCB_BILLED_DATE_NULL(new Long(111), "2011-01-01", "2011-09-22", null, new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency()),
    CG_AWARD_MONTHLY_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(1000), new KualiDecimal(1000), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency()),
    CG_AWARD_MILESTONE_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MS.createBillingFrequency()),
    CG_AWARD_PREDETERMINED_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", Date.valueOf("2011-09-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency()),
    CG_AWARD_QUAR_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", Date.valueOf("2011-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_QUAR.createBillingFrequency()),
    CG_AWARD_SEMI_ANN_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", Date.valueOf("2011-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_SEMI_ANN.createBillingFrequency()),
    CG_AWARD_ANNUAL_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", Date.valueOf("2010-06-30"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_ANNUALLY.createBillingFrequency()),
    CG_AWARD_LOCB_BILLED_DATE_VALID(new Long(111), "2011-01-01", "2011-09-22", Date.valueOf("2010-12-13"), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency()),
    CG_AWARD_MONTHLY_INVALID_DATES(new Long(11), "2012-01-01", "2011-09-22", new Date(System.currentTimeMillis() + 604800000), new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, ArConstants.INV_AWARD, true, BillingFrequencyFixture.BILL_FREQ_MON.createBillingFrequency());

    private Long proposalNumber;
    private String awardBeginningDate;
    private String awardEndingDate;
    private Date lastBilledDate;

    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal minInvoiceAmount;
    private KualiDecimal awardTotalAmount;
    private boolean excludedFromInvoicing;
    private String invoicingOptionCode;
    private boolean active;
    private BillingFrequency billingFrequency;

    private ARAwardFixture(Long proposalNumber, String awardBeginningDate, String awardEndingDate, Date lastBilledDate, KualiDecimal awardDirectCostAmount, KualiDecimal awardIndirectCostAmount, KualiDecimal minInvoiceAmount, boolean excludedFromInvoicing, String invoicingOptionCode, boolean active, BillingFrequency billingFrequency) {

        this.proposalNumber = proposalNumber;
        this.awardBeginningDate = awardBeginningDate;
        this.awardEndingDate = awardEndingDate;
        this.lastBilledDate = lastBilledDate;
        this.awardDirectCostAmount = awardDirectCostAmount;
        this.awardIndirectCostAmount = awardIndirectCostAmount;
        this.minInvoiceAmount = minInvoiceAmount;
        this.excludedFromInvoicing = excludedFromInvoicing;
        this.invoicingOptionCode = invoicingOptionCode;
        this.active = active;
        this.billingFrequency = billingFrequency;

    }

    public ContractsAndGrantsBillingAward createAward() {
        Award award = new Award();
        award.setProposalNumber(this.proposalNumber);
        award.setAwardBeginningDate(Date.valueOf(this.awardBeginningDate));
        award.setAwardEndingDate(Date.valueOf(this.awardEndingDate));
        award.setAwardDirectCostAmount(this.awardDirectCostAmount);
        award.setAwardIndirectCostAmount(this.awardIndirectCostAmount);
        award.setMinInvoiceAmount(this.minInvoiceAmount);
        award.setExcludedFromInvoicing(this.excludedFromInvoicing);
        award.setInvoicingOptionCode(this.invoicingOptionCode);
        award.setActive(this.active);
        award.setBillingFrequency(billingFrequency);
        if (ObjectUtils.isNotNull(billingFrequency)) {
            award.setBillingFrequencyCode(billingFrequency.getFrequency());
        }

        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_2.createAwardAccount();

        if (ObjectUtils.isNotNull(this.lastBilledDate)) {
            awardAccount_1.setCurrentLastBilledDate(this.lastBilledDate);
            awardAccount_2.setCurrentLastBilledDate(this.lastBilledDate);
        }

        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        awardAccounts.add(awardAccount_2);
        award.setAwardAccounts(awardAccounts);

        BillingFrequencyFixture.BILL_FREQ_LOCB.createBillingFrequency();

        // Set auto approve to true
        award.setAutoApproveIndicator(true);

        return award;
    }

    public Award setAgencyFromFixture(Award award) {
        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();
        award.setAgency((Agency)agency);
        award.setAgencyNumber(agency.getAgencyNumber());
        return award;
    }

    public Award setDunningCampaignFromFixture(Award award) {
        DunningCampaign dunningCampaign = DunningCampaignFixture.AR_DUNC1.createDunningCampaign();
        award.setDunningCampaign(dunningCampaign.getCampaignID());
        return award;
    }

    public Award setAwardOrganizationFromFixture(Award award) {
        AwardOrganization awardOrganization = ARAwardOrganizationFixture.AWD_ORG1.createAwardOrganization();
        award.getAwardOrganizations().add(awardOrganization);
        return award;
    }

}
