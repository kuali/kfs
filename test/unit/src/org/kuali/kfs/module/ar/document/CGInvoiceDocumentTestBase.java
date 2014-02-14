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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceMilestoneFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Basic setup to create Invoice Document
 */
public class CGInvoiceDocumentTestBase extends KualiTestBase {

    BusinessObjectService boService;
    DocumentService documentService;
    ContractsGrantsInvoiceCreateDocumentService cginService;
    ContractsAndGrantsBillingAward award;
    ContractsGrantsInvoiceDocument document;

    @Override
    @ConfigureContext(session = khuntley)
    protected void setUp() throws Exception {
        super.setUp();
        boService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        cginService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        // this award is already present in the test drive's database

        award = ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        award = ARAwardFixture.CG_AWARD_INV_ACCOUNT.setAgencyFromFixture((Award) award);

        document = (ContractsGrantsInvoiceDocument) documentService.getNewDocument("CINV");
        Date start = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Date stop = start;
        stop.setYear(start.getYear() + 1);
        if (ObjectUtils.isNotNull(award)) {


            // creating invoice document directly without using the service to get over validations.
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (!awardAccount.isFinalBilledIndicator()) {
                    // I don't want to have to do this, but in order to get the tests to pass with the code as currently
                    // written we need to save these so they can be retrieved later.
                    boService.save((AwardAccount) awardAccount);

                    List<ContractsAndGrantsBillingAwardAccount> list = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
                    list.clear();
                    // only one account is added into the list to create CINV
                    list.add(awardAccount);
                    Map<String, Object> criteria = new HashMap<String,Object>();
                    criteria.put("accountNumber", awardAccount.getAccountNumber());
                    criteria.put("chartOfAccountsCode", awardAccount.getChartOfAccountsCode());

                    Account acct = boService.findByPrimaryKey(Account.class, criteria);
                    String coaCode = acct.getChartOfAccountsCode(); //awardAccount.getAccount().getChartOfAccountsCode();
                    String orgCode = acct.getOrganizationCode(); //awardAccount.getAccount().getOrganizationCode();
                    criteria.clear();
                    Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
                    criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    OrganizationAccountingDefault organizationAccountingDefault = boService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault)) {
                        organizationAccountingDefault = new OrganizationAccountingDefault();
                        organizationAccountingDefault.setChartOfAccountsCode(coaCode);
                        organizationAccountingDefault.setOrganizationCode(orgCode);
                        organizationAccountingDefault.setUniversityFiscalYear(currentYear);
                        boService.save(organizationAccountingDefault);
                    }
                    criteria.clear();
                    criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");
                    criteria.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, "CINV");

                    OffsetDefinition offset = boService.findByPrimaryKey(OffsetDefinition.class, criteria);
                    if (ObjectUtils.isNull(offset)) {
                        offset = new OffsetDefinition();
                        offset.setChartOfAccountsCode(coaCode);
                        offset.setUniversityFiscalYear(currentYear);
                        offset.setFinancialObjectCode("8000");
                        offset.setFinancialDocumentTypeCode("CINV");
                        offset.setFinancialBalanceTypeCode("AC");
                        boService.save(offset);
                    }
                    document = cginService.createCGInvoiceDocumentByAwardInfo(award, list, coaCode, orgCode);
                }
            }
        }
    }

    /**
     * Gets the document attribute.
     *
     * @return Returns the document.
     */
    public ContractsGrantsInvoiceDocument getDocument() {
        try {
            if (ObjectUtils.isNull(document)) {
                this.setUp();
            }
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
        return document;
    }

    /**
     * Sets the document attribute value.
     *
     * @param document The document to set.
     */
    public void setDocument(ContractsGrantsInvoiceDocument document) {
        this.document = document;
    }

    protected void setupBills(String documentNumber, Long proposalNumber, boolean billedIndicator) {
        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_1.createInvoiceBill();
        invBill_1.setDocumentNumber(documentNumber);
        invBill_1.setProposalNumber(proposalNumber);
        invBill_1.setBilledIndicator(billedIndicator);
        boService.save(invBill_1);
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

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(proposalNumber);
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        boService.save(predeterminedBillingSchedule);
        boService.save(bill);
    }

    protected void setupMilestones(String documentNumber, Long proposalNumber, boolean billedIndicator) {
        List<InvoiceMilestone> invoiceMilestones = new ArrayList<InvoiceMilestone>();
        InvoiceMilestone invMilestone_1 = InvoiceMilestoneFixture.INV_MLSTN_1.createInvoiceMilestone();
        invMilestone_1.setDocumentNumber(documentNumber);
        invMilestone_1.setProposalNumber(proposalNumber);
        invMilestone_1.setBilledIndicator(billedIndicator);
        boService.save(invMilestone_1);
        invoiceMilestones.add(invMilestone_1);
        document.setInvoiceMilestones(invoiceMilestones);

        Milestone milestone = new Milestone();
        milestone.setProposalNumber(invMilestone_1.getProposalNumber());
        milestone.setMilestoneNumber(invMilestone_1.getMilestoneNumber());
        milestone.setMilestoneIdentifier(invMilestone_1.getMilestoneIdentifier());
        milestone.setMilestoneDescription(invMilestone_1.getMilestoneDescription());
        milestone.setMilestoneAmount(invMilestone_1.getMilestoneAmount());
        milestone.setMilestoneActualCompletionDate(invMilestone_1.getMilestoneActualCompletionDate());
        milestone.setMilestoneExpectedCompletionDate(invMilestone_1.getMilestoneExpectedCompletionDate());
        milestone.setBilledIndicator(invMilestone_1.isBilledIndicator());
        milestone.setAward(document.getAward());

        MilestoneSchedule milestoneSchedule = new MilestoneSchedule();
        milestoneSchedule.setProposalNumber(proposalNumber);
        List<Milestone> milestones = new ArrayList<Milestone>();
        milestones.add(milestone);
        milestoneSchedule.setMilestones(milestones);
        boService.save(milestoneSchedule);
        boService.save(milestone);
    }


}
