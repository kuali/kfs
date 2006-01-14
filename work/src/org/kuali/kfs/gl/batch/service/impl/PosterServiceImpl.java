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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.ReversalEntry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.PosterReportService;
import org.kuali.module.gl.service.PosterService;
import org.kuali.module.gl.service.VerifyTransactionService;

/**
 * @author jsissom
 *  
 */
public class PosterServiceImpl implements PosterService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceImpl.class);

  private List transactionPosters;
  private VerifyTransactionService verifyTransaction;
  private PosterReportService posterReportService;
  private OriginEntryService originEntryService;
  private DateTimeService dateTimeService;

  /**
   *  
   */
  public PosterServiceImpl() {
    super();
  }

  /**
   * Post scrubbed GL entries to GL tables.
   */
  public void postMainEntries() {
    LOG.debug("postMainEntries() started");
    postEntries(PosterService.MODE_ENTRIES);
  }
  
  /**
   * Post reversal GL entries to GL tables.
   */
  public void postReversalEntries() {
    LOG.debug("postReversalEntries() started");
    postEntries(PosterService.MODE_REVERSAL);
  }

  /**
   * Post ICR GL entries to GL tables.
   */
  public void postIcrEntries() {
    LOG.debug("postIcrEntries() started");
    postEntries(PosterService.MODE_ICR);
  }

  /*
   * Actually post the entries.  The mode variable
   * decides which entries to post.
   */
  private void postEntries(int mode) {
    LOG.debug("postEntries() started");

    String validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
    String invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
    OriginEntryGroup validGroup = null;
    OriginEntryGroup invalidGroup = null;

    Date runDate = dateTimeService.getCurrentDate();

    Collection groups = null;
    switch (mode) {
      case PosterService.MODE_ENTRIES:
        validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
        invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
        groups = originEntryService.getGroupsToPost(runDate);
        break;
      case PosterService.MODE_REVERSAL:
        validEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_VALID;
        invalidEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_ERROR;

        // TODO get the reversal entries from the reversal table
        throw new IllegalArgumentException("Not implemented....");
      case PosterService.MODE_ICR:
        validEntrySourceCode = OriginEntrySource.ICR_POSTER_VALID;
        invalidEntrySourceCode = OriginEntrySource.ICR_POSTER_ERROR;
        groups = originEntryService.getIcrGroupsToPost(runDate);
        break;
      default:
        throw new IllegalArgumentException("Invalid poster mode " + mode);
    }

    // Create new Groups for output transactions
    validGroup = originEntryService.createGroup(runDate, validEntrySourceCode, true, false, false);
    invalidGroup = originEntryService.createGroup(runDate, invalidEntrySourceCode, false, false, false);

    Map reportError = new HashMap();

    // Build the summary map so all the possible combinations of destination &
    // operation
    // are included in the summary part of the report.
    Map reportSummary = new HashMap();
    for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
      PostTransaction poster = (PostTransaction) posterIter.next();
      reportSummary.put(poster.getDestinationName() + ",D", new Integer(0));
      reportSummary.put(poster.getDestinationName() + ",I", new Integer(0));
      reportSummary.put(poster.getDestinationName() + ",U", new Integer(0));
    }

    for (Iterator iter = groups.iterator(); iter.hasNext();) {
      OriginEntryGroup group = (OriginEntryGroup) iter.next();

      Iterator entries = originEntryService.getEntriesByGroup(group);
      while (entries.hasNext()) {
        Transaction tran = (Transaction) entries.next();

        if (mode == PosterService.MODE_ENTRIES) {
          addReporting(reportSummary, " GL_ORIGIN_ENTRY_T", "S");
        } else if (mode == PosterService.MODE_REVERSAL) {
          addReporting(reportSummary, " GL_REVERSAL_T", "S");
        } else {
          addReporting(reportSummary, " GL_ORIGIN_ENTRY_T (ICR)", "S");
        }

        // If these are reversal entries, we need to reverse the entry and
        // modify a few fields
        if (mode == PosterService.MODE_REVERSAL) {
          if (!(tran instanceof ReversalEntry)) {
            throw new IllegalArgumentException("Reversal transactions must be in the Reversal class, not " + tran.getClass().getName());
          }

          // Revese the debit/credit code
          ReversalEntry reversal = (ReversalEntry) tran;
          if ("D".equals(reversal.getDebitOrCreditCode())) {
            reversal.setDebitOrCreditCode("C");
          } else if ("C".equals(reversal.getDebitOrCreditCode())) {
            reversal.setDebitOrCreditCode("D");
          }
          reversal.setReversalDate(null);

          // TODO Look up univ_fiscal_yr & univ_fiscal_prd_cd in sh_unit_date_t,fatal if not found
          // TODO Look up period in AccountingPeriod to see if it is open, if closed, use today's date & period & print warning
        }

        List errors = verifyTransaction.verifyTransaction(tran);
        if (errors.size() > 0) {
          // Error on this transaction
          reportError.put(tran, errors);
          addReporting(reportSummary, "~WARNING", "S");

          originEntryService.createEntry(tran, invalidGroup);
        } else {
          // No error so post it
          for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
            PostTransaction poster = (PostTransaction) posterIter.next();
            String actionCode = poster.post(tran, mode, runDate);

            if (actionCode.startsWith("E") ) {
              errors = new ArrayList();
              errors.add(actionCode);
              reportError.put(tran, errors);
            } else if (actionCode.indexOf("I") >= 0) {
              addReporting(reportSummary, poster.getDestinationName(), "I");
            } else if (actionCode.indexOf("U") >= 0) {
              addReporting(reportSummary, poster.getDestinationName(), "U");
            } else if (actionCode.indexOf("D") >= 0) {
              addReporting(reportSummary, poster.getDestinationName(), "D");
            }
          }

          originEntryService.createEntry(tran, validGroup);
        }
      }
    }

    // Generate the report
    posterReportService.generateReport(reportError, reportSummary, runDate, mode);
  }

  private void addReporting(Map reporting, String destination, String operation) {
    String key = destination + "," + operation;
    if (reporting.containsKey(key)) {
      Integer c = (Integer) reporting.get(key);
      reporting.put(key, new Integer(c.intValue() + 1));
    } else {
      reporting.put(key, new Integer(1));
    }
  }

  public void setVerifyTransactionService(VerifyTransactionService vt) {
    verifyTransaction = vt;
  }

  public void setTransactionPosters(List p) {
    transactionPosters = p;
  }

  public void setPosterReportService(PosterReportService prs) {
    posterReportService = prs;
  }

  public void setOriginEntryService(OriginEntryService oes) {
    originEntryService = oes;
  }

  public void setDateTimeService(DateTimeService dts) {
    dateTimeService = dts;
  }
}
