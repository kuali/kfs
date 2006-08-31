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
package org.kuali.module.financial.document;

import java.sql.Timestamp;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.rule.AccountingLineRule;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BasicFormatWithLineDescriptionAccountingLineParser;
import org.kuali.module.financial.rules.CashReceiptFamilyRule;

/**
 * Abstract class which defines behavior common to CashReceipt-like documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
abstract public class CashReceiptFamilyBase extends TransactionalDocumentBase {
    private String campusLocationCode; // TODO Needs to be an actual object - also need to clarify this
    private Timestamp depositDate;

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
            if (amount != null) {
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


    /**
     * Returns the sum total of the document's contents
     * 
     * @return
     */
    abstract public KualiDecimal getSumTotalAmount();
}