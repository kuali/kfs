/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.core;

import java.util.List;

import junit.framework.Assert;

import org.kuali.kfs.fp.businessobject.SalesTax;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public class ArCoreTestUtils extends Assert {

    private static DocumentService documentService;
    private static CustomerInvoiceDetailService invoiceDetailService;

    static {
        documentService = SpringContext.getBean(DocumentService.class);
        invoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
    }

    protected static void confirmCustomerInvoiceValid(CustomerInvoiceDocument invoice, KualiDecimal expectedOpenAmount, KualiDecimal expectedTotalAmount,
            int numDiscounts, boolean isReversal, boolean isOpen, List<InvoiceDetailExpecteds> expecteds) {

        invoice.updateDiscountAndParentLineReferences();

        assertTrue("Document should be Final", invoice.getDocumentHeader().getWorkflowDocument().isFinal());
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

    protected static CustomerInvoiceDocument createFinalizedInvoiceOneLine() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentOneLine();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
        String docNumber = document.getDocumentNumber();
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }

    protected static CustomerInvoiceDocument createFinalizedInvoiceTwoLines() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentTwoLines();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
        String docNumber = document.getDocumentNumber();
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }

    protected static CustomerInvoiceDocument createFinalizedInvoiceOneLineDiscounted() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentOneLineDiscounted();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
        String docNumber = document.getDocumentNumber();
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }

    protected static CustomerInvoiceDocument createFinalizedInvoiceTwoLinesOneIsDiscounted() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentTwoLinesOneIsDiscounted();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
        String docNumber = document.getDocumentNumber();
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }

    protected static CustomerInvoiceDocument createFinalizedInvoiceTwoLinesDiscounted() throws Exception {
        CustomerInvoiceDocument document = newInvoiceDocumentTwoLinesDiscounted();
        documentService.routeDocument(document, "Unit test routing document.", null);
        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
        String docNumber = document.getDocumentNumber();
        document = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(docNumber);
        return document;
    }

    protected static CustomerInvoiceDocument newInvoiceDocumentOneLine() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL };
        return CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
    }

    protected static CustomerInvoiceDocument newInvoiceDocumentTwoLines() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL,
                CustomerInvoiceDetailFixture.FIVE_HUNDRED_DOLLAR_INVOICE_DETAIL };
        return CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
    }

    protected static CustomerInvoiceDocument newInvoiceDocumentOneLineDiscounted() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL };
        CustomerInvoiceDocument invoice =  CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
        invoice = addDiscountLine(invoice, 0, new KualiDecimal("-250.00"));
        invoice.updateDiscountAndParentLineReferences();
        return invoice;
    }

    protected static CustomerInvoiceDocument newInvoiceDocumentTwoLinesOneIsDiscounted() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL,
                CustomerInvoiceDetailFixture.FIVE_HUNDRED_DOLLAR_INVOICE_DETAIL };
        CustomerInvoiceDocument invoice = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
        invoice = addDiscountLine(invoice, 0, new KualiDecimal("-250.00"));
        invoice.updateDiscountAndParentLineReferences();
        return invoice;
    }

    protected static CustomerInvoiceDocument newInvoiceDocumentTwoLinesDiscounted() throws WorkflowException {
        CustomerInvoiceDetailFixture[] details = { CustomerInvoiceDetailFixture.ONE_THOUSAND_DOLLAR_INVOICE_DETAIL,
                CustomerInvoiceDetailFixture.FIVE_HUNDRED_DOLLAR_INVOICE_DETAIL };
        CustomerInvoiceDocument invoice = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER.createCustomerInvoiceDocument(details);
        invoice = addDiscountLine(invoice, 0, new KualiDecimal("-250.00"));
        invoice = addDiscountLine(invoice, 1, new KualiDecimal("-250.00"));
        invoice.updateDiscountAndParentLineReferences();
        return invoice;
    }

    private static CustomerInvoiceDocument addDiscountLine(CustomerInvoiceDocument invoice, int seqNumberBeingDiscounted, KualiDecimal amount) {

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

    protected static KualiDecimal fifteenHundred() { return new KualiDecimal("1500.00"); }
    protected static KualiDecimal twelveFifty()    { return new KualiDecimal("1250.00"); }
    protected static KualiDecimal oneThousand()    { return new KualiDecimal("1000.00"); }
    protected static KualiDecimal sevenFifty()     { return new KualiDecimal("750.00"); }
    protected static KualiDecimal fiveHundred()    { return new KualiDecimal("500.00"); }
    protected static KualiDecimal twoFifty()       { return new KualiDecimal("250.00"); }

}
