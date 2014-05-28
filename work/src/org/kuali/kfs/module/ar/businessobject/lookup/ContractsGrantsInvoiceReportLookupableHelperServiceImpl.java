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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for the Contracts and Grants Invoice Report.
 */
public class ContractsGrantsInvoiceReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceReportLookupableHelperServiceImpl.class);

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsInvoiceReport> displayList = new ArrayList<ContractsGrantsInvoiceReport>();
        Collection<ContractsGrantsInvoiceDocument> openCGInvoiceDocs = contractsGrantsInvoiceDocumentService.getAllOpenContractsGrantsInvoiceDocuments(true);

        String invoiceReportOption = lookupForm.getFields().get(ArConstants.INVOICE_REPORT_OPTION);

        java.util.Date today = new java.util.Date();
        Date sqlToday = new java.sql.Date(today.getTime());

        // build search result fields
        for (ContractsGrantsInvoiceDocument openCGInvoiceDoc : openCGInvoiceDocs) {
            if (invoiceReportOption.equals(ArConstants.PAST_DUE_INVOICES) && openCGInvoiceDoc.getInvoiceDueDate().before(today)) {
                // check if due date is not passed then not add to result
                continue;
            }

            FinancialSystemDocumentHeader documentHeader = (FinancialSystemDocumentHeader) openCGInvoiceDoc.getDocumentHeader();
            ContractsGrantsInvoiceReport cgInvoiceReport = new ContractsGrantsInvoiceReport();

            WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();

            String documentNumber = ObjectUtils.isNull(documentHeader) ? "" : documentHeader.getDocumentNumber();
            cgInvoiceReport.setDocumentNumber(openCGInvoiceDoc.getDocumentNumber());
            cgInvoiceReport.setProposalNumber(openCGInvoiceDoc.getProposalNumber());
            cgInvoiceReport.setInvoiceType(workflowDocument.getDocumentTypeName());

            Date docCreateDate = workflowDocument.getDateCreated().toDate();
            cgInvoiceReport.setInvoiceDate(new java.sql.Date(docCreateDate.getTime()));
            cgInvoiceReport.setInvoiceDueDate(openCGInvoiceDoc.getInvoiceDueDate());
            if (openCGInvoiceDoc.isOpenInvoiceIndicator()) {
                cgInvoiceReport.setOpenInvoiceIndicator(ArConstants.ReportsConstants.INVOICE_INDICATOR_OPEN);
            }
            else {
                cgInvoiceReport.setOpenInvoiceIndicator(ArConstants.ReportsConstants.INVOICE_INDICATOR_CLOSE);
            }
            cgInvoiceReport.setCustomerNumber(openCGInvoiceDoc.getAccountsReceivableDocumentHeader().getCustomerNumber());
            cgInvoiceReport.setCustomerName(openCGInvoiceDoc.getAccountsReceivableDocumentHeader().getCustomer().getCustomerName());
            cgInvoiceReport.setInvoiceAmount(documentHeader.getFinancialDocumentTotalAmount());

            // get payment amount
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("financialDocumentReferenceInvoiceNumber", documentNumber);
            Collection<InvoicePaidApplied> paidAppliedInvoices = businessObjectService.findMatching(InvoicePaidApplied.class, criteria);

            KualiDecimal paymentAmount = KualiDecimal.ZERO;
            for (InvoicePaidApplied invoicePaidApplied : paidAppliedInvoices) {
                paymentAmount = paymentAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
            }

            cgInvoiceReport.setPaymentAmount(paymentAmount);
            cgInvoiceReport.setRemainingAmount(documentHeader.getFinancialDocumentTotalAmount().subtract(paymentAmount));

            // calculate ageInDays : current date - created date
            cgInvoiceReport.setAgeInDays((sqlToday.getTime() - docCreateDate.getTime()) / KFSConstants.MILLSECONDS_PER_DAY);

            // filter using lookupForm.getFieldsForLookup()
            if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgInvoiceReport, "ContractsGrantsInvoiceReport")) {
                displayList.add(cgInvoiceReport);
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);

        return displayList;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }
}
