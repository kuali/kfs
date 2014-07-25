/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.fp.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.FinancialProcessingWorkflowConstants;
import org.kuali.kfs.fp.batch.ProcurementCardLoadStep;
import org.kuali.kfs.fp.businessobject.ProcurementCardHolder;
import org.kuali.kfs.fp.businessobject.ProcurementCardSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the Procurement Card Document Class. The procurement cards distributes expenses from clearing accounts. It is a two-sided
 * document, but only target lines are displayed because source lines cannot be changed. Transaction, Card, and Vendor information
 * are associated with the document to help better distribute the expense.
 */
public class ProcurementCardDocument extends CapitalAccountingLinesDocumentBase implements AmountTotaling, CapitalAssetEditable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardDocument.class);

    protected ProcurementCardHolder procurementCardHolder;

    protected List transactionEntries;
    protected ProcurementCardTargetAccountingLine newTargetLine;
    protected transient CapitalAssetManagementModuleService capitalAssetManagementModuleService;
    protected boolean autoApprovedIndicator;
    private static final Integer NO_POST_BACK_IS_ALLOWED = new Integer(0);

    /**
     * Default constructor.
     */
    public ProcurementCardDocument() {
        super();
        transactionEntries = new ArrayList<ProcurementCardTransactionDetail>();
    }

    /**
     * @return Returns the transactionEntries.
     */
    public List getTransactionEntries() {
        return transactionEntries;
    }

    /**
     * @param transactionEntries The transactionEntries to set.
     */
    public void setTransactionEntries(List transactionEntries) {
        this.transactionEntries = transactionEntries;
    }

    /**
     * Gets the procurementCardHolder attribute.
     *
     * @return Returns the procurementCardHolder.
     */
    public ProcurementCardHolder getProcurementCardHolder() {
        return procurementCardHolder;
    }

    /**
     * Sets the procurementCardHolder attribute value.
     *
     * @param procurementCardHolder The procurementCardHolder to set.
     */
    public void setProcurementCardHolder(ProcurementCardHolder procurementCardHolder) {
        this.procurementCardHolder = procurementCardHolder;
    }

    /**
     * Removes the target accounting line at the given index from the transaction detail entry.
     *
     * @param index
     */
    public void removeTargetAccountingLine(int index) {
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) getTargetAccountingLines().get(index);

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getTargetAccountingLines().remove(line);
            }
        }
    }

    /**
     * Override to set the accounting line in the transaction detail object.
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */

    @Override
    public void addSourceAccountingLine(SourceAccountingLine sourceLine) {
        ProcurementCardSourceAccountingLine line = (ProcurementCardSourceAccountingLine) sourceLine;

        line.setSequenceNumber(this.getNextSourceLineNumber());

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getSourceAccountingLines().add(line);
            }
        }

        this.nextSourceLineNumber = new Integer(this.getNextSourceLineNumber().intValue() + 1);
    }

    /**
     * Override to set the accounting line in the transaction detail object.
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#addTargetAccountingLine(TargetAccountingLine)
     */
    @Override
    public void addTargetAccountingLine(TargetAccountingLine targetLine) {
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) targetLine;

        line.setSequenceNumber(this.getNextTargetLineNumber());

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getTargetAccountingLines().add(line);
            }
        }

        this.nextTargetLineNumber = new Integer(this.getNextTargetLineNumber().intValue() + 1);
    }

    /**
     * Override to get source accounting lines out of transactions
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLines()
     */
    @Override
    public List getSourceAccountingLines() {
        List sourceAccountingLines = new ArrayList();

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            for (Iterator iterator = transactionEntry.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                SourceAccountingLine sourceLine = (SourceAccountingLine) iterator.next();
                sourceAccountingLines.add(sourceLine);
            }
        }

        return sourceAccountingLines;
    }

    /**
     * Get source accounting lines for specific transactions line
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLines()
     */
    public List getSourceAccountingLines(Integer docuemntLineNumber) {
        List sourceAccountingLines = new ArrayList();

        ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) transactionEntries.get(docuemntLineNumber);
        for (Iterator iterator = transactionEntry.getSourceAccountingLines().iterator(); iterator.hasNext();) {
            SourceAccountingLine sourceLine = (SourceAccountingLine) iterator.next();
            sourceAccountingLines.add(sourceLine);
        }
        return sourceAccountingLines;
    }

    /**
     * Override to get target accounting lines out of transactions
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLines()
     */
    @Override
    public List getTargetAccountingLines() {
        List targetAccountingLines = new ArrayList();

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            for (Iterator iterator = transactionEntry.getTargetAccountingLines().iterator(); iterator.hasNext();) {
                TargetAccountingLine targetLine = (TargetAccountingLine) iterator.next();
                targetAccountingLines.add(targetLine);
            }
        }

        return targetAccountingLines;
    }

    /**
     * Get target accounting lines for specific transactions line
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLines()
     */
    public List getTargetAccountingLines(Integer docuemntLineNumber) {
        List targetAccountingLines = new ArrayList();

        ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) transactionEntries.get(docuemntLineNumber);
        for (Iterator iterator = transactionEntry.getTargetAccountingLines().iterator(); iterator.hasNext();) {
            TargetAccountingLine targetLine = (TargetAccountingLine) iterator.next();
            targetAccountingLines.add(targetLine);
        }
        return targetAccountingLines;
    }


    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return ProcurementCardSourceAccountingLine.class;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    @Override
    public Class getTargetAccountingLineClass() {
        return ProcurementCardTargetAccountingLine.class;
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        // Updating for rice-1.0.0 api changes. doRouteStatusChange() went away, so
        // that functionality needs to be a part of doRouteStatusChange now.
        // handleRouteStatusChange did not happen on a save
        if (!KewApiConstants.ACTION_TAKEN_SAVED_CD.equals(statusChangeEvent.getDocumentEventCode())) {
            this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);
        }
    }

    /**
     * On procurement card documents, positive source amounts are credits, negative source amounts are debits.
     *
     * @param transactionalDocument The document the accounting line being checked is located in.
     * @param accountingLine The accounting line being analyzed.
     * @return True if the accounting line given is a debit accounting line, false otherwise.
     * @throws Throws an IllegalStateException if one of the following rules are violated: the accounting line amount is zero or the
     *         accounting line is not an expense or income accounting line.
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDebit(FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase.IsDebitUtils#isDebitConsideringSection(AccountingDocumentRuleBase,
     *      AccountingDocument, AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        // disallow error correction
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        isDebitUtils.disallowErrorCorrectionDocumentCheck(this);
        return isDebitUtils.isDebitConsideringSection(this, (AccountingLine) postable);
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#postProcessSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this, documentTypeName);
        }
    }

    /**
     * @return CapitalAssetManagementModuleService
     */
    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        if (capitalAssetManagementModuleService == null) {
            capitalAssetManagementModuleService = SpringContext.getBean(CapitalAssetManagementModuleService.class);
        }
        return capitalAssetManagementModuleService;
    }

    /**
     * Provides answers to the following splits: IsDocumentAutoApproved
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(FinancialProcessingWorkflowConstants.IS_DOCUMENT_AUTO_APPROVED)) {
            return isAutoApprovedIndicator();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * set the autoApprovedIndicator
     *
     * @param value- the new value to set
     */
    public void setAutoApprovedIndicator(boolean value)
    {
        autoApprovedIndicator = value;
    }

    /**
     * get the AutoApprovedIndicator
     *
     * @return the value of autoApprovedIndicator
     */
    public boolean isAutoApprovedIndicator()
    {
        return autoApprovedIndicator;
    }

    /**
     * @return the previous fiscal year used with all GLPE
     */
    public static final Integer getPreviousFiscalYear() {
        YearEndPendingEntryService yearEndPendingEntryService = SpringContext.getBean(YearEndPendingEntryService.class);
        Integer previousFiscalYear = yearEndPendingEntryService.getPreviousFiscalYear();
        return previousFiscalYear;
    }


    /**
     * @see   org.kuali.kfs.sys.document.AccountingDocumentBase#customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail, GeneralLedgerPendingEntry)
     *
     * If the transaction falls in allow posting back rules, then it will be posted to the previous Fiscal year
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {

        int lineNumber = 0;
        if(postable instanceof ProcurementCardSourceAccountingLine) {
            ProcurementCardSourceAccountingLine cardDetail =  (ProcurementCardSourceAccountingLine) postable;
            lineNumber = cardDetail.getFinancialDocumentTransactionLineNumber() - 1;
        }
        else if (postable instanceof ProcurementCardTargetAccountingLine) {
            ProcurementCardTargetAccountingLine cardDetail =  (ProcurementCardTargetAccountingLine) postable;
            lineNumber = cardDetail.getFinancialDocumentTransactionLineNumber() - 1;
        }

        Date postingDate = getProcurementCardTransactionPostingDetailDate(lineNumber);

        if (ObjectUtils.isNotNull(postingDate) && allowBackpost(postingDate)) {
            Integer prevFiscYr = getPreviousFiscalYear();

            explicitEntry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);
            explicitEntry.setUniversityFiscalYear(prevFiscYr);

            List<SourceAccountingLine> srcLines = getSourceAccountingLines(lineNumber);

            for (SourceAccountingLine src : srcLines) {
                src.setPostingYear(prevFiscYr);
            }

            List<TargetAccountingLine> trgLines = getTargetAccountingLines(lineNumber);

            for (TargetAccountingLine trg : trgLines) {
                trg.setPostingYear(prevFiscYr);
            }
        }
    }

    /**
    * Get Transaction Date - should be only one value. If not, we'll take the first one.
    *
    * @return Date
    */
    private Date getProcurementCardTransactionPostingDetailDate(Integer lineNumber) {
        Date date = null;

        ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) transactionEntries.get(lineNumber);
        date = transactionEntry.getTransactionPostingDate();

        return date;
     }


    /**
     * Allow Backpost
     * This method returns true if ALLOW_BACKPOST_DAYS number of days greater than zero
     * and transaction posting date is before the current FY start
     * and today date falls in the period of ALLOW_BACKPOST_DAYS number of days.
     * Otherwise it returns false.
     *
     * @param tranDate
     * @return
     */
    public boolean allowBackpost(Date tranDate) {
        ParameterService      parameterService      = SpringContext.getBean(ParameterService.class);
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        int allowBackpostDays = Integer.parseInt(parameterService.getParameterValueAsString(
                ProcurementCardLoadStep.class, KFSParameterKeyConstants.FpParameterConstants.ALLOW_BACKPOST_DAYS));

        if (allowBackpostDays == NO_POST_BACK_IS_ALLOWED){
            if (LOG.isDebugEnabled()) {
                LOG.debug("no backpost is allowed; posting entry to current FY");
            }
            return false;
        }

        Calendar today = Calendar.getInstance();
        Integer prevFiscYr = getPreviousFiscalYear();
        java.util.Date priorClosingDateTemp = universityDateService.getLastDateOfFiscalYear(prevFiscYr);


        Calendar priorClosingDate = Calendar.getInstance();
        priorClosingDate.setTime(priorClosingDateTemp);

        // adding 1 to set the date to midnight the day after backpost is allowed so that preqs allow backpost on the last day
        Calendar allowBackpostDate = Calendar.getInstance();
        allowBackpostDate.setTime(priorClosingDate.getTime());
        allowBackpostDate.add(Calendar.DATE, allowBackpostDays + 1);

        Calendar tranCal = Calendar.getInstance();
        tranCal.setTime(tranDate);

        // if today is after the closing date but before/equal to the allowed backpost date and the transaction date is for the
        // prior year, set the year to prior year
        if ((today.compareTo(priorClosingDate) > 0) && (today.compareTo(allowBackpostDate) <= 0) && (tranCal.compareTo(priorClosingDate) <= 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("allowBackpost() within range to allow backpost; posting entry to period 13 of previous FY");
            }
            return true;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("allowBackpost() not within range to allow backpost; posting entry to current FY");
        }
        return false;
    }

}
