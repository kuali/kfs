/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceAccount;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.impl.ContractsGrantsInvoiceDocumentServiceImpl;
import org.kuali.kfs.module.ar.fixture.ARAgencyFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardInvoiceAccountFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceDetailAccountObjectCodeFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceGeneralDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceMilestoneFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceSuspensionCategoryFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the ContractsGrantsInvoiceDocumentService
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceDocumentServiceTest extends KualiTestBase {

    ContractsGrantsInvoiceDocumentServiceImpl contractsGrantsInvoiceDocumentServiceImpl = new ContractsGrantsInvoiceDocumentServiceImpl();
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        contractsGrantsInvoiceDocumentServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setContractsGrantsInvoiceDocumentService(SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class));
        super.setUp();
    }

    /**
     * Tests the prorateBill() method of service.
     */
    public void testProrateBill() throws WorkflowException {


        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        contractsGrantsInvoiceDocument.setAward(award);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();

        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceDetail invoiceDetail_1 = InvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        InvoiceDetail invoiceDetail_2 = InvoiceDetailFixture.INV_DTL2.createInvoiceDetail();
        List<InvoiceDetail> invoiceDetails = new ArrayList<InvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        KualiDecimal value1 = new KualiDecimal(5);
        KualiDecimal value2 = new KualiDecimal(0);
        contractsGrantsInvoiceDocument.getInvoiceDetails().get(0).setExpenditures(value1);
        contractsGrantsInvoiceDocument.getInvoiceDetails().get(1).setExpenditures(value2);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // setup various invoice detail collections on invoice document
        contractsGrantsInvoiceDocumentServiceImpl.generateValuesForCategories(award.getActiveAwardAccounts(), contractsGrantsInvoiceDocument);

        contractsGrantsInvoiceDocumentServiceImpl.prorateBill(contractsGrantsInvoiceDocument);

        assertEquals(value1, contractsGrantsInvoiceDocument.getInvoiceDetails().get(0).getExpenditures());
        assertEquals(value2, contractsGrantsInvoiceDocument.getInvoiceDetails().get(1).getExpenditures());

        // change the award total, it should now prorate
        contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().setAwardTotal(new KualiDecimal(4));

        contractsGrantsInvoiceDocumentServiceImpl.prorateBill(contractsGrantsInvoiceDocument);

        assertEquals(new KualiDecimal(4.00), contractsGrantsInvoiceDocument.getInvoiceDetails().get(0).getExpenditures());
        assertEquals(new KualiDecimal(0), contractsGrantsInvoiceDocument.getInvoiceDetails().get(1).getExpenditures());
    }

    /**
     * Tests the invoice create date and award ending date comparision.
     */
    public void isInvoiceCreateDateAfterAwardEndingDate() {
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);

        assertNotNull(contractsGrantsInvoiceDocument);

        contractsGrantsInvoiceDocument.setAward(award);
        // contractsGrantsInvoiceDocument is created today and award ending date is set to 2011-09-22, so following method always
        // returns true
        assertTrue(contractsGrantsInvoiceDocumentServiceImpl.isInvoiceCreateDateAfterAwardEndingDate(contractsGrantsInvoiceDocument));

    }

    /**
     * Tests the updateInvoiceDetailTotalDirectCost() method of service.
     */
    public void testUpdateInvoiceDetailTotalDirectCost() {
        InvoiceDetail invoiceDetail_1 = InvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        InvoiceDetail invoiceDetail_2 = InvoiceDetailFixture.INV_DTL3.createInvoiceDetail();
        KualiDecimal value1 = new KualiDecimal(2.23);
        KualiDecimal value2 = new KualiDecimal(5.43);
        invoiceDetail_1.setExpenditures(value1);
        invoiceDetail_2.setExpenditures(value2);
        List<InvoiceDetail> invoiceDetails = new ArrayList<InvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);

        KualiDecimal expectedResult = new KualiDecimal(7.66);
        assertTrue(expectedResult.compareTo(contractsGrantsInvoiceDocumentServiceImpl.getInvoiceDetailExpenditureSum(invoiceDetails)) == 0);
    }

    /**
     * Tests the recalculateAccountDetails() method of service.
     */
    public void testRecalculateAccountDetails() {
        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();

        invoiceAccountDetail_1.setExpenditureAmount(new KualiDecimal(4.50));
        invoiceAccountDetail_2.setExpenditureAmount(new KualiDecimal(5.50));

        List<InvoiceAccountDetail> invoiceAccountDetails = new ArrayList<InvoiceAccountDetail>();
        invoiceAccountDetails.add(invoiceAccountDetail_1);
        invoiceAccountDetails.add(invoiceAccountDetail_2);

        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_1 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD1.createInvoiceDetailAccountObjectCode();
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_2 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD2.createInvoiceDetailAccountObjectCode();

        KualiDecimal value1 = (new KualiDecimal(3.01));
        KualiDecimal value2 = (new KualiDecimal(2.02));

        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value1);
        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value2);

        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_1);
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_2);

        contractsGrantsInvoiceDocumentServiceImpl.recalculateAccountDetails(invoiceAccountDetails, invoiceDetailAccountObjectCodes);

        assert (invoiceAccountDetails.get(0).getExpenditureAmount().compareTo(value1) == 0);
        assert (invoiceAccountDetails.get(1).getExpenditureAmount().compareTo(value2) == 0);
    }

    /**
     * Tests the recalculateNewTotalBilled() method of service.
     */
    public void testRecalculateNewTotalBilled() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        contractsGrantsInvoiceDocument.setAward(award);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();

        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        KualiDecimal value1 = new KualiDecimal(5.10);
        KualiDecimal value2 = new KualiDecimal(6.50);

        // set InvoiceDetails
        InvoiceDetail invoiceDetail_1 = InvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        InvoiceDetail invoiceDetail_2 = InvoiceDetailFixture.INV_DTL3.createInvoiceDetail();

        invoiceDetail_1.setExpenditures(value1);
        invoiceDetail_2.setExpenditures(value2);

        List<InvoiceDetail> invoiceDetails = new ArrayList<InvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        // set InvoiceAccountDetails
        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();

        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // setup various invoice detail collections on invoice document
        contractsGrantsInvoiceDocumentServiceImpl.generateValuesForCategories(award.getActiveAwardAccounts(), contractsGrantsInvoiceDocument);

        // set InvoiceDetailAccountObjectCodes
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_1 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD1.createInvoiceDetailAccountObjectCode();
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_2 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD2.createInvoiceDetailAccountObjectCode();

        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value1);
        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value2);

        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_1);
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_2);

        KualiDecimal originalTotalBilledValue = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled();

        contractsGrantsInvoiceDocumentServiceImpl.recalculateNewTotalBilled(contractsGrantsInvoiceDocument);

        // assert newTotalBilled hasn't changed
        assert (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().compareTo((originalTotalBilledValue)) == 0);

        // making values in account detail different. This simulates that the user have changed
        // the current expenditure amount and will cause recalucation.
        value1 = new KualiDecimal(10.22);
        value2 = new KualiDecimal(8.44);

        invoiceDetails.get(0).setExpenditures(value1);
        invoiceDetails.get(1).setExpenditures(value1);

        contractsGrantsInvoiceDocumentServiceImpl.recalculateNewTotalBilled(contractsGrantsInvoiceDocument);

        // assert new value
        assert (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().compareTo(new KualiDecimal(18.66)) == 0);
    }

    /**
     * Tests updateSuspensionCategoriesOnDocument() method.
     */
    @SuppressWarnings("deprecation")
    public void updateSuspensionCategoriesOnDocumentTest() {

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD2.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        contractsGrantsInvoiceDocument.setAward(award);

        InvoiceSuspensionCategory invoiceSuspensionCategory_1 = InvoiceSuspensionCategoryFixture.INV_SUSPEN_CTGR1.createInvoiceSuspensionCategory();
        InvoiceSuspensionCategory invoiceSuspensionCategory_2 = InvoiceSuspensionCategoryFixture.INV_SUSPEN_CTGR2.createInvoiceSuspensionCategory();
        List<InvoiceSuspensionCategory> invoiceSuspensionCategories = new ArrayList<InvoiceSuspensionCategory>();
        invoiceSuspensionCategories.add(invoiceSuspensionCategory_1);
        invoiceSuspensionCategories.add(invoiceSuspensionCategory_2);
        contractsGrantsInvoiceDocument.setInvoiceSuspensionCategories(invoiceSuspensionCategories);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        contractsGrantsInvoiceDocumentServiceImpl.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        // expected suspensionCategories are below

        assertEquals(7, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    /**
     * This method is intented to test if the source accounting lines are created properly for every type of GLPE and invoicing
     * option in the award.
     */
    public void testCreateSourceAccountingLinesAndGLPEs() {

        String coaCode = "BL";
        String orgCode = "SRS";


        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        contractsGrantsInvoiceDocument.setBillByChartOfAccountCode(coaCode);
        contractsGrantsInvoiceDocument.setBilledByOrganizationCode(orgCode);

        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(coaCode);
        accountsReceivableDocumentHeader.setProcessingOrganizationCode(orgCode);

        contractsGrantsInvoiceDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);


        InvoiceGeneralDetail inv_Gnrl_Dtl_1 = InvoiceGeneralDetailFixture.INV_GNRL_DTL3.createInvoiceGeneralDetail();
        InvoiceGeneralDetail inv_Gnrl_Dtl_Mlstn = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        InvoiceGeneralDetail inv_Gnrl_Dtl_Bill = InvoiceGeneralDetailFixture.INV_GNRL_DTL4.createInvoiceGeneralDetail();
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        InvoiceDetail invoiceDetail_1 = InvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        InvoiceDetail invoiceDetail_2 = InvoiceDetailFixture.INV_DTL3.createInvoiceDetail();
        List<InvoiceDetail> invoiceDetails = new ArrayList<InvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        KualiDecimal value1 = new KualiDecimal(5);

        KualiDecimal value2 = new KualiDecimal(0);
        contractsGrantsInvoiceDocument.getInvoiceDetails().get(0).setExpenditures(value1);
        contractsGrantsInvoiceDocument.getInvoiceDetails().get(1).setExpenditures(value2);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL3.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL4.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);


        List<InvoiceMilestone> invoiceMilestones = new ArrayList<InvoiceMilestone>();
        InvoiceMilestone invMilestone_1 = InvoiceMilestoneFixture.INV_MLSTN_1.createInvoiceMilestone();
        invoiceMilestones.add(invMilestone_1);
        contractsGrantsInvoiceDocument.setInvoiceMilestones(invoiceMilestones);


        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_1.createInvoiceBill();
        invoiceBills.add(invBill_1);
        contractsGrantsInvoiceDocument.setInvoiceBills(invoiceBills);

        // To set Organization Accounting default
        Map<String, Object> criteria = new HashMap<String, Object>();

        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        OrganizationAccountingDefault organizationAccountingDefault = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

        if (ObjectUtils.isNull(organizationAccountingDefault)) {
            organizationAccountingDefault = new OrganizationAccountingDefault();
            organizationAccountingDefault.setChartOfAccountsCode("BL");
            organizationAccountingDefault.setOrganizationCode("SRS");
            organizationAccountingDefault.setUniversityFiscalYear(currentYear);
        }
        organizationAccountingDefault.setDefaultInvoiceFinancialObjectCode("5000");
        organizationAccountingDefault.setDefaultPaymentFinancialObjectCode("8118");
        SpringContext.getBean(BusinessObjectService.class).save(organizationAccountingDefault);


        // To test for all combinations of GLPE and Award Invoicing options
        // GLPE is 1.
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
//        parameterService.setParameterForTesting(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART);

        // To check if the source accounting lines are created as expected.
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE_2.createCustomerInvoiceDetail();

        // 1. Invoicing by Award
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1a. Award with Milestones
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Mlstn);
        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1b. Award with Bills
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Bill);
        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 2. Invoicing by Account
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 3. Invoicing by Contract Control Account. For this case the account number might be different so setting a different
        // invoice account detail here.
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_CCA.createAward());
        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);


        // GLPE is 2.
//        parameterService.setParameterForTesting(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND);
        // Setting subfund based on the test account number 1031400.
//        parameterService.setParameterForTesting(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND, "GENFND=8110");

        // To check if the source accounting lines are created as expected.
        customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE_2.createCustomerInvoiceDetail();

        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // 1. Invoicing by Award
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1a. Award with Milestones
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Mlstn);
        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1b. Award with Bills
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Bill);
        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 2. Invoicing by Account
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 3. Invoicing by Contract Control Account. For this case the account number might be different so setting a different
        // invoice account detail here.
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        contractsGrantsInvoiceDocument.setAward(ARAwardFixture.CG_AWARD_INV_CCA.createAward());
        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);


        // GLPE is 3 - Using income Account.
//        parameterService.setParameterForTesting(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU);


        // Use the same customer Invoice detail as Subfund.

        customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE_2.createCustomerInvoiceDetail();


        CustomerInvoiceAccount custInvAcct = ARAwardInvoiceAccountFixture.AWD_INV_ACCT_1.createCustomerInvoiceAccount();


        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // 1. Invoicing by Award
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        ContractsAndGrantsBillingAward awardInvAward = ARAwardFixture.CG_AWARD_INV_AWARD.createAward();
        contractsGrantsInvoiceDocument.setAward(awardInvAward);

        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1a. Award with Milestones - use the same values from Monthly
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Mlstn);

        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1b. Award with Bills - use the same values from Monthly
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Bill);

        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);


        // 2. Invoicing by Account
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        awardInvAward = ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        contractsGrantsInvoiceDocument.setAward(awardInvAward);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 3. Invoicing by Contract Control Account. For this case the account number might be different so setting a different
        // invoice account detail here.
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        awardInvAward = ARAwardFixture.CG_AWARD_INV_CCA.createAward();
        contractsGrantsInvoiceDocument.setAward(awardInvAward);
        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

    }

    /**
     * This method compares the source accounting lines.
     *
     * @param contractsGrantsInvoiceDocument The invoice document object.
     * @param customerInvoiceDetail The customer invoice detail object.
     */
    public void compareSourceAccountingLines(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, CustomerInvoiceDetail customerInvoiceDetail) {

        // To clear source accounting lines every time to check all combinations.
        contractsGrantsInvoiceDocument.getSourceAccountingLines().clear();

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        try {
            contractsGrantsInvoiceDocumentService.createSourceAccountingLinesAndGLPEs(contractsGrantsInvoiceDocument);

        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }


        CustomerInvoiceDetail invoiceDetail = (CustomerInvoiceDetail) contractsGrantsInvoiceDocument.getSourceAccountingLine(0);

        assertEquals(invoiceDetail.getAccountNumber(), customerInvoiceDetail.getAccountNumber());
        assertEquals(invoiceDetail.getChartOfAccountsCode(), customerInvoiceDetail.getChartOfAccountsCode());
        assertEquals(invoiceDetail.getFinancialObjectCode(), customerInvoiceDetail.getFinancialObjectCode());
        // For GLPE 3, the AR object code will not be set. so exclude that.
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        if (!isUsingReceivableFAU) {

            assertEquals(invoiceDetail.getAccountsReceivableObjectCode(), customerInvoiceDetail.getAccountsReceivableObjectCode());
        }
        assertEquals(invoiceDetail.getAmount(), customerInvoiceDetail.getAmount());


    }

    /**
     * Tests isAwardHasClosedAccountWithCurrentExpenditures() method of service.
     */
    public void testIsAwardHasClosedAccountWithCurrentExpenditures() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);

        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_1 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD1.createInvoiceDetailAccountObjectCode();
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_2 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD2.createInvoiceDetailAccountObjectCode();

        KualiDecimal value1 = (new KualiDecimal(3.01));
        KualiDecimal value2 = (new KualiDecimal(2.02));

        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value1);
        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value2);

        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_1);
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_2);

        assertFalse(contractsGrantsInvoiceDocumentServiceImpl.isAwardHasClosedAccountWithCurrentExpenditures(contractsGrantsInvoiceDocument));

    }

    /**
     * Tests the GetInvoiceDocumentsForReferralToCollectionsLookup() method of service class.
     */
    public void testGetInvoiceDocumentsForReferralToCollectionsLookup() {
        contractsGrantsInvoiceDocumentServiceImpl.setContractsGrantsInvoiceDocumentDao(SpringContext.getBean(ContractsGrantsInvoiceDocumentDao.class));
        Map<String, String> fieldValues = new LinkedHashMap<String, String>();
        fieldValues.put("agencyNumber", "20770");

        assertTrue(contractsGrantsInvoiceDocumentServiceImpl.getInvoiceDocumentsForReferralToCollectionsLookup(fieldValues).size() > 0);
    }
}
