/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;

/**
 * This class handles Actions for lookup flow
 */

public class CustomerInvoiceAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceAction.class);

   // private static final String TOTALS_TABLE_KEY = "totalsTable";


    public CustomerInvoiceAction() {
        super();
    }

    

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
     
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }   

   public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
       CustomerInvoiceForm csForm = (CustomerInvoiceForm)form;
       csForm.setChartCode(null);
       csForm.setOrgCode(null);
       csForm.setOrgType(null); 
       csForm.setRunDate(null);
       return mapping.findForward(KFSConstants.MAPPING_BASIC);
   }   
   
   public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      CustomerInvoiceForm csForm = (CustomerInvoiceForm)form;
      
      csForm.setOperationSelected(true);
      Date date = csForm.getRunDate();
       AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
       List<File> reports = new ArrayList<File>();
       if (csForm.getOrgType() != null) {
      if (csForm.getOrgType().equals("B"))
          reports = reportService.generateInvoicesByBillingOrg(csForm.getChartCode(), csForm.getOrgCode(), date);
      else if (csForm.getOrgType().equals("P"))
          reports = reportService.generateInvoicesByProcessingOrg(csForm.getChartCode(), csForm.getOrgCode(), date);
       
     // System.out.println("invoiceAction");
      //csForm.setReports(reports);
      System.out.println(reports);
       }
    //   String fileName = csForm.getChartCode()+csForm.getOrgCode()+date+"-ConcatedPDFs.pdf";
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
          int pageOffset = 0;
          ArrayList master = new ArrayList();
          int f = 0;
       //   File file = new File(fileName);
          Document document = null;
          PdfCopy  writer = null;
          for (Iterator itr = reports.iterator(); itr.hasNext();) {
              // we create a reader for a certain document
              String reportName = ((File)itr.next()).getAbsolutePath();
              PdfReader reader = new PdfReader(reportName);
              reader.consolidateNamedDestinations();
              // we retrieve the total number of pages
              int n = reader.getNumberOfPages();
//              List bookmarks = SimpleBookmark.getBookmark(reader);
//              if (bookmarks != null) {
//                  if (pageOffset != 0)
//                      SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
//                  master.addAll(bookmarks);
//              }
              pageOffset += n;
              System.out.println("There are " + n + " pages in " + reportName);
              
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
                  System.out.println("Processed page " + i);
              }
              writer.freeReader(reader);
              f++;
          }
          if (!master.isEmpty())
              writer.setOutlines(master);
          // step 5: we close the document
          
          document.close();
         // csForm.setReports(file);
      }
      catch(Exception e) {
          e.printStackTrace();
      }
//      String fileName = "/Users/kwk43/ConcatedPDFs.pdf";
//      OutputStream out = response.getOutputStream();
//      File f = new File(fileName);
//      InputStream in = new BufferedInputStream(new FileInputStream(f));
//      byte[] bytes = new byte[(int)f.length()];
//      in.read(bytes);
//      out.write(bytes);
      
   //   request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, reports);
   
          // get directory of template
 //         String directory = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);
//System.out.println(directory);
         // DisbursementVoucherDocument document = (DisbursementVoucherDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(request.getParameter(KFSPropertyConstants.DOCUMENT_NUMBER));

          // set workflow document back into form to prevent document authorizer "invalid (null)
          // document.documentHeader.workflowDocument" since we are bypassing form submit and just linking directly to the action
         // DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
         // dvForm.getDocument().getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
          
//          ByteArrayOutputStream baos = new ByteArrayOutputStream();
//          File f = new File(fileName);
//        InputStream in = new BufferedInputStream(new FileInputStream(f));
//        byte[] bytes = new byte[(int)f.length()];
//        in.read(bytes);
//        baos.write(bytes);
        //  DisbursementVoucherCoverSheetService coverSheetService = SpringContext.getBean(DisbursementVoucherCoverSheetService.class);

          //coverSheetService.generateDisbursementVoucherCoverSheet(directory, DisbursementVoucherCoverSheetServiceImpl.DV_COVERSHEET_TEMPLATE_NM, document, baos);
              System.out.println(baos.size());
          String fileName = csForm.getChartCode()+csForm.getOrgCode()+date+"-ConcatedPDFs.pdf";   
          WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName);
          return (null);

      
   }
   
   
}
