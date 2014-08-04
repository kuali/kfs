/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.service;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFundManagerFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * Shared methods for Contracts & Grants Invoice creation services testing
 */
public abstract class ContractsGrantsInvoiceCreateTestBase extends KualiTestBase {
    protected BusinessObjectService businessObjectService;
    protected ConfigurationService configurationService;
    protected DocumentService documentService;
    protected KualiModuleService kualiModuleService;
    protected AccountingPeriodService accountingPeriodService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;
    protected String errorOutputFile;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        configurationService = SpringContext.getBean(ConfigurationService.class);
        kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        accountingPeriodService = SpringContext.getBean(AccountingPeriodService.class);
        verifyBillingFrequencyService = SpringContext.getBean(VerifyBillingFrequencyService.class);

        ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
        String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);
        errorOutputFile = destinationFolderPath + "JUNIT TEST.log";
    }

    @Override
    public void tearDown() throws Exception {
        File errors = new File(errorOutputFile);

        if (errors.exists()) {
            errors.delete();
        }

        super.tearDown();
    }

    protected List<ContractsAndGrantsBillingAward> setupAwards() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAgencyFromFixture((Award) award);
        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1.createAwardAccount();
        awardAccount_1.refreshReferenceObject("account");
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award)award).setAwardAccounts(awardAccounts);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAwardOrganizationFromFixture((Award) award);
        AwardFundManager awardFundManager = ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager();
        ((Award)award).getAwardFundManagers().add(awardFundManager);
        ((Award)award).setAwardPrimaryFundManager(ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager());
        awards.add(award);

        return awards;
    }

    protected void setupBills(ContractsGrantsInvoiceDocument document) {
        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_2.createInvoiceBill();
        invBill_1.setDocumentNumber(document.getDocumentNumber());
        invBill_1.setProposalNumber(document.getProposalNumber());
        invBill_1.setBilledIndicator(false);

        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(document.getAward(), currPeriod);
        Date invoiceDate = pair[1];
        Date billDate = new Date(new DateTime(invoiceDate.getTime()).minusDays(1).toDate().getTime());

        invBill_1.setBillDate(billDate);

        businessObjectService.save(invBill_1);
        invoiceBills.add(invBill_1);
        document.setInvoiceBills(invoiceBills);

        Bill bill = new Bill();
        bill.setProposalNumber(invBill_1.getProposalNumber());
        bill.setBillNumber(invBill_1.getBillNumber());
        bill.setBillDescription(invBill_1.getBillDescription());
        bill.setBillIdentifier(invBill_1.getBillIdentifier());
        bill.setBillDate(invBill_1.getBillDate());
        bill.setEstimatedAmount(invBill_1.getEstimatedAmount());
        bill.setBilledIndicator(invBill_1.isBilledIndicator());
        bill.setAward(document.getAward());
        bill.setActive(true);

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(document.getProposalNumber());
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        businessObjectService.save(predeterminedBillingSchedule);
        businessObjectService.save(bill);
    }
}
