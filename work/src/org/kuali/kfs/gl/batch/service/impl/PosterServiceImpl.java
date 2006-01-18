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
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.dao.IcrAutomatedEntryDao;
import org.kuali.module.chart.dao.IndirectCostRecoveryThresholdDao;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.PosterReport;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.ReversalEntry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.ExpenditureTransactionDao;
import org.kuali.module.gl.dao.ReversalEntryDao;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.PosterService;

/**
 * @author jsissom
 *  
 */
public class PosterServiceImpl implements PosterService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceImpl.class);

  private List transactionPosters;
  private VerifyTransaction verifyTransaction;
  private PosterReport posterReportService;
  private OriginEntryService originEntryService;
  private DateTimeService dateTimeService;
  private ReversalEntryDao reversalEntryDao;
  private UniversityDateDao universityDateDao;
  private AccountingPeriodService accountingPeriodService;
  private ExpenditureTransactionDao expenditureTransactionDao;
  private IcrAutomatedEntryDao icrAutomatedEntryDao;
  private IndirectCostRecoveryThresholdDao indirectCostRecoveryThresholdDao;

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
    UniversityDate runUniversityDate = universityDateDao.getByPrimaryKey(runDate);

    Collection groups = null;
    Iterator reversalTransactions = null;
    switch (mode) {
      case PosterService.MODE_ENTRIES:
        validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
        invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
        groups = originEntryService.getGroupsToPost(runDate);
        break;
      case PosterService.MODE_REVERSAL:
        validEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_VALID;
        invalidEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_ERROR;
        reversalTransactions = reversalEntryDao.getByDate(runDate);
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

    // Process all the groups or reversalTransactions
    if ( groups != null ) {
      LOG.debug("postEntries() Processing groups");
      for (Iterator iter = groups.iterator(); iter.hasNext();) {
        OriginEntryGroup group = (OriginEntryGroup) iter.next();

        Iterator entries = originEntryService.getEntriesByGroup(group);
        while (entries.hasNext()) {
          Transaction tran = (Transaction) entries.next();

          process(tran,mode,reportSummary,reportError,invalidGroup,validGroup,runUniversityDate);
        }
      }
    } else {
      LOG.debug("postEntries() Processing reversal transactions");
      while (reversalTransactions.hasNext()) {
        Transaction tran = (Transaction)reversalTransactions.next();

        process(tran,mode,reportSummary,reportError,invalidGroup,validGroup,runUniversityDate);
      }
    }

    // Generate the report
    posterReportService.generateReport(reportError, reportSummary, runDate, mode);
  }

  private void process(Transaction tran,int mode,Map reportSummary,Map reportError,OriginEntryGroup invalidGroup,OriginEntryGroup validGroup,UniversityDate runUniversityDate) {

    List errors = new ArrayList();

    ReversalEntry reversal = null;
    Transaction originalTransaction = tran;

    // Update select count in the report
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
      reversal = new ReversalEntry(tran);

      // Revese the debit/credit code
      if ("D".equals(reversal.getDebitOrCreditCode())) {
        reversal.setDebitOrCreditCode("C");
      } else if ("C".equals(reversal.getDebitOrCreditCode())) {
        reversal.setDebitOrCreditCode("D");
      }

      UniversityDate udate = universityDateDao.getByPrimaryKey(reversal.getDocumentReversalDate());
      if ( udate != null ) {
        reversal.setUniversityFiscalYear(udate.getUniversityFiscalYear());
        reversal.setUniversityFiscalAccountingPeriod(udate.getUniversityFiscalAccountingPeriod());

        AccountingPeriod ap = accountingPeriodService.getByPeriod(reversal.getUniversityFiscalAccountingPeriod(),reversal.getUniversityFiscalYear());
        if ( ap != null ) {
          if ( "C".equals(ap.getUniversityFiscalPeriodStatusCode()) ) {
            reversal.setUniversityFiscalYear(runUniversityDate.getUniversityFiscalYear());
            reversal.setUniversityFiscalAccountingPeriod(runUniversityDate.getUniversityFiscalAccountingPeriod());
          }
          reversal.setDocumentReversalDate(null);
          String newDescription = "AUTO REVERSAL-" + reversal.getTransactionLedgerEntryDescription();
          if ( newDescription.length() > 40 ) {
            newDescription = newDescription.substring(0,39);
          }
          reversal.setTransactionLedgerEntryDescription(newDescription);
          tran = reversal;
        } else {
          errors.add("Date from university date not in AccountingPeriod table");
        }
      } else {
        errors.add("Date from reversal not in UniversityDate table");
      }
    }

    if ( errors.size() == 0 ) {
      errors = verifyTransaction.verifyTransaction(tran);
    }

    if (errors.size() > 0) {
      // Error on this transaction
      reportError.put(tran, errors);
      addReporting(reportSummary, "~WARNING", "S");

      originEntryService.createEntry(tran, invalidGroup);
    } else {
      // Now check each poster to see if it needs to verify the transaction.  If
      // it returns errors, we won't post it
      errors = new ArrayList();
      for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
        PostTransaction poster = (PostTransaction) posterIter.next();
        if ( poster instanceof VerifyTransaction ) {
          VerifyTransaction vt = (VerifyTransaction)poster;

          errors.addAll(verifyTransaction.verifyTransaction(tran));
        }
      }

      if ( errors.size() > 0) {
        // Error on this transaction
        reportError.put(tran, errors);
        addReporting(reportSummary, "~WARNING", "S");

        originEntryService.createEntry(tran, invalidGroup);
      } else {
        // No error so post it
        for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
          PostTransaction poster = (PostTransaction) posterIter.next();
          String actionCode = poster.post(tran, mode, runUniversityDate.getUniversityDate());

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
      }

      originEntryService.createEntry(tran, validGroup);

      // Delete the reversal entry
      if ( mode == PosterService.MODE_REVERSAL ) {
        reversalEntryDao.delete( (ReversalEntry)originalTransaction );
      }
    }    
  }

  public void generateIcrTransactions() {
    LOG.debug("generateIcrTransactions() started");
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

  public void setVerifyTransaction(VerifyTransaction vt) {
    verifyTransaction = vt;
  }

  public void setTransactionPosters(List p) {
    transactionPosters = p;
  }

  public void setPosterReport(PosterReport prs) {
    posterReportService = prs;
  }

  public void setOriginEntryService(OriginEntryService oes) {
    originEntryService = oes;
  }

  public void setDateTimeService(DateTimeService dts) {
    dateTimeService = dts;
  }

  public void setReversalEntryDao(ReversalEntryDao red) {
    reversalEntryDao = red;
  }

  public void setUniversityDateDao(UniversityDateDao udd) {
    universityDateDao = udd;
  }

  public void setAccountingPeriodService(AccountingPeriodService aps) {
    accountingPeriodService = aps;
  }

  public void setExpenditureTransactionDao(ExpenditureTransactionDao etd) {
    expenditureTransactionDao = etd;
  }

  public void setIcrAutomatedEntryDao(IcrAutomatedEntryDao iaed) {
    icrAutomatedEntryDao = iaed;
  }

  public void setIndirectCostRecoveryThresholdDao(IndirectCostRecoveryThresholdDao icrtd) {
      indirectCostRecoveryThresholdDao = icrtd;
  }
}
