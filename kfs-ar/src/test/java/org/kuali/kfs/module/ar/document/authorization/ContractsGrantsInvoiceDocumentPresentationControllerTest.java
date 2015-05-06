package org.kuali.kfs.module.ar.document.authorization;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.service.UniversityDateService;

import java.sql.Date;

import static org.junit.Assert.*;

public class ContractsGrantsInvoiceDocumentPresentationControllerTest {

    @Test
    public void testCannotCorrectAlreadyCorrectedDocument() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = getContractsGrantsInvoiceDocumentPresentationController(true);
        validateCanErrorCorrect(presentationController, null, "hi there, i am testing", false, false);
    }

    @Test
    public void testCannotCorrectReversedInvoice() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = getContractsGrantsInvoiceDocumentPresentationController(true);
        validateCanErrorCorrect(presentationController, null, "", true, false);
    }

    @Test
    public void testCannotCorrectNonFinalDocument() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = getContractsGrantsInvoiceDocumentPresentationController(false);
        validateCanErrorCorrect(presentationController, null, "", false, false);
    }

    @Test
    public void testCanCorrectFinalDocumentWithAppliedInvoices() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = getContractsGrantsInvoiceDocumentPresentationController(true);
        validateCanErrorCorrect(presentationController, null, "", false, true);
    }

    @Test
    public void testCanCorrectFinalDocumentWithAppliedInvoicesNullDate() throws Exception {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = getContractsGrantsInvoiceDocumentPresentationController(true);
        validateCanErrorCorrect(presentationController, null, "", false, true);
    }

    @Test
    public void testCannotCorrectPriorYearInvoice() {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = getContractsGrantsInvoiceDocumentPresentationController(true);
        validateCanErrorCorrect(presentationController, "2014-06-25", "", false, false);
    }

    @Test
    public void testCanCorrectEarlierPriorYearInvoice() {
        ContractsGrantsInvoiceDocumentPresentationController presentationController = getContractsGrantsInvoiceDocumentPresentationController(true);
        validateCanErrorCorrect(presentationController, "2014-07-01", "", false, true);
    }

    private ContractsGrantsInvoiceDocumentPresentationController getContractsGrantsInvoiceDocumentPresentationController(final boolean docFinalNoAppliedAmounts) {
        final ContractsGrantsInvoiceDocumentPresentationController contractsGrantsInvoiceDocumentPresentationController = new ContractsGrantsInvoiceDocumentPresentationController() {
            @Override
            protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
                return docFinalNoAppliedAmounts;
            }
        };

        UniversityDateService fakeUniversityDateService = new FiscalYear2015UniversityDateService();
        contractsGrantsInvoiceDocumentPresentationController.setUniversityDateService(fakeUniversityDateService);

        return contractsGrantsInvoiceDocumentPresentationController;
    }

    private class FiscalYear2015UniversityDateService implements UniversityDateService {

        @Override
        public UniversityDate getCurrentUniversityDate() {
            UniversityDate universityDate = new UniversityDate();
            universityDate.setUniversityDate(Date.valueOf("2014-07-02"));
            return universityDate;
        }

        @Override
        public Integer getFiscalYear(java.util.Date date) {
            return 2015;
        }

        @Override
        public java.util.Date getFirstDateOfFiscalYear(Integer fiscalYear) {
            return Date.valueOf("2014-07-01");
        }

        @Override
        public java.util.Date getLastDateOfFiscalYear(Integer fiscalYear) {
            return Date.valueOf("2015-06-30");
        }

        @Override
        public Integer getCurrentFiscalYear() {
            return 2015;
        }

    }

    private void validateCanErrorCorrect(ContractsGrantsInvoiceDocumentPresentationController presentationController, String dateApprovedString, String correctedByDocumentId, boolean invoiceReversal, boolean expectedResult) {
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setCorrectedByDocumentId(correctedByDocumentId);

        DateTime dateApproved = null;
        if (dateApprovedString != null) {
            dateApproved = new DateTime(Date.valueOf(dateApprovedString));
        }
        assertEquals("canErrorCorrect returned unexpected value ", expectedResult, presentationController.canErrorCorrect(null, documentHeader, invoiceReversal, dateApproved));
    }

}