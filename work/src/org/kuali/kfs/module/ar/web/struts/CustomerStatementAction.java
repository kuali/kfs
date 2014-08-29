/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.kfs.module.ar.service.AccountsReceivableWebUtilityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsWebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class handles Actions for lookup flow
 */
public class CustomerStatementAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerStatementAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerStatementForm csForm = (CustomerStatementForm)form;
        csForm.clear();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = getApplicationBaseUrl();
        CustomerStatementForm csForm = (CustomerStatementForm) form;
        String chartCode = csForm.getChartCode();
        String orgCode = csForm.getOrgCode();
        String customerNumber = csForm.getCustomerNumber();
        String accountNumber = csForm.getAccountNumber();
        String statementFormat = csForm.getStatementFormat();
        String includeZeroBalanceCustomers = csForm.getIncludeZeroBalanceCustomers();

        HashMap<String, String> params = new HashMap<String, String>();
        if(StringUtils.isNotBlank(chartCode)) {
            params.put(ArPropertyConstants.CustomerStatementFields.CHART_CODE, chartCode);
            request.setAttribute(ArPropertyConstants.CustomerStatementFields.CHART_CODE, chartCode);
        }
        if(StringUtils.isNotBlank(orgCode)) {
            params.put(ArPropertyConstants.CustomerStatementFields.ORG_CODE, orgCode);
            request.setAttribute(ArPropertyConstants.CustomerStatementFields.ORG_CODE, orgCode);
        }
        if(StringUtils.isNotBlank(customerNumber)) {
            params.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customerNumber);
            request.setAttribute(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customerNumber);
        }
        if(StringUtils.isNotBlank(accountNumber)) {
            params.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            request.setAttribute(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }
        if(StringUtils.isNotBlank(statementFormat)) {
            params.put(ArPropertyConstants.CustomerStatementFields.STATEMENT_FORMAT, statementFormat);
        } else {
            params.put(ArPropertyConstants.CustomerStatementFields.STATEMENT_FORMAT, ArConstants.STATEMENT_FORMAT_SUMMARY);
        }
        if(StringUtils.isNotBlank(includeZeroBalanceCustomers)) {
            params.put(ArPropertyConstants.CustomerStatementFields.INCLUDE_ZERO_BALANCE_CUSTOMERS, includeZeroBalanceCustomers);
        } else {
            params.put(ArPropertyConstants.CustomerStatementFields.INCLUDE_ZERO_BALANCE_CUSTOMERS, ArConstants.INCLUDE_ZERO_BALANCE_NO);
        }

        String printPDFUrl = getUrlForPrintStatement(basePath, ArConstants.PRINT_STATEMENT_PDF_METHOD, params);
        String displayTabbedPageUrl = getUrlForPrintStatement(basePath, KFSConstants.START_METHOD, params);

        request.setAttribute(ArPropertyConstants.PRINT_PDF_URL, printPDFUrl);
        request.setAttribute(ArPropertyConstants.DISPLAY_TABBED_PAGE_URL, displayTabbedPageUrl);
        request.setAttribute(ArPropertyConstants.PRINT_LABEL, ArConstants.CUSTOMER_STATEMENT_LABEL);

        return mapping.findForward(ArConstants.MAPPING_PRINT_PDF);
    }

    public ActionForward printStatementPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerStatementForm csForm = (CustomerStatementForm)form;
        String chartCode = request.getParameter("chartCode");
        chartCode = chartCode==null?"":chartCode;
        String orgCode = request.getParameter("orgCode");
        orgCode = orgCode==null?"":orgCode;
        String customerNumber = request.getParameter("customerNumber");
        customerNumber = customerNumber==null?"":customerNumber;
        String accountNumber = request.getParameter("accountNumber");
        accountNumber = accountNumber==null?"":accountNumber;
        String statementFormat = request.getParameter("statementFormat");
        String includeZeroBalanceCustomers = request.getParameter("includeZeroBalanceCustomers");

        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        List<CustomerStatementResultHolder> reports = new ArrayList<CustomerStatementResultHolder>();

        StringBuilder fileName = new StringBuilder();
        String contentDisposition = "";

        if ( !StringUtils.isBlank(chartCode) && !StringUtils.isBlank(orgCode)) {
            reports = reportService.generateStatementByBillingOrg(chartCode, orgCode, statementFormat, includeZeroBalanceCustomers);
            fileName.append(chartCode);
            fileName.append(orgCode);
        } else if (!StringUtils.isBlank(customerNumber)) {
            reports = reportService.generateStatementByCustomer(customerNumber.toUpperCase(), statementFormat, includeZeroBalanceCustomers);
            fileName.append(customerNumber);
        } else if (!StringUtils.isBlank(accountNumber)) {
            reports = reportService.generateStatementByAccount(accountNumber, statementFormat, includeZeroBalanceCustomers);
            fileName.append(accountNumber);
        }
        fileName.append("-StatementBatchPDFs.pdf");

        if (reports.size() != 0) {
            List<byte[]> contents = new ArrayList<>();
            for (CustomerStatementResultHolder customerStatementResultHolder : reports) {
                File file = customerStatementResultHolder.getFile();
                byte[] data = Files.readAllBytes(file.toPath());
                contents.add(data);
            }

            ByteArrayOutputStream baos = SpringContext.getBean(AccountsReceivableWebUtilityService.class).buildPdfOutputStream(contents);
            KfsWebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, fileName.toString(), Boolean.parseBoolean(request.getParameter(KFSConstants.ReportGeneration.USE_JAVASCRIPT)));

            // update reported data for the detailed statement
            if (statementFormat.equalsIgnoreCase(ArConstants.STATEMENT_FORMAT_DETAIL)) {
                CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
                for (CustomerStatementResultHolder data : reports) {
                    // update reported invoice info
                    if (data.getInvoiceNumbers() != null) {
                        List<String> invoiceNumbers = data.getInvoiceNumbers();
                        for (String number : invoiceNumbers) {
                            customerInvoiceDocumentService.updateReportedDate(number);
                        }
                    }
                    // update reported customer info
                    customerInvoiceDocumentService.updateReportedInvoiceInfo(data);
                }
            }

            return null;
        }
        csForm.setMessage("No Reports Generated");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Creates a URL to be used in printing the purchase order.
     *
     * @param basePath String: The base path of the current URL
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    private String getUrlForPrintStatement(String basePath, String methodToCall, HashMap<String, String> params) {
        String baseUrl = basePath + "/" + ArConstants.UrlActions.CUSTOMER_STATEMENT;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.putAll(params);

        return UrlFactory.parameterizeUrl(baseUrl, parameters);
    }

}
