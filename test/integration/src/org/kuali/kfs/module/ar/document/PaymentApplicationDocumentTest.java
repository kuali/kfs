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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
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
import org.kuali.kfs.sys.service.FinancialSystemUserService;
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
        
        assertTrue(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply,null,null));
    }

    // FIXME TODO This should succeed when saving but fail when submitting.
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
        
        assertTrue(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply,null,null));
        
        assertFalse(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply,null,null,true,false));
    }
    
    public void testUnderApplyingSucceedsWithUnapplied() throws Exception {
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
        
        KualiDecimal[] unappliedAmounts = new KualiDecimal[] {
                new KualiDecimal(9)
        };

        assertTrue(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply,null,null));
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
        
        assertFalse(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply,null,null));
    }

    public void testAddingNonInvoicedLinesSucceedsWhenBalanced() throws Exception {
        
        // Set our customer up to owe $10
        CustomerInvoiceDetailFixture[] invoiceDetailFixtures = 
            new CustomerInvoiceDetailFixture[] { CustomerInvoiceDetailFixture.TEN_DOLLAR_INVOICE_DETAIL };

        // Receive a payment of $2 from the Customer.
        CashControlDetailSpec[] cashControlDetailSpecs = new CashControlDetailSpec[] {
                CashControlDetailSpec.specFor(new KualiDecimal(2))                
        };
        
        NonInvoiced[] nonInvoiceds = new NonInvoiced[] {
                buildNonInvoiced("BL","0212002","0795",new KualiDecimal(2))
        };
        
        assertTrue(applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,null,null,nonInvoiceds));
    }
    
    // ------
    // ------ Utility methods. Non test methods.
    // ------
    
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
        cashControlDocument.setUniversityFiscalYear(universityDateService.getCurrentFiscalYear());
        
        AccountsReceivableDocumentHeader arDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        arDocumentHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
        
        // Set the processing chart and org
        UserSession userSession = GlobalVariables.getUserSession();
        ChartOrgHolder organization = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(userSession.getPerson(), ArConstants.AR_NAMESPACE_CODE);
        arDocumentHeader.setProcessingChartOfAccountCode(organization.getChartOfAccountsCode());
        arDocumentHeader.setProcessingOrganizationCode(organization.getOrganizationCode());
        
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
    
    public InvoiceAndCashControlDocumentPair createCashControlDocument(CustomerInvoiceDetailFixture[] invoiceDetailFixtures, CashControlDetailSpec[] cashControlDetailSpecs) throws WorkflowException {
        
        // Create an invoice
        CustomerInvoiceDocument invoice = 
            CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocumentAndReturnIt(
                    CustomerInvoiceDocumentFixture.CIDOC_WITH_FAU_RECEIVABLE, invoiceDetailFixtures, null);
        
        // Create a cash control document as well.
        CashControlDocument cashControlDocument = createAndSaveNewCashControlDocument(cashControlDetailSpecs);
        
        return new InvoiceAndCashControlDocumentPair(invoice,cashControlDocument);
    }
    
    /**
     * This method will apply funds and save the payment application documents. 
     * It will not route or approve the documents.
     * 
     * @param invoiceDetailFixtures
     * @param cashControlDetailSpecs
     * @param amountsToApply
     * @param unappliedAmounts
     * @param nonInvoiceds
     * @return
     * @throws Exception
     */
    public boolean applyFundsToPaymentApplication(CustomerInvoiceDetailFixture[] invoiceDetailFixtures, CashControlDetailSpec[] cashControlDetailSpecs, KualiDecimal[] amountsToApply, KualiDecimal[] unappliedAmounts, NonInvoiced[] nonInvoiceds) throws Exception {
        return applyFundsToPaymentApplication(invoiceDetailFixtures,cashControlDetailSpecs,amountsToApply,unappliedAmounts,nonInvoiceds,false,false);
    }
    
    /**
     * This method will create save, route and approve payment application documents as specified.
     * 
     * @param invoiceDetailFixtures
     * @param cashControlDetailSpecs
     * @param amountsToApply
     * @param unappliedAmounts
     * @param nonInvoiceds
     * @param routeDocument
     * @param approveDocument
     * @return
     * @throws Exception
     */
    public boolean applyFundsToPaymentApplication(CustomerInvoiceDetailFixture[] invoiceDetailFixtures, CashControlDetailSpec[] cashControlDetailSpecs, KualiDecimal[] amountsToApply, KualiDecimal[] unappliedAmounts, NonInvoiced[] nonInvoiceds, boolean routeDocument, boolean approveDocument) throws Exception {
        // Verify that we have an amount to apply to each invoice detail.
        if(null != amountsToApply && cashControlDetailSpecs.length != amountsToApply.length) {
            throw new Exception("The number of cash control detail specs must equal the number of amounts to apply.");
        }
        if(null != unappliedAmounts && cashControlDetailSpecs.length != unappliedAmounts.length) {
            throw new Exception("The number of cash control detail specs must equal the number of unapplied amounts.");
        }
        if(null != nonInvoiceds && cashControlDetailSpecs.length != nonInvoiceds.length) {
            throw new Exception("The number of cash control detail specs must equal the number of non-invoiced lines.");
        }
        
        // Create the invoice and cash control document we need to be able to test the payment application document.
        InvoiceAndCashControlDocumentPair pair = createCashControlDocument(invoiceDetailFixtures,cashControlDetailSpecs);
        CashControlDocument cashControlDocument = pair.cashControlDocument;
        CustomerInvoiceDocument invoice = pair.invoiceDocument;
        
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
        
        // Each cash control detail has a reference to a payment application document which records
        // the receipt of funds. To test the payment application document we modify these payment application
        // documents according to the arguments passed into this method.
        for(CashControlDetail cashControlDetail : cashControlDetails) {
            
            // payments are credit against the customer balance via the payment application document referenced from the cash control document.
            PaymentApplicationDocument paymentApplicationDocument = cashControlDetail.getReferenceFinancialDocument();
            
            // ------ Set invoice paid applieds
            
            // Make sure we've got an amount to apply directly to this document
            if(null != amountsToApply && counter < amountsToApply.length && null != amountsToApply[counter]) {
                // Create a new applied payment
                // Applying one big payment is just as good as applying a bunch of smaller payments from a testing perspective
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
                    //sampleInvoiceDetail.setAmountToBeApplied(invoicePaidApplied.getInvoiceItemAppliedAmount());
                }
            }
            
            // ------ Set the unapplied amount
            
            // Make sure we've got an unapplied amount specified for this detail
            if(null != unappliedAmounts && counter < unappliedAmounts.length && null != unappliedAmounts[counter]) {
                NonAppliedHolding nonAppliedHolding = new NonAppliedHolding();
                nonAppliedHolding.setFinancialDocumentLineAmount(unappliedAmounts[counter]);
                nonAppliedHolding.setReferenceFinancialDocumentNumber(paymentApplicationDocument.getDocumentNumber());
                paymentApplicationDocument.setNonAppliedHolding(nonAppliedHolding);
            }
            
            // ------ Set the non-invoiced amount
            
            int nonInvoicedLineCounter = 1;
            if(null != nonInvoiceds && counter < nonInvoiceds.length && null != nonInvoiceds[counter]) {
                NonInvoiced nonInvoiced = nonInvoiceds[counter];
                nonInvoiced.setFinancialDocumentPostingYear(paymentApplicationDocument.getPostingYear());
                nonInvoiced.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());
                nonInvoiced.setFinancialDocumentLineNumber(nonInvoicedLineCounter++);
                paymentApplicationDocument.getNonInvoiceds().add(nonInvoiced);
            }
            
            // Try to save the document
            try {
                documentService.saveDocument(paymentApplicationDocument);
            } catch(ValidationException validationException) {
                // Indicate a failures
                aggregateOperationSucceeds &= false;
            }
            
            // Try to route the document
            if(routeDocument) {
                try {
                    documentService.routeDocument(paymentApplicationDocument, "Unit tests", new ArrayList());
                } catch(ValidationException validationException) {
                    // Indicate a failures
                    aggregateOperationSucceeds &= false;
                }
            }
            
            // Try to approve the document
            if(approveDocument) {
                try {
                    documentService.approveDocument(paymentApplicationDocument, "Unit tests", new ArrayList());
                } catch(ValidationException validationException) {
                    // Indicate a failures
                    aggregateOperationSucceeds &= false;
                }
            }
        }
        
        return aggregateOperationSucceeds;
    }
    
    private NonInvoiced buildNonInvoiced(String chartOfAccountsCode, String accountNumber, String financialObjectCode, KualiDecimal financialDocumentLineAmount) {
        NonInvoiced nonInvoiced = new NonInvoiced();
        nonInvoiced.setChartOfAccountsCode(chartOfAccountsCode);
        nonInvoiced.setAccountNumber(accountNumber);
        nonInvoiced.setFinancialObjectCode(financialObjectCode);
        nonInvoiced.setFinancialDocumentLineAmount(financialDocumentLineAmount);
        return nonInvoiced;
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
    
    private class NonInvoicedLineSpec {
        String chartCode;
        String accountNumber;
        String objectCode;
        KualiDecimal amount;
        
        NonInvoicedLineSpec() {}
        NonInvoicedLineSpec(String chartCode, String accountNumber, String objectCode, KualiDecimal amount) {
            this.chartCode = chartCode;
            this.accountNumber = accountNumber;
            this.objectCode = objectCode;
            this.amount = amount;
        }
        
    }
    
}

