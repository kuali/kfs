/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.util;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.Transaction;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class...
 * @author Bin Gao
 */
public abstract class AbstractPdfReportGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractPdfReportGenerator.class);
    public static final String PDF_FILE_EXTENSION = ".pdf";

    // generate the PDF report with the given information
    public void generatePdfReport(Date reportingDate, String title, String reportFileName) {
        Document document = this.getDocument();

        PDFPageHelper pageHelper = new PDFPageHelper();
        pageHelper.setRunDate(reportingDate);
        pageHelper.setHeaderFont(this.getHeaderFont());
        pageHelper.setTitle(title);

        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(reportFileName));
            pdfWriter.setPageEvent(pageHelper);

            document.open();
            document.add(this.getReportContents());
        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }
        finally {
            this.closeDocument(document);
        }
    }
    
    public abstract Element getReportContents();
    
    protected String generateReportFileName(String reportNamePrefix, String destinationDirectory, Date reportingDate){        
        String reportFilename = destinationDirectory + "/" + reportNamePrefix + "_";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        reportFilename = reportFilename + dateFormat.format(reportingDate);
        reportFilename = reportFilename + PDF_FILE_EXTENSION;
        
        return reportFilename;
    }
    
    protected Document getDocument(){
        return new Document(PageSize.A4.rotate());
    }
    
    protected Font getHeaderFont(){
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
    }
    
    protected Font getTextFont(){
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
    }

    // close the document and release the resource
    private void closeDocument(Document document) {
        try {
            if ((document != null) && document.isOpen()) {
                document.close();
            }
        }
        catch (Throwable t) {
            LOG.error("generateReport() Exception closing report", t);
        }
    }
}
