/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.core;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.fp.businessobject.SalesTax;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class OpenAmountsTest extends KualiTestBase {

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private CashControlDocumentService cashControlDocumentService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private CustomerInvoiceDetailService invoiceDetailService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        universityDateService = SpringContext.getBean(UniversityDateService.class);
        invoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
    }

    public void testInvoiceOpenAmount_NoDiscounts() throws Exception {

        CustomerInvoiceDocument document = createFinalizedInvoiceOneLine();
        List<DetailExpecteds> expecteds = new ArrayList<DetailExpecteds>();
        expecteds.add(new DetailExpecteds(oneThousand(), oneThousand(), oneThousand(), false, 0));
        runCustomerInvoiceDocument(document, oneThousand(), oneThousand(), 0, false, true, expecteds);
        
        document = createFinalizedInvoiceTwoLines();
        expecteds = new ArrayList<DetailExpecteds>();
        expecteds.add(0, new DetailExpecteds(oneThousand(), oneThousand(), oneThousand(), false, 0));
        expecteds.add(1, new DetailExpecteds(fiveHundred(), fiveHundred(), fiveHundred(), false, 0));
        runCustomerInvoiceDocument(document, fifteenHundred(), fifteenHundred(), 0, false, true, expecteds);
        
    }
    
    public void testInvoiceOpenAmount_WithDiscounts() throws Exception {

        CustomerInvoiceDocument document = createFinalizedInvoiceOneLineDiscounted();
        List<DetailExpecteds> expecteds = new ArrayList<DetailExpecteds>();
        expecteds.add(new DetailExpecteds(oneThousand(), sevenFifty(), sevenFifty(), true, 1, Arrays.asList(twoFifty())));
        runCustomerInvoiceDocument(document, sevenFifty(), sevenFifty(), 1, false, true, expecteds);
        
        document = createFinalizedInvoiceTwoLinesOneIsDiscounted();
        expecteds = new ArrayList<DetailExpecteds>();
        expecteds.add(new DetailExpecteds(oneThousand(), sevenFifty(), sevenFifty(), true, 1, Arrays.asList(twoFifty())));
        expecteds.add(new DetailExpecteds(fiveHundred(), fiveHundred(), fiveHundred(), false, 0));
        runCustomerInvoiceDocument(document, twelveFifty(), twelveFifty(), 1, false, true, expecteds);
        
        document = createFinalizedInvoiceTwoLinesDiscounted();
        expecteds = new ArrayList<DetailExpecteds>();
        expecteds.add(new DetailExpecteds(oneThousand(), sevenFifty(), sevenFifty(), true, 1, Arrays.asList(twoFifty())));
        expecteds.add(new DetailExpecteds(fiveHundred(), twoFifty(), twoFifty(), true, 1, Arrays.asList(twoFifty())));
        runCustomerInvoiceDocument(document, oneThousand(), oneThousand(), 2, false, true, expecteds);
        
    }
    
    private void runCustomerInvoiceDocument(CustomerInvoiceDocument invoice, KualiDecimal expectedOpenAmount, KualiDecimal expectedTotalAmount, 
                                            int numDiscounts, boolean isReversal, boolean isOpen, List<DetailExpecteds> expecteds) {
        
        invoice.updateDiscountAndParentLineReferences();
        
        assertTrue("Document should be Final", invoice.getDocumentHeader().getWorkflowDocument().stateIsFinal());
        assertEquals("OpenAmount wrong.", expectedOpenAmount, invoice.getOpenAmount());
        assertEquals("TotalAmount wrong.", expectedTotalAmount, invoice.getTotalDollarAmount());
        assertEquals("Discounts wrong size.", numDiscounts, invoice.getDiscounts().size());
        assertEquals("IsReversal wrong.", isReversal, invoice.isInvoiceReversal());
        assertEquals("IsOpen wrong.", isOpen, invoice.isOpenInvoiceIndicator());
        
        int x = 0;
        for (CustomerInvoiceDetail detail : invoice.getCustomerInvoiceDetailsWithoutDiscounts()) {
            
            assertEquals("Amount wrong.", expecteds.get(x).getAmount(), detail.getAmount());
            assertEquals("AmountDiscounted wrong.", expecteds.get(x).getAmountDiscounted(), detail.getAmountDiscounted());

            assertEquals("AmountOpen wrong.", expecteds.get(x).getAmountOpen(), detail.getAmountOpen());
            assertEquals("WriteoffAmount wrong.", expecteds.get(x).getAmountOpen(), detail.getWriteoffAmount());
            
            //  should always return zero, since we're only looking at NON discount lines
            assertEquals("AmountToApply should always be zero.", KualiDecimal.ZERO, detail.getAmountToApply());
            
            assertEquals("Details tested here should never be discount lines.", false, detail.isDiscountLine());
            assertEquals("Details tested here should never be discount lines.", expecteds.get(x).isDiscounted(), detail.isDiscountLineParent());
            assertEquals("Should be non-null when discounted lines.", expecteds.get(x).isDiscounted(), (detail.getDiscountCustomerInvoiceDetail() != null));
            assertNull("Should always be null since we only have non-discount lines.", detail.getParentDiscountCustomerInvoiceDetail());
            assertEquals("", expecteds.get(x).isDiscounted(), (detail.getInvoiceItemDiscountLineNumber() != null));
            
            List<InvoicePaidApplied> paidApplieds = detail.getMatchingInvoicePaidAppliedsMatchingAnyDocumentFromDatabase();
            List<KualiDecimal> expectedApplieds = expecteds.get(x).getPaidAppliedAmounts();
            assertEquals("PaidApplieds should be the right size.", expectedApplieds.size(), paidApplieds.size());
            
            int y = 0;
            for (KualiDecimal expectedAmount : expectedApplieds) {
                assertEquals("", expectedAmount, paidApplieds.get(y).getInvoiceItemAppliedAmount());
                y += 1;
            }
            
            x += 1;
        }
    }
    
    private static KualiDecimal fifteenHundred() { return new KualiDecimal("1500.00"); }
    private static KualiDecimal twelveFifty()    { return new KualiDecimal("1250.00"); }
    private static KualiDecimal oneThousand()    { return new KualiDecimal("1000.00"); }
    private static KualiDecimal sevenFifty()     { return new KualiDecimal("750.00"); }
    private static KualiDecimal fiveHundred()    { return new KualiDecimal("500.00"); }
    private static KualiDecimal twoFifty()       { return new KualiDecimal("250.00"); }

    private class DetailExpecteds {
        
        private KualiDecimal amount;
        private KualiDecimal amountDiscounted;
        private KualiDecimal amountOpen;
        private boolean discounted;
        private int paidAppliedsCount;
        private List<KualiDecimal> paidAppliedAmounts;
        
        public DetailExpecteds() {
            paidAppliedAmounts = new ArrayList<KualiDecimal>();
        }
        
        public DetailExpecteds(KualiDecimal amount, KualiDecimal amountDiscounted, KualiDecimal amountOpen, boolean discounted, int paidAppliedsCount) {
            this();
            this.amount = amount;
            this.amountDiscounted = amountDiscounted;
            this.amountOpen = amountOpen;
            this.discounted = discounted;
            this.paidAppliedsCount = paidAppliedsCount;
        }
        
        public DetailExpecteds(KualiDecimal amount, KualiDecimal amountDiscounted, KualiDecimal amountOpen, boolean discounted, int paidAppliedsCount, List<KualiDecimal> paidAppliedAmounts) {
            this(amount, amountDiscounted, amountOpen, discounted, paidAppliedsCount);
            this.paidAppliedAmounts.addAll(paidAppliedAmounts);
        }

        public KualiDecimal getAmount() {
            return amount;
        }

        public void setAmount(KualiDecimal amount) {
            this.amount = amount;
        }

        public KualiDecimal getAmountDiscounted() {
            return amountDiscounted;
        }

        public void setAmountDiscounted(KualiDecimal amountDiscounted) {
            this.amountDiscounted = amountDiscounted;
        }

        public KualiDecimal getAmountOpen() {
            return amountOpen;
        }

        public void setAmountOpen(KualiDecimal amountOpen) {
            this.amountOpen = amountOpen;
        }

        public boolean isDiscounted() {
            return discounted;
        }

        public void setDiscounted(boolean discounted) {
            this.discounted = discounted;
        }

        public int getPaidAppliedsCount() {
            return paidAppliedsCount;
        }

        public void setPaidAppliedsCount(int paidAppliedsCount) {
            this.paidAppliedsCount = paidAppliedsCount;
        }

        public List<KualiDecimal> getPaidAppliedAmounts() {
            return paidAppliedAmounts;
        }

        public void setPaidAppliedAmounts(List<KualiDecimal> paidAppliedAmounts) {
            this.paidAppliedAmounts = paidAppliedAmounts;
        }
        
    }
    
    private CustomerInvoiceDocument createFinalizedInvoiceOneLine() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentOneLine();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForStatusChange(10, document.getDocumentHeader().getWorkflowDocument(), "F");
        String docNumber = document.getDocumentNumber();
        document = null;
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }
    
    private CustomerInvoiceDocument createFinalizedInvoiceTwoLines() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentTwoLines();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForStatusChange(10, document.getDocumentHeader().getWorkflowDocument(), "F");
        String docNumber = document.getDocumentNumber();
        document = null;
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }
    
    private CustomerInvoiceDocument createFinalizedInvoiceOneLineDiscounted() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentOneLineDiscounted();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForStatusChange(10, document.getDocumentHeader().getWorkflowDocument(), "F");
        String docNumber = document.getDocumentNumber();
        document = null;
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }
    
    private CustomerInvoiceDocument createFinalizedInvoiceTwoLinesOneIsDiscounted() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentTwoLinesOneIsDiscounted();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForStatusChange(10, document.getDocumentHeader().getWorkflowDocument(), "F");
        String docNumber = document.getDocumentNumber();
        document = null;
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }
    
    private CustomerInvoiceDocument createFinalizedInvoiceTwoLinesDiscounted() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentTwoLinesDiscounted();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForStatusChange(10, document.getDocumentHeader().getWorkflowDocument(), "F");
        String docNumber = document.getDocumentNumber();
        document = null;
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }
    
    private CustomerInvoiceDocument newInvoiceDocumentOneLine() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL };
        return CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
    }
    
    private CustomerInvoiceDocument newInvoiceDocumentTwoLines() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL, 
                CustomerInvoiceDetailFixture.FIVE_HUNDRED_DOLLAR_INVOICE_DETAIL };
        return CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
    }

    private CustomerInvoiceDocument newInvoiceDocumentOneLineDiscounted() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL };
        CustomerInvoiceDocument invoice =  CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
        invoice = addDiscountLine(invoice, 0, new KualiDecimal("-250.00"));
        invoice.updateDiscountAndParentLineReferences();
        return invoice;
    }
    
    private CustomerInvoiceDocument newInvoiceDocumentTwoLinesOneIsDiscounted() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL, 
                CustomerInvoiceDetailFixture.FIVE_HUNDRED_DOLLAR_INVOICE_DETAIL };
        CustomerInvoiceDocument invoice = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
        invoice = addDiscountLine(invoice, 0, new KualiDecimal("-250.00"));
        invoice.updateDiscountAndParentLineReferences();
        return invoice;
    }

    private CustomerInvoiceDocument newInvoiceDocumentTwoLinesDiscounted() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL, 
                CustomerInvoiceDetailFixture.FIVE_HUNDRED_DOLLAR_INVOICE_DETAIL };
        CustomerInvoiceDocument invoice = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
        invoice = addDiscountLine(invoice, 0, new KualiDecimal("-250.00"));
        invoice = addDiscountLine(invoice, 1, new KualiDecimal("-250.00"));
        invoice.updateDiscountAndParentLineReferences();
        return invoice;
    }

    private CustomerInvoiceDocument addDiscountLine(CustomerInvoiceDocument invoice, int seqNumberBeingDiscounted, KualiDecimal amount) {
        
        CustomerInvoiceDetail lineBeingDiscounted = (CustomerInvoiceDetail) invoice.getSourceAccountingLine(seqNumberBeingDiscounted);
        CustomerInvoiceDetail discountingLine = invoiceDetailService.getDiscountCustomerInvoiceDetailForCurrentYear(lineBeingDiscounted, invoice);
        discountingLine.setAmount(amount);
        discountingLine.setInvoiceItemUnitPrice(amount.bigDecimalValue());
        discountingLine.refreshNonUpdateableReferences();
        
        // add it to the document
        invoice.addSourceAccountingLine(discountingLine);

        // add PK fields to sales tax if needed
        if (discountingLine.isSalesTaxRequired()) {
            SalesTax salesTax = discountingLine.getSalesTax();
            if (ObjectUtils.isNotNull(salesTax)) {
                salesTax.setDocumentNumber(discountingLine.getDocumentNumber());
                salesTax.setFinancialDocumentLineTypeCode(discountingLine.getFinancialDocumentLineTypeCode());
                salesTax.setFinancialDocumentLineNumber(discountingLine.getSequenceNumber());
            }
        }

        // Update the doc total
        if (invoice instanceof AmountTotaling) {
            ((FinancialSystemDocumentHeader) invoice.getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) invoice).getTotalDollarAmount());
        }
        
        // also set parent customer invoice detail line to have discount line seq number
        lineBeingDiscounted.setInvoiceItemDiscountLineNumber(discountingLine.getSequenceNumber());
        
        return invoice;
    }
    
}
