/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * This class tests the GLPE Helper for the Customer Invoice Document
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class CustomerInvoiceDocumentGeneralLedgerPostingTest extends KualiTestBase {

    /**
     * This method tests if general ledger entries are created correctly for income, sales tax, and district tax
     */
    public void testGenerateGeneralLedgerPendingEntries_BasicGLPEs() throws WorkflowException {

        // get document with GLPE's generated
        CustomerInvoiceDocument doc = getCustomerInvoiceDocumentWithGLPEs(ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU, CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE, CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL);
        
        // check if basic GLPE's (income, sales tax, district tax) are generated correctly
        CustomerInvoiceDetail testCustomerInvoiceDetail = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL.createCustomerInvoiceDetail();
        checkBasicGeneralLedgerPendingEntries(doc, testCustomerInvoiceDetail);
    }
    
    /**
     * This method tests if general ledger entries are created correctly when the receivable is set to use the FAU
     */
    public void testGenerateGeneralLedgerPendingEntries_ReceivableFAU() throws WorkflowException {

        // get document with GLPE's generated
        CustomerInvoiceDocument doc = getCustomerInvoiceDocumentWithGLPEs(ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU, CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE, CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL);

        // check the receivable GLPE
        String receivableChartOfAccountsCode = CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE.paymentChartOfAccountsCode;
        String receivableAccountNumber = CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE.paymentAccountNumber;
        String receivableSubAccountNumber = CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE.paymentSubAccountNumber; 
        String receivableFinancialObjectCode = CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE.paymentFinancialObjectCode;
        String receivableFinancialSubObjectCode = GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode(); //TODO What should this value really be?
        String receivableProjectCode = CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE.paymentProjectCode;
        String receivableOrgRefId = CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE.paymentOrganizationReferenceIdentifier;
        
        checkReceivableGeneralLedgerPendingEntries(doc, receivableChartOfAccountsCode, receivableAccountNumber, receivableSubAccountNumber, receivableFinancialObjectCode, receivableFinancialSubObjectCode, receivableProjectCode, receivableOrgRefId);
    }

    /**
     * This method tests if general ledger entries are created correctly when the receivable is set to use the Chart of Accounts
     * Code
     */
    public void testGenerateGeneralLedgerPendingEntries_ReceivableChart() throws WorkflowException {

        // get document with GLPE's generated
        CustomerInvoiceDocument doc = getCustomerInvoiceDocumentWithGLPEs(ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART, CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER, CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE);

        // check the receivable
        CustomerInvoiceDetail testCustomerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE.createCustomerInvoiceDetail();
        testCustomerInvoiceDetail.refreshReferenceObject("chart");
        String receivableChartOfAccountsCode = testCustomerInvoiceDetail.getChartOfAccountsCode();
        String receivableAccountNumber = testCustomerInvoiceDetail.getAccountNumber();
        String receivableSubAccountNumber = testCustomerInvoiceDetail.getSubAccountNumber();
        String receivableFinancialObjectCode = testCustomerInvoiceDetail.getChart().getFinAccountsReceivableObjCode();
        String receivableFinancialSubObjectCode = GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode(); //TODO What should this value really be?
        String receivableProjectCode = testCustomerInvoiceDetail.getProjectCode();
        String receivableOrgRefId = testCustomerInvoiceDetail.getOrganizationReferenceId();        
        
        checkReceivableGeneralLedgerPendingEntries(doc, receivableChartOfAccountsCode, receivableAccountNumber, receivableSubAccountNumber, receivableFinancialObjectCode, receivableFinancialSubObjectCode, receivableProjectCode, receivableOrgRefId);
    }

    /**
     * This method tests if general ledger entries are created correctly when the receivable is set to use the Sub Fund Group
     * 
     * TODO Test needs to be written after AR Object Code is added to Sub Fund Group
     */
    public void testGenerateGeneralLedgerPendingEntries_SubFundGroup() throws WorkflowException {
     // get document with GLPE's generated
        CustomerInvoiceDocument doc = getCustomerInvoiceDocumentWithGLPEs(ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND, CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER, CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE);
        
     // check the receivable
        CustomerInvoiceDetail testCustomerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE.createCustomerInvoiceDetail();
        testCustomerInvoiceDetail.refreshReferenceObject("account");
        String receivableChartOfAccountsCode = testCustomerInvoiceDetail.getChartOfAccountsCode();
        String receivableAccountNumber = testCustomerInvoiceDetail.getAccountNumber();
        String receivableSubAccountNumber = testCustomerInvoiceDetail.getSubAccountNumber();
        String receivableFinancialObjectCode = SpringContext.getBean(CustomerInvoiceDetailService.class).getAccountsReceivableObjectCodeBasedOnReceivableParameter(testCustomerInvoiceDetail);
        String receivableFinancialSubObjectCode = GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode(); //TODO What should this value really be?
        String receivableProjectCode = testCustomerInvoiceDetail.getProjectCode();
        String receivableOrgRefId = testCustomerInvoiceDetail.getOrganizationReferenceId();        
        
        checkReceivableGeneralLedgerPendingEntries(doc, receivableChartOfAccountsCode, receivableAccountNumber, receivableSubAccountNumber, receivableFinancialObjectCode, receivableFinancialSubObjectCode, receivableProjectCode, receivableOrgRefId);        
    }


    /**
     * This method returns a Customer Invoice Document with generated GLPE's based on the receivable offset generation method
     * specified
     * 
     * @param receivableOffsetGenerationMethodValue
     * @param customerInvoiceDocumentFixture
     * @return
     */
    public CustomerInvoiceDocument getCustomerInvoiceDocumentWithGLPEs(String receivableOffsetGenerationMethodValue, CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture customerInvoiceDetailFixture) throws WorkflowException {
        // update system parameter to make system use FAU receivable for Customer Invoice Document
        TestUtils.setSystemParameter(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD, receivableOffsetGenerationMethodValue);

        // create Customer Invoice Document
        CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures = new CustomerInvoiceDetailFixture[] { customerInvoiceDetailFixture };
        CustomerInvoiceDocument doc = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDetailFixtures);

        // generate general pending entries
        SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(doc);

        return doc;
    }


    /**
     * This method checks the invoice, state tax, district tax GLPE entries
     * 
     * TODO add tests for tax GLPEs
     * 
     * @param income
     * @param testCustomerInvoiceDetail
     */
    public void checkBasicGeneralLedgerPendingEntries(CustomerInvoiceDocument doc, CustomerInvoiceDetail testCustomerInvoiceDetail) {        
        
        String receivableOffsetGenerationMethod = TestUtils.getParameterService().getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        int index = receivableOffsetGenerationMethod.equals(ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU)? 2 : 1; 
        GeneralLedgerPendingEntry income = (GeneralLedgerPendingEntry) doc.getGeneralLedgerPendingEntries().get(index);
        
        assertEquals("Income Chart of Accounts Code should be " + testCustomerInvoiceDetail.getChartOfAccountsCode() + " but is actually " + income.getChartOfAccountsCode(), testCustomerInvoiceDetail.getChartOfAccountsCode(), income.getChartOfAccountsCode());
        assertEquals("Income Account Number should be " + testCustomerInvoiceDetail.getAccountNumber() + " but is actually " + income.getAccountNumber(), testCustomerInvoiceDetail.getAccountNumber(), income.getAccountNumber());
        assertEquals("Income Sub Account Number should be " + testCustomerInvoiceDetail.getSubAccountNumber() + " but is actually " + income.getSubAccountNumber(), testCustomerInvoiceDetail.getSubAccountNumber(), income.getSubAccountNumber());
        assertEquals("Income Financial Object Code should be " + testCustomerInvoiceDetail.getFinancialObjectCode() + " but is actually " + income.getFinancialObjectCode(), testCustomerInvoiceDetail.getFinancialObjectCode(), income.getFinancialObjectCode());
        assertEquals("Income Financial Sub Object Code should be " + testCustomerInvoiceDetail.getFinancialSubObjectCode() + " but is actually " + income.getFinancialSubObjectCode(), testCustomerInvoiceDetail.getFinancialSubObjectCode(), income.getFinancialSubObjectCode());
        assertEquals("Income Project Code should be " + testCustomerInvoiceDetail.getProjectCode() + " but is actually " + income.getProjectCode(), testCustomerInvoiceDetail.getProjectCode(), income.getProjectCode());
        assertEquals("Income Org Ref Id should be " + testCustomerInvoiceDetail.getOrganizationReferenceId() + " but is actually " + income.getOrganizationReferenceId(), testCustomerInvoiceDetail.getOrganizationReferenceId(), income.getOrganizationReferenceId());
    }

    /**
     * This method tests if the passed in receivable GLPE is equal to the passed in expected receivable chart, account number, and
     * object code
     * 
     * @param receivable
     * @param expectedReceivableChartOfAccountsCode
     * @param expectedReceivableAccountNumber
     * @param expectedReceivableFinancialObjectCode
     */
    public void checkReceivableGeneralLedgerPendingEntries(CustomerInvoiceDocument doc, String expectedReceivableChartOfAccountsCode, String expectedReceivableAccountNumber, String expectedReceivableSubAccountNumber, String expectedReceivableFinancialObjectCode, String expectedReceivableFinancialSubObjectCode, String expectedReceivableProjectCode, String expectedReceivableOrgRefId) {
        //receivable is always in index 0
        GeneralLedgerPendingEntry receivable = (GeneralLedgerPendingEntry) doc.getGeneralLedgerPendingEntries().get(0);
        assertEquals("Receivable Chart of Accounts Code should be " + expectedReceivableChartOfAccountsCode + " but is actually " + receivable.getChartOfAccountsCode(), expectedReceivableChartOfAccountsCode, receivable.getChartOfAccountsCode());
        assertEquals("Receivable Account Number should be " + expectedReceivableChartOfAccountsCode + " but is actually " + receivable.getAccountNumber(), expectedReceivableAccountNumber, receivable.getAccountNumber());
        assertEquals("Receivable Financial Object Code should be " + expectedReceivableFinancialObjectCode + " but is actually " + receivable.getFinancialObjectCode(), expectedReceivableFinancialObjectCode, receivable.getFinancialObjectCode());
        assertEquals("Receivable Sub Account Number should be " + expectedReceivableSubAccountNumber + " but is actually " + receivable.getSubAccountNumber(), expectedReceivableSubAccountNumber, receivable.getSubAccountNumber());
        assertEquals("Receivable Financial Sub Object Code should be " + expectedReceivableFinancialSubObjectCode + " but is actually " + receivable.getFinancialSubObjectCode(), expectedReceivableFinancialSubObjectCode, receivable.getFinancialSubObjectCode());
        //assertEquals("Receivable Project Code should be " + expectedReceivableProjectCode + " but is actually " + receivable.getProjectCode(), expectedReceivableProjectCode, receivable.getProjectCode());
        assertEquals("Receivable Org Ref Id should be " + expectedReceivableOrgRefId + " but is actually " + receivable.getOrganizationReferenceId(), expectedReceivableOrgRefId, receivable.getOrganizationReferenceId());
    }
}

