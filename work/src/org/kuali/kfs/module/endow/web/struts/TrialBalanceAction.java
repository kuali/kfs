/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.web.struts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.web.struts.CustomerStatementAction;
import org.kuali.kfs.module.endow.report.TrialBalanceReport;
import org.kuali.kfs.module.endow.report.service.TrialBalanceReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TrialBalanceAction extends KualiAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerStatementAction.class);

    public TrialBalanceAction() {
        super();
    }

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm)form;
        trialBalanceForm.setMessage("Ready to generate reports");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm)form;
        trialBalanceForm.clear();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String basePath = getApplicationBaseUrl();
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm) form;
        String kemid= trialBalanceForm.getKemid();
        String benefittingOrganziationCampus= trialBalanceForm.getBenefittingOrganziationCampus();
        String benefittingOrganziationChart= trialBalanceForm.getBenefittingOrganziationChart();
        String benefittingOrganziation= trialBalanceForm.getBenefittingOrganziation();
        String typeCode= trialBalanceForm.getTypeCode();
        String purposeCode= trialBalanceForm.getPurposeCode();
        String combineGroupCode= trialBalanceForm.getCombineGroupCode();
        String asOfDate= trialBalanceForm.getAsOfDate();
        String endowmnetOption= trialBalanceForm.getEndowmnetOption();
        String message= trialBalanceForm.getMessage();

        // validate
        List<TrialBalanceReport> trialBalanceReports = null;
        if (StringUtils.isNotEmpty(kemid)) {
            List<String> kemids = new ArrayList<String>();
            kemids.add(kemid);
            
            TrialBalanceReportService trialBalanceReportService = SpringContext.getBean(TrialBalanceReportService.class);
            trialBalanceReports = trialBalanceReportService.getTrialBalanceReports(kemids);
        }
        
        if (trialBalanceReports != null && trialBalanceReports.size() > 0) {
            Document document = new Document();
            document.setPageSize(PageSize.LETTER);
            document.addTitle("Endowment Trial Balance");
            
            try {
                response.setContentType("application/pdf");
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();
    
                // headers
                Phrase header = new Paragraph(new Date().toString());
                document.add(header);
                
                Paragraph title = new Paragraph("KEMID Trial Balance");
                title.setAlignment(Element.ALIGN_CENTER);
                title.add("\nAs of <Date>\n\n\n");
                document.add(title);
                           
                PdfPTable table = new PdfPTable(7);
                // titles
                table.addCell("KEMID");
                table.addCell("KEMID Name");
                table.addCell("Income Cash Balance");
                table.addCell("Principal\nCash\nBalance");
                table.addCell("KEMID Total\nMarket Value");
                table.addCell("Available\nExpendable\nFunds");
                table.addCell("FY Remainder\nEstimated\nIncome");
                //body
                for (TrialBalanceReport trialBalanceReport : trialBalanceReports) {
                    table.addCell(trialBalanceReport.getKemid());
                    table.addCell(trialBalanceReport.getKemidName());
                    table.addCell(trialBalanceReport.getInocmeCashBalance().toString());
                    table.addCell(trialBalanceReport.getPrincipalcashBalance().toString());
                    table.addCell(trialBalanceReport.getKemidTotalMarketValue().toString());
                    table.addCell(trialBalanceReport.getAvailableExpendableFunds().toString());
                    table.addCell(trialBalanceReport.getFyRemainderEstimatedIncome().toString());
                }
                //totals
                table.addCell("TOTALS");
                table.addCell("");
                table.addCell("SUM2");
                table.addCell("SUM4");
                table.addCell("SUM5");
                table.addCell("SUM6");
                table.addCell("SUM7");
                document.add(table);
                
            } catch (Exception e) {
                
            }
            document.close();
        }
                
        trialBalanceForm.setMessage("Printing is done");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);      
        
    }
    
    public ActionForward printStatementPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm)form;
        System.out.println("**********************************asdfasdfsdfsdf");
        /*
        String chartCode = request.getParameter("chartCode");
        chartCode = chartCode==null?"":chartCode;
        String orgCode = request.getParameter("orgCode");
        orgCode = orgCode==null?"":orgCode;
        String customerNumber = request.getParameter("customerNumber");
        customerNumber = customerNumber==null?"":customerNumber;
        String accountNumber = request.getParameter("accountNumber");
        accountNumber = accountNumber==null?"":accountNumber;
        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        List<File> reports = new ArrayList<File>();
        
        StringBuilder fileName = new StringBuilder();
        String contentDisposition = "";
        
        if ( !StringUtils.isBlank(chartCode) && !StringUtils.isBlank(orgCode)) {
            reports = reportService.generateStatementByBillingOrg(chartCode, orgCode); 
            fileName.append(chartCode);
            fileName.append(orgCode);
        } else if (!StringUtils.isBlank(customerNumber)) {
            reports = reportService.generateStatementByCustomer(customerNumber.toUpperCase());
            fileName.append(customerNumber);
        } else if (!StringUtils.isBlank(accountNumber)) {
            reports = reportService.generateStatementByAccount(accountNumber);
            fileName.append(accountNumber);
        }
        if (reports.size() !=0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                int pageOffset = 0;
                ArrayList master = new ArrayList();
                int f = 0;
                //   File file = new File(fileName);
                Document document = null;
                PdfCopy  writer = null;
                for (File file : reports) {
                    // we create a reader for a certain document
                    String reportName = file.getAbsolutePath();
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
                // trialBalanceForm.setReports(file);
                
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
                e.printStackTrace();
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
            
            return null;
        }
        */
        trialBalanceForm.setMessage("No Reports Generated");
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
        result.append("/endowReportTrialBalance.do?methodToCall=").append(methodToCall);
        Set<String> keys = params.keySet();
        for(String key : keys) {
            result.append("&").append(key).append("=").append(params.get(key));
        }

        return result.toString();
    }
}
