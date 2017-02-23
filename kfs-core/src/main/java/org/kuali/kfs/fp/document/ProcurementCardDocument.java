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

package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.ProcurementCardHolder;
import org.kuali.kfs.fp.businessobject.ProcurementCardSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;

import edu.arizona.kfs.fp.businessobject.ProcurementCardTransactionDetail;

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
        if (StringUtils.equals(nodeName, KFSConstants.FinancialProcessingWorkflowConstants.IS_DOCUMENT_AUTO_APPROVED)){
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

}
