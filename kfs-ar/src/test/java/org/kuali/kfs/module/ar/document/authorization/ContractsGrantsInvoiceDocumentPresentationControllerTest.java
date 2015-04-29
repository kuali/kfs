package org.kuali.kfs.module.ar.document.authorization;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;

import java.util.Calendar;

import static org.junit.Assert.*;

public class ContractsGrantsInvoiceDocumentPresentationControllerTest {

    @Test
    public void testCannotCorrectAlreadyCorrectedDocument() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = new ContractsGrantsInvoiceDocumentPresentationController() {
            @Override
            protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
                return true;
            }
        };
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setCorrectedByDocumentId("hi there, i am testing");
        assertFalse(presentationController.canErrorCorrect(null, documentHeader, false, null));
    }

    @Test
    public void testCannotCorrectReversedInvoice() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = new ContractsGrantsInvoiceDocumentPresentationController() {
            @Override
            protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
                return true;
            }
        };
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setCorrectedByDocumentId("");
        assertFalse(presentationController.canErrorCorrect(null, documentHeader, true, null));
    }

    @Test
    public void testCannotCorrectNonFinalDocument() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = new ContractsGrantsInvoiceDocumentPresentationController() {
            @Override
            protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
                return false;
            }
        };
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setCorrectedByDocumentId("");
        assertFalse(presentationController.canErrorCorrect(null, documentHeader, false, null));
    }

    @Test
    public void testCanCorrectFinalDocumentWithAppliedInvoices() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = new ContractsGrantsInvoiceDocumentPresentationController() {
            @Override
            protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
                return true;
            }
        };
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setCorrectedByDocumentId("");
        assertTrue(presentationController.canErrorCorrect(null, documentHeader, false, new DateTime()));
    }

    @Test
    public void testCanCorrectFinalDocumentWithAppliedInvoicesNullDate() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = new ContractsGrantsInvoiceDocumentPresentationController() {
            @Override
            protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
                return true;
            }
        };
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setCorrectedByDocumentId("");
        assertTrue(presentationController.canErrorCorrect(null, documentHeader, false, null));
    }

    @Test
    public void testCannotCorrectPriorYearInvoice() {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = new ContractsGrantsInvoiceDocumentPresentationController() {
            @Override
            protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
                return true;
            }
        };
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setCorrectedByDocumentId("");
        assertFalse(presentationController.canErrorCorrect(null, documentHeader, false, new DateTime().minusDays(366)));
    }
}