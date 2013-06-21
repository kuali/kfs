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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsAgingOpenInvoicesReport;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsAgingOpenInvoicesReportService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to get the services for PDF generation and other services for Contracts Grants Aging open Invoices report
 */
@Transactional
public class ContractsGrantsAgingOpenInvoicesReportServiceImpl implements ContractsGrantsAgingOpenInvoicesReportService {

    private ContractsGrantsAgingReportService contractsGrantsAgingReportService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;

    /**
     * Gets the contractsGrantsAgingReportService attribute.
     *
     * @return Returns the contractsGrantsAgingReportService.
     */
    public ContractsGrantsAgingReportService getContractsGrantsAgingReportService() {
        return contractsGrantsAgingReportService;
    }

    /**
     * Sets the contractsGrantsAgingReportService attribute value.
     *
     * @param contractsGrantsAgingReportService The contractsGrantsAgingReportService to set.
     */
    public void setContractsGrantsAgingReportService(ContractsGrantsAgingReportService contractsGrantsAgingReportService) {
        this.contractsGrantsAgingReportService = contractsGrantsAgingReportService;
    }

    /**
     * Gets the customerInvoiceDocumentService attribute.
     *
     * @return Returns the customerInvoiceDocumentService.
     */
    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    /**
     * Sets the customerInvoiceDocumentService attribute value.
     *
     * @param customerInvoiceDocumentService The customerInvoiceDocumentService to set.
     */
    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

    /**
     * This method populates ContractsGrantsAgingOpenInvoicesReportDetails (Contracts Grants Open Invoices Report)
     *
     * @param urlParameters
     */
    @Override
    public List getPopulatedReportDetails(Map urlParameters) {
        List results = new ArrayList();
        String customerNumber = ((String[]) urlParameters.get(KFSPropertyConstants.CUSTOMER_NUMBER))[0];
        String customerName = ((String[]) urlParameters.get(KFSPropertyConstants.CUSTOMER_NAME))[0];

        String orgCode = ObjectUtils.isNotNull(urlParameters.get(ArPropertyConstants.ContractsGrantsAgingReportFields.FORM_ORGANIZATION_CODE)) ? ((String[]) urlParameters.get(ArPropertyConstants.ContractsGrantsAgingReportFields.FORM_ORGANIZATION_CODE))[0] : null;
        String chartCode = ObjectUtils.isNotNull(urlParameters.get(ArPropertyConstants.ContractsGrantsAgingReportFields.FORM_CHART_CODE)) ? ((String[]) urlParameters.get(ArPropertyConstants.ContractsGrantsAgingReportFields.FORM_CHART_CODE))[0] : null;
        String strBeginDate = ObjectUtils.isNotNull(urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_BEGIN_DATE)) ? ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_BEGIN_DATE))[0] : null;
        String strEndDate = ObjectUtils.isNotNull(urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_END_DATE)) ? ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_END_DATE))[0] : null;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        java.sql.Date startDate = null;
        java.sql.Date endDate = null;
        List<ContractsGrantsInvoiceDocument> selectedInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {

            if (ObjectUtils.isNotNull(strBeginDate) && StringUtils.isNotEmpty(strBeginDate)) {
                startDate = new java.sql.Date(dateFormat.parse(strBeginDate).getTime());
            }

            if (ObjectUtils.isNotNull(strEndDate) && StringUtils.isNotEmpty(strEndDate)) {
                endDate = new java.sql.Date(dateFormat.parse(strEndDate).getTime());
            }

            Map<String, String> fieldValueMap = new HashMap<String, String>();
            for (Object key : urlParameters.keySet()) {
                String val = ((String[]) urlParameters.get(key))[0];
                fieldValueMap.put(key.toString(), val);
            }

            Map<String, List<ContractsGrantsInvoiceDocument>> map = contractsGrantsAgingReportService.filterContractsGrantsAgingReport(fieldValueMap, startDate, endDate);
            if (ObjectUtils.isNotNull(map) && !map.isEmpty()) {
                selectedInvoices = map.get(customerNumber + "-" + customerName);
            }

            if (selectedInvoices.size() == 0) {
                return results;
            }

        }
        catch (ParseException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        populateReportDetails(selectedInvoices, results);
        return results;
    }

    /**
     * This method prepare the report model object to display on jsp page.
     *
     * @param invoices
     * @param results
     */
    protected void populateReportDetails(List<ContractsGrantsInvoiceDocument> invoices, List results) {
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            ContractsGrantsAgingOpenInvoicesReport detail = new ContractsGrantsAgingOpenInvoicesReport();
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
            detail.setFinalInvoice(invoice.getInvoiceGeneralDetail().isFinalBillIndicator() ? KFSConstants.ParameterValues.STRING_YES : KFSConstants.ParameterValues.STRING_NO);
            // set agency number, proposal number, account number
            detail.setProposalNumber(invoice.getProposalNumber().toString());

            // Set Agency Number
            ContractsAndGrantsCGBAgency cgAgency = this.getAgencyByCustomer(invoice.getAccountsReceivableDocumentHeader().getCustomerNumber());
            if (ObjectUtils.isNotNull(cgAgency)) {
                detail.setAgencyNumber(cgAgency.getAgencyNumber());
            }

            // Set Account number
            List<CustomerInvoiceDetail> details = invoice.getSourceAccountingLines();
            String accountNum = (ObjectUtils.isNotNull(details) && CollectionUtils.isNotEmpty(details) && ObjectUtils.isNotNull(details.get(0))) ? details.get(0).getAccountNumber() : "";
            detail.setAccountNumber(accountNum);
            results.add(detail);

        }
    }

    /**
     * This method retrives the agecy for particular customer
     *
     * @param customerNumber
     * @return Returns the agency for the customer
     */
    private ContractsAndGrantsCGBAgency getAgencyByCustomer(String customerNumber) {
        Map args = new HashMap();
        args.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        return SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAgency.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAgency.class, args);
    }
}
