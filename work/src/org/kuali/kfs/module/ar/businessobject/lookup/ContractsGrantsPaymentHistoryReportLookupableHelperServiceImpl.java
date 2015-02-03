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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsPaymentHistoryReport;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Defines a custom lookup for the Payment History Report.
 */
public class ContractsGrantsPaymentHistoryReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected FinancialSystemDocumentService financialSystemDocumentService;

    /**
     * Overridden to validate the invoie amount and payment date fields to make sure they are parsable
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        if (!StringUtils.isBlank(fieldValues.get(ArPropertyConstants.PAYMENT_DATE))) {
            validateDateField(fieldValues.get(ArPropertyConstants.PAYMENT_DATE), ArPropertyConstants.PAYMENT_DATE, getDateTimeService());
        }
        if (!StringUtils.isBlank(fieldValues.get(ArPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX+ArPropertyConstants.PAYMENT_DATE))) {
            validateDateField(fieldValues.get(ArPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX+ArPropertyConstants.PAYMENT_DATE), ArPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX+ArPropertyConstants.PAYMENT_DATE, getDateTimeService());
        }
        if (!StringUtils.isBlank(fieldValues.get(ArPropertyConstants.INVOICE_AMOUNT))) {
            try {
                Formatter f = new CurrencyFormatter();
                f.format(fieldValues.get(ArPropertyConstants.INVOICE_AMOUNT));
            } catch (FormatException fe) {
                // we'll assume this was a parse exception
                final String label = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), ArPropertyConstants.INVOICE_AMOUNT);
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.INVOICE_AMOUNT, KFSKeyConstants.ERROR_NUMERIC, label);
            }
        }
        super.validateSearchParameters(fieldValues);
    }

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

        Map<String, String> invoiceAppliedLookupFields = new HashMap<>();
        invoiceAppliedLookupFields.put(ArPropertyConstants.CUSTOMER_INVOICE_DOCUMENT+"."+KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
        if (lookupFormFields.containsKey(ArPropertyConstants.PAYMENT_NUMBER)) {
            invoiceAppliedLookupFields.put(KFSPropertyConstants.DOCUMENT_NUMBER, (String)lookupFormFields.get(ArPropertyConstants.PAYMENT_NUMBER));
        }
        if (lookupFormFields.containsKey(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER)) {
            invoiceAppliedLookupFields.put(ArPropertyConstants.CUSTOMER_INVOICE_DOCUMENT+"."+ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, (String)lookupFormFields.get(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER));
        }
        if (lookupFormFields.containsKey(ArPropertyConstants.PAYMENT_AMOUNT)) {
            invoiceAppliedLookupFields.put(ArPropertyConstants.CustomerInvoiceDetailFields.INVOICE_ITEM_APPLIED_AMOUNT, (String)lookupFormFields.get(ArPropertyConstants.PAYMENT_AMOUNT));
        }
        if (lookupFormFields.containsKey(ArPropertyConstants.INVOICE_NUMBER)) {
            invoiceAppliedLookupFields.put(ArPropertyConstants.CustomerInvoiceDocumentFields.FINANCIAL_DOCUMENT_REF_INVOICE_NUMBER, (String)lookupFormFields.get(ArPropertyConstants.INVOICE_NUMBER));
        }

        Collection<InvoicePaidApplied> invoicePaidApplieds = getLookupService().findCollectionBySearchHelper(InvoicePaidApplied.class, invoiceAppliedLookupFields, true);

        // build search result fields
        // For each Cash Control doc, get a list of payment app doc numbers
        try {
            for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
                boolean useInvoicePaidApplied = true;
                final Document doc = getDocumentService().getByDocumentHeaderId(invoicePaidApplied.getDocumentNumber());
                if (doc instanceof PaymentApplicationDocument) {
                    final PaymentApplicationDocument paymentApp = (PaymentApplicationDocument)doc;
                    if (getFinancialSystemDocumentService().getUnsuccessfulDocumentStatuses().contains(paymentApp.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode())) {
                        useInvoicePaidApplied = false;
                    }
                    if (!StringUtils.isBlank((String)lookupFormFields.get(ArPropertyConstants.APPLIED_INDICATOR))) {
                        final String appliedIndicator = (String)lookupFormFields.get(ArPropertyConstants.APPLIED_INDICATOR);
                        if (KRADConstants.YES_INDICATOR_VALUE.equals(appliedIndicator) && !getFinancialSystemDocumentService().getSuccessfulDocumentStatuses().contains(paymentApp.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode())) {
                            useInvoicePaidApplied = false;
                        } else if (KRADConstants.NO_INDICATOR_VALUE.equals(appliedIndicator) && !getFinancialSystemDocumentService().getPendingDocumentStatuses().contains(paymentApp.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode())) {
                            useInvoicePaidApplied = false;
                        }
                    }
                    final java.util.Date paymentAppFinalDate = paymentApp.getDocumentHeader().getWorkflowDocument().getDateFinalized().toDate();
                    if (!StringUtils.isBlank((String)lookupFormFields.get(ArPropertyConstants.PAYMENT_DATE))) {
                        final java.util.Date toPaymentDate = getDateTimeService().convertToDate((String)lookupFormFields.get(ArPropertyConstants.PAYMENT_DATE));
                        if (!KfsDateUtils.isSameDay(paymentAppFinalDate, toPaymentDate) && toPaymentDate.before(paymentAppFinalDate)) {
                            useInvoicePaidApplied = false;
                        }
                    }
                    if (!StringUtils.isBlank((String)lookupFormFields.get(ArPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX+ArPropertyConstants.PAYMENT_DATE))) {
                        final java.util.Date fromPaymentDate = getDateTimeService().convertToDate((String)lookupFormFields.get(ArPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX+ArPropertyConstants.PAYMENT_DATE));
                        if (!KfsDateUtils.isSameDay(paymentAppFinalDate, fromPaymentDate) && fromPaymentDate.after(paymentAppFinalDate)) {
                            useInvoicePaidApplied = false;
                        }
                    }

                    final ContractsGrantsInvoiceDocument cgInvoiceDocument = getBusinessObjectService().findBySinglePrimaryKey(ContractsGrantsInvoiceDocument.class, invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber());
                    if (!StringUtils.isBlank((String)lookupFormFields.get(ArPropertyConstants.INVOICE_AMOUNT))) {
                        final KualiDecimal invoiceAmount = new KualiDecimal((String)lookupFormFields.get(ArPropertyConstants.INVOICE_AMOUNT));
                        if (!invoiceAmount.equals(cgInvoiceDocument.getTotalDollarAmount())) {
                            useInvoicePaidApplied = false;
                        }
                    }
                    if (!StringUtils.isBlank((String)lookupFormFields.get(KFSPropertyConstants.AWARD_NUMBER))) {
                        if (!StringUtils.equals(cgInvoiceDocument.getInvoiceGeneralDetail().getAward().getProposalNumber().toString(),(String)lookupFormFields.get(KFSPropertyConstants.AWARD_NUMBER))) {
                            useInvoicePaidApplied = false;
                        }
                    }
                    if (!StringUtils.isBlank((String)lookupFormFields.get(ArPropertyConstants.REVERSED_INDICATOR))) {
                        final String reversedIndicator = (String)lookupFormFields.get(ArPropertyConstants.REVERSED_INDICATOR);
                        if (KRADConstants.YES_INDICATOR_VALUE.equals(reversedIndicator) && !cgInvoiceDocument.isInvoiceReversal()) {
                            useInvoicePaidApplied = false;
                        } else if (KRADConstants.NO_INDICATOR_VALUE.equals(reversedIndicator) && cgInvoiceDocument.isInvoiceReversal()) {
                            useInvoicePaidApplied = false;
                        }
                    }

                    if (useInvoicePaidApplied) {
                        ContractsGrantsPaymentHistoryReport cgPaymentHistoryReport = new ContractsGrantsPaymentHistoryReport();

                        cgPaymentHistoryReport.setPaymentNumber(invoicePaidApplied.getDocumentNumber());
                        cgPaymentHistoryReport.setPaymentAmount(invoicePaidApplied.getInvoiceItemAppliedAmount());
                        cgPaymentHistoryReport.setInvoiceNumber(invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber());

                        cgPaymentHistoryReport.setPaymentDate(new java.sql.Date(paymentApp.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis()));
                        cgPaymentHistoryReport.setAppliedIndicator(getFinancialSystemDocumentService().getSuccessfulDocumentStatuses().contains(paymentApp.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode()));

                        cgPaymentHistoryReport.setAwardNumber(cgInvoiceDocument.getInvoiceGeneralDetail().getProposalNumber());
                        cgPaymentHistoryReport.setReversedIndicator(cgInvoiceDocument.isInvoiceReversal());
                        cgPaymentHistoryReport.setCustomerNumber(cgInvoiceDocument.getCustomerNumber());
                        cgPaymentHistoryReport.setCustomerName(cgInvoiceDocument.getCustomer().getCustomerName());
                        cgPaymentHistoryReport.setInvoiceAmount(cgInvoiceDocument.getTotalDollarAmount());

                        displayList.add(cgPaymentHistoryReport);
                    }
                }
            }
            buildResultTable(lookupForm, displayList, resultTable);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not open payment application document related to search", we);
        }
        catch (ParseException pe) {
            throw new RuntimeException("I tried to validate the date and amount fields related to search, I really did.  But...I guess I didn't try hard enough", pe);
        }
        return displayList;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

}
