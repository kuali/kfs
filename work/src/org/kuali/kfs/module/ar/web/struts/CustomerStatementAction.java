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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * This class handles Actions for lookup flow
 */

public class CustomerStatementAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerStatementAction.class);

    public CustomerStatementAction() {
        super();
    }

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
            params.put("chartCode", chartCode);
        }
        if(StringUtils.isNotBlank(orgCode)) {
            params.put("orgCode", orgCode);
        }
        if(StringUtils.isNotBlank(customerNumber)) {
            params.put("customerNumber", customerNumber);
        }
        if(StringUtils.isNotBlank(accountNumber)) {
            params.put("accountNumber", accountNumber);
        }
        if(StringUtils.isNotBlank(statementFormat)) {
            params.put("statementFormat", statementFormat);
        } else {
            params.put("statementFormat", "Summary");   
        }
        if(StringUtils.isNotBlank(includeZeroBalanceCustomers)) {
            params.put("includeZeroBalanceCustomers", includeZeroBalanceCustomers);
        } else {
            params.put("includeZeroBalanceCustomers", "No");
        }
                
        String methodToCallPrintPDF = "printStatementPDF";
        String methodToCallStart = "start";
        String printPDFUrl = getUrlForPrintStatement(basePath, methodToCallPrintPDF, params);
        String displayTabbedPageUrl = getUrlForPrintStatement(basePath, methodToCallStart, params);
        
        request.setAttribute("printPDFUrl", printPDFUrl);
        request.setAttribute("displayTabbedPageUrl", displayTabbedPageUrl);
        if(!StringUtils.isBlank(chartCode)) {
            request.setAttribute("chartCode", chartCode);
        }
        if(!StringUtils.isBlank(orgCode)) {
            request.setAttribute("orgCode", orgCode);
        }
        if(!StringUtils.isBlank(customerNumber)) {
            request.setAttribute("customerNumber", customerNumber);
        }
        if(!StringUtils.isBlank(accountNumber)) {
            request.setAttribute("accountNumber", accountNumber);
        }
        request.setAttribute("printLabel", "Customer Statement");
        return mapping.findForward("arPrintPDF");
        
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
        if (reports.size() != 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                int pageOffset = 0;
                ArrayList<PdfReader> master = new ArrayList<PdfReader>();
                int f = 0;
                //   File file = new File(fileName);
                Document document = null;
                PdfCopy  writer = null;
                for (CustomerStatementResultHolder customerStatementResultHolder : reports) {
                    File file = customerStatementResultHolder.getFile();
                    // we create a reader for a certain document
                    String reportName = file.getAbsolutePath();
                    PdfReader reader = new PdfReader(reportName);
                    reader.consolidateNamedDestinations();
                    // we retrieve the total number of pages
                    int n = reader.getNumberOfPages();
                    List<PdfReader> bookmarks = SimpleBookmark.getBookmark(reader);
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
                        writer = new PdfCopy(document, baos);
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
                
                if (!master.isEmpty())
                    writer.setOutlines(master);
                // step 5: we close the document

                document.close();
                // csForm.setReports(file);
                
                StringBuffer sbContentDispValue = new StringBuffer();
                String useJavascript = request.getParameter("useJavascript");
                if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
                    sbContentDispValue.append("attachment");
                }
                else {
                    sbContentDispValue.append("inline");
                }
                sbContentDispValue.append("; filename=");
                sbContentDispValue.append(fileName);
                
                contentDisposition = sbContentDispValue.toString();
            }
            catch(Exception e) {
                LOG.error("problem during printStatementPDF()", e);
            } 

            fileName.append("-StatementBatchPDFs.pdf");

            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", contentDisposition);
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentLength(baos.size());

            // write to output
            ServletOutputStream sos = response.getOutputStream();
            baos.writeTo(sos);
            sos.flush();
            sos.close();
            
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
        StringBuffer result = new StringBuffer(basePath);
        result.append("/arCustomerStatement.do?methodToCall=").append(methodToCall);
        Set<String> keys = params.keySet();
        for(String key : keys) {
            result.append("&").append(key).append("=").append(params.get(key));
        }

        return result.toString();
    }

}
