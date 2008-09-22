/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.dataaccess.AccountsReceivableDocumentHeaderDao;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerOpenItemReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.exception.UnknownDocumentIdException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerOpenItemReportServiceImpl implements CustomerOpenItemReportService {

    private AccountsReceivableDocumentHeaderDao accountsReceivableDocumentHeaderDao;
    private WorkflowDocumentService workflowDocumentService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private FinancialSystemDocumentHeaderDao financialSystemDocumentHeaderDao;
    private DocumentService documentService;
    private DateTimeService dateTimeService;

    /**
     * This method populates CustomerOpenItemReportDetails (Customer History Report).
     * 
     * @param customerNumber
     */
    public List getPopulatedReportDetails(String customerNumber) {
        List results = new ArrayList();

        Collection arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersByCustomerNumber(customerNumber);
        if (arDocumentHeaders.size() == 0)
            return results;

        UniversalUser user = GlobalVariables.getUserSession().getFinancialSystemUser();

        List finSysDocHeaderIds = new ArrayList();
        List invoiceIds = new ArrayList();
        List paymentApplicationIds = new ArrayList();

        Hashtable details = new Hashtable();
        KualiWorkflowDocument workflowDocument;


        for (Iterator itr = arDocumentHeaders.iterator(); itr.hasNext();) {
            AccountsReceivableDocumentHeader documentHeader = (AccountsReceivableDocumentHeader) itr.next();
            CustomerOpenItemReportDetail detail = new CustomerOpenItemReportDetail();

            // populate workflow document
            try {
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(documentHeader.getDocumentNumber()), user);
            }
            catch (WorkflowException e) {
                throw new UnknownDocumentIdException("No document found for documentHeaderId '" + documentHeader.getDocumentNumber() + "'", e);
            }

            // do not display not approved documents
            Date approvedDate = getSqlDate(workflowDocument.getRouteHeader().getDateApproved());
            if (ObjectUtils.isNull(approvedDate)) {
                continue;
            }

            // populate Document Type
            String documentType = workflowDocument.getDocumentType();
            detail.setDocumentType(documentType);

            // populate Document Number
            String documentNumber = documentHeader.getDocumentNumber();
            detail.setDocumentNumber(documentNumber);

            if (documentType.equals("CustomerInvoiceDocument"))
                invoiceIds.add(documentNumber);
            else {
                // populate Approved Date -> for invoices Due Date, for all other documents Approved Date
                detail.setDueApprovedDate(approvedDate);

                if (documentType.equals("PaymentApplicationDocument"))
                    paymentApplicationIds.add(documentNumber);
                else
                    finSysDocHeaderIds.add(documentNumber);
            }
            details.put(documentNumber, detail);
        }

        // for invoices
        if (invoiceIds.size() > 0)
            populateReportDetailsForInvoices(invoiceIds, results, details);

        // for payment applications
        if (paymentApplicationIds.size() > 0)
            populateReportDetailsForPaymentApplications(paymentApplicationIds, results, details);

        // for all other documents
        if (finSysDocHeaderIds.size() > 0)
            populateReportDetails(finSysDocHeaderIds, results, details);

        return results;
    }

    /**
     * This method populates CustomerOpenItemReportDetails for CustomerInvoiceDocuments (Customer History Report).
     * 
     * @param finSysDocHeaderIds <=> documentNumbers of CustomerInvoiceDocuments
     * @param results <=> CustomerOpenItemReportDetails to display in the report
     * @param details <=> <key = documentNumber, value = customerOpenItemReportDetail>
     */
    private void populateReportDetailsForInvoices(List invoiceIds, List results, Hashtable details) {
        CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection invoices = getDocuments(CustomerInvoiceDocument.class, invoiceIds);

        for (Iterator itr = invoices.iterator(); itr.hasNext();) {
            CustomerInvoiceDocument invoice = (CustomerInvoiceDocument) itr.next();
            String documentNumber = invoice.getDocumentNumber();

            CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

            // populate Document Description
            String documentDescription = invoice.getInvoiceDescription();
            if (ObjectUtils.isNotNull(documentDescription) && StringUtils.isNotEmpty(documentDescription))
                detail.setDocumentDescription(documentDescription);
            else {
                documentDescription = invoice.getDocumentHeader().getDocumentDescription();
                if (ObjectUtils.isNotNull(documentDescription))
                    detail.setDocumentDescription(documentDescription);
                else
                    detail.setDocumentDescription("");
            }

            // populate Due/Approved Date -> for invoice it is Due Date, and for all other documents Approved Date
            detail.setDueApprovedDate(invoice.getInvoiceDueDate());

            // populate Document Payment Amount
            detail.setDocumentPaymentAmount(invoice.getDocumentHeader().getFinancialDocumentTotalAmount());

            // populate Unpaid/Unapplied Amount
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
    private void populateReportDetailsForPaymentApplications(List paymentApplicationIds, List results, Hashtable details) {
        Collection paymentApplications = getDocuments(PaymentApplicationDocument.class, paymentApplicationIds);

        for (Iterator itr = paymentApplications.iterator(); itr.hasNext();) {
            PaymentApplicationDocument paymentApplication = (PaymentApplicationDocument) itr.next();
            String documentNumber = paymentApplication.getDocumentNumber();

            CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

            // populate Document Description
            String documentDescription = paymentApplication.getDocumentHeader().getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription))
                detail.setDocumentDescription(documentDescription);
            else
                detail.setDocumentDescription("");

            // populate Document Payment Amount
            detail.setDocumentPaymentAmount(paymentApplication.getTotalAppliedAmount().negated());

            // populate Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(paymentApplication.getTotalUnappliedFunds().negated());

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
    public void populateReportDetails(List finSysDocHeaderIds, List results, Hashtable details) {
        Collection financialSystemDocHeaders = financialSystemDocumentHeaderDao.getByDocumentNumbers(finSysDocHeaderIds);

        for (Iterator itr = financialSystemDocHeaders.iterator(); itr.hasNext();) {
            FinancialSystemDocumentHeader fsDocumentHeader = (FinancialSystemDocumentHeader) itr.next();
            String documentNumber = fsDocumentHeader.getDocumentNumber();

            CustomerOpenItemReportDetail detail = (CustomerOpenItemReportDetail) details.get(documentNumber);

            // populate Document Description
            String documentDescription = fsDocumentHeader.getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription))
                detail.setDocumentDescription(documentDescription);
            else
                detail.setDocumentDescription("");

            // populate Document Payment Amount
            detail.setDocumentPaymentAmount(fsDocumentHeader.getFinancialDocumentTotalAmount().negated());

            // Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(KualiDecimal.ZERO);

            results.add(detail);
        }
    }

    /*
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

    private Date getSqlDate(Calendar cal) {
        Date sqlDueDate = null;

        if (ObjectUtils.isNull(cal))
            return sqlDueDate;
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
    public List getPopulatedReportDetails(Map urlParameters) {
        List results = new ArrayList();
        
        // get arDocumentHeaders
        Collection<AccountsReceivableDocumentHeader> arDocumentHeaders = getARDocumentHeaders(urlParameters);
        if (arDocumentHeaders.size() == 0)
            return results;
        
        // get ids of arDocumentHeaders
        List<String> arDocumentHeaderIds = new ArrayList<String>();
        for (AccountsReceivableDocumentHeader arDocHeader : arDocumentHeaders) {
            arDocumentHeaderIds.add(arDocHeader.getDocumentNumber());
        }
        
        // get invoices
        Collection<CustomerInvoiceDocument> invoices = getDocuments(CustomerInvoiceDocument.class, arDocumentHeaderIds);
        if (ObjectUtils.isNull(invoices) | invoices.size()==0 )
            return results;
        
        List<CustomerInvoiceDocument> selectedInvoices = new ArrayList();

        String reportOption = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_OPTION))[0];
        String columnTitle = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.COLUMN_TITLE))[0];

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        
        java.util.Date reportRunDate= null;
        java.util.Date beginDate = null;
        java.util.Date endDate = null;
        try {
            reportRunDate = dateFormat.parse(((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_RUN_DATE))[0]);
            if (!StringUtils.equals(columnTitle, KFSConstants.CustomerOpenItemReport.ALL_DAYS)) {
                beginDate = dateFormat.parse(((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_BEGIN_DATE))[0]);
                endDate = dateFormat.parse(((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_END_DATE))[0]);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        
        // Processing Organization
        if (StringUtils.equals(reportOption, ArConstants.CustomerAgingReportFields.PROCESSING_ORG)) {
            
            // All days
            // 1. invoice open amount > 0
            // 2. invoice billing dates <= reportRunDate
            if (StringUtils.equals(columnTitle, KFSConstants.CustomerOpenItemReport.ALL_DAYS)) {
                for (CustomerInvoiceDocument invoice:invoices) {
                    if (ObjectUtils.isNull(invoice.getClosedDate()) && ObjectUtils.isNotNull(invoice.getBillingDate())&& !reportRunDate.before(invoice.getBillingDate()))
                        selectedInvoices.add(invoice);
                }
            }
            // *days
            // 1. invoice open amount > 0
            // 2. beginDate <= invoice billing date <= endDate
            else {
                for (CustomerInvoiceDocument invoice:invoices) {
                    if (ObjectUtils.isNull(invoice.getClosedDate())&& ObjectUtils.isNotNull(invoice.getBillingDate()) && !beginDate.after(invoice.getBillingDate()) && !endDate.before(invoice.getBillingDate()) )
                        selectedInvoices.add(invoice);
                }  
            }
        }
        // Billing Organization
        else if (StringUtils.equals(reportOption, ArConstants.CustomerAgingReportFields.BILLING_ORG)) {
            // All days
            // 1. with open amounts > 0
            // 2. billingDates <= reportRunDate
            // 3. billByChartOfAccountsCode = chartCode
            // 4. billbyOrganizationCode = orgCode
            String chartCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.CHART_CODE))[0];
            String orgCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.ORGANIZATION_CODE))[0];
            if (StringUtils.equals(columnTitle, KFSConstants.CustomerOpenItemReport.ALL_DAYS)) {
                for (CustomerInvoiceDocument invoice:invoices) {
                    // get only invoices with open amounts
                    if (ObjectUtils.isNull(invoice.getClosedDate())&& ObjectUtils.isNotNull(invoice.getBillingDate()) && !reportRunDate.before(invoice.getBillingDate()) && StringUtils.equals(chartCode, invoice.getBillByChartOfAccountCode()) && StringUtils.equals(orgCode, invoice.getBilledByOrganizationCode()) )
                        selectedInvoices.add(invoice);
                }
            }
            // *days
            // 1. invoice open amount > 0
            // 2. beginDate <= invoice billing date <= endDate
            // 3. billByChartOfAccountsCode = chartCode
            // 4. billbyOrganizationCode = orgCode
            else {
                for (CustomerInvoiceDocument invoice:invoices) {
                    if (ObjectUtils.isNull(invoice.getClosedDate())&& ObjectUtils.isNotNull(invoice.getBillingDate()) && !beginDate.after(invoice.getBillingDate()) && !endDate.before(invoice.getBillingDate()) && StringUtils.equals(chartCode, invoice.getBillByChartOfAccountCode()) && StringUtils.equals(orgCode, invoice.getBilledByOrganizationCode()) )
                        selectedInvoices.add(invoice);
                }
            }
        } 
        // Account
        else {
            // All days
            // 1. invoice open amount > 0
            // 2. invoice billing dates <= reportRunDate
            // 3. paymentAccountNumber = accountNumber
            String accountNumber = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.ACCOUNT_NUMBER))[0];
            if (StringUtils.equals(columnTitle, KFSConstants.CustomerOpenItemReport.ALL_DAYS)) {
                for (CustomerInvoiceDocument invoice:invoices) {
                    // get only invoices with open amounts
                    if (ObjectUtils.isNull(invoice.getClosedDate())&& ObjectUtils.isNotNull(invoice.getBillingDate()) && !reportRunDate.before(invoice.getBillingDate()) && ObjectUtils.isNotNull(invoice.getPaymentAccountNumber()) && StringUtils.equals(accountNumber, invoice.getPaymentAccountNumber()) )
                        selectedInvoices.add(invoice);
                }
            }
            // *days
            // 1. invoice open amount > 0
            // 2. beginDate <= invoice billing date <= endDate
            // 3. paymentAccountNumber = accountNumber
            else {
                for (CustomerInvoiceDocument invoice:invoices) {
                    if (ObjectUtils.isNull(invoice.getClosedDate())&& ObjectUtils.isNotNull(invoice.getBillingDate()) && !beginDate.after(invoice.getBillingDate()) && !endDate.before(invoice.getBillingDate())  && ObjectUtils.isNotNull(invoice.getPaymentAccountNumber()) && StringUtils.equals(accountNumber, invoice.getPaymentAccountNumber()) )
                        selectedInvoices.add(invoice);
                }
            }
        }
        if (selectedInvoices.size() == 0)
            return results;
        
        populateReportDetails(selectedInvoices,results);
        
        return results;
    }

    /**
     * This method retrieves ARDocumentHeader objects for "Customer Open Item Report"
     * 
     * @param urlParameters
     * @return ARDocumentHeader objects meeting the search criteria
     */
    private Collection getARDocumentHeaders(Map urlParameters) {
        Collection arDocumentHeaders;

        String reportOption = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_OPTION))[0];
        String customerNumber = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER))[0];

        if (StringUtils.equals(reportOption, ArConstants.CustomerAgingReportFields.PROCESSING_ORG)) {
            String processingChartCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.CHART_CODE))[0];
            String processingOrganizationCode = ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.ORGANIZATION_CODE))[0];
            arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersByCustomerNumberByProcessingOrgCodeAndChartCode(customerNumber, processingChartCode, processingOrganizationCode);
        } // reportOption is "Billing Organization" or "Account"
        else {
            arDocumentHeaders = accountsReceivableDocumentHeaderDao.getARDocumentHeadersByCustomerNumber(customerNumber);
        }
        return arDocumentHeaders;
    }
    
    private void populateReportDetails(List<CustomerInvoiceDocument> invoices, List results) {
        for (CustomerInvoiceDocument invoice:invoices) {
            CustomerOpenItemReportDetail detail = new CustomerOpenItemReportDetail();
            // Document Type
            detail.setDocumentType(invoice.getDocumentHeader().getWorkflowDocument().getDocumentType());
            // Document Number
            detail.setDocumentNumber(invoice.getDocumentNumber());
            // Document Description
            String documentDescription = invoice.getInvoiceDescription();
            if (ObjectUtils.isNotNull(documentDescription) && StringUtils.isNotEmpty(documentDescription))
                detail.setDocumentDescription(documentDescription);
            else {
                documentDescription = invoice.getDocumentHeader().getDocumentDescription();
                if (ObjectUtils.isNotNull(documentDescription))
                    detail.setDocumentDescription(documentDescription);
                else
                    detail.setDocumentDescription("");
            }
            // Due Date
            detail.setDueApprovedDate(invoice.getInvoiceDueDate());
            // Document Payment Amount
            detail.setDocumentPaymentAmount(invoice.getDocumentHeader().getFinancialDocumentTotalAmount());
            // Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(customerInvoiceDocumentService.getOpenAmountForCustomerInvoiceDocument(invoice));
            results.add(detail);
            
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

    public void setFinancialSystemDocumentHeaderDao(FinancialSystemDocumentHeaderDao financialSystemDocumentHeaderDao) {
        this.financialSystemDocumentHeaderDao = financialSystemDocumentHeaderDao;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
