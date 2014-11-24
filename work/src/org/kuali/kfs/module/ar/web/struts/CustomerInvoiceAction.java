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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * This class handles Actions for lookup flow
 */

public class CustomerInvoiceAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceAction.class);

    /**
     * 
     * Constructs a CustomerInvoiceAction.java.
     */
    public CustomerInvoiceAction() {
        super();
    }

    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }   

    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceForm ciForm = (CustomerInvoiceForm)form;
        ciForm.setChartCode(null);
        ciForm.setOrgCode(null);
        ciForm.setOrgType(null); 
        ciForm.setRunDate(null);
        ciForm.setMessage(null);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }   

    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceForm ciForm = (CustomerInvoiceForm)form;

        String org = ciForm.getOrgCode();
        String chart = ciForm.getChartCode();
        Date date = ciForm.getRunDate();
        
        StringBuilder fileName = new StringBuilder();
        
        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        List<File> reports = new ArrayList<File>();
        if (ciForm.getOrgType() != null && chart != null && org != null) {
            if (ciForm.getOrgType().equals("B")) {
                reports = reportService.generateInvoicesByBillingOrg(chart, org, date);
            }
            else if (ciForm.getOrgType().equals("P")) {
                reports = reportService.generateInvoicesByProcessingOrg(chart, org, date);
            }
            fileName.append(chart);
            fileName.append(org);
            if (date != null) {
                fileName.append(date);  
            }
        } else if (ciForm.getUserId() != null) {
            reports = reportService.generateInvoicesByInitiator(ciForm.getUserId(), date);
            fileName.append(ciForm.getUserId());
        }
        if (reports.size()>0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                int pageOffset = 0;
                ArrayList master = new ArrayList();
                int f = 0;
                Document document = null;
                PdfCopy  writer = null;
                for (Iterator<File> itr = reports.iterator(); itr.hasNext();) {
                    // we create a reader for a certain document
                    String reportName = itr.next().getAbsolutePath();
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
                // csForm.setReports(file);
            }
            catch(Exception e) {
                e.printStackTrace();
            } 
            fileName.append("-InvoiceBatchPDFs.pdf");
            

            WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName.toString());
            ciForm.setMessage(reports.size()+" Reports Generated");
            return null;
        }
        ciForm.setMessage("No Reports Generated");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
