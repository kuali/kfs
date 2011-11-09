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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Abstract class which defines behavior common to CashReceipt-like documents.
 */
abstract public class CashReceiptFamilyBase extends CapitalAccountingLinesDocumentBase implements CapitalAssetEditable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashReceiptFamilyBase.class);
    protected String campusLocationCode; // TODO Needs to be an actual object - also need to clarify this
    protected Date depositDate;

    /**
     * Constructs a CashReceiptFamilyBase
     */
    public CashReceiptFamilyBase() {
        setCampusLocationCode(KFSConstants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE);
    }

    /**
     * Documents in the CashReceiptFamily do not perform Sufficient Funds checking
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#documentPerformsSufficientFundsCheck()
     */
    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        return false;
    }

    /**
     * Gets the campusLocationCode attribute.
     * 
     * @return Returns the campusLocationCode.
     */
    public String getCampusLocationCode() {
        return campusLocationCode;
    }

    /**
     * Sets the campusLocationCode attribute value.
     * 
     * @param campusLocationCode The campusLocationCode to set.
     */
    public void setCampusLocationCode(String campusLocationCode) {
        this.campusLocationCode = campusLocationCode;
    }

    /**
     * Gets the depositDate attribute.
     * 
     * @return Returns the depositDate.
     */
    public Date getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the depositDate attribute value.
     * 
     * @param depositDate The depositDate to set.
     */
    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }


    /**
     * Total for a Cash Receipt according to the spec should be the sum of the amounts on accounting lines belonging to object codes
     * having the 'income' object type, less the sum of the amounts on accounting lines belonging to object codes having the
     * 'expense' object type.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceTotal()
     */
    @Override
    public KualiDecimal getSourceTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        AccountingLineBase al = null;
        if (ObjectUtils.isNull(getSourceAccountingLines()) || getSourceAccountingLines().isEmpty()) {
            refreshReferenceObject(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES);
        }
        Iterator iter = getSourceAccountingLines().iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            try {
                KualiDecimal amount = al.getAmount().abs();
                if (amount != null && amount.isNonZero()) {
                    if (isDebit(al)) {
                        total = total.subtract(amount);
                    }
                    else { // in this context, if it's not a debit, it's a credit
                        total = total.add(amount);
                    }
                }
            }
            catch (Exception e) {
                // Possibly caused by accounting lines w/ bad data
                LOG.error("Error occured trying to compute Cash receipt total, returning 0", e);
                return KualiDecimal.ZERO;
            }
        }
        return total;
    }

    /**
     * Cash Receipts only have source lines, so this should always return 0.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetTotal()
     */
    @Override
    public KualiDecimal getTargetTotal() {
        return KualiDecimal.ZERO;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Returns true if accounting line is debit
     * 
     * @param financialDocument
     * @param accountingLine
     * @param true if accountline line
     * @see IsDebitUtils#isDebitConsideringType(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // error corrections are not allowed
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        isDebitUtils.disallowErrorCorrectionDocumentCheck(this);
        return isDebitUtils.isDebitConsideringType(this, (AccountingLine) postable);
    }

    /**
     * Overrides to set the entry's description to the description from the accounting line, if a value exists.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line in accounting document
     * @param explicitEntry general ledger pending entry
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        String accountingLineDescription = postable.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(accountingLineDescription)) {
            explicitEntry.setTransactionLedgerEntryDescription(accountingLineDescription);
        }
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return ObjectUtils.isNull(capitalAssetInformation) ? null : capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }
}
