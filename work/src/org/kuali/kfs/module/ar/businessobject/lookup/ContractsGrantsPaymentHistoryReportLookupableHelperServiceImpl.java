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

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsPaymentHistoryReport;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Defines a custom lookup for the Payment History Report.
 */
public class ContractsGrantsPaymentHistoryReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    private static final Log LOG = LogFactory.getLog(ContractsGrantsPaymentHistoryReportLookupableHelperServiceImpl.class);

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

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("invoiceDocumentType", ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);

        Collection<CashControlDocument> cgCashControlDocs = businessObjectService.findMatching(CashControlDocument.class, criteria);


        // build search result fields
        // For each Cash Control doc, get a list of payment app doc numbers
        for (CashControlDocument cashControlDoc : cgCashControlDocs) {

            List<CashControlDetail> cashControlDetailList = cashControlDoc.getCashControlDetails();

            // For each payment app doc number get a list of payment application docs
            for (CashControlDetail cashControlDetail : cashControlDetailList) {

                PaymentApplicationDocument paymentApplicationDoc = cashControlDetail.getReferenceFinancialDocument();

                // If the retrieved APP went to final

                boolean isFinal = false;
                try {
                    isFinal = paymentApplicationDoc.getDocumentHeader().getWorkflowDocument().isFinal();
                }
                catch (Exception e) {
                    LOG.debug(e.toString() + " happened from paymentApplicationDoc.getDocumentHeader().getWorkflowDocument()");
                }

                if (isFinal) {

                    List<InvoicePaidApplied> appliedPayments = paymentApplicationDoc.getInvoicePaidApplieds();

                    for (InvoicePaidApplied appliedPayment : appliedPayments) {
                        ContractsGrantsPaymentHistoryReport cgPaymentHistoryReport = new ContractsGrantsPaymentHistoryReport();

                        cgPaymentHistoryReport.setPaymentNumber(paymentApplicationDoc.getDocumentNumber());
                        Timestamp ts = new Timestamp(paymentApplicationDoc.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis());
                        try {
                            cgPaymentHistoryReport.setPaymentDate(dateTimeService.convertToSqlDate(ts));
                        }
                        catch (ParseException ex) {
                            // TODO Auto-generated catch block
                            LOG.error("problem during ContractsGrantsPaymentHistoryReportLookupableHelperServiceImpl.performLookup()", ex);
                        }

                        cgPaymentHistoryReport.setCustomerNumber(cashControlDetail.getCustomerNumber());
                        cgPaymentHistoryReport.setCustomerName(cashControlDetail.getCustomer().getCustomerName());
                        cgPaymentHistoryReport.setPaymentAmount(appliedPayment.getInvoiceItemAppliedAmount());
                        cgPaymentHistoryReport.setInvoiceNumber(appliedPayment.getFinancialDocumentReferenceInvoiceNumber());
                        cgPaymentHistoryReport.setInvoiceAmount(appliedPayment.getCustomerInvoiceDocument().getTotalDollarAmount());

                        criteria.clear();
                        criteria.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, cgPaymentHistoryReport.getInvoiceNumber());
                        ContractsGrantsInvoiceDocument cgInvoiceDocument = businessObjectService.findByPrimaryKey(ContractsGrantsInvoiceDocument.class, criteria);

                        cgPaymentHistoryReport.setAwardNumber(cgInvoiceDocument.getProposalNumber());
                        cgPaymentHistoryReport.setReversedIndicator(appliedPayment.getCustomerInvoiceDocument().isInvoiceReversal());

                        cgPaymentHistoryReport.setAppliedIndicator(true);

                        if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgPaymentHistoryReport, "ContractsGrantsPaymentHistoryReport")) {
                            displayList.add(cgPaymentHistoryReport);
                        }
                    }

                }
            }
        }
        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }



    @Override
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }



    @Override
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService
     */

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


}
