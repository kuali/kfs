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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ScrubberService;
import org.springframework.util.StringUtils;

/**
 * @author Anthony Potts
 * @version $Id: ScrubberServiceImpl.java,v 1.21 2006-01-30 18:05:50 wesprice Exp $
 */

public class ScrubberServiceImpl implements ScrubberService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceImpl.class);

    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private OffsetDefinitionService offsetDefinitionService;
    private ObjectCodeService objectCodeService;
    private ChartService chartService;
    private AccountService accountService;
    private KualiConfigurationService kualiConfigurationService;
    private UniversityDateDao universityDateDao;
    private Date runDate;
    private Calendar runCal;
    UniversityDate univRunDate;
    Collection groupsToScrub;
    OriginEntryGroup validGroup;
    OriginEntryGroup errorGroup;
    OriginEntryGroup expiredGroup;
    Map documentError;
    Map reportSummary;
    List transactionErrors;

    private OriginEntry previousEntry;
    private Calendar wsPreviousCal;
    private String wsAccountChange;
    private String wsExpiredChart;
    private String wsExpiredAccount;
    private boolean eof;
    
    private int writeSwitchStatusCD = 0;

    private ScrubberUtil scrubberUtil = new ScrubberUtil();

    public ScrubberServiceImpl() {
      super();
    	originEntryGroupService = new OriginEntryGroupServiceImpl();
    }
    
//    MessageResources messages = getResources(SpringServiceLocator.KUALI_CONFIGURATION_SERVICE);
    
    /* (non-Javadoc)
     * @see org.kuali.module.gl.service.ScrubberService#scrubEntries()
     */
    public void scrubEntries() {
        documentError = new HashMap();
        reportSummary = new HashMap();
        
        // setup an object to hold the "default" date information
        runDate = new Date(dateTimeService.getCurrentDate().getTime());
        univRunDate = universityDateDao.getByPrimaryKey(runDate);
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);
        if (univRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        // Create the groups that will store the valid and error entries that come out of the scrubber
        validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, true, true, false);
        errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, true, false);
        expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, true, false);

        groupsToScrub = originEntryGroupService.getGroupsToScrub(runDate);

        for (Iterator groupIterator = groupsToScrub.iterator(); groupIterator.hasNext();) {
            OriginEntryGroup grp = (OriginEntryGroup) groupIterator.next();

            for (Iterator entryIterator = originEntryService.getEntriesByGroup(grp); entryIterator.hasNext();) {
                ++scrubberUtil.originCount;
                eof = !entryIterator.hasNext();
                OriginEntry entry = (OriginEntry) entryIterator.next();
                processUnitOfWork(entry, previousEntry);
            }
           // if no errors, set "process" of the incoming group to "N"

            if (documentError.size() == 0) {
                grp.setProcess(new Boolean(false));
                originEntryGroupService.save(grp);
            }
        }

        // write out report and errors
        // PRINT_STATISTICS
        
/*      
 *      print out final error that says what errors were found:
 *      if WS-fatal-errors-yes = yes then... "HIGHEST SEVERITY ERRORS WERE FATAL"
 *      
 *      if WS-NON-FATAL-ERRORS-YES = yes then... "HIGHEST SEVERITY ERRORS WERE NON-FATAL"
 *      
 *      if WS-WARNING-YES = yes then... "HIGHEST SEVERITY ERRORS WERE WARNINGS"
*/
        }

    private OriginEntry processUnitOfWork(OriginEntry originEntry, OriginEntry previousEntry) { /* 2500-process-unit-of-work */
        transactionErrors = new ArrayList();

        OriginEntry workingEntry = null;
        
        // first, need to see if the key fields are the same as last unit of
        // work (for performance reasons)
        checkUnitOfWork(originEntry, workingEntry);

        workingEntry = new OriginEntry();

        if (StringUtils.hasText(originEntry.getSubAccountNumber()) && !Constants.DASHES_SUB_ACCOUNT_NUMBER.equals(originEntry.getSubAccountNumber())) {
            workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
        } else {
            workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }

        if (StringUtils.hasText(originEntry.getFinancialSubObjectCode()) && !Constants.DASHES_SUB_OBJECT_CODE.equals(originEntry.getFinancialSubObjectCode())) {
            workingEntry.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode());
            if (checkGLObject(originEntry.getFinancialSubObject(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_OBJECT_CODE_NOT_FOUND))) {
                workingEntry.setFinancialSubObject(originEntry.getFinancialSubObject());
            }
        } else {
        	workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        }

        if (StringUtils.hasText(originEntry.getProjectCode()) && !Constants.DASHES_PROJECT_CODE.equals(originEntry.getProjectCode())) {
            checkGLObject(originEntry.getProject(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_PROJECT_CODE_NOT_FOUND));
        } else {
        	workingEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        }

        if (originEntry.getTransactionDate() == null) {
            workingEntry.setTransactionDate(new Date(runDate.getTime()));
        } else {
            workingEntry.setTransactionDate(originEntry.getTransactionDate());
        }

        // Check transaction date against the date in univDate
        // if not equal read the univDate table with this trnsaction date.
        // if found:
        //      workEntry.setTransactionDt(univDate.getUnivDt())
        //      wsHoldPrevDate = univDate.getUnivDt();
        Calendar workingCal = Calendar.getInstance();
        workingCal.setTimeInMillis(workingEntry.getTransactionDate().getTime());
        Calendar univDateCal = Calendar.getInstance();
        univDateCal.setTimeInMillis(univRunDate.getUniversityDate().getTime());
        if (!workingCal.equals(wsPreviousCal)) {
            UniversityDate tmpDate = universityDateDao.getByPrimaryKey(workingEntry.getTransactionDate());
            if (tmpDate == null) {
                tmpDate = univRunDate;
            }
            workingEntry.setUniversityFiscalYear(tmpDate.getUniversityFiscalYear());
            workingEntry.setUniversityFiscalPeriodCode(tmpDate.getUniversityFiscalAccountingPeriod());
            wsPreviousCal = workingCal;
        } // TODO: what should the else do?

        if (workingEntry.getOption() == null) {
            workingEntry.setOption(new Options());
        }
        if (originEntry.getUniversityFiscalYear() == null || originEntry.getUniversityFiscalYear().intValue() == 0) {
            workingEntry.setUniversityFiscalYear(univRunDate.getUniversityFiscalYear());
            workingEntry.getOption().setUniversityFiscalYear(workingEntry.getUniversityFiscalYear());
            workingEntry.refreshReferenceObject("option");
            checkGLObject(workingEntry.getOption(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        } else {
            workingEntry.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
            workingEntry.setOption(originEntry.getOption());
            if (!checkGLObject(workingEntry.getOption(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND))) {
                workingEntry.setOption(new Options());
                workingEntry.setUniversityFiscalYear(univRunDate.getUniversityFiscalYear());
                workingEntry.getOption().setUniversityFiscalYear(workingEntry.getUniversityFiscalYear());
                workingEntry.refreshReferenceObject("option");
                if (workingEntry.getOption() == null) {
                    throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
                }
            }
        }

        if (checkGLObject(originEntry.getDocumentType(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_TYPE_NOT_FOUND))) {
            workingEntry.setFinancialDocumentTypeCode(originEntry.getFinancialDocumentTypeCode());
            workingEntry.setDocumentType(originEntry.getDocumentType());
        }

        checkGLObject(originEntry.getOrigination(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND));

        if (!StringUtils.hasText(originEntry.getFinancialDocumentNumber())) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_NUMBER_REQUIRED));
        }

        checkGLObject(originEntry.getChart(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_FOUND));

        if (originEntry.getChart() != null && !originEntry.getChart().isFinChartOfAccountActiveIndicator()) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_ACTIVE));
        }

        checkGLObject(originEntry.getAccount(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNT_NOT_FOUND));

        
        if ("ACLO".equals(originEntry.getFinancialDocumentTypeCode())) { // TODO: move to properties ANNUAL_CLOSING
            resolveAccount(originEntry, workingEntry);
        } else {
            wsAccountChange = originEntry.getAccountNumber(); 
        }
        
        if (!wsAccountChange.equals(workingEntry.getAccountNumber()) && originEntry.getAccount() != null) {
            if (originEntry.getAccount().isAccountClosedIndicator()) {
                transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNT_CLOSED));
            } else {
                transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNT_EXPIRED));
            }
        }

        // TODO: need to specifically check for the correct number of dashes
        if (!Constants.DASHES_SUB_ACCOUNT_NUMBER.equals(originEntry.getSubAccountNumber())) {
            checkGLObject(originEntry.getSubAccount(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND));
        }

        if (originEntry.getSubAccount() != null && "ACLO".equals(originEntry.getFinancialDocumentTypeCode())
                && !originEntry.getSubAccount().isSubAccountActiveIndicator()) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE));
        }

        if (StringUtils.hasText(originEntry.getFinancialObjectCode())) {
            workingEntry.setFinancialObjectCode(originEntry.getFinancialObjectCode());
            checkGLObject(originEntry.getFinancialObject(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND));
        } else {
            workingEntry.setFinancialObjectCode(Constants.DASHES_OBJECT_CODE);
        }
        
        if (originEntry.getFinancialObject() != null && StringUtils.hasText(originEntry.getFinancialObject().getFinancialObjectTypeCode())) {
            checkGLObject(originEntry.getFinancialObject().getFinancialObjectType(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND));
        }

        if (StringUtils.hasText(originEntry.getFinancialSubObjectCode())) {
            checkGLObject(originEntry.getFinancialSubObject(),kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_OBJECT_CODE_NOT_FOUND));
            if (originEntry.getFinancialSubObject() != null && !originEntry.getFinancialSubObject().isFinancialSubObjectActiveIndicator()) {
                // if NOT active, set it to dashes
                workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE); // TODO: replace with constants
                workingEntry.setFinancialSubObject(null);
            }
        }

        if (StringUtils.hasText(originEntry.getFinancialBalanceTypeCode())) {
            // now validate balance type against balance type table (free)
            if (checkGLObject(originEntry.getBalanceType(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND))) {
                workingEntry.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
                workingEntry.setBalanceType(originEntry.getBalanceType());
            }
        } else {
            // if balance type of originEntry is null, get it from option table
            workingEntry.setFinancialBalanceTypeCode(originEntry.getOption().getActualFinancialBalanceTypeCd());
            // TODO: need option object to have objects not just primitives
            if (workingEntry.getBalanceType() == null) {
                workingEntry.setBalanceType(new BalanceTyp());
            }
            workingEntry.getBalanceType().setCode(originEntry.getOption().getActualFinancialBalanceTypeCd());
            workingEntry.refreshReferenceObject("balanceType");
            checkGLObject(workingEntry.getBalanceType(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND));
        }

        // validate fiscalperiod against sh_acct_period_t (free)
        if (StringUtils.hasText(originEntry.getUniversityFiscalPeriodCode())) {
            checkGLObject(originEntry.getAccountingPeriod(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND));
        }

        // validate that the fiscalperiod is open "fiscal period closed"
        if (originEntry.getAccountingPeriod() != null && originEntry.getAccountingPeriod().getUniversityFiscalPeriodStatusCode() == Constants.ACCOUNTING_PERIOD_STATUS_CLOSED) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_FISCAL_PERIOD_CLOSED));
        }

        if(originEntry.getBalanceType() != null && originEntry.getBalanceType().isFinancialOffsetGenerationIndicator() &&
                originEntry.getTransactionLedgerEntryAmount().isNegative()) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_TRANS_CANNOT_BE_NEGATIVE_IF_OFFSET));
        }
  
        // if offsetGenerationCode = "N" and debitCreditCode != null then error
        // "debit or credit indicator must be empty(space)"
        // if offsetGenerationCode = "Y" and debitCreditCode == null then error
        // "debit or credit indicator must be 'D' or 'C'"
        if(originEntry.getBalanceType() != null && !originEntry.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if(!" ".equals(originEntry.getTransactionDebitCreditCode())) { // todo: move space to constant
                transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_EMPTY));
            }
        } else {
            if(!originEntry.isCredit() && !originEntry.isDebit()) {
                transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_D_OR_C));
            }
        }

        // if ProjectCode is inactive then error - "Project Code must be active"
        if (originEntry.getProject() != null && !originEntry.getProject().isActive()) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_PROJECT_CODE_MUST_BE_ACTIVE));
        }

        // if DocReferenceNumber == null then
        // documentReferenceTypeCode = null
        // FSReferenceOriginCode = null
        // if transactionEncumbranceUpdateCode = "R" then
        // error "reference document number cannot be null if update code is 'R'"
        // else
        // validate documentReferenceNumber - "documentreference number is not in DB"
        // validate documentReferenceTypeCode - "reference document type is not
        // in document type table"
        // validate FSRefOriginCode - "FS origin code is not in DB"
        if (StringUtils.hasText(originEntry.getFinancialDocumentReferenceNbr())) {
            if (checkGLObject(originEntry.getReferenceDocumentType(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_DOCUMENT_TYPE_NOT_FOUND))) {
                workingEntry.setReferenceFinDocumentTypeCode(originEntry.getReferenceFinDocumentTypeCode());
                workingEntry.setReferenceDocumentType(originEntry.getReferenceDocumentType());
            }
            if (checkGLObject(originEntry.getFinSystemRefOriginationCode(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_ORIGINATION_CODE_NOT_FOUND))) {
                workingEntry.setFinSystemRefOriginationCode(originEntry.getFinancialSystemOriginationCode());
            }
        } else {
            workingEntry.setFinancialDocumentReferenceNbr(null);
            workingEntry.setReferenceFinDocumentTypeCode(null);
            workingEntry.setReferenceDocumentType(null);
            workingEntry.setFinSystemRefOriginationCode(null);

            if ("R".equals(originEntry.getTransactionEncumbranceUpdtCd())) { // TODO: change to constant
                transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_IF_UPDATE_CODE_IS_R));
            }
        }

        // if reversalDate != null
        // validate it against sh_univ_date_t - error "reversal date not in table"
        if (originEntry.getFinancialDocumentReversalDate() != null) {
            if (checkGLObject(originEntry.getReversalDate(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REVERSAL_DATE_NOT_FOUND))) {
                workingEntry.setFinancialDocumentReversalDate(originEntry.getFinancialDocumentReversalDate());
                workingEntry.setReversalDate(originEntry.getReversalDate());
            }
        }

        // if balanceTypeEncumberanceCode = "Y" AND fundBalanceCode != "Y" AND
        // (encumberanceUpdateCode != "D" and "R" and "N")
        // "The encumberance update code is not equal D R or N"
        if (originEntry.getBalanceType() != null && originEntry.getBalanceType().isFinBalanceTypeEncumIndicator() &&
                !originEntry.getObjectType().isFundBalanceIndicator()) {
            if ("D".equals(originEntry.getTransactionEncumbranceUpdtCd()) ||
                    "R".equals(originEntry.getTransactionEncumbranceUpdtCd()) ||
                    "N".equals(originEntry.getTransactionEncumbranceUpdtCd())) {
                workingEntry.setTransactionEncumbranceUpdtCd(originEntry.getTransactionEncumbranceUpdtCd());
            } else {
                transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ENC_UPDATE_CODE_NOT_DRN));
            }
        }
                
        // if offsetGenerationCode = "Y" AND DocumentType = "ACLO" (annual
        // closing) AND UniversityFiscalPeriod != "BB" (beginning balance) AND
        // UniversityFiscalPeriod != "CB" (contract balance)
        // if TransactionDebitCreditCode = "D"
        //  add amount to offsetAmountAccumulator
        //  add to debitAmountAccumulator
        // else
        //  subtract amount from offsetAmountAccumulator
        //  add to creditAmountAccumulator
        if (originEntry.getBalanceType().isFinancialOffsetGenerationIndicator() &&
                !"BB".equals(originEntry.getUniversityFiscalPeriodCode()) &&
                !"CB".equals(originEntry.getUniversityFiscalPeriodCode()) &&
                !originEntry.getFinancialDocumentTypeCode().equals("ACLO")) {
            if (originEntry.isDebit()) {
                scrubberUtil.offsetAmountAccumulator.add(originEntry.getTransactionLedgerEntryAmount());
            } else {
                scrubberUtil.offsetAmountAccumulator.subtract(originEntry.getTransactionLedgerEntryAmount());
            }
        }
        if (originEntry.isDebit()) {
            scrubberUtil.debitAmountAccumulator.add(originEntry.getTransactionLedgerEntryAmount());
        } else {
            scrubberUtil.creditAmountAccumulator.add(originEntry.getTransactionLedgerEntryAmount());
        }
        
        // if (ObjectTypeCode = "EE" or "EX" or "ES" or "TE") AND
        //  (BalanceTypeCode = "EX" or "IE" or "PE") AND (holdFundGroupCD = "CG")
        //  AND (holdSubAccountTypeCD == "CS") AND UniversityFiscalPeriod != "BB"
        //  (beginning balance) AND UniversityFiscalPeriod != "CB" (contract
        //  balance) AND DocumentTypeCD != "JV" and != "AA" //todo: move to properties
        //      DO COST SHARING! (move into separate method)
        if ((workingEntry.getOption().getFinObjTypeExpenditureexpCd().equals(workingEntry.getFinancialObjectTypeCode()) ||
                workingEntry.getOption().getFinObjTypeExpNotExpendCode().equals(workingEntry.getFinancialObjectTypeCode()) ||
                workingEntry.getOption().getFinObjTypeExpendNotExpCode().equals(workingEntry.getFinancialObjectTypeCode()) ||
                "TE".equals(workingEntry.getFinancialObjectTypeCode())) &&
                "CG".equals(originEntry.getAccount().getSubFundGroupCode()) &&
                "CS".equals(originEntry.getA21SubAccount().getSubAccountTypeCode()) &&
                !"BB".equals(originEntry.getUniversityFiscalPeriodCode()) &&
                !"CB".equals(originEntry.getUniversityFiscalPeriodCode()) &&
                !"JV".equals(originEntry.getFinancialDocumentTypeCode()) &&
                !"AA".equals(originEntry.getFinancialDocumentTypeCode())) {
            if (originEntry.getOption().getExtrnlEncumFinBalanceTypCd().equals(workingEntry.getFinancialBalanceTypeCode()) ||
            		originEntry.getOption().getIntrnlEncumFinBalanceTypCd().equals(workingEntry.getFinancialBalanceTypeCode()) ||
            		originEntry.getOption().getPreencumbranceFinBalTypeCd().equals(workingEntry.getFinancialBalanceTypeCode())) {
                // Do cost sharing!
                costShareEncumbrance(originEntry);
            }
            // if (ObjectTypeCode = "EE" or "EX" or "ES" or "TE") AND
            // (BalanceTypeCode = "AC") AND (holdFundGroupCD = "CG") AND
            // (holdSubAccountTypeCD == "CS") AND UniversityFiscalPeriod != "BB"
            // (beginning balance) AND UniversityFiscalPeriod != "CB" (contract
            // balance) AND DocumentTypeCD != "JV" and != "AA" //todo: move to properties
            //  if (debitCreditCD = "D")
            //      subtract amount from costSharingAccumulator
            //  else
            //      add amount to costSharingAccumulator
            if (originEntry.getOption().getActualFinancialBalanceTypeCd().equals(workingEntry.getFinancialBalanceTypeCode())) {
                if (workingEntry.isDebit()) {
                    scrubberUtil.costSharingAccumulator.subtract(originEntry.getTransactionLedgerEntryAmount());
                } else {
                    scrubberUtil.costSharingAccumulator.add(originEntry.getTransactionLedgerEntryAmount());
                }
            }
            
        }


        // if no entry errors
        //  write out the workingEntry to table
        // if no document errors and DocumentTypeCd != "JV"
        //  3000-user-processing
        // if expiredFinCOACd != null //where is this from!?
        // 
        // if costSharingAccumulator != 0
        //  3000-cost-share
        // else
        //  write out errors
        // return!
        
        this.writeSwitchStatusCD = ScrubberUtil.FROM_WRITE;
        if (transactionErrors.size() > 0) {
            // copy all the errors for this entry to the main error list
            documentError.put(originEntry, transactionErrors);

            // write this entry as a scrubber error
            createOutputEntry(originEntry, errorGroup);

            writeErrors();
        } else {
            // write this entry as a scrubber valid
            createOutputEntry(workingEntry, validGroup);

            if (documentError.size() == 0 && !"JV".equals(workingEntry.getFinancialDocumentTypeCode())) {
                userProcessing(workingEntry);
            }

            if (wsExpiredAccount != null) {
                OriginEntry expiredEntry = new OriginEntry(workingEntry);
                expiredEntry.setChartOfAccountsCode(wsExpiredChart);
                expiredEntry.setAccountNumber(wsExpiredAccount);
                createOutputEntry(expiredEntry, expiredGroup);
            }
            
            if (scrubberUtil.costSharingAccumulator.isNonZero()) {
                costShare(workingEntry);
            }
        }
        return workingEntry;
    }// End of method
    
    private boolean checkGLObject(Object glObject, String errorMessage) {
        if (glObject == null) {
            if (StringUtils.hasText(errorMessage)) {
                transactionErrors.add(errorMessage);
            } else {
                transactionErrors.add("The following object is null: " + glObject.getClass().getName());
            }
            return false;
        }
        return true;
    }

    /**
     * 2100-Edit Account
     * 
     * The purpose of this method is to see if an account is closed. If the account
     * is closed then it tries to find a continuation account in which to post.
     * 
     * The user can select to ignore continuation account check, this is done by
     * inputing a parameter to the Scrubber that says ingore this check.
     * 
     * An account is determined to be closed if its expiration date has been set
     * and/or Account closed flag in the CA_ACCOUNT_T table has been set.
     * If an account is closed then this method will call 2107-TEST-EXPIRED-CG.
     * 
     * @param originEntry
     * @param workingEntry
     */
    private void resolveAccount(OriginEntry originEntry, OriginEntry workingEntry) { /* (2100-edit-account) */

        scrubberUtil.wsAccount = originEntry.getAccount();
        
        // Assume we have the current CA_ACCOUNT_T row.
        // if(account.getAcctExpirationDt() == null &&
        //           !account.getAcctClosedInd.equals("Y")) {
        if (scrubberUtil.wsAccount.getAccountExpirationDate() == null &&
           !scrubberUtil.wsAccount.isAccountClosedIndicator()) {
            workingEntry.setAccountNumber(scrubberUtil.wsAccount.getAccountNumber());
            return;
        }

        // Check to see if the FS-ORIGIN-CD is numeric or equal to EU or equal to PL
        // and the Account is not closed.
        if ((org.apache.commons.lang.StringUtils.isNumeric(workingEntry.getFinancialSystemOriginationCode()) ||
                "EU".equals(workingEntry.getFinancialSystemOriginationCode()) ||
                "PL".equals(workingEntry.getFinancialSystemOriginationCode())) &&
                scrubberUtil.wsAccount.isAccountClosedIndicator()) {
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            wsAccountChange = workingEntry.getAccountNumber();
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT));
            return;
        }

        // TODO: BalanceType and ObjectType are in a table based on fiscal year!!! use that
        if ((org.apache.commons.lang.StringUtils.isNumeric(workingEntry.getFinancialSystemOriginationCode()) ||
                "EU".equals(workingEntry.getFinancialSystemOriginationCode()) ||
                "PL".equals(workingEntry.getFinancialSystemOriginationCode()) ||
                originEntry.getOption().getExtrnlEncumFinBalanceTypCd().equals(workingEntry.getFinancialBalanceTypeCode()) ||
                originEntry.getOption().getIntrnlEncumFinBalanceTypCd().equals(workingEntry.getFinancialBalanceTypeCode()) ||
                originEntry.getOption().getPreencumbranceFinBalTypeCd().equals(workingEntry.getFinancialBalanceTypeCode()) ||
                "TOPS".equals(workingEntry.getFinancialDocumentTypeCode()) ||
                "CD".equals(workingEntry.getFinancialDocumentTypeCode().trim()) ||
                "LOCR".equals(workingEntry.getFinancialDocumentTypeCode())) &&
                !scrubberUtil.wsAccount.isAccountClosedIndicator()) {
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            return;
        }
        
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTimeInMillis(scrubberUtil.wsAccount.getAccountExpirationDate().getTime());
        
        if(tmpCal.get(Calendar.DAY_OF_YEAR) <= runCal.get(Calendar.DAY_OF_YEAR) ||
                scrubberUtil.wsAccount.isAccountClosedIndicator()) {
            testExpiredCg(originEntry, workingEntry);
        } else {
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
        }
    }// End of method

    /**
     * 2510-CHECK-UOW
     * 
     * The purpose of this method is to determine wether or not an offset entry should
     * be generated. It uses the "unit of work to make this decision. The unit of work
     * is made up of the following fields document type code, origin code, document number, 
     * chart of accounts code, accountnumber, subaccount number, balance type, dcoument 
     * reversal date, fiscal period. If the unit of work for the current transaction is
     * different than the unit of work of the previous transaction and the offset
     * accumulator is not equal to zero then an offset should be generated. Note, offsets
     * will not be generated for journal vouchers or if there were document errors. The
     * actual offset transaction is built in the method 3000-offset and then written
     * to the database in this method.
     *  
     * This method is also responsible for initializing any work field used in the scrubber.
     * 
     * @param originEntry
     * @param workingEntry
     */
    private void checkUnitOfWork(OriginEntry originEntry, OriginEntry workingEntry) { /* 2510-check-uow */
        //See if the unit of work has changed, if not return.

        if (previousEntry == null) {
            previousEntry = new OriginEntry();
            return;
        }
        
        if(!eof && originEntry.getFinancialDocumentTypeCode().equals(previousEntry.getFinancialDocumentTypeCode()) &&
                    originEntry.getFinancialSystemOriginationCode().equals(previousEntry.getFinancialSystemOriginationCode()) &&
                    originEntry.getFinancialDocumentNumber().equals(previousEntry.getFinancialDocumentNumber()) &&
                    originEntry.getChartOfAccountsCode().equals(previousEntry.getChartOfAccountsCode()) &&
                    originEntry.getAccountNumber().equals(previousEntry.getAccountNumber()) &&
                    originEntry.getSubAccountNumber().equals(previousEntry.getSubAccountNumber()) &&
                    originEntry.getFinancialBalanceTypeCode().equals(previousEntry.getFinancialBalanceTypeCode()) &&
                    originEntry.getFinancialDocumentReversalDate().equals(previousEntry.getFinancialDocumentReversalDate()) &&
                    originEntry.getUniversityFiscalPeriodCode().equals(previousEntry.getUniversityFiscalPeriodCode())) {
            return;
        }

        if (scrubberUtil.originCount == 1) {
            copyPrimaryFields(originEntry, previousEntry);
            initScrubberValues();
            return;
        }

        // TODO: Address claim on cash here
        
        // Check scrbOffsetAmount to see if an offset needs to be generated.
        if(scrubberUtil.offsetAmountAccumulator.isNonZero() &&
                documentError.size() == 0 &&
                !"JV".equals(workingEntry.getFinancialDocumentTypeCode())) {
            generateOffset(workingEntry);
            this.writeSwitchStatusCD = ScrubberUtil.FROM_OFFSET;
            
            if (transactionErrors.size() == 0) {
                createOutputEntry(workingEntry, validGroup);                
            } else {
                createOutputEntry(workingEntry, errorGroup);
                writeErrors();
            }
            
            initScrubberValues();
            
            return;
        }

    }// End of method

    /**
     * Copies the primary fields only from one entry to another
     * 
     * @param fromEntry
     * @param toEntry
     */
    private void copyPrimaryFields(OriginEntry fromEntry, OriginEntry toEntry) {
        toEntry.setChartOfAccountsCode(fromEntry.getChartOfAccountsCode());
        toEntry.setFinancialDocumentTypeCode(fromEntry.getFinancialDocumentTypeCode());
        toEntry.setFinancialSystemOriginationCode(fromEntry.getFinancialSystemOriginationCode());
        toEntry.setFinancialDocumentNumber(fromEntry.getFinancialDocumentNumber());
        toEntry.setAccountNumber(fromEntry.getAccountNumber());
        toEntry.setSubAccountNumber(fromEntry.getSubAccountNumber());
        toEntry.setFinancialBalanceTypeCode(fromEntry.getFinancialBalanceTypeCode());
        toEntry.setFinancialDocumentReversalDate(fromEntry.getFinancialDocumentReversalDate());
        toEntry.setUniversityFiscalPeriodCode(fromEntry.getUniversityFiscalPeriodCode());
    }

    /**
     * 3000-USER-PROCESSING
     * 
     * This method determines whether it should generate a "Capitalization Entry" and
     * then will generate it and its corresponding offset.
     * 
     * The user can set a switch at start up time that indicates to the Scrubber
     * whetehr or not to perform capitalization.
     * 
     * Capitalzation is not performed under the following conditions:
     * 1. Document types of Transfer of Funds, Year End Transfer of Funds,
     *    Auxiliary Voucher, Accrual Entry, Adjustment, or Recode.
     * 2. A fiscal period of Beginning Balance or Contract beginning Balance
     * 3. An Annual Closing document type.
     * 4. Or certain Object Subtypes.
     * 5. A subfund group of Enternal Agency
     * 6. The hospital chart of account
     * 
     * The object code for capitalization is determined by looking at the object
     * subtype for the current input object. The chart of accounts code and account
     * number are determined by calling 4000-PLANT-FUND-ACCT.
     * 
     * Next this method will determine if it should generate a "Liability Entry" and
     * then generate it and its corresponding offset.
     * 
     * The user can set a switch at start up time that indicates to the Scrubber
     * whether or not to generate a liability.
     * A liability is not generated under the following conditions:
     * 1. Document types of Transfer of Funds, Year End Transfer of Funds,
     *    Auxiliary Voucher, Accrual Entry, Adjustment, or Recode.
     * 2. A fiscal period of Beginning Balance or Contract beginning Balance
     * 3. An Annual Closing document type.
     * 4. An object subtype of "CL"
     * 5. A subfund group of Enternal Agency
     * 6. The hospital chart of account
     * 
     * The object code is set to "9630" and then 4000-PLANT-FUND-ACCT is called to
     * get the appropriate chart of accounts code and account number.
     * Object code "9899" fund balance is used for the offset.
     * 
     * Next this method will determine if it should generate a "Plant Indebetedbess Entry"
     * and then generate it and its corresponding offset.
     * 
     * The user can set a switch at start up time that indicates to the Scrubber
     * whether or not to generate a plant indebetedness entry.
     * 
     * A plant indebetedness entry will be generated if the following conditions are true:
     * 1. A subfund group of PFCMR or PFRI
     * 2. Anobject subtype of PI
     * 
     * This is a "Generated Transfer to Net Plant" entry.
     * It uses the original object code for the direct entry and "9899" fund balnace for
     * the offset entry.
     * 
     * Now it needs to generate the "Generated Transfer From Entry"
     * and its offset. The Campus Plant Fund Chart and Account Number are used for this
     * entry. These values are obtaine from the CA_ORG_T table. "9899" fund balance is
     * used for the offset object code.
     * 
     * @param workingEntry
     */
    private void userProcessing(OriginEntry workingEntry) { // 3000-USER-PROCESSING
        String tmpObjectCode = workingEntry.getFinancialObjectCode();
        String tmpObjectTypeCode = workingEntry.getFinancialObjectTypeCode();
        String tmpDebitOrCreditCode = workingEntry.getTransactionDebitCreditCode();
        String tmpDescription = workingEntry.getTransactionLedgerEntryDesc();
        String tmpAccountNumber = workingEntry.getAccountNumber();
        String tmpSubAccountNumber = workingEntry.getSubAccountNumber();
        String tmpCOA = workingEntry.getChartOfAccountsCode();

        boolean performCap = true;
        boolean performLiability = true;
        boolean performPlant = true;
        
        if ( workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getActualFinancialBalanceTypeCd())
                && performCap
                && !"TF".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"YETF".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AV".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AVAC".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AVAE".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AVRC".equals(workingEntry.getFinancialDocumentTypeCode())
                && (!"BB".equals(workingEntry.getUniversityFiscalPeriodCode())
                        && !"CB".equals(workingEntry.getUniversityFiscalPeriodCode())
                        && !"ACLO".equals(workingEntry.getFinancialDocumentTypeCode()))
                && ("AM".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "AF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "BD".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "BF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "BI".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "BR".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "BX".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "BY".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "CM".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "CF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "C1".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "C2".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "C3".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "ES".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "IF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "LA".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "LE".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "LF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "LI".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "LR".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "UC".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                        || "UF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode()))
                && !"EXTAGY".equals(workingEntry.getAccount().getSubFundGroupCode())) {
            this.writeSwitchStatusCD = ScrubberUtil.FROM_CAMS;
            if ("AM".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // ART_AND_MUSEUM
                workingEntry.setFinancialObjectCode("8615"); // ART_AND_MUSEUM_OBJECTS
/*
 *      REMOVED PER STERLINGS OK
 *      
 *             } else if ("AF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) {
                workingEntry.setObjectCode("8616"); // ???

*/
            } else if ("BD".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // BUILDING
                workingEntry.setFinancialObjectCode("8601"); // INSTITUTIONAL_PLANT_BLDG
            } else if ("BF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // BUILDING_AND_ATTACHED_FIXT_FEDERAL_FUNDED
                workingEntry.setFinancialObjectCode("8605"); // PLANT_BUILDING_FEDERAL_FUNDED
            } else if ("BI".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // BOND_INSURANCE
                workingEntry.setFinancialObjectCode("8629"); // BOND_ISSUANCE_EXPENSE
            } else if ("BR".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // BUILDING_IMPROVEMENTS_AND_RENOVATIONS
                workingEntry.setFinancialObjectCode("8601"); // INSTITUTIONAL_PLANT_BLDG
            } else if ("BX".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // BUILDING_IMPROVEMENTS_AND RENOVATIONS_FEDERALLY_FUNDED
                workingEntry.setFinancialObjectCode("8640"); // ???
            } else if ("BY".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // BUILDING_IMPROVEMENTS_AND RENOVATIONS_FEDERALLY_OWNED
                workingEntry.setFinancialObjectCode("8641"); // ???
            } else if ("CM".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // CAPITAL_MOVEABLE_EQUIPMENT
                workingEntry.setFinancialObjectCode("8610"); // CAPITAL_EQUIPMENT
            } else if ("CF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // CAPITAL_MOVEABLE_EQUIPMENT_FEDERALLY_FUNDED
                workingEntry.setFinancialObjectCode("8611"); // CAP_EQUIP_FED_FUNDING
            } else if ("C1".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // CAPITAL_LEASE_CAPITAL_THREASHOLD_1
                workingEntry.setFinancialObjectCode("8627"); // ?
            } else if ("C2".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // CAPITAL_LEASE_CAPITAL_THREASHOLD_2
                workingEntry.setFinancialObjectCode("8628"); // BOND_ISSUANCE_EXPENSE
            } else if ("C3".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // CAPITAL_LEASE_DEBT_BELOW_CAPITAL_THRESHHOLD
                workingEntry.setFinancialObjectCode("9607"); // BOND_ISSUANCE_EXPENSE
            } else if ("ES".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // EQUIPMENT_STARTUP_COSTS
                workingEntry.setFinancialObjectCode("8630"); // EQUIPMENT_START_UP
            } else if ("IF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // INFRASTRUCTURE
                workingEntry.setFinancialObjectCode("8604"); // INST_PLANT_INFRASTRUCTURE
            } else if ("LA".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // LAND
                workingEntry.setFinancialObjectCode("8603"); // INSTITUTIONAL_PLANT_LAND
            } else if ("LE".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // LEASEHOLD_IMPROVEMENTS
                workingEntry.setFinancialObjectCode("8608"); // LEASEHOLD_IMPROVEMENTS_OBJ
            } else if ("LF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // LIBRARY_AQUISITIONS_FEDERALLY_FUNDED
                workingEntry.setFinancialObjectCode("8614"); // LIBRARY_FED_FUNDING
            } else if ("LI".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // LIBRARY_ACQUISITIONS
                workingEntry.setFinancialObjectCode("8613"); // LIBRARY
            } else if ("LR".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // LAND_IMPROVEMENTS
                workingEntry.setFinancialObjectCode("8665"); // ???
            } else if ("UC".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // UNIVERSITY_CONSTRUCTED
                workingEntry.setFinancialObjectCode("8618"); // UNIVER_EQUIP_UNDER_CONST
            } else if ("UF".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) { // UNIVERSITY_CONSTRUCTION_FEDERALLY_FUNDED
                workingEntry.setFinancialObjectCode("8619"); // EQUIP_UNDER_CONST_FED_FUNDE
            }
            workingEntry.setFinancialObjectTypeCode("AS"); // TODO: constant 
            workingEntry.setTransactionLedgerEntryDesc("GENERATED CAPITALIZATION"); // TODO: move to parameter 
            plantFundAccountLookup(workingEntry, tmpCOA, tmpAccountNumber);
            createOutputEntry(workingEntry, validGroup);
            
            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE TODO: constant
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE TODO: constant
            
            if (workingEntry.isDebit()) {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }
            createOutputEntry(workingEntry, validGroup);
        }
        
        if ( workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getActualFinancialBalanceTypeCd())
                && performLiability
                && !"TF".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"YETF".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AV".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AVAC".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AVAE".equals(workingEntry.getFinancialDocumentTypeCode())
                && !"AVRC".equals(workingEntry.getFinancialDocumentTypeCode())
                && (!"BB".equals(workingEntry.getUniversityFiscalPeriodCode())
                        && !"CB".equals(workingEntry.getUniversityFiscalPeriodCode())
                        && !"ACLO".equals(workingEntry.getFinancialDocumentTypeCode()))
                && "CL".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                && !"EXTAGY".equals(workingEntry.getAccount().getSubFundGroupCode())) {
            workingEntry.setFinancialObjectCode("9603"); // NOTES_PAYABLE_CAPITAL_LEASE TODO: constant
            this.writeSwitchStatusCD = ScrubberUtil.FROM_LIAB;
            workingEntry.setFinancialObjectTypeCode("LI"); // LIABILITY TODO: constant
            workingEntry.setTransactionDebitCreditCode(tmpDebitOrCreditCode);
            workingEntry.setTransactionLedgerEntryDesc("GENERATED LIABILITY"); // TODO: constant
            plantFundAccountLookup(workingEntry, tmpCOA, tmpAccountNumber);
            createOutputEntry(workingEntry, validGroup);
            
            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE TODO: constant
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE TODO: constant
            
            if (workingEntry.isDebit()) {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }
            createOutputEntry(workingEntry, validGroup);
        }
        
        workingEntry.setFinancialObjectCode(tmpObjectCode);
        workingEntry.setFinancialObjectTypeCode(tmpObjectTypeCode);
        workingEntry.setTransactionDebitCreditCode(tmpDebitOrCreditCode);
        workingEntry.setTransactionLedgerEntryDesc(tmpDescription);
        workingEntry.setAccountNumber(tmpAccountNumber);
        workingEntry.setSubAccountNumber(tmpSubAccountNumber);

        if (workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getFinObjectTypeFundBalanceCd())
                && ("PFCMR".equals(workingEntry.getAccount().getSubFundGroupCode())
                        || "PFRI".equals(workingEntry.getAccount().getSubFundGroupCode()))
                && "PI".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                && performPlant) {
            this.writeSwitchStatusCD = ScrubberUtil.FROM_PLANT_INDEBTEDNESS;

            workingEntry.setTransactionLedgerEntryDesc("GENERATED TRANSFER TO NET PLANT");
            if (workingEntry.isDebit()) {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }
            createOutputEntry(workingEntry, validGroup);
            
            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE TODO: constant
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE TODO: constant
            workingEntry.setTransactionDebitCreditCode(tmpDebitOrCreditCode);
            createOutputEntry(workingEntry, validGroup);

            workingEntry.setFinancialObjectCode(tmpObjectCode);
            workingEntry.setFinancialObjectTypeCode(tmpObjectTypeCode);
            workingEntry.setTransactionDebitCreditCode(tmpDebitOrCreditCode);
            workingEntry.setTransactionLedgerEntryDesc(tmpDescription);
            workingEntry.setAccountNumber(tmpAccountNumber);
            workingEntry.setSubAccountNumber(tmpSubAccountNumber);
            
            // TODO: do we need to refresh this object first?
            if (checkGLObject(workingEntry.getAccount().getOrganization(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_INVALID_ORG_CODE_FOR_PLANT_FUND))) {
                workingEntry.setAccountNumber(workingEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                workingEntry.setChartOfAccountsCode(workingEntry.getAccount().getOrganization().getCampusPlantChartCode());
            }

            workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER); // TODO: constant
            workingEntry.setTransactionLedgerEntryDesc(tmpCOA + tmpAccountNumber + "GENERATED PLANT FUND TRANSFER");
            createOutputEntry(workingEntry, validGroup);

            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE TODO: constant
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE TODO: constant
            
            if (workingEntry.isDebit()) {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }
            createOutputEntry(workingEntry, validGroup);

        }

        workingEntry.setFinancialObjectCode(tmpObjectCode);
        workingEntry.setFinancialObjectTypeCode(tmpObjectTypeCode);
        workingEntry.setTransactionDebitCreditCode(tmpDebitOrCreditCode);
        workingEntry.setTransactionLedgerEntryDesc(tmpDescription);
        workingEntry.setAccountNumber(tmpAccountNumber);
        workingEntry.setSubAccountNumber(tmpSubAccountNumber);
        workingEntry.setChartOfAccountsCode(tmpCOA);
        
    }// End of method

    /**
     * @param inputEntry
     * @param tmpChart
     * @param tmpAccount
     */
    private void plantFundAccountLookup(OriginEntry inputEntry, String tmpChart, String tmpAccount) { // 4000-PLANT_FUND_ACCOUNT
        inputEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER); //TODO: constant
        if (inputEntry.getChartOfAccountsCode().equals(inputEntry.getAccount().getOrganization().getChartOfAccountsCode())
                && inputEntry.getAccount().getOrganizationCode() == inputEntry.getAccount().getOrganization().getOrganizationCode()
                && tmpChart.equals(inputEntry.getAccount().getChartOfAccountsCode())
                && tmpAccount.equals(inputEntry.getAccount().getAccountNumber())) {
            if ("AM".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "AF".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "BD".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "BF".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "BI".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "BR".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "BX".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "BY".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "IF".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "LA".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "LE".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "LF".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "LI".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "LR".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())) {
                inputEntry.setAccountNumber(inputEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                inputEntry.setChartOfAccountsCode(inputEntry.getAccount().getOrganization().getCampusPlantChartCode());
            } else if ("CL".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "CM".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "CF".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "C1".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "C2".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "C3".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "ES".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "UC".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                    || "UF".equals(inputEntry.getFinancialObject().getFinancialObjectSubTypeCode())) {
                inputEntry.setAccountNumber(inputEntry.getAccount().getOrganization().getOrganizationPlantAccountNumber());
                inputEntry.setChartOfAccountsCode(inputEntry.getAccount().getOrganization().getOrganizationPlantChartCode());
            }
        }
        // In COBOL, the CA_ORG_T table is read at this time to reset the org information. I dont think this is necessary
    }// End of method
    
    /**
     * 2115-CHECK-CG
     * The purpose of this method is to get the fund group code, subfund group active
     * code, and the subfund group type code from the CA_SUB_FUND_GRP_T table.
     * 
     * If the account from the input transaction is closed then this routine is never
     * executed.
     * 
     * If the fund group code obtained is a "Contract & Grants" fund group then
     * this method call 2117-CHANGE-EXPIRATION, otherwise it just returns.
     */
    private void checkCg() {
        if(scrubberUtil.wsAccount.isAccountClosedIndicator()) {
            return;
        }

        if (checkGLObject(scrubberUtil.wsAccount.getSubFundGroup(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_FUND_GROUP_NOT_FOUND))) {
            if ("CG".equals(scrubberUtil.wsAccount.getSubFundGroupCode())) {
                changeExpiration();
            }
        }
    }
    
    /**
     * 2107-TEST-EXPIRED-CG
     * 
     * This method calls 2115-CHECK-CG. When it returns from this call this method
     * check to see the current entry from the CA_ACCOUNT_T is closed or expired. If
     * it is not then this methods returns, otherwise it will call 2110-GET-UNXPRD-ACCT.
     * 
     * @param originEntry
     * @param workingEntry
     */
    private void testExpiredCg(OriginEntry originEntry, OriginEntry workingEntry) {
        checkCg();

        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTimeInMillis(scrubberUtil.wsAccount.getAccountExpirationDate().getTime());
        if(tmpCal.get(Calendar.DAY_OF_YEAR) > runCal.get(Calendar.DAY_OF_YEAR) &&
                !workingEntry.getAccount().isAccountClosedIndicator()) {
            workingEntry.setAccountNumber(scrubberUtil.wsAccount.getAccountNumber());
        } else {
            getUnexpiredAccount(originEntry, workingEntry);
        }
    }

    /**
     * 2110-GET-UNXPRD-ACCT
     * 
     * The puropse of this method is to call the method 2120-ACCT-EXPIRATION and
     * check what this method return. If the account limit or an account error
     * occurred in 2120-ACCT-EXPIRATION this method will generate the appropriate message
     * and return.
     * 
     * If a continuation account was found then this method will call the
     * 2130-READ-NEW-COAT method. (handled by OJB for us)
     * 
     * @param originEntry
     * @param workingEntry
     */
    private void getUnexpiredAccount(OriginEntry originEntry, OriginEntry workingEntry) {
        if (scrubberUtil.wsAccount != null) {
            wsExpiredChart = scrubberUtil.wsAccount.getChartOfAccountsCode(); 
            wsExpiredAccount = scrubberUtil.wsAccount.getAccountNumber(); 
        }
        
        int retValue = accountExpiration(originEntry);

        if (retValue == ScrubberUtil.ACCOUNT_LIMIT) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED));
            return;
        }

        if (retValue == ScrubberUtil.ACCOUNT_ERROR) {
            transactionErrors.add("CONTINUATION ACCT NOT IN ACCT");
            return;
        }

        //FOUND
        workingEntry.setChartOfAccountsCode(scrubberUtil.wsAccount.getChartOfAccountsCode());
        workingEntry.setAccountNumber(scrubberUtil.wsAccount.getAccountNumber());
        originEntry.setTransactionLedgerEntryDesc("AUTO FR " + originEntry.getChartOfAccountsCode() + " " + originEntry.getAccountNumber() + " " + originEntry.getTransactionLedgerEntryDesc());

        if (!originEntry.getChartOfAccountsCode().equals(workingEntry.getChartOfAccountsCode())) {
            workingEntry.setChartOfAccountsCode(scrubberUtil.wsAccount.getChartOfAccountsCode());
            workingEntry.getChart().setChartOfAccountsCode(scrubberUtil.wsAccount.getChartOfAccountsCode());
            workingEntry.refreshReferenceObject("chart");
            checkGLObject(workingEntry.getChart(), "Contiunation chart not found in table");
        }
        
     }
    
    /**
     * 2120-ACCT-EXPIRATION
     * 
     * The purpose of this method is to look for a continuation account. It accomplishes
     * this by reading the CA_ACCOUNT_T table with the most recent continuation account
     * from the current account table entry. This method will continue to read the
     * CA_ACCOUNT_T table until one of the following conditions is met:
     * 
     * 1. An account with an expiration date not set or is active.
     * 2. An error occurs while reading the CA_ACCOUNT_T table or an account
     *    is not found.
     * 3. The user limit has been reached, in the case of IU the limit is 10 reads.
     * 
     * Note while reading the CA_ACCOUNT_T a continuation account is found and its
     * expiration date is not null or spaces then this method will 2115-CHECK-CG.
     * 
     * @param originEntry
     * @return
     */
    private int accountExpiration(OriginEntry originEntry) {
        String wsContinuationChart = originEntry.getAccount().getContinuationFinChrtOfAcctCd();
        String wsContinuationAccount = originEntry.getAccount().getContinuationAccountNumber();
                
        for(int i = 0; i < 10; ++i) {
            scrubberUtil.wsAccount = accountService.getByPrimaryId(wsContinuationChart, wsContinuationAccount);

            if(scrubberUtil.wsAccount != null) {
                if(scrubberUtil.wsAccount.getAccountExpirationDate() == null) {
                    return ScrubberUtil.ACCOUNT_FOUND;
                } else {
                    checkCg();
                    Calendar tmpCal = Calendar.getInstance();
                    tmpCal.setTimeInMillis(scrubberUtil.wsAccount.getAccountExpirationDate().getTime());
                    
                    if(tmpCal.get(Calendar.DAY_OF_YEAR) <= runCal.get(Calendar.DAY_OF_YEAR)) {
                        wsContinuationChart = scrubberUtil.wsAccount.getContinuationFinChrtOfAcctCd();
                        wsContinuationAccount = scrubberUtil.wsAccount.getContinuationAccountNumber();
                    } else {
                        return ScrubberUtil.ACCOUNT_FOUND;
                    }
                }
            } else {
                return ScrubberUtil.ACCOUNT_ERROR;
            }
        }

        return ScrubberUtil.ACCOUNT_LIMIT;
    }
    
    /**
     * 2117-CHANGE-EXPIRATION
     * 
     * This method is called because the account being processed is in the "Contract
     * and Grants" fund group.
     * 
     * The purpose of the method is to extend the "Contract and Grants" expiration
     * date by three months.
     */
    private void changeExpiration() {
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTimeInMillis(scrubberUtil.wsAccount.getAccountExpirationDate().getTime());
        tempCal.add(Calendar.MONTH, 3); //TODO: make this configurable
        scrubberUtil.wsAccount.setAccountExpirationDate(new Timestamp(tempCal.getTimeInMillis()));
    }

    /**
     * 3000-COST-SHARE
     * 
     * The purpose of this method is to generate a "Cost Share Entry" and its
     * corresponding offset.
     * 
     * Object code "9915" is used for the cost share object code. The offset object code
     * is determined by reading the GL_OFFSET_DEFN_T table based on the fiscal year,
     * chart of accounts, document type, and balance type code. The object type code
     * is obtained from CA_OBJECT_CODE_T based on fiscal year, chart of accounts code,
     * and object code.
     * 
     * Next it generates an entry based on the cost share chart of accounts and
     * cost share account number from in the CA_A21_SUB_ACCT_T table for the original
     * transaction chart and account number. the object code for this entry is obtained
     * by call the method SET-OBJECT-2400. The offset object code for this entry is
     * obtained by reading the GL_OFFSET_DEFN_T table based on fiscal year, chart,
     * document type, balance type from the original input transaction. The object type code
     * is obtained from the CA_OBJECT_CODE_T table for the object code from the
     * original input transaction.
     * 
     * @param workingEntry
     */
    private void costShare(OriginEntry workingEntry) {

        OriginEntry csEntry = new OriginEntry();

        this.writeSwitchStatusCD = ScrubberUtil.FROM_COST_SHARE;
        csEntry.setFinancialObjectCode("9915"); //TODO: TRSFRS_OF_FUNDS_REVENUE constant
        csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE); //TODO: constant
        csEntry.setFinancialObjectTypeCode("TE"); //TODO: constant
        csEntry.setTrnEntryLedgerSequenceNumber(new Integer(0));
        csEntry.setTransactionLedgerEntryDesc("COSTSHARE_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant
        if (scrubberUtil.costSharingAccumulator.isPositive()) {
            csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(scrubberUtil.costSharingAccumulator);
        } else {
            csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(scrubberUtil.costSharingAccumulator.negated());
        }
        csEntry.setTransactionDate(runDate);
        csEntry.setOrganizationDocumentNumber("");
        csEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        csEntry.setOrganizationReferenceId(null);
        csEntry.setReferenceFinDocumentTypeCode(null);
        csEntry.setFinSystemRefOriginationCode(null);
        csEntry.setFinancialDocumentReferenceNbr(null);
        csEntry.setReversalDate(null);
        csEntry.setTransactionEncumbranceUpdtCd("");

        createOutputEntry(csEntry, validGroup);

        csEntry.setTransactionLedgerEntryDesc("OFFSET_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant

        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                "TF", csEntry.getFinancialBalanceTypeCode());
        if (offset == null) {
            transactionErrors.add("OFFSET DEFINITION NOT FOUND");
        } else {
            csEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            if(offset.getFinancialSubObjectCode() == null) {
                csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE); // TODO: constant
            } else {
                csEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        }

        ObjectCode objectCode = objectCodeService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialObjectCode());
        if (objectCode == null) {
            transactionErrors.add("NO OBJECT FOR OBJECT ON OFSD");
        } else {
            csEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            if(csEntry.isCredit()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
        }

        createOutputEntry(csEntry, validGroup);
        
        csEntry.setTransactionLedgerEntryDesc("COSTSHARE_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant
        csEntry.setChartOfAccountsCode("");

        csEntry.setChartOfAccountsCode(csEntry.getA21SubAccount().getCostSharingChartOfAccountsCode());
        csEntry.setAccountNumber(csEntry.getA21SubAccount().getCostSharingAccountNumber());

        lookupObjectCode(csEntry);

        if(csEntry.getA21SubAccount().getCostSharingAccountNumber() == null) {
            csEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        } else {
            csEntry.setSubAccountNumber(csEntry.getA21SubAccount().getCostSharingSubAccountNumber());
        }
        
        csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE); //TODO: move into constants
        csEntry.setFinancialObjectTypeCode("TE"); //TODO: move into constants
        csEntry.setTrnEntryLedgerSequenceNumber(new Integer(0));
        csEntry.setTransactionLedgerEntryDesc("COSTSHARE_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant

        if (scrubberUtil.costSharingAccumulator.isPositive()) {
            csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(scrubberUtil.costSharingAccumulator);
        } else {
            csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(scrubberUtil.costSharingAccumulator.negated());
        }

        csEntry.setTransactionDate(runDate);
        csEntry.setOrganizationDocumentNumber("");
        csEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        csEntry.setOrganizationReferenceId(null);
        csEntry.setReferenceFinDocumentTypeCode(null);
        csEntry.setFinSystemRefOriginationCode(null);
        csEntry.setFinancialDocumentReferenceNbr(null);
        csEntry.setReversalDate(null);
        csEntry.setTransactionEncumbranceUpdtCd("");

        createOutputEntry(csEntry, validGroup);

        csEntry.setTransactionLedgerEntryDesc("OFFSET_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant

        if (offset == null) {
            transactionErrors.add("OFFSET DEFINITION NOT FOUND");
        } else {
            csEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            if(offset.getFinancialSubObjectCode() == null) {
                csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE); // TODO: constant
            } else {
                csEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        }

        objectCode = objectCodeService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialObjectCode());
        if (objectCode == null) {
            transactionErrors.add("NO OBJECT FOR OBJECT ON OFSD");
        } else {
            csEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            if(csEntry.isCredit()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
        }

        createOutputEntry(csEntry, validGroup);
        scrubberUtil.costSharingAccumulator = KualiDecimal.ZERO;
    }// End of method

    /**
     * 3200-COST-SHARE-ENC
     * 
     * The purpose of this method is to generate a "Cost Share Encumbrance" transaction
     * for the current transaction and its offset.
     * 
     * The cost share chart and account for current transaction are obtained from the
     * CA_A21_SUB_ACCT_T table. This method calls the method SET-OBJECT-2004 to get
     * the Cost Share Object Code. It then writes out the cost share transaction.
     * Next it read the GL_OFFSET_DEFN_T table for the offset object code that
     * corresponds to the cost share object code. In addition to the object code it
     * needs to get subobject code. It then reads the CA_OBJECT_CODE_T table to make
     * sure the offset object code found in the GL_OFFSET_DEFN_T is valid  and to
     * get the object type code associated with this object code. It writes out the
     * offset transaction and returns.
     * 
     * @param inputEntry
     */
    private void costShareEncumbrance(OriginEntry inputEntry) {

        OriginEntry csEntry = new OriginEntry(inputEntry);
        this.writeSwitchStatusCD = ScrubberUtil.FROM_COST_SHARE_ENCUMBRANCE;

        csEntry.setTransactionLedgerEntryDesc(csEntry.getTransactionLedgerEntryDesc().substring(0, 29) +
                "FR" + csEntry.getChartOfAccountsCode()+ csEntry.getAccountNumber());

        csEntry.setChartOfAccountsCode(csEntry.getA21SubAccount().getCostSharingChartOfAccountsCode());
        csEntry.setAccountNumber(csEntry.getA21SubAccount().getCostSharingAccountNumber());
        csEntry.setSubAccountNumber(csEntry.getA21SubAccount().getCostSharingSubAccountNumber());

        if(!StringUtils.hasText(csEntry.getSubAccountNumber())) {
            csEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }

        csEntry.setFinancialBalanceTypeCode("CE"); // TODO: constant
        csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        csEntry.setTrnEntryLedgerSequenceNumber(new Integer(0));
        
        if (StringUtils.hasText(inputEntry.getTransactionDebitCreditCode())) {
            if (inputEntry.getTransactionLedgerEntryAmount().isPositive()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                csEntry.setTransactionLedgerEntryAmount(inputEntry.getTransactionLedgerEntryAmount().negated());
            }
        }

        csEntry.setTransactionDate(runDate);

        lookupObjectCode(csEntry);
        createOutputEntry(csEntry, validGroup);

        csEntry.setTransactionLedgerEntryDesc("GENERATED OFFSET"); //TODO: constant
        
        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialDocumentTypeCode(), csEntry.getFinancialBalanceTypeCode());
        if (offset == null) {
            transactionErrors.add("OFFSET DEFINITION NOT FOUND");
        } else {
            csEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            if(offset.getFinancialSubObjectCode() == null) {
                csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE); // TODO: constant
            } else {
                csEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        }

        ObjectCode objectCode = objectCodeService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialObjectCode());
        if (objectCode == null) {
            transactionErrors.add("NO OBJECT FOR OBJECT ON OFSD");
        } else {
            csEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            if(csEntry.isCredit()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
        }

        csEntry.setTransactionDate(runDate);
        csEntry.setOrganizationDocumentNumber("");
        csEntry.setProjectCode(Constants.DASHES_PROJECT_CODE); // constant
        csEntry.setOrganizationReferenceId(null);
        csEntry.setReferenceFinDocumentTypeCode(null);
        csEntry.setFinSystemRefOriginationCode(null);
        csEntry.setFinancialDocumentReferenceNbr(null);
        csEntry.setReversalDate(null);
        csEntry.setTransactionEncumbranceUpdtCd("");

        createOutputEntry(csEntry, validGroup); // TODO: is this created if there have been errors?!
    }// End of method

    /**
     * SET-OBJECT-2004
     * 
     * The purpose of this method is to find a cost share object code. It accomplishes
     * this by reading the CA_OBJECT_CODE_T based on input transaction's object code,
     * fiscal year, and chart of accounts code. It then checks the object level code
     * for the object it just read to determine what the cost share object code should be.
     * 
     * As an example if the object level of the object code on the input transaction is
     * "Travel" then this methods sets the cost share object code to "9960".
     * 
     * This method will then verify the cost share object code against the CA_OBJECT_CODE_T
     * table and obtain the corresponding object type to put into the output transaction.
     * 
     * @param inputEntry
     */
    private void lookupObjectCode(OriginEntry inputEntry) { // SET-OBECT-2004

        // TODO: cant we just do an inputEntry
        inputEntry.refreshReferenceObject("financialObject");

        checkGLObject(inputEntry.getFinancialObject(), "NO OBJECT FOR OBJECT ON OFSD");

        String objectCode = inputEntry.getFinancialObjectCode();
        String inputObjectLevelCode = inputEntry.getFinancialObject().getFinancialObjectLevelCode();
        String inputObjectCode = inputEntry.getFinancialObjectCode();

        // TODO: MOVE ALL THIS TO CONSTANTS
        if("ACSA".equals(inputObjectLevelCode)) { //ACADEMIC SALARIES
            objectCode = "9920"; //TRSFRS_OF_FUNDS_ACAD_SAL
        } else if("BASE".equals(inputObjectLevelCode)) { //ASSESMENTS_EXPENDITURES
            objectCode = "9959"; //TRANSFER_OUT_20_REALLOCATION
        } else if("BENF".equals(inputObjectLevelCode) &&
                ("9956".equals(inputObjectCode) || "5700".compareTo(inputObjectCode) < 0 )) { //BENEFITS
            objectCode = "9956"; //TRSFRS_OF_FUNDS_FRINGE_BENF
        } else if("BENF".equals(inputObjectLevelCode)) { //BENEFITS
            objectCode = "9957"; //TRSFRS_OF_FUNDS_RETIREMENT 
        } else if("BISA".equals(inputObjectLevelCode)) { //BI-WEEKLY_SALARY
            objectCode = "9925"; //TRSFRS_OF_FUNDS_CLER_SAL 
        } else if("CAP".equals(inputObjectLevelCode)) { //CAPITAL_ASSETS
            objectCode = "9970"; //TRSFRS_OF_FUNDS_CAPITAL  
        } else if("CORE".equals(inputObjectLevelCode)) { //ALLOTMENTS_AND_CHARGES_OUT
            // Do nothing
        } else if("CORI".equals(inputObjectLevelCode)) { //ALLOTMENTS_AND_CHARGES_IN
            // Do nothing
        } else if("FINA".equals(inputObjectLevelCode) &&
                ("9954".equals(inputObjectCode) || "5400".equals(inputObjectCode))) { //STUDENT_FINANCIAL_AID - TRSFRS_OF_FUNDS_FEE_REM  - GRADUATE_FEE_REMISSIONS
            objectCode = "9954"; //TRSFRS_OF_FUNDS_CAPITAL  
        } else if("FINA".equals(inputObjectLevelCode)) { //STUDENT_FINANCIAL_AID
            objectCode = "9958"; //TRSFRS_OF_FUNDS_FELL_AND_SCHO 
        } else if("HRCO".equals(inputObjectLevelCode)) { //HOURLY_COMPENSATION
            objectCode = "9930"; //TRSFRS_OF_FUNDS_WAGES 
        } else if("ICOE".equals(inputObjectLevelCode)) { //INDIRECT_COST_RECOVERY_EXPENSE
            objectCode = "9955"; //TRSFRS_OF_FUNDS_INDRCT_COST 
        } else if("PART".equals(inputObjectLevelCode)) { //PART_TIME_INSTRUCTION_NON_STUDENT
            objectCode = "9923"; //TRSFRS_OF_FUNDS_ACAD_ASSIST 
        } else if("PRSA".equals(inputObjectLevelCode)) { //PROFESSIONAL_SALARIES
            objectCode = "9924"; //TRSF_OF_FUNDS_PROF_SAL 
        } else if("RESV".equals(inputObjectLevelCode)) { //RESERVES
            objectCode = "9979"; //TRSFRS_OF_FUNDS_UNAPP_BAL
        } else if("SAAP".equals(inputObjectLevelCode)) { //SALARY_ACCRUAL_EXPENSE
            objectCode = "9923"; //TRSFRS_OF_FUNDS_ACAD_ASSIST
        } else if("TRAN".equals(inputObjectLevelCode)) { //TRANSFER_EXPENSE
            objectCode = "9959"; //TRANSFER_OUT_20_REALLOCATION
        } else if("TRAV".equals(inputObjectLevelCode)) { //TRAVEL
            objectCode = "9960"; //TRSFRS_OF_FUNDS_TRAVEL
        } else if("TREX".equals(inputObjectLevelCode)) { //TRANSFER_5199_EXPENSE
            objectCode = "9959"; //TRANSFER_OUT_20_REALLOCATION
        } else if("TRIN".equals(inputObjectLevelCode)) { //TRANSFER_1699_INCOME
            objectCode = "9915"; //TRSFRS_OF_FUNDS_REVENUE  
        } else {
            objectCode = "9940"; //TRSFRS_OF_FUNDS_SUP_AND_EXP 
        }
        
        inputEntry.setFinancialObjectCode(objectCode);
        inputEntry.refreshReferenceObject("financialObject"); // TODO: this needs to be checked!

        if (inputEntry.getFinancialObject() == null) {
            transactionErrors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_COST_SHARE_OBJECT_NOT_FOUND));
        } else {
            inputEntry.setFinancialObjectTypeCode(inputEntry.getFinancialObject().getFinancialObjectTypeCode());
        }
    }// End of method

    /**
     * 3000-Offset.
     * 
     * The purpose of this method is to build the actual offset transaction.
     * It does this by performing the following steps:
     * 1. Getting the offset object code and offset subobject code from the
     *    GL Offset Definition Table.
     * 2. For the offset object code it needs to get the associated object type,
     *    object subtype, and object active code.
     *    
     * @param workingEntry
     */
    private void generateOffset(OriginEntry workingEntry) {
        workingEntry.setTransactionLedgerEntryDesc("OFFSET_DESCRIPTION"); // TODO: get from property

        // See if we have the offset definition table assoicated with
        // this transaction. We need the offset object code from it.

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(workingEntry.getUniversityFiscalYear(), workingEntry.getChartOfAccountsCode(), workingEntry.getFinancialDocumentTypeCode(), workingEntry.getFinancialBalanceTypeCode());
        
        if(checkGLObject(offsetDefinition, kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND))) {
            workingEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            if (offsetDefinition.getFinancialSubObject() == null) {
                workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            } else {
                workingEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());
            }
            
            if (checkGLObject(offsetDefinition.getFinancialObject(), kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND))) {
                workingEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());
            }
            
        }

        workingEntry.setTransactionLedgerEntryAmount(scrubberUtil.offsetAmountAccumulator);
        
        if (scrubberUtil.offsetAmountAccumulator.isPositive()) {
            workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        } else {
            workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            workingEntry.setTransactionLedgerEntryAmount(workingEntry.getTransactionLedgerEntryAmount().multiply(new KualiDecimal(-1)));
        }
        
        workingEntry.setOrganizationDocumentNumber(null);
        workingEntry.setOrganizationReferenceId(null);
        workingEntry.setReferenceFinDocumentTypeCode(null);
        workingEntry.setFinSystemRefOriginationCode(null);
        workingEntry.setFinancialDocumentReferenceNbr(null);
        workingEntry.setTransactionEncumbranceUpdtCd(null);
        workingEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        workingEntry.setTransactionDate(runDate);
    }// End of method

    /**
     * 2520-init-SRCbArea
     */
    private void initScrubberValues() {
        wsExpiredAccount = null;
        wsExpiredChart = null;
        scrubberUtil.offsetAmountAccumulator = new KualiDecimal(0.0);
        scrubberUtil.creditAmountAccumulator = new KualiDecimal(0.0);
        scrubberUtil.debitAmountAccumulator = new KualiDecimal(0.0);
    }

    public void setOriginEntryService(OriginEntryService oes) {
        this.originEntryService = oes;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService groupService) {
        this.originEntryGroupService = groupService;
    }

    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }

    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    /**
     * Sets the offsetDefinitionService attribute value.
     * @param offsetDefinitionService The offsetDefinitionService to set.
     */
    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    /**
     * Sets the objectCodeService attribute value.
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
    private void createOutputEntry(OriginEntry inputEntry, OriginEntryGroup group) {
        originEntryService.createEntry(inputEntry, group);

        switch (writeSwitchStatusCD) {
        case ScrubberUtil.FROM_WRITE:
            ++scrubberUtil.writeCount;
            break;
        case ScrubberUtil.FROM_OFFSET:
            ++scrubberUtil.offsetCount;
            break;
        case ScrubberUtil.FROM_CAMS:
            ++scrubberUtil.camsCount;
            break;
        case ScrubberUtil.FROM_LIAB:
            ++scrubberUtil.liabCount;
            break;
        case ScrubberUtil.FROM_PLANT_INDEBTEDNESS:
            ++scrubberUtil.plantIndebtednessCount;
            break;
        case ScrubberUtil.FROM_COST_SHARE:
            ++scrubberUtil.costShareCount;
            break;
        case ScrubberUtil.FROM_COST_SHARE_ENCUMBRANCE:
            ++scrubberUtil.costShareEncCount;
            break;
        default:
            break;
        }
    }
    
    private void writeErrors() { // 8220-WRITE-ERRORS
        switch (writeSwitchStatusCD) {
        case ScrubberUtil.FROM_WRITE:
            ++scrubberUtil.errorCount;
            break;
        case ScrubberUtil.FROM_OFFSET:
            ++scrubberUtil.errorCount;
            break;
        case ScrubberUtil.FROM_COST_SHARE:
            ++scrubberUtil.errorCountCostShare;
            break;
        case ScrubberUtil.FROM_COST_SHARE_ENCUMBRANCE:
            ++scrubberUtil.errorCountCostShareEnc;
            break;
        default:
            break;
        }
    }
}
