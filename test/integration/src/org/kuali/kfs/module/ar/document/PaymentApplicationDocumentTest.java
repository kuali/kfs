/*
 * Copyright 2008 The Kuali Foundation.
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
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentTestUtil;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = khuntley)
public class PaymentApplicationDocumentTest extends KualiTestBase {
    static private String ANNOTATION = "PaymentApplicationDocument testing";
    static private String TESTING = "PaymentApplicationDocument testing";
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private CashControlDocumentService cashControlDocumentService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        universityDateService = SpringContext.getBean(UniversityDateService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testCreatePaymentApplicationDocument() throws Exception {
        PaymentApplicationDocument document = (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);
        document.getDocumentHeader().setDocumentDescription("Testing");
        //documentService.saveDocument(document);
    }
    
    // Test that payment application documents are created for each cash control detail
    public void testCreatedUponApprovingCashControlDocument() throws Exception {
        List<CashControlDetailSpec> specs = new ArrayList<CashControlDetailSpec>();
        specs.add(new CashControlDetailSpec("ABB2","9999",new Date(System.currentTimeMillis()),new KualiDecimal(1000)));
        
        CashControlDocument cashControlDocument = createAndSaveNewCashControlDocument(specs);
        
        for(CashControlDetail detail : cashControlDocument.getCashControlDetails()) {
            assertNotNull(detail.getReferenceFinancialDocument());
        }
    }
    
    public InvoiceAndCashControlDocumentPair createCashControlDocument(CustomerInvoiceDetailFixture[] invoiceDetailFixtures, CashControlDetailSpec[] cashControlDetailSpecs) throws WorkflowException {
        
        // Create an invoice
        CustomerInvoiceDocument invoice = 
            CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocumentAndReturnIt(
                    CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE, invoiceDetailFixtures, null);
        
        // Create a cash control document as well.
        CashControlDocument cashControlDocument = createAndSaveNewCashControlDocument(cashControlDetailSpecs);
        
        return new InvoiceAndCashControlDocumentPair(invoice,cashControlDocument);
    }
    
    public boolean applyFundsToPaymentApplication(CustomerInvoiceDetailFixture[] invoiceDetailFixtures, CashControlDetailSpec[] cashControlDetailSpecs, KualiDecimal[] amountsToApply) throws Exception {
        // Verify that we have an amount to apply to each invoice detail.
        if(cashControlDetailSpecs.length != amountsToApply.length) {
            throw new Exception("The number of cash control detail specs must equal the number of amounts to apply.");
        }
        
        // Create the invoice and cash control document we need to be able to test the payment application document.
        InvoiceAndCashControlDocumentPair pair = createCashControlDocument(invoiceDetailFixtures,cashControlDetailSpecs);
        CashControlDocument cashControlDocument = pair.cashControlDocument;
        CustomerInvoiceDocument invoice = pair.invoiceDocument;
        
        // The customer now has $1 credit. They owed us $1 and paid us $2.
        
        // Get convenient handles to the various relevant details.
        List<CashControlDetail> cashControlDetails = cashControlDocument.getCashControlDetails();
        List<CustomerInvoiceDetail> customerInvoiceDetails = invoice.getSourceAccountingLines();
        
        // Pick a sample invoice detail that we can apply payments to.
        CustomerInvoiceDetail sampleInvoiceDetail = customerInvoiceDetails.iterator().next();
        
        // Now try to apply too much money in receivables (cash control details) against the the outstanding balance (sample invoice detail)
        
        // counter allows us to match amounts to apply with specific invoice details
        int counter = 0;
        
        // aggregateOperationSucceeds allows us to measure the success of all operations in aggregate
        boolean aggregateOperationSucceeds = true;
        
        for(CashControlDetail cashControlDetail : cashControlDetails) {
            
            // payments are credit against the customer balance via the payment application document referenced from the cash control document.
            PaymentApplicationDocument paymentApplicationDocument = cashControlDetail.getReferenceFinancialDocument();
            
            // Create a new applied payment
            InvoicePaidApplied invoicePaidApplied = new InvoicePaidApplied();
            
            // set the document number for the invoice paid applied to the payment application document number.
            invoicePaidApplied.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());
           
            // Set the invoice paid applied ref doc number to the document number for the customer invoice document
            invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(invoice.getDocumentNumber());
            
            // Apply this payment to the sample invoice detail
            invoicePaidApplied.setInvoiceItemNumber(sampleInvoiceDetail.getInvoiceItemNumber());
            
            // Apply too much money (double the amount owed)
            // sampleInvoiceDetail.getAmount().multiply(new KualiDecimal(2))
            invoicePaidApplied.setInvoiceItemAppliedAmount(amountsToApply[counter]);
            
            invoicePaidApplied.setUniversityFiscalYear(universityDateService.getCurrentFiscalYear());
            invoicePaidApplied.setUniversityFiscalPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
            invoicePaidApplied.setPaidAppliedItemNumber(cashControlDetailSpecs.length);
            
            // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not be null
            if (invoicePaidApplied != null) {
                
                // add it to the payment application document list of applied payments
                paymentApplicationDocument.getInvoicePaidApplieds().add(invoicePaidApplied);

                // set the new applied amount for the customer invoice detail
                sampleInvoiceDetail.setAmountToBeApplied(invoicePaidApplied.getInvoiceItemAppliedAmount());
            }

            // Try to save the document
            try {
                documentService.saveDocument(paymentApplicationDocument);
            } catch(ValidationException validationException) {
                // Indicate a failures
                aggregateOperationSucceeds &= false;
            }
            
        }
        
        return aggregateOperationSucceeds;
    }
    
    public void testExactlyApplyingSucceeds() throws Exception {
        // Set our customer up to owe $1
        CustomerInvoiceDetailFixture[] invoiceDetailFixtures = 
            new CustomerInvoiceDetailFixture[] { CustomerInvoiceDetailFixture.ONE_DOLLAR_INVOICE_DETAIL };

        // Receive a payment of $1 from the Customer.
        CashControlDetailSpec[] cashControlDetailSpecs = new CashControlDetailSpec[] {
                CashControlDetailSpec.specFor(new KualiDecimal(1))                
        };
        
        KualiDecimal[] amountsToApply = new KualiDecimal[] {
                new KualiDecimal(1)
        };
        
        assertTrue(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply));
    }

    public void testUnderApplyingFailsWithoutUnapplied() throws Exception {
        // Set our customer up to owe $10
        CustomerInvoiceDetailFixture[] invoiceDetailFixtures = 
            new CustomerInvoiceDetailFixture[] { CustomerInvoiceDetailFixture.TEN_DOLLAR_INVOICE_DETAIL };

        // Receive a payment of $10 from the Customer.
        CashControlDetailSpec[] cashControlDetailSpecs = new CashControlDetailSpec[] {
                CashControlDetailSpec.specFor(new KualiDecimal(10))                
        };
        
        KualiDecimal[] amountsToApply = new KualiDecimal[] {
                new KualiDecimal(1)
        };
        
        assertFalse(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply));
    }
        
    public void testOverApplyingFails() throws Exception {
        
        // Set our customer up to owe $1
        CustomerInvoiceDetailFixture[] invoiceDetailFixtures = 
            new CustomerInvoiceDetailFixture[] { CustomerInvoiceDetailFixture.ONE_DOLLAR_INVOICE_DETAIL };

        // Receive a payment of $2 from the Customer.
        CashControlDetailSpec[] cashControlDetailSpecs = new CashControlDetailSpec[] {
                CashControlDetailSpec.specFor(new KualiDecimal(2))                
        };
        
        KualiDecimal[] amountsToApply = new KualiDecimal[] {
                new KualiDecimal(2)
        };
        
        assertFalse(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply));
    }
    
    public void testUnderApplyingFails() throws Exception {
        // TODO implement
    }
    
    protected void changeCurrentUser(UserNameFixture sessionUser) throws Exception {
        GlobalVariables.setUserSession(new UserSession(sessionUser.toString()));
    }
    
    private CashControlDocument createAndSaveNewCashControlDocument(CashControlDetailSpec[] specs) throws WorkflowException {
        List<CashControlDetailSpec> _specs = new ArrayList<CashControlDetailSpec>();
        for(CashControlDetailSpec spec : specs) {
            _specs.add(spec);
        }
        return createAndSaveNewCashControlDocument(_specs);
    }

    private CashControlDocument createAndSaveNewCashControlDocument(List<CashControlDetailSpec> specs) throws WorkflowException {
        CashControlDocument cashControlDocument = createNewCashControlDocument(specs);
        documentService.saveDocument(cashControlDocument);
        return cashControlDocument;
    }
    
    private CashControlDocument createNewCashControlDocument(CashControlDetailSpec[] specs) throws WorkflowException {
        List<CashControlDetailSpec> _specs = new ArrayList<CashControlDetailSpec>();
        for(CashControlDetailSpec spec : specs) {
            _specs.add(spec);
        }
        return createNewCashControlDocument(_specs);
    }
    
    private CashControlDocument createNewCashControlDocument(List<CashControlDetailSpec> specs) throws WorkflowException {
        CashControlDocument cashControlDocument = (CashControlDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CashControlDocument.class);// documentService.getNewDocument(CashControlDocument.class);
        cashControlDocument.getDocumentHeader().setDocumentDescription(TESTING);
        
        AccountsReceivableDocumentHeader arDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        arDocumentHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
        
        // Set the processing chart and org
        ChartOrgHolder currentUser = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.KNSAuthorizationService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        arDocumentHeader.setProcessingChartOfAccountCode(currentUser.getChartOfAccountsCode());
        arDocumentHeader.setProcessingOrganizationCode(currentUser.getOrganizationCode());
        
        ErrorMap e = GlobalVariables.getErrorMap();
        int errorCount = e.size();
        try {
            documentService.saveDocument(cashControlDocument);
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            if(e.size() != errorCount) {
                boolean breakHere = true;
            }
        }
        for(CashControlDetailSpec spec : specs) {
            CashControlDetail cashControlDetail = buildCashControlDetail(cashControlDocument,spec);
            cashControlDocumentService.addNewCashControlDetail(TESTING, cashControlDocument, cashControlDetail);
            PaymentApplicationDocument paymentApplicationDocument = cashControlDocumentService.createAndSavePaymentApplicationDocument(TESTING, cashControlDocument, cashControlDetail);
            cashControlDetail.setReferenceFinancialDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        }
        return cashControlDocument;
    }
    
    private CashControlDetail buildCashControlDetail(CashControlDocument cashControlDocument, CashControlDetailSpec spec) {
        CashControlDetail cashControlDetail = new CashControlDetail();
        cashControlDetail.setCashControlDocument(cashControlDocument);
        cashControlDetail.setDocumentNumber(cashControlDocument.getDocumentNumber());
        cashControlDetail.setCustomerNumber(spec.customerNumber);
        cashControlDetail.setCustomerPaymentMediumIdentifier(spec.customerPaymentMediumIdentifier);
        cashControlDetail.setCustomerPaymentDate(spec.customerPaymentDate);
        cashControlDetail.setFinancialDocumentLineAmount(spec.financialDocumentLineAmount);
        return cashControlDetail;
    }
    
    private class InvoiceAndCashControlDocumentPair {
        CashControlDocument cashControlDocument;
        CustomerInvoiceDocument invoiceDocument;
        
        InvoiceAndCashControlDocumentPair() {};
        InvoiceAndCashControlDocumentPair(CustomerInvoiceDocument invoice, CashControlDocument cashControl) {
            this.invoiceDocument = invoice;
            this.cashControlDocument = cashControl;
        }
    }
    
}

