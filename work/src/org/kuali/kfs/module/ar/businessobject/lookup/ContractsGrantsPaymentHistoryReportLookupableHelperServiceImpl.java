/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsPaymentHistoryReport;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for the Payment History Report.
 */
public class ContractsGrantsPaymentHistoryReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
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

        Collection<ContractsGrantsPaymentHistoryReport> displayList = new ArrayList<ContractsGrantsPaymentHistoryReport>();

        lookupFormFields.put(ArPropertyConstants.INVOICE_DOCUMENT_TYPE, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
        Collection<CashControlDocument> cgCashControlDocs = getLookupService().findCollectionBySearchHelper(CashControlDocument.class, lookupFormFields, true);


        // build search result fields
        // For each Cash Control doc, get a list of payment app doc numbers
        for (CashControlDocument cashControlDoc : cgCashControlDocs) {

            List<CashControlDetail> cashControlDetailList = cashControlDoc.getCashControlDetails();

            // For each payment app doc number get a list of payment application docs
            for (CashControlDetail cashControlDetail : cashControlDetailList) {

                PaymentApplicationDocument paymentApplicationDoc = cashControlDetail.getReferenceFinancialDocument();

                // If the retrieved APP went to final

                final boolean isFinal = paymentApplicationDoc.getDocumentHeader().getWorkflowDocument().isFinal();

                if (isFinal) {

                    List<InvoicePaidApplied> appliedPayments = paymentApplicationDoc.getInvoicePaidApplieds();

                    for (InvoicePaidApplied appliedPayment : appliedPayments) {
                        ContractsGrantsPaymentHistoryReport cgPaymentHistoryReport = new ContractsGrantsPaymentHistoryReport();

                        cgPaymentHistoryReport.setPaymentNumber(paymentApplicationDoc.getDocumentNumber());
                        cgPaymentHistoryReport.setPaymentDate(new java.sql.Date(paymentApplicationDoc.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis()));

                        cgPaymentHistoryReport.setCustomerNumber(cashControlDetail.getCustomerNumber());
                        if (!ObjectUtils.isNull(cashControlDetail.getCustomer())) {
                            cgPaymentHistoryReport.setCustomerName(cashControlDetail.getCustomer().getCustomerName());
                        }
                        cgPaymentHistoryReport.setPaymentAmount(appliedPayment.getInvoiceItemAppliedAmount());
                        cgPaymentHistoryReport.setInvoiceNumber(appliedPayment.getFinancialDocumentReferenceInvoiceNumber());
                        cgPaymentHistoryReport.setInvoiceAmount(appliedPayment.getCustomerInvoiceDocument().getTotalDollarAmount());

                        Map<String, String> criteria = new HashMap<String, String>();
                        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, cgPaymentHistoryReport.getInvoiceNumber());
                        ContractsGrantsInvoiceDocument cgInvoiceDocument = businessObjectService.findByPrimaryKey(ContractsGrantsInvoiceDocument.class, criteria);

                        cgPaymentHistoryReport.setAwardNumber(cgInvoiceDocument.getInvoiceGeneralDetail().getProposalNumber());
                        cgPaymentHistoryReport.setReversedIndicator(appliedPayment.getCustomerInvoiceDocument().isInvoiceReversal());

                        cgPaymentHistoryReport.setAppliedIndicator(true);

                        displayList.add(cgPaymentHistoryReport);
                    }

                }
            }
        }
        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }
}
