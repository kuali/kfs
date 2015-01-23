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

package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.impl.ContractsGrantsInvoiceDocumentServiceImpl;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARProposalFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceGeneralDetailFixture;
import org.kuali.kfs.module.ar.service.CostCategoryService;
import org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentServiceImpl;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.util.ReflectionMap;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the ContractsGrantsInvoiceDocument class
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceDocumentTest extends KualiTestBase {
    private BusinessObjectService businessObjectService;

    public CostCategory category;
    public ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument;
    public ContractsGrantsInvoiceDocumentServiceImpl contractsGrantsInvoiceDocumentServiceImpl;
    ContractsGrantsInvoiceCreateDocumentServiceImpl contractsGrantsInvoiceCreateDocumentServiceImpl;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        contractsGrantsInvoiceDocumentServiceImpl = new ContractsGrantsInvoiceDocumentServiceImpl();
        contractsGrantsInvoiceDocumentServiceImpl.setBusinessObjectService(businessObjectService);
        contractsGrantsInvoiceDocumentServiceImpl.setObjectCodeService(SpringContext.getBean(ObjectCodeService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setUniversityDateService(SpringContext.getBean(UniversityDateService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setOptionsService(SpringContext.getBean(OptionsService.class));

        contractsGrantsInvoiceCreateDocumentServiceImpl = new ContractsGrantsInvoiceCreateDocumentServiceImpl();
        contractsGrantsInvoiceCreateDocumentServiceImpl.setAccountingPeriodService(SpringContext.getBean(AccountingPeriodService.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setAwardAccountObjectCodeTotalBilledDao(SpringContext.getBean(AwardAccountObjectCodeTotalBilledDao.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setBusinessObjectService(businessObjectService);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setDateTimeService(SpringContext.getBean(DateTimeService.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setUniversityDateService(SpringContext.getBean(UniversityDateService.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setContractsGrantsInvoiceDocumentService(contractsGrantsInvoiceDocumentServiceImpl);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setCostCategoryService(SpringContext.getBean(CostCategoryService.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setOptionsService(SpringContext.getBean(OptionsService.class));

        category = new CostCategory();
        category.setCategoryCode("testCode");
        category.setCategoryName("testName");

        CostCategoryObjectCode costCategoryObjectCode = new CostCategoryObjectCode();
        costCategoryObjectCode.setCategoryCode("testCode");
        costCategoryObjectCode.setChartOfAccountsCode("BL");
        costCategoryObjectCode.setFinancialObjectCode("5000");
        category.setObjectCodes(new ArrayList<CostCategoryObjectCode>());
        category.getObjectCodes().add(costCategoryObjectCode);
        contractsGrantsInvoiceDocument = new ContractsGrantsInvoiceDocument();

    }

    @ConfigureContext(session = wklykins)
    public void testUpdateLastBilledDate() {

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_1 = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_2 = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        ContractAndGrantsProposal proposal = ARProposalFixture.CG_PRPSL1.createProposal();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();

        award.getActiveAwardAccounts().clear();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        ContractsAndGrantsBillingAwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_2.createAwardAccount();

        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        awardAccounts.add(awardAccount_2);

        setAwardAccountsToAward(award.getProposalNumber(), awardAccounts);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL3.createInvoiceAccountDetail();

        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument_1.setAccountDetails(accountDetails);
        accountDetails = new ArrayList<InvoiceAccountDetail>();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL4.createInvoiceAccountDetail();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument_2.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail_1 = InvoiceGeneralDetailFixture.INV_GNRL_DTL3.createInvoiceGeneralDetail();
        InvoiceGeneralDetail invoiceGeneralDetail_2 = InvoiceGeneralDetailFixture.INV_GNRL_DTL3.createInvoiceGeneralDetail();
        invoiceGeneralDetail_1.setAward(award);
        invoiceGeneralDetail_2.setAward(award);

        // To set last Billed Date to invoice and check if they are being set to award Accounts and award properly.
        Date lastBilledDate_1 = Date.valueOf("2011-10-26");
        Date lastBilledDate_2 = Date.valueOf("2011-10-27");
        invoiceGeneralDetail_1.setLastBilledDate(lastBilledDate_1);
        invoiceGeneralDetail_2.setLastBilledDate(lastBilledDate_2);
        contractsGrantsInvoiceDocument_1.setInvoiceGeneralDetail(invoiceGeneralDetail_1);
        contractsGrantsInvoiceDocument_2.setInvoiceGeneralDetail(invoiceGeneralDetail_2);
        // Now there are two invoices, to check combinations of invoices, when one by one go to final and corrected.

        // 1. invoice 1 is set to final.
        contractsGrantsInvoiceDocument_1.getInvoiceGeneralDetail().setFinalBillIndicator(true);
        contractsGrantsInvoiceDocumentService.updateLastBilledDate(contractsGrantsInvoiceDocument_1);
        // Now to retrieve the Award Account and check the values.

        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_1.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_1.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount_1.getProposalNumber());
        ContractsAndGrantsBillingAwardAccount awdAcct_1 = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_2.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_2.getChartOfAccountsCode());
        ContractsAndGrantsBillingAwardAccount awdAcct_2 = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);


        assertEquals(lastBilledDate_1, awdAcct_1.getCurrentLastBilledDate());

        assertEquals(null, awdAcct_2.getCurrentLastBilledDate());
        assertEquals(null, award.getLastBilledDate());

        // 2. invoice 2 is set to final.
        contractsGrantsInvoiceDocument_2.getInvoiceGeneralDetail().setFinalBillIndicator(true);
        contractsGrantsInvoiceDocumentService.updateLastBilledDate(contractsGrantsInvoiceDocument_2);
        mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_1.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_1.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount_1.getProposalNumber());
        awdAcct_1 = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_2.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_2.getChartOfAccountsCode());
        awdAcct_2 = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);

        assertEquals(lastBilledDate_1, awdAcct_1.getCurrentLastBilledDate());
        assertEquals(lastBilledDate_2, awdAcct_2.getCurrentLastBilledDate());
        mapKey.clear();
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        contractsGrantsInvoiceDocument_2.getInvoiceGeneralDetail().setAward(null);
        award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, mapKey);
        assertEquals(lastBilledDate_1, award.getLastBilledDate());

        // 3. invoice 1 or 2 is corrected.
        contractsGrantsInvoiceDocument_1.getInvoiceGeneralDetail().setFinalBillIndicator(true);
        contractsGrantsInvoiceDocument_1.getFinancialSystemDocumentHeader().setFinancialDocumentInErrorNumber(contractsGrantsInvoiceDocument_2.getDocumentNumber());
        contractsGrantsInvoiceDocument_1.isInvoiceReversal();
        contractsGrantsInvoiceDocumentService.updateLastBilledDate(contractsGrantsInvoiceDocument_1);
        mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_1.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_1.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount_1.getProposalNumber());
        awdAcct_1 = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_2.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_2.getChartOfAccountsCode());
        awdAcct_2 = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);

        assertEquals(null, awdAcct_1.getCurrentLastBilledDate());
        assertEquals(lastBilledDate_2, awdAcct_2.getCurrentLastBilledDate());
        mapKey.clear();
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, mapKey);

        assertEquals(null, award.getLastBilledDate());


    }

    /**
     * This method sets values to Award respective to junit testing
     *
     * @param proposalNumber
     * @param fieldValues
     */
    private void setAwardAccountsToAward(Long proposalNumber, List<ContractsAndGrantsBillingAwardAccount> awardAccounts) {

        // Award and proposal is being saved
        Proposal proposal = businessObjectService.findBySinglePrimaryKey(Proposal.class, proposalNumber);
        if (ObjectUtils.isNull(proposal)) {
            proposal = new Proposal();
            proposal.setProposalNumber(proposalNumber);
        }
        businessObjectService.save(proposal);

        Award award = businessObjectService.findBySinglePrimaryKey(Award.class, proposalNumber);
        if (ObjectUtils.isNull(award)) {
            award = new Award();
            award.setProposalNumber(proposalNumber);

        }
        businessObjectService.save(award);

        List<AwardAccount> awdAccts = new ArrayList<AwardAccount>();

        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
            AwardAccount awdAcct = businessObjectService.findByPrimaryKey(AwardAccount.class, mapKey);
            if (ObjectUtils.isNull(awdAcct)) {
                awdAcct = new AwardAccount();
                awdAcct.setAccountNumber(awardAccount.getAccountNumber());
                awdAcct.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                awdAcct.setProposalNumber(awardAccount.getProposalNumber());
            }

            businessObjectService.save(awdAcct);

            awdAccts.add(awdAcct);
        }
        award.setAwardAccounts(awdAccts);

    }

    public void testInvoiceDetails() {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        invoiceGeneralDetail.setProposalNumber(award.getProposalNumber());
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL4.createInvoiceDetail();
        ContractsGrantsInvoiceDetail invoiceDetail_2 = ContractsGrantsInvoiceDetailFixture.INV_DTL5.createInvoiceDetail();
        ContractsGrantsInvoiceDetail invoiceDetail_3 = ContractsGrantsInvoiceDetailFixture.INV_DTL6.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);
        invoiceDetails.add(invoiceDetail_3);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        // setup various invoice detail collections on invoice document
        List<ContractsGrantsInvoiceDetail> generatedInvoiceDetails = contractsGrantsInvoiceCreateDocumentServiceImpl.generateValuesForCategories(contractsGrantsInvoiceDocument.getDocumentNumber(), contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes(), new HashMap<String, KualiDecimal>(), new ArrayList<AwardAccountObjectCodeTotalBilled>());
        contractsGrantsInvoiceDocument.getInvoiceDetails().addAll(generatedInvoiceDetails);

        // all
        List<ContractsGrantsInvoiceDetail> allInvoiceDetails = contractsGrantsInvoiceDocument.getInvoiceDetails();

        // non-totals
        List<ContractsGrantsInvoiceDetail> invoiceDetailsWithoutIdc = contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails();
        List<ContractsGrantsInvoiceDetail> invoiceDetailsIdcOnly = contractsGrantsInvoiceDocument.getIndirectCostInvoiceDetails();
        List<ContractsGrantsInvoiceDetail> invoiceDetailsWithIdc = contractsGrantsInvoiceDocument.getInvoiceDetails();

        // totals
        ContractsGrantsInvoiceDetail totalCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalCostInvoiceDetail();
        ContractsGrantsInvoiceDetail totalDirectCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalDirectCostInvoiceDetail();
        ContractsGrantsInvoiceDetail totalInDirectCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalIndirectCostInvoiceDetail();

        // non-totals
        assertEquals(12, allInvoiceDetails.size());
        assertEquals(11, invoiceDetailsWithoutIdc.size());
        assertEquals(1, invoiceDetailsIdcOnly.size());
        assertEquals(12, invoiceDetailsWithIdc.size());

        // totals
        assertNotNull(totalCostInvoiceDetail);
        assertEquals(new KualiDecimal(960.00), totalCostInvoiceDetail.getTotalBudget());
        assertEquals(new KualiDecimal(1020.00), totalCostInvoiceDetail.getCumulativeExpenditures());
        assertEquals(new KualiDecimal(-60.00), totalCostInvoiceDetail.getBudgetRemaining());
        assertEquals(new KualiDecimal(0.00), totalCostInvoiceDetail.getTotalPreviouslyBilled());
        assertEquals(new KualiDecimal(0.00), totalCostInvoiceDetail.getTotalAmountBilledToDate());
        assertEquals(new KualiDecimal(960.00), totalCostInvoiceDetail.getAmountRemainingToBill());

        assertNotNull(totalDirectCostInvoiceDetail);
        assertEquals(new KualiDecimal(640.00), totalDirectCostInvoiceDetail.getTotalBudget());
        assertEquals(new KualiDecimal(680.00), totalDirectCostInvoiceDetail.getCumulativeExpenditures());
        assertEquals(new KualiDecimal(-40.00), totalDirectCostInvoiceDetail.getBudgetRemaining());
        assertEquals(new KualiDecimal(0.00), totalDirectCostInvoiceDetail.getTotalPreviouslyBilled());
        assertEquals(new KualiDecimal(0.00), totalDirectCostInvoiceDetail.getTotalAmountBilledToDate());
        assertEquals(new KualiDecimal(640.00), totalDirectCostInvoiceDetail.getAmountRemainingToBill());

        assertNotNull(totalInDirectCostInvoiceDetail);
        assertEquals(new KualiDecimal(320.00), totalInDirectCostInvoiceDetail.getTotalBudget());
        assertEquals(new KualiDecimal(340.00), totalInDirectCostInvoiceDetail.getCumulativeExpenditures());
        assertEquals(new KualiDecimal(-20.00), totalInDirectCostInvoiceDetail.getBudgetRemaining());
        assertEquals(new KualiDecimal(0.00), totalInDirectCostInvoiceDetail.getTotalPreviouslyBilled());
        assertEquals(new KualiDecimal(0.00), totalInDirectCostInvoiceDetail.getTotalAmountBilledToDate());
        assertEquals(new KualiDecimal(320.00), totalInDirectCostInvoiceDetail.getAmountRemainingToBill());

    }

    public void testCheckAwardContractControlAccounts_ValidNoInvoicingOption() throws Exception {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        List<String> errorString = contractsGrantsInvoiceDocumentServiceImpl.checkAwardContractControlAccounts(award);
        assertNotNull(errorString);
        assertEquals(0, errorString.size());
    }

    public void testCheckAwardContractControlAccounts_ValidInvoicingByAccount() throws Exception {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        refreshAccounts(award);

        List<String> errorString = contractsGrantsInvoiceDocumentServiceImpl.checkAwardContractControlAccounts(award);
        assertNotNull(errorString);
        assertEquals(0, errorString.size());
    }

    public void testCheckAwardContractControlAccounts_ValidInvoicingByContractControlAccount() throws Exception {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_INV_CCA.createAward();

        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1.createAwardAccount();
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();

        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        awardAccounts.add(awardAccount_2);
        ((Award)award).setAwardAccounts(awardAccounts);
        refreshAccounts(award);

        List<String> errorString = contractsGrantsInvoiceDocumentServiceImpl.checkAwardContractControlAccounts(award);
        assertNotNull(errorString);
        assertEquals(0, errorString.size());
    }

    public void testCheckAwardContractControlAccounts_InvalidInvoicingByContractControlAccountNoCCA() throws Exception {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_INV_CCA.createAward();
        refreshAccounts(award);
        List<String> errorString = contractsGrantsInvoiceDocumentServiceImpl.checkAwardContractControlAccounts(award);
        assertNotNull(errorString);
        assertEquals(2, errorString.size());
        assertTrue(errorString.contains(ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT));
        assertTrue(errorString.contains(award.getInvoicingOptionDescription()));
    }

    public void testCheckAwardContractControlAccounts_ValidInvoicingByAward() throws Exception {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_INV_AWARD.createAward();

        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1.createAwardAccount();
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();

        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        awardAccounts.add(awardAccount_2);
        ((Award)award).setAwardAccounts(awardAccounts);
        refreshAccounts(award);

        List<String> errorString = contractsGrantsInvoiceDocumentServiceImpl.checkAwardContractControlAccounts(award);
        assertNotNull(errorString);
        assertEquals(0, errorString.size());
    }

    public void testCheckAwardContractControlAccounts_InvalidInvoicingByAwardNoCCA() throws Exception {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_INV_AWARD.createAward();
        refreshAccounts(award);

        List<String> errorString = contractsGrantsInvoiceDocumentServiceImpl.checkAwardContractControlAccounts(award);
        assertNotNull(errorString);
        assertEquals(2, errorString.size());
        assertTrue(errorString.contains(ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT));
        assertTrue(errorString.contains(award.getInvoicingOptionDescription()));
    }

    public void testCheckAwardContractControlAccounts_InvalidInvoicingByAwardMultipleCCAs() throws Exception {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_INV_AWARD.createAward();
        award.getActiveAwardAccounts().clear();

        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1.createAwardAccount();
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_3.createAwardAccount();

        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        awardAccounts.add(awardAccount_2);
        ((Award)award).setAwardAccounts(awardAccounts);
        refreshAccounts(award);

        List<String> errorString = contractsGrantsInvoiceDocumentServiceImpl.checkAwardContractControlAccounts(award);
        assertNotNull(errorString);
        assertEquals(2, errorString.size());
        assertTrue(errorString.contains(ArKeyConstants.AwardConstants.ERROR_MULTIPLE_CTRL_ACCT));
        assertTrue(errorString.contains(award.getInvoicingOptionDescription()));
    }

    private void refreshAccounts(ContractsAndGrantsBillingAward award) {
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        for (ContractsAndGrantsBillingAwardAccount awardAccount: awardAccounts) {
            ((AwardAccount)awardAccount).refreshReferenceObject("account");
        }
    }

    public void testBeanMapVersionOfDocument() {
        ContractsGrantsInvoiceDocument cinvDoc = new ContractsGrantsInvoiceDocument();
        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setProposalNumber(new Long(80075L));
        cinvDoc.setInvoiceGeneralDetail(invoiceGeneralDetail);
        InvoiceAccountDetail invoiceAccountDetail = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<>();
        accountDetails.add(invoiceAccountDetail);
        cinvDoc.setAccountDetails(accountDetails);

        Map<String, Object> map = new ReflectionMap(cinvDoc);
        assertEquals(new Long(80075L), map.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER));
        assertEquals("MILE", map.get(ArPropertyConstants.INVOICE_GENERAL_DETAIL+"." + ArPropertyConstants.BILLING_FREQUENCY_CODE));
        assertEquals("9000000", map.get("accountDetails[0]."+KFSPropertyConstants.ACCOUNT_NUMBER));
        assertNull(map.get("zebra"));
        assertNull(map.get(ArPropertyConstants.INVOICE_GENERAL_DETAIL+".zebra"));
        assertNull(map.get("accountDetails[2]."+KFSPropertyConstants.ACCOUNT_NUMBER));
    }
}
