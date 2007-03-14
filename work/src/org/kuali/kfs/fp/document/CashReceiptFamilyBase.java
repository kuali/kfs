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
package org.kuali.module.financial.document;

import java.sql.Timestamp;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.rule.AccountingLineRule;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BasicFormatWithLineDescriptionAccountingLineParser;
import org.kuali.module.financial.rules.CashReceiptFamilyRule;

/**
 * Abstract class which defines behavior common to CashReceipt-like documents.
 */
abstract public class CashReceiptFamilyBase extends AccountingDocumentBase {
    private String campusLocationCode; // TODO Needs to be an actual object - also need to clarify this
    private Timestamp depositDate;

    /**
     * Constructs a CashReceiptFamilyBase
     */
    public CashReceiptFamilyBase() {
        setCampusLocationCode(Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE);
    }

    /**
     * Documents in the CashReceiptFamily do not perform Sufficient Funds checking
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#documentPerformsSufficientFundsCheck()
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
    public Timestamp getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the depositDate attribute value.
     * 
     * @param depositDate The depositDate to set.
     */
    public void setDepositDate(Timestamp depositDate) {
        this.depositDate = depositDate;
    }


    /**
     * Total for a Cash Receipt according to the spec should be the sum of the amounts on accounting lines belonging to object codes
     * having the 'income' object type, less the sum of the amounts on accounting lines belonging to object codes having the
     * 'expense' object type.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceTotal()
     */
    @Override
    public KualiDecimal getSourceTotal() {
        CashReceiptFamilyRule crFamilyRule = (CashReceiptFamilyRule) SpringServiceLocator.getKualiRuleService().getBusinessRulesInstance(this, AccountingLineRule.class);
        KualiDecimal total = KualiDecimal.ZERO;
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();

            KualiDecimal amount = al.getAmount().abs();
            if (amount != null && amount.isNonZero()) {
                if (crFamilyRule.isDebit(this, al)) {
                    total = total.subtract(amount);
                }
                else if (crFamilyRule.isCredit(al, this)) {
                    total = total.add(amount);
                }
                else {
                    throw new IllegalStateException("could not determine credit/debit for accounting line");
                }
            }
        }
        return total;
    }

    /**
     * Cash Receipts only have source lines, so this should always return 0.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetTotal()
     */
    @Override
    public KualiDecimal getTargetTotal() {
        return KualiDecimal.ZERO;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }


    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BasicFormatWithLineDescriptionAccountingLineParser();
    }
}