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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.DocumentService;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class ReportingLoadTest extends KualiTestBase {

    private static final int INVOICES_TO_CREATE = 2;
    private static final String PRINT_SETTING = ArConstants.PrintInvoiceOptions.PRINT_BY_PROCESSING_ORG;
    private static final String INITIATOR = "khuntley";
    private static final int[] INVOICE_AGES = { -5, -18, -35, -65, -95, -125 };

    private static final String AGING_RPT_LOOKUPABLE_SERVICE = "arCustomerAgingReportLookupable";
    private static final String AGING_RPT_OPTION = "PROCESSING ORGANIZATION";
    private static final String AGING_RPT_PROCESSING_OR_BILLING_CHART = "UA";
    private static final String AGING_RPT_ORG = "VPIT";
    private static final String AGING_RPT_ACCOUNT_CHART = "BL";
    private static final String AGING_RPT_ACCOUNT = "1031400";

    private DocumentService documentService;
    private AccountsReceivableReportService reportService;
    private ConfigurationService kualiConfigService;
    private DateTimeService dateTimeService;

    private List<String> invoicesCreated;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        kualiConfigService = SpringContext.getBean(ConfigurationService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);

        invoicesCreated = new ArrayList<String>();

    }

    public void testNothingToStopTestFailureReport() {
        // do nothing here, this is just to stop the continuous build system
        // from reporting that a test does nothing.
        assertTrue(true);
    }

    /**
     *
     * Use this method to setup a bunch of invoices that are ready to print.
     *
     * This method doesnt actually print anything, though, so use this if you're perf
     * testing against the web app and just need some test data.
     *
     * You'll have to re-run this after each print, as it clears the print flag
     * when you print the invoices.
     *
     * NOTE - add 'test' to the method name to make it junit-able
     *
     * @throws Exception
     */
    public void createManyInvoicesForPrintTesting() throws Exception {
        createManyInvoiceReadyForAgingReport();
    }

    /**
     *
     * Use this method to test the performance of the Customer Report,
     * which creates a multi-page PDF.
     *
     * @throws Exception
     */
    public void runCustomerOpenReport() throws Exception {
        createManyInvoiceReadyForAgingReport();

        //  test the following line for perf
        List<CustomerStatementResultHolder> reports = reportService.generateStatementByBillingOrg(AGING_RPT_PROCESSING_OR_BILLING_CHART, AGING_RPT_ORG, "S", "N");
        assertNotNull("Reports list should not be null.", reports);
        assertFalse("Reports should not be empty.", reports.isEmpty());
    }

    /**
     *
     * Use this method to actually seed a bunch of test data, and then run the aging
     * report queries.
     *
     * Use this when you dont want to test against the GUI, and just want to profile the
     * app directly, via a unit test.
     *
     * NOTE - add 'test' to the method name to make it junit-able
     *
     * @throws Exception
     */
    public void customerAgingReport() throws Exception {
        createManyInvoiceReadyForAgingReport();

        Map<String,String> fieldValues = new HashMap<String,String>();
        fieldValues.put(KFSConstants.BACK_LOCATION, "");
        fieldValues.put(KFSConstants.DOC_FORM_KEY, "");
        fieldValues.put(ArPropertyConstants.CustomerAgingReportFields.REPORT_OPTION, AGING_RPT_OPTION);
        fieldValues.put(ArPropertyConstants.CustomerAgingReportFields.ACCOUNT_CHART_CODE, AGING_RPT_ACCOUNT_CHART);
        fieldValues.put(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, AGING_RPT_ACCOUNT);
        fieldValues.put(ArPropertyConstants.CustomerAgingReportFields.PROCESSING_OR_BILLING_CHART_CODE, AGING_RPT_PROCESSING_OR_BILLING_CHART);
        fieldValues.put(KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME, AGING_RPT_ORG);
        java.util.Date today = dateTimeService.getCurrentDate();
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        fieldValues.put(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE, format.format(today));

        LookupableHelperService lookupableHelperService = LookupableSpringContext.getLookupable(AGING_RPT_LOOKUPABLE_SERVICE).getLookupableHelperService();

        //CustomerAgingReportLookupableHelperServiceImpl arLookupableHelperService =
        //    (CustomerAgingReportLookupableHelperServiceImpl) lookupableHelperService;

        //TODO Performance test this method call
        List<? extends BusinessObject> searchResults = lookupableHelperService.getSearchResults(fieldValues);

        assertTrue("Search results should not be null.", searchResults != null);
        assertTrue("Search results should not be empty.", !searchResults.isEmpty());

    }

    /**
     *
     * Use this method to actually seed a bunch of test data, and then run the customer
     * invoices.  It will generate the PDFs using Jasper, and save the resulting concatenated
     * PDF to the ar reports directory.  It is a very close simulation to what the web-app
     * does, except when the web-app concatenates the PDF's, it buffers and streams them
     * to the client.
     *
     * Use this when you dont want to test against the GUI, and just want to profile the
     * app directly, via a unit test.
     *
     * NOTE - add 'test' to the method name to make it junit-able
     *
     * @throws Exception
     */
    public void customerInvoiceReportPrinting() throws Exception {

        // this step is required for all the reporting/printing runs
        createManyInvoicesReadyForPrinting();

        //TODO Performance test these three lines
        List<File> reports = reportService.generateInvoicesByInitiator(INITIATOR, null);
        assertTrue("List of reports should not be empty.", !reports.isEmpty());
        concatenateReportsIntoOnePdf(reports);

    }

    private String getOutputPathAndFileName() {
        String reportsDirectory = kualiConfigService.getPropertyValueAsString(KFSConstants.REPORTS_DIRECTORY_KEY);
        StringBuilder fileName = new StringBuilder();
        fileName.append(reportsDirectory + File.separator);
        fileName.append(ArConstants.Lockbox.LOCKBOX_REPORT_SUBFOLDER + File.separator);
        fileName.append("PERFTESTING-ARINVOICES-" + INITIATOR + ".pdf");
        return fileName.toString();
    }

    private void concatenateReportsIntoOnePdf(List<File> reports) throws Exception {

        FileOutputStream fileOutputStream = new FileOutputStream(getOutputPathAndFileName());

        int pageOffset = 0;
        ArrayList master = new ArrayList();
        int f = 0;
        Document document = null;
        PdfCopy  writer = null;
        for (Iterator<File> itr = reports.iterator(); itr.hasNext();) {
            // we create a reader for a certain document
            String reportName = itr.next().getAbsolutePath();
            PdfReader reader = new PdfReader(reportName);
            reader.consolidateNamedDestinations();
            // we retrieve the total number of pages
            int n = reader.getNumberOfPages();
            List bookmarks = SimpleBookmark.getBookmark(reader);
            if (bookmarks != null) {
                if (pageOffset != 0) {
                    SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
                }
                master.addAll(bookmarks);
            }
            pageOffset += n;

            if (f == 0) {
                // step 1: creation of a document-object
                document = new Document(reader.getPageSizeWithRotation(1));
                // step 2: we create a writer that listens to the document
                writer = new PdfCopy(document, fileOutputStream);
                // step 3: we open the document
                document.open();
            }
            // step 4: we add content
            PdfImportedPage page;
            for (int i = 0; i < n; ) {
                ++i;
                page = writer.getImportedPage(reader, i);
                writer.addPage(page);
            }
            writer.freeReader(reader);
            f++;
        }

        if (!master.isEmpty()) {
            writer.setOutlines(master);
        }

        // step 5: we close the document
        document.close();

        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private void createManyInvoiceReadyForAgingReport() throws Exception {

        int typeOfInvoice = 1;
        int ageOfInvoice = 0;
        for (int i = 0; i < INVOICES_TO_CREATE; i++) {

            //  create a scenario invoice
            CustomerInvoiceDocument document = createScenarioInvoice(typeOfInvoice);
            invoicesCreated.add(document.getDocumentNumber());

            //  age the invoice
            document.setBillingDate(dateXDaysAgo(ageOfInvoice));

            //  route it, and wait for it go to final
            documentService.blanketApproveDocument(document, "BlanketApproved by performance testing script.", null);
            WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());

            //  increement or reset the scenario type
            if (typeOfInvoice == 5) {
                typeOfInvoice = 1;
            }
            else {
                typeOfInvoice++;
            }

            //  increement or reset the age index
            if (ageOfInvoice == INVOICE_AGES.length - 1) {
                ageOfInvoice = 0;
            }
            else {
                ageOfInvoice++;
            }
        }
    }

    private java.sql.Date dateXDaysAgo(int daysAgo) {
        return new java.sql.Date(DateUtils.addDays(dateTimeService.getCurrentSqlDate(), INVOICE_AGES[daysAgo]).getTime());
    }

    private void createManyInvoicesReadyForPrinting() throws Exception {

        int typeOfInvoice = 1;
        for (int i = 0; i < INVOICES_TO_CREATE; i++) {

            //  create a scenario invoice
            CustomerInvoiceDocument document = createScenarioInvoice(typeOfInvoice);
            invoicesCreated.add(document.getDocumentNumber());

            //  route it, and wait for it go to final
            documentService.blanketApproveDocument(document, "BlanketApproved by performance testing script.", null);
            WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());

            //  increement or reset the scenario type
            if (typeOfInvoice == 5) {
                typeOfInvoice = 1;
            }
            else {
                typeOfInvoice++;
            }
        }
    }

    private CustomerInvoiceDocument createScenarioInvoice(int scenarioNumber) throws Exception {
        CustomerInvoiceDocument document;
        switch (scenarioNumber) {
            case 1:
                document = ArCoreTestUtils.newInvoiceDocumentOneLine();
                document.setPrintInvoiceIndicator(PRINT_SETTING);
                break;
            case 2:
                document = ArCoreTestUtils.newInvoiceDocumentTwoLines();
                document.setPrintInvoiceIndicator(PRINT_SETTING);
                break;
            case 3:
                document = ArCoreTestUtils.newInvoiceDocumentOneLineDiscounted();
                document.setPrintInvoiceIndicator(PRINT_SETTING);
                break;
            case 4:
                document = ArCoreTestUtils.newInvoiceDocumentTwoLinesOneIsDiscounted();
                document.setPrintInvoiceIndicator(PRINT_SETTING);
                break;
            case 5:
                document = ArCoreTestUtils.newInvoiceDocumentTwoLinesDiscounted();
                break;
            default:
                throw new RuntimeException("An invalid scenarioNumber was passed in.");
        }
        document.setPrintInvoiceIndicator(PRINT_SETTING);
        return document;
    }

}
