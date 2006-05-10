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
package org.kuali.module.gl.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.kuali.module.gl.service.PosterService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.TransactionReport;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id$
 */
public class ReportServiceImpl implements ReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportServiceImpl.class);
    
    String reportsDirectory;

    public ReportServiceImpl() {
        super();
        
        ResourceBundle rb = ResourceBundle.getBundle("configuration");
        reportsDirectory = rb.getString("htdocs.directory") + "/reports";
    }

    public void generateIcrReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries) {
        LOG.debug("Entering generateIcrReports()");
        TransactionReport tr = new TransactionReport();
        String title = "ICR Generation Report";
        tr.generateReport(reportErrors,reportSummary,runDate,title,"icr_generation",reportsDirectory);
    }

    public void generatePosterReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries, int mode) {
        LOG.debug("Entering generatePosterReports()");

        TransactionReport tr = new TransactionReport();

        String title = "Poster Report ";
        String filename;
        if ( mode == PosterService.MODE_ENTRIES ) {
          title = title + "(Post pending entries)";
          filename = "poster_main";
        } else if ( mode == PosterService.MODE_ICR ) {
          title = title + "(Post ICR entries)";
          filename = "poster_icr";
        } else {
          title = title + "(Post reversal entries)";
          filename = "poster_reversal";
        }

        tr.generateReport(reportErrors,reportSummary,runDate,title,filename, reportsDirectory);
    }

    public void generateScrubberReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries) {
        LOG.debug("Entering generateScrubberReports()");

        String title = "Scrubber Report ";
        TransactionReport tr = new TransactionReport();
        tr.generateReport(reportErrors, reportSummary, runDate, title, "scrubber", reportsDirectory);

        title = "Ledger Report ";
        LedgerReport lr = new LedgerReport();
        lr.generateReport(ledgerEntries, runDate, title, "ledger", reportsDirectory);
    }

    public void generateYearEndEncumbranceForwardReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries) {
        LOG.debug("Entering generateYearEndEncumbranceReports()");
        TransactionReport transactionReport = new TransactionReport();
        String title = "Encumbrance Closing Report ";
        transactionReport.generateReport(null, reportSummary, runDate, title, "year_end_encumbrance_closing", reportsDirectory);
    }

    public void generateYearEndBalanceForwardReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries) {
        LOG.debug("Entering generateYearEndBalanceForwardReports()");
        TransactionReport transactionReport = new TransactionReport();
        String title = "Balance Forward Report ";
        transactionReport.generateReport(null, reportSummary, runDate, title, "year_end_balance_forward", reportsDirectory);
    }

}
