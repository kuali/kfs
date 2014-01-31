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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.dataaccess.AccountsReceivableDocumentHeaderDao;
import org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDetailDao;
import org.kuali.kfs.module.ar.document.dataaccess.NonAppliedHoldingDao;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerOpenItemReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerOpenItemReportServiceImpl implements CustomerOpenItemReportService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerOpenItemReportServiceImpl.class);

    protected AccountsReceivableDocumentHeaderDao accountsReceivableDocumentHeaderDao;
    protected WorkflowDocumentService workflowDocumentService;
    protected CustomerInvoiceDocumentService customerInvoiceDocumentService;
    protected DocumentService documentService;
    protected DateTimeService dateTimeService;
    protected CustomerInvoiceDetailDao customerInvoiceDetailDao;
    protected NonAppliedHoldingDao nonAppliedHoldingDao;
    protected BusinessObjectService businessObjectService;

    /**
     * This method populates CustomerOpenItemReportDetails (Customer History Report).
     *
     * @param customerNumber
     */

    @Override
    public List getPopulatedReportDetails(String customerNumber) {

        List results = new ArrayList();

        Collection arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(customerNumber);
        if (arDocumentHeaders.size() == 0) {
            return results;
        }

        String userId = GlobalVariables.getUserSession().getPrincipalId();
        List finSysDocHeaderIds = new ArrayList();
        List invoiceIds = new ArrayList();
        List paymentApplicationIds = new ArrayList();
        List unappliedHoldingIds = new ArrayList();
        Hashtable details = new Hashtable();
        WorkflowDocument workflowDocument;

        for (Iterator itr = arDocumentHeaders.iterator(); itr.hasNext();) {
            AccountsReceivableDocumentHeader documentHeader = (AccountsReceivableDocumentHeader) itr.next();
            CustomerOpenItemReportDetail detail = new CustomerOpenItemReportDetail();

            // populate workflow document
            workflowDocument = WorkflowDocumentFactory.loadDocument(userId, documentHeader.getDocumentNumber());

            // do not display not approved documents
            if (ObjectUtils.isNull(workflowDocument.getDateApproved())) {
                continue;
            }
            Date approvedDate = getSqlDate(workflowDocument.getDateApproved().toCalendar(Locale.getDefault()));

            // Document Type
            String documentType = workflowDocument.getDocumentTypeName();
            detail.setDocumentType(documentType);

            // Document Number
            String documentNumber = documentHeader.getDocumentNumber();
            detail.setDocumentNumber(documentNumber);

            // KFSMI-8356 Adding CINV to the invoice list
            if (documentType.equals(KFSConstants.FinancialDocumentTypeCodes.CUSTOMER_INVOICE) || documentType.equals(KFSConstants.ContractsGrantsModuleDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE)) {
                invoiceIds.add(documentNumber);
            }
            else {
                // Approved Date -> for invoices Due Date, for all other documents Approved Date
                detail.setDueApprovedDate(approvedDate);

                if (documentType.equals(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE)) {
                    paymentApplicationIds.add(documentNumber);
                }
                else {
                    finSysDocHeaderIds.add(documentNumber);
                }
            }
            details.put(documentNumber, detail);
        }

        // add Unapplied Payment Applications
        Collection<NonAppliedHolding> arNonAppliedHoldings = nonAppliedHoldingDao.getNonAppliedHoldingsForCustomer(customerNumber);
        for (NonAppliedHolding nonAppliedHolding : arNonAppliedHoldings) {
            // populate workflow document
            workflowDocument = WorkflowDocumentFactory.loadDocument(userId, nonAppliedHolding.getReferenceFinancialDocumentNumber());

            CustomerOpenItemReportDetail detail = new CustomerOpenItemReportDetail();
            detail.setDocumentType(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
            detail.setDocumentNumber(nonAppliedHolding.getReferenceFinancialDocumentNumber());
            if (ObjectUtils.isNull(workflowDocument.getDateApproved())) {
                continue;
            }
            Date documentApprovedDate = getSqlDate(workflowDocument.getDateApproved().toCalendar(Locale.getDefault()));
            detail.setDueApprovedDate(documentApprovedDate);
            details.put(nonAppliedHolding.getReferenceFinancialDocumentNumber(), detail);
            unappliedHoldingIds.add(nonAppliedHolding.getReferenceFinancialDocumentNumber());
        }

        // for invoices
        if (invoiceIds.size() > 0) {
            populateReportDetailsForInvoices(invoiceIds, results, details);
        }

        // for payment applications
        if (paymentApplicationIds.size() > 0) {
            try {
                populateReportDetailsForPaymentApplications(paymentApplicationIds, results, details);
            } catch(WorkflowException w) {
                LOG.error("WorkflowException while populating report details for PaymentApplicationDocument", w);
            }
        }

        // for unapplied payment applications
        if (unappliedHoldingIds.size() > 0) {
            try {
                populateReportDetailsForUnappliedPaymentApplications(unappliedHoldingIds, results, details);
            } catch(WorkflowException w) {
                LOG.error("WorkflowException while populating report details for PaymentApplicationDocument", w);
            }
        }

        // for all other documents
        if (finSysDocHeaderIds.size() > 0) {
            populateReportDetails(finSysDocHeaderIds, results, details);
        }

        return results;
    }

    /**
     * This method populates CustomerOpenItemReportDetails for CustomerInvoiceDocuments (Customer History Report).
     *
     * @param finSysDocHeaderIds <=> documentNumbers of CustomerInvoiceDocuments
     * @param results <=> CustomerOpenItemReportDetails to display in the report
     * @param details <=> <key = documentNumber, value = customerOpenItemReportDetail>
     */
    protected void populateReportDetailsForInvoices(List invoiceIds, List results, Hashtable details) {
        Collection invoices = getDocuments(CustomerInvoiceDocument.class, invoiceIds);
        Collection cgInvoices = getDocuments(ContractsGrantsInvoiceDocument.class, invoiceIds);
        if (CollectionUtils.isNotEmpty(cgInvoices)) {
            invoices.addAll(cgInvoices);
        }

        for (Iterator itr = invoices.iterator(); itr.hasNext();) {
            CustomerInvoiceDocument invoice = (CustomerInvoiceDocument) itr.next();
            String documentNumber = invoice.getDocumentNumber();

            CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

            // Document Description
            String documentDescription = invoice.getDocumentHeader().getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription)) {
                detail.setDocumentDescription(documentDescription);
            }
            else {
                detail.setDocumentDescription("");
            }

            // Billing Date
            detail.setBillingDate(invoice.getBillingDate());

            // Due/Approved Date -> for invoice it is Due Date, and for all other documents Approved Date
            detail.setDueApprovedDate(invoice.getInvoiceDueDate());

            // Document Payment Amount
            detail.setDocumentPaymentAmount(invoice.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount());

            // Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(customerInvoiceDocumentService.getOpenAmountForCustomerInvoiceDocument(invoice));

            results.add(detail);
        }
    }

    /**
     * This method populates CustomerOpenItemReportDetails for PaymentApplicationDocuments (Customer History Report).
     *
     * @param paymentApplicationIds <=> documentNumbers of PaymentApplicationDocuments
     * @param results <=> CustomerOpenItemReportDetails to display in the report
     * @param details <=> <key = documentNumber, value = customerOpenItemReportDetail>
     */
    protected void populateReportDetailsForPaymentApplications(List paymentApplicationIds, List results, Hashtable details) throws WorkflowException {
        Collection paymentApplications = getDocuments(PaymentApplicationDocument.class, paymentApplicationIds);

        for (Iterator itr = paymentApplications.iterator(); itr.hasNext();) {
            PaymentApplicationDocument paymentApplication = (PaymentApplicationDocument) itr.next();
            String documentNumber = paymentApplication.getDocumentNumber();

            CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

            // populate Document Description
            String documentDescription = paymentApplication.getDocumentHeader().getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription)) {
                detail.setDocumentDescription(documentDescription);
            }
            else {
                detail.setDocumentDescription("");
            }

            // populate Document Payment Amount
            detail.setDocumentPaymentAmount(paymentApplication.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().negated());

            // populate Unpaid/Unapplied Amount if the customer number is the same
            if (ObjectUtils.isNotNull(paymentApplication.getNonAppliedHolding())) {
                if (paymentApplication.getNonAppliedHolding().getCustomerNumber().equals(paymentApplication.getAccountsReceivableDocumentHeader().getCustomerNumber())) {
                    detail.setUnpaidUnappliedAmount(paymentApplication.getNonAppliedHolding().getAvailableUnappliedAmount().negated());
                } else {
                    detail.setUnpaidUnappliedAmount(KualiDecimal.ZERO);
                }
            } else {
                detail.setUnpaidUnappliedAmount(KualiDecimal.ZERO);
            }

            results.add(detail);
        }
    }

    /**
     * This method populates CustomerOpenItemReportDetails for UnappliedPaymentApplicationDocuments (Customer History Report).
     *
     * @param unappliedHoldingIds <=> documentNumbers of UnappliedPaymentApplicationDocuments
     * @param results <=> CustomerOpenItemReportDetails to display in the report
     * @param details <=> <key = documentNumber, value = customerOpenItemReportDetail>
     */
    protected void populateReportDetailsForUnappliedPaymentApplications(List unappliedHoldingIds, List results, Hashtable details) throws WorkflowException {
        Collection paymentApplications = getDocuments(PaymentApplicationDocument.class, unappliedHoldingIds);

        for (Iterator itr = paymentApplications.iterator(); itr.hasNext();) {
            PaymentApplicationDocument paymentApplication = (PaymentApplicationDocument) itr.next();
            String documentNumber = paymentApplication.getDocumentNumber();

            if ((paymentApplication.getNonAppliedHolding().getCustomerNumber().equals(paymentApplication.getAccountsReceivableDocumentHeader().getCustomerNumber()))) {
                continue;
            }

            CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

            // populate Document Description
            String documentDescription = paymentApplication.getDocumentHeader().getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription)) {
                detail.setDocumentDescription(documentDescription);
            }
            else {
                detail.setDocumentDescription("");
            }

            // populate Document Payment Amount - original document amount
            detail.setDocumentPaymentAmount(paymentApplication.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().negated());

            // populate Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(paymentApplication.getNonAppliedHolding().getAvailableUnappliedAmount().negated());

            results.add(detail);
        }
    }

    /**
     * This method populates CustomerOpenItemReportDetails for CustomerCreditMemoDocuments and WriteOffDocuments <=> all documents
     * but CustomerInvoiceDocument and PaymentApplicationDocument (Customer History Report).
     *
     * @param finSysDocHeaderIds <=> documentNumbers of FinancialSystemDocumentHeaders
     * @param results <=> CustomerOpenItemReportDetails to display in the report
     * @param details <=> <key = documentNumber, value = customerOpenItemReportDetail>
     */
    public void populateReportDetails(List<String> finSysDocHeaderIds, List results, Hashtable details) {
        Collection<FinancialSystemDocumentHeader> financialSystemDocHeaders = new ArrayList<FinancialSystemDocumentHeader>();
        for (String documentNumber : finSysDocHeaderIds) {
            FinancialSystemDocumentHeader header = businessObjectService.findBySinglePrimaryKey(FinancialSystemDocumentHeader.class, documentNumber);
            if ( header != null ) {
                financialSystemDocHeaders.add( header );
            }
        }

        for ( FinancialSystemDocumentHeader fsDocumentHeader : financialSystemDocHeaders ) {
            CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(fsDocumentHeader.getDocumentNumber());

            // populate Document Description
            detail.setDocumentDescription(StringUtils.trimToEmpty(fsDocumentHeader.getDocumentDescription()));

            // populate Document Payment Amount
            detail.setDocumentPaymentAmount(fsDocumentHeader.getFinancialDocumentTotalAmount().negated());

            // Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(KualiDecimal.ZERO);

            results.add(detail);
        }
    }

    /**
     * This method returns collection of documents of type classToSearchFrom Note: can be used for documents only, not for
     * *DocumentHeaders @param documentNumbers
     */
    public Collection getDocuments(Class classToSearchFrom, List documentNumbers) {
        List docs;

        try {
            docs = documentService.getDocumentsByListOfDocumentHeaderIds(classToSearchFrom, documentNumbers);
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve documents", e);
        }
        return docs;
    }

    /**
     * @param cal
     * @return
     */
    protected Date getSqlDate(Calendar cal) {
        Date sqlDueDate = null;

        if (ObjectUtils.isNull(cal)) {
            return sqlDueDate;
        }
        try {
            sqlDueDate = dateTimeService.convertToSqlDate(new Timestamp(cal.getTime().getTime()));
        }
        catch (ParseException e) {
            // TODO: throw an error here, but don't die
        }
        return sqlDueDate;
    }

    /**
     * This method populates CustomerOpenItemReportDetails (Customer Open Item Report)
     *
     * @param urlParameters
     */
    @Override
    public List getPopulatedReportDetails(Map urlParameters) {
        List results = new ArrayList();

        // get arDocumentHeaders
        Collection<AccountsReceivableDocumentHeader> arDocumentHeaders = getARDocumentHeaders(urlParameters);
        if (arDocumentHeaders.size() == 0) {
            return results;
        }

        // get ids of arDocumentHeaders
        List<String> arDocumentHeaderIds = new ArrayList<String>();
        for (AccountsReceivableDocumentHeader arDocHeader : arDocumentHeaders) {
            arDocumentHeaderIds.add(arDocHeader.getDocumentNumber());
        }

        // get invoices
        String reportOption = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_OPTION))[0];
        Collection<CustomerInvoiceDocument> invoices;
        Collection<CustomerInvoiceDetail> details = null;
        if (StringUtils.equals(reportOption, ArConstants.CustomerAgingReportFields.ACCT)) {
            String accountNumber = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.ACCOUNT_NUMBER))[0];
            details = customerInvoiceDetailDao.getCustomerInvoiceDetailsByAccountNumberByInvoiceDocumentNumbers(accountNumber, arDocumentHeaderIds);
            invoices = getInvoicesByAccountNumberByDocumentIds(accountNumber, arDocumentHeaderIds, details);
        }
        else {
            invoices = getDocuments(CustomerInvoiceDocument.class, arDocumentHeaderIds);
        }
        if (ObjectUtils.isNull(invoices) | invoices.size() == 0) {
            return results;
        }

        List<CustomerInvoiceDocument> selectedInvoices = new ArrayList();

        String columnTitle = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.COLUMN_TITLE))[0];
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        java.util.Date reportRunDate = null;
        java.util.Date beginDate = null;
        java.util.Date endDate = null;
        try {
            reportRunDate = dateFormat.parse(((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_RUN_DATE))[0]);
            if (!StringUtils.equals(columnTitle, KFSConstants.CustomerOpenItemReport.ALL_DAYS)) {
                endDate = dateFormat.parse(((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_END_DATE))[0]);
                String strBeginDate = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_BEGIN_DATE))[0];
                if (StringUtils.isNotEmpty(strBeginDate)) {
                    beginDate = dateFormat.parse(strBeginDate);
                }
            }
        }
        catch (ParseException e) {
            LOG.error("problem during CustomerOpenItemReportServiceImpl.getPopulatedReportDetails()", e);
        }

        // Billing Organization
        if (StringUtils.equals(reportOption, ArConstants.CustomerAgingReportFields.BILLING_ORG)) {
            // All days
            // 1. invoice open amount > 0
            // 2. billingDate <= reportRunDate
            // 3. billByChartOfAccountsCode = processingOrBillingChartCode
            // 4. billbyOrganizationCode = orgCode
            String chartCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.PROCESSING_OR_BILLING_CHART_CODE))[0];
            String orgCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.ORGANIZATION_CODE))[0];
            if (StringUtils.equals(columnTitle, KFSConstants.CustomerOpenItemReport.ALL_DAYS)) {
                for (CustomerInvoiceDocument invoice : invoices) {
                    // get only invoices with open amounts
                    if (ObjectUtils.isNull(invoice.getClosedDate()) && ObjectUtils.isNotNull(invoice.getBillingDate()) && !reportRunDate.before(invoice.getBillingDate()) && StringUtils.equals(chartCode, invoice.getBillByChartOfAccountCode()) && StringUtils.equals(orgCode, invoice.getBilledByOrganizationCode())) {
                        selectedInvoices.add(invoice);
                    }
                }
            }
            // *days
            // 1. invoice open amount > 0
            // 2. beginDate <= invoice billing date <= endDate
            // 3. billByChartOfAccountsCode = chartCode
            // 4. billbyOrganizationCode = orgCode
            else {
                for (CustomerInvoiceDocument invoice : invoices) {
                    if (ObjectUtils.isNull(invoice.getClosedDate()) && ObjectUtils.isNotNull(invoice.getBillingDate()) && StringUtils.equals(chartCode, invoice.getBillByChartOfAccountCode()) && StringUtils.equals(orgCode, invoice.getBilledByOrganizationCode())) {
                        if ((ObjectUtils.isNotNull(beginDate) && !beginDate.after(invoice.getBillingDate()) && !endDate.before(invoice.getBillingDate())) || (ObjectUtils.isNull(beginDate) && !endDate.before(invoice.getBillingDate()))) {
                            selectedInvoices.add(invoice);
                        }
                    }
                }
            }
        }
        // Processing Organization or Account
        else {
            // All days
            // 1. invoice open amount > 0
            // 2. invoice billing dates <= reportRunDate
            if (StringUtils.equals(columnTitle, KFSConstants.CustomerOpenItemReport.ALL_DAYS)) {
                for (CustomerInvoiceDocument invoice : invoices) {
                    if (ObjectUtils.isNull(invoice.getClosedDate()) && ObjectUtils.isNotNull(invoice.getBillingDate()) && !reportRunDate.before(invoice.getBillingDate())) {
                        selectedInvoices.add(invoice);
                    }
                }
            }
            // *days
            // 1. invoice open amount > 0
            // 2. beginDate <= invoice billing date <= endDate
            else {
                for (CustomerInvoiceDocument invoice : invoices) {
                    if (ObjectUtils.isNull(invoice.getClosedDate()) && ObjectUtils.isNotNull(invoice.getBillingDate())) {
                        if ((ObjectUtils.isNotNull(beginDate) && !beginDate.after(invoice.getBillingDate()) && !endDate.before(invoice.getBillingDate())) || (ObjectUtils.isNull(beginDate) && !endDate.before(invoice.getBillingDate()))) {
                            selectedInvoices.add(invoice);
                        }
                    }
                }
            }
        }

        if (selectedInvoices.size() == 0) {
            return results;
        }

        if (StringUtils.equals(reportOption, ArConstants.CustomerAgingReportFields.ACCT)) {
            populateReporDetails(selectedInvoices, results, details);
        }
        else {
            populateReportDetails(selectedInvoices, results);
        }

        return results;
    }

    /**
     * This method retrieves ARDocumentHeader objects for "Customer Open Item Report"
     *
     * @param urlParameters
     * @return ARDocumentHeader objects meeting the search criteria
     */
    protected Collection getARDocumentHeaders(Map urlParameters) {
        Collection arDocumentHeaders;

        String reportOption = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_OPTION))[0];
        String customerNumber = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER))[0];

        if (StringUtils.equals(reportOption, ArConstants.CustomerAgingReportFields.PROCESSING_ORG)) {
            String processingChartCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.PROCESSING_OR_BILLING_CHART_CODE))[0];
            String processingOrganizationCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.ORGANIZATION_CODE))[0];
            arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersByCustomerNumberByProcessingOrgCodeAndChartCode(customerNumber, processingChartCode, processingOrganizationCode);
        } // reportOption is "Billing Organization" or "Account"
        else {
            arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(customerNumber);
        }
        return arDocumentHeaders;
    }

    /**
     * This method gets called only if reportOption is Account Gets invoices based on the selected invoice details <=> invoice
     * details meeting search criteria i.e. the accountNumber and the list of documentIds
     *
     * @param accountNumber
     * @param arDocumentHeaderIds
     * @param details (will get populated here)
     * @return invoices
     */
    protected Collection<CustomerInvoiceDocument> getInvoicesByAccountNumberByDocumentIds(String accountNumber, List arDocumentHeaderIds, Collection<CustomerInvoiceDetail> details) {
        Collection<CustomerInvoiceDocument> invoices = null;

        if (ObjectUtils.isNull(details) | details.size() == 0) {
            return invoices;
        }

        // get list of invoice document ids (eliminate duplicate invoice document ids)
        List<String> documentIds = new ArrayList();
        for (CustomerInvoiceDetail detail : details) {
            String documentNumber = detail.getDocumentNumber();
            if (!documentIds.contains(documentNumber)) {
                documentIds.add(documentNumber);
            }
        }

        // get invoices for the document ids list
        if (documentIds.size() != 0) {
            invoices = getDocuments(CustomerInvoiceDocument.class, documentIds);
        }

        return invoices;
    }

    /**
     * @param selectedInvoices
     * @param results
     * @param invoiceDetails
     */
    protected void populateReporDetails(List<CustomerInvoiceDocument> selectedInvoices, List results, Collection<CustomerInvoiceDetail> invoiceDetails) {

        for (Iterator<CustomerInvoiceDocument> iter = selectedInvoices.iterator(); iter.hasNext();) {
            CustomerInvoiceDocument invoice = iter.next();
            String documentNumber = invoice.getDocumentNumber();

            KualiDecimal amount = KualiDecimal.ZERO;
            KualiDecimal taxAmount = KualiDecimal.ZERO;
            KualiDecimal openAmount = KualiDecimal.ZERO;

            boolean foundFlag = false;

            for (CustomerInvoiceDetail invoiceDetail : invoiceDetails) {
                String tempDocumentNumber = invoiceDetail.getDocumentNumber();
                if (!StringUtils.equals(documentNumber, tempDocumentNumber)) {
                    continue;
                }
                foundFlag = true;

                KualiDecimal itemAmount = invoiceDetail.getAmount();
                if (ObjectUtils.isNotNull(itemAmount)) {
                    amount = amount.add(itemAmount);
                }

                KualiDecimal itemTaxAmount = invoiceDetail.getInvoiceItemTaxAmount();
                if (ObjectUtils.isNotNull(itemTaxAmount)) {
                    taxAmount = taxAmount.add(itemTaxAmount);
                }

                KualiDecimal openItemAmount = invoiceDetail.getAmountOpen();
                if (ObjectUtils.isNotNull(openItemAmount)) {
                    openAmount = openAmount.add(openItemAmount);
                }
            }
            // How is this possible?
            // invoiceDetails are for the list of invoices(invoices) meeting seach criteria including accountNumber and selected
            // arDocumentHeaders
            // -> list of invoices gets modified based on report run date and chosen date bucket -> selectedInvoices
            // selectedInvoices.size() <= invoices.size()
            if (!foundFlag) {
                continue;
            }

            CustomerOpenItemReportDetail detail = new CustomerOpenItemReportDetail();
            // Document Type
            detail.setDocumentType(invoice.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            // Document Number
            detail.setDocumentNumber(documentNumber);
            // Document Description
            String documentDescription = invoice.getDocumentHeader().getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription)) {
                detail.setDocumentDescription(documentDescription);
            }
            else {
                detail.setDocumentDescription("");
            }
            // Billing Date
            detail.setBillingDate(invoice.getBillingDate());
            // Due Date
            detail.setDueApprovedDate(invoice.getInvoiceDueDate());
            // Document Payment Amount
            detail.setDocumentPaymentAmount(amount.add(taxAmount));
            // Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(openAmount);
            results.add(detail);
        }
    }

    /**
     * @param invoices
     * @param results
     */
    protected void populateReportDetails(List<CustomerInvoiceDocument> invoices, List results) {
        for (CustomerInvoiceDocument invoice : invoices) {
            CustomerOpenItemReportDetail detail = new CustomerOpenItemReportDetail();
            // Document Type
            detail.setDocumentType(invoice.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            // Document Number
            detail.setDocumentNumber(invoice.getDocumentNumber());
            // Document Description
            String documentDescription = invoice.getDocumentHeader().getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription)) {
                detail.setDocumentDescription(documentDescription);
            }
            else {
                detail.setDocumentDescription("");
            }
            // Billing Date
            detail.setBillingDate(invoice.getBillingDate());
            // Due Date
            detail.setDueApprovedDate(invoice.getInvoiceDueDate());
            // Document Payment Amount
            detail.setDocumentPaymentAmount(invoice.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount());
            // Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(customerInvoiceDocumentService.getOpenAmountForCustomerInvoiceDocument(invoice));
            results.add(detail);

        }
    }

    @Override
    public Collection<String> getDocumentNumbersOfReferenceReports(String customerNumber) {

        Collection<String> documentNumbers = new ArrayList();

        Collection arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(customerNumber);
        String userId = GlobalVariables.getUserSession().getPrincipalId();

        List paymentApplicationIds = new ArrayList();
        List creditMemoIds = new ArrayList();
        List writeOffIds = new ArrayList();
        WorkflowDocument workflowDocument;

        for (Iterator itr = arDocumentHeaders.iterator(); itr.hasNext();) {
            AccountsReceivableDocumentHeader documentHeader = (AccountsReceivableDocumentHeader) itr.next();

            // populate workflow document
            workflowDocument = WorkflowDocumentFactory.loadDocument(userId, documentHeader.getDocumentNumber());

            // do not display not approved documents
            if (ObjectUtils.isNull(workflowDocument.getDateApproved())) {
                continue;
            }
            Date approvedDate = getSqlDate(workflowDocument.getDateApproved().toCalendar(Locale.getDefault()));

            // Document Type
            String documentType = workflowDocument.getDocumentTypeName();

            // Document Number
            String documentNumber = documentHeader.getDocumentNumber();

            if (documentType.equals(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE)){
                paymentApplicationIds.add(documentNumber);
            }
            else if (documentType.equals(KFSConstants.FinancialDocumentTypeCodes.CUSTOMER_CREDIT_MEMO)) {
                creditMemoIds.add(documentNumber);
            }
            else if (documentType.equals(ArConstants.INVOICE_WRITEOFF_DOCUMENT_TYPE_CODE)) {
                writeOffIds.add(documentNumber);
            }
        }

        if (paymentApplicationIds.size() > 0){
            Collection<PaymentApplicationDocument> paymentApplications = getDocuments(PaymentApplicationDocument.class, paymentApplicationIds);
            for (PaymentApplicationDocument paymentApplicationDocument: paymentApplications ) {
                for (InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()){
                    documentNumbers.add(invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber());
                }
            }
        }

        if (creditMemoIds.size() > 0){
            Collection<CustomerCreditMemoDocument> creditMemoDetailDocs = getDocuments(CustomerCreditMemoDocument.class, creditMemoIds);
            for (CustomerCreditMemoDocument creditMemo: creditMemoDetailDocs){
                documentNumbers.add(creditMemo.getFinancialDocumentReferenceInvoiceNumber());
            }
        }

        if (writeOffIds.size() > 0){
            Collection<CustomerInvoiceWriteoffDocument> customerInvoiceWriteoffDocs = getDocuments(CustomerInvoiceWriteoffDocument.class, writeOffIds);
            for (CustomerInvoiceWriteoffDocument invoiceWriteoffDocument : customerInvoiceWriteoffDocs) {
                documentNumbers.add(invoiceWriteoffDocument.getFinancialDocumentReferenceInvoiceNumber());
            }
        }

        return documentNumbers;
    }

    @Override
    public List getPopulatedUnpaidUnappliedAmountReportDetails(String customerNumber, String refDocumentNumber) {

        List results = new ArrayList();

        Collection<AccountsReceivableDocumentHeader> arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(customerNumber);
        String userId = GlobalVariables.getUserSession().getPrincipalId();

        Hashtable details = new Hashtable();
        List paymentApplicationIds = new ArrayList();
        List creditMemoIds = new ArrayList();
        List writeOffIds = new ArrayList();

        WorkflowDocument workflowDocument;

        for (AccountsReceivableDocumentHeader documentHeader : arDocumentHeaders) {
            CustomerOpenItemReportDetail detail = new CustomerOpenItemReportDetail();

            // populate workflow document
            workflowDocument = WorkflowDocumentFactory.loadDocument(userId, documentHeader.getDocumentNumber());

            // do not display not approved documents
            if (ObjectUtils.isNull(workflowDocument.getDateApproved())) {
                continue;
            }
            Date approvedDate = getSqlDate(workflowDocument.getDateApproved().toCalendar(Locale.getDefault()));

            // Document Type
            String documentType = workflowDocument.getDocumentTypeName();
            detail.setDocumentType(documentType);

            // Document Number
            String documentNumber = documentHeader.getDocumentNumber();
            detail.setDocumentNumber(documentNumber);

            // Approved Date -> for invoices Due Date, for all other documents Approved Date
            detail.setDueApprovedDate(approvedDate);

            if (documentType.equals("APP")) {
                paymentApplicationIds.add(documentNumber);
            }
            else if (documentType.equals("CRM")) {
                creditMemoIds.add(documentNumber);
            }
            else if (documentType.equals("INVW")) {
                writeOffIds.add(documentNumber);
            }

            details.put(documentNumber, detail);
        }

        if (paymentApplicationIds.size() > 0){
            try {
                populateReportDetailsForUnpaidUnappliedPaymentApplications(paymentApplicationIds, results, details, refDocumentNumber);
            } catch(WorkflowException w) {
                LOG.error("WorkflowException while populating report details for PaymentApplicationDocument", w);
            }
        }

        if (creditMemoIds.size() > 0){
            populateReportDetailsForCreditMemo(creditMemoIds, results, details, refDocumentNumber);

        }

        if (writeOffIds.size() > 0){
            populateReportDetailsForWriteOff(writeOffIds, results, details, refDocumentNumber);
        }


        return results;
    }

    protected void populateReportDetailsForUnpaidUnappliedPaymentApplications(List paymentApplicationIds, List results, Hashtable details, String refDocumentNumber) throws WorkflowException {
        Collection<PaymentApplicationDocument> paymentApplications = getDocuments(PaymentApplicationDocument.class, paymentApplicationIds);

        for (PaymentApplicationDocument paymentApplication : paymentApplications) {
            for (InvoicePaidApplied invoicePaidApplied : paymentApplication.getInvoicePaidApplieds()){

                if (invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber().equals(refDocumentNumber)){
                    String documentNumber = paymentApplication.getDocumentNumber();

                    CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

                    // populate Document Description
                    String documentDescription = paymentApplication.getDocumentHeader().getDocumentDescription();
                    if (ObjectUtils.isNotNull(documentDescription)) {
                        detail.setDocumentDescription(documentDescription);
                    }
                    else {
                        detail.setDocumentDescription("");
                    }

                    // populate Document Payment Amount
                    detail.setDocumentPaymentAmount(paymentApplication.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().negated());

                    // populate Unpaid/Unapplied Amount if the customer number is the same
                    if (ObjectUtils.isNotNull(paymentApplication.getNonAppliedHolding())) {
                        if (paymentApplication.getNonAppliedHolding().getCustomerNumber().equals(paymentApplication.getAccountsReceivableDocumentHeader().getCustomerNumber())) {
                            detail.setUnpaidUnappliedAmount(paymentApplication.getNonAppliedHolding().getAvailableUnappliedAmount().negated());
                        }
                    }

                    results.add(detail);
                }
            }
        }
    }

    protected void populateReportDetailsForCreditMemo(List creditMemoIds, List results, Hashtable details, String refDocumentNumber){
        Collection<CustomerCreditMemoDocument> creditMemoDetailDocs = getDocuments(CustomerCreditMemoDocument.class, creditMemoIds);
        for (CustomerCreditMemoDocument creditMemo: creditMemoDetailDocs){
            if (creditMemo.getFinancialDocumentReferenceInvoiceNumber().equals(refDocumentNumber)){
                String documentNumber = creditMemo.getDocumentNumber();

                CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

                // populate Document Description
                String documentDescription = creditMemo.getDocumentHeader().getDocumentDescription();
                if (ObjectUtils.isNotNull(documentDescription)) {
                    detail.setDocumentDescription(documentDescription);
                }
                else {
                    detail.setDocumentDescription("");
                }

                // populate Document Payment Amount
                detail.setDocumentPaymentAmount(creditMemo.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().negated());

                // Unpaid/Unapplied Amount
                detail.setUnpaidUnappliedAmount(KualiDecimal.ZERO);
                results.add(detail);
            }
        }
    }

    protected void populateReportDetailsForWriteOff(List writeOffIds, List results, Hashtable details, String refDocumentNumber){
        Collection<CustomerInvoiceWriteoffDocument> customerInvoiceWriteoffDocs = getDocuments(CustomerInvoiceWriteoffDocument.class, writeOffIds);
        for (CustomerInvoiceWriteoffDocument invoiceWriteoffDocument : customerInvoiceWriteoffDocs) {
            if (invoiceWriteoffDocument.getFinancialDocumentReferenceInvoiceNumber().equals(refDocumentNumber)){
                String documentNumber = invoiceWriteoffDocument.getDocumentNumber();

                CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

                // populate Document Description
                String documentDescription = invoiceWriteoffDocument.getDocumentHeader().getDocumentDescription();
                if (ObjectUtils.isNotNull(documentDescription)) {
                    detail.setDocumentDescription(documentDescription);
                }
                else {
                    detail.setDocumentDescription("");
                }

                // populate Document Payment Amount
                detail.setDocumentPaymentAmount(invoiceWriteoffDocument.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().negated());

                // Unpaid/Unapplied Amount
                detail.setUnpaidUnappliedAmount(KualiDecimal.ZERO);
                results.add(detail);
            }
        }
    }

    public void setAccountsReceivableDocumentHeaderDao(AccountsReceivableDocumentHeaderDao accountsReceivableDocumentHeaderDao) {
        this.accountsReceivableDocumentHeaderDao = accountsReceivableDocumentHeaderDao;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setCustomerInvoiceDetailDao(CustomerInvoiceDetailDao customerInvoiceDetailDao) {
        this.customerInvoiceDetailDao = customerInvoiceDetailDao;
    }

    public void setNonAppliedHoldingDao(NonAppliedHoldingDao nonAppliedHoldingDao) {
        this.nonAppliedHoldingDao = nonAppliedHoldingDao;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
