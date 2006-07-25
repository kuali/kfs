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

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.financial.bo.AdvanceDepositDetail;

/**
 * This is the business object that represents the AdvanceDeposit document in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since an Advance Deposit document is a one sided
 * transactional document, only accepting funds into the university, the accounting line data will be held in the source accounting
 * line data structure only.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AdvanceDepositDocument extends CashReceiptDocumentBase {
    // holds details about each advance deposit
    private List<AdvanceDepositDetail> advanceDeposits = new ArrayList<AdvanceDepositDetail>();

    // incrementers for detail lines
    private Integer nextAdvanceDepositLineNumber = 1;

    // monetary attributes
    private KualiDecimal totalAdvanceDepositAmount = KualiDecimal.ZERO;

    /**
     * Default constructor that calls super.
     */
    public AdvanceDepositDocument() {
        super();
    }

    /**
     * Gets the total advance deposit amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotalAdvanceDepositAmount() {
        return totalAdvanceDepositAmount;
    }

    /**
     * This method returns the advance deposit total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalAdvanceDepositAmount() {
        return (String) new CurrencyFormatter().format(totalAdvanceDepositAmount);
    }

    /**
     * Sets the total advance deposit amount which is the sum of all advance deposits on this document.
     * 
     * @param advanceDepositAmount
     */
    public void setTotalAdvanceDepositAmount(KualiDecimal advanceDepositAmount) {
        this.totalAdvanceDepositAmount = advanceDepositAmount;
    }

    /**
     * Gets the list of advance deposits which is a list of AdvanceDepositDetail business objects.
     * 
     * @return List
     */
    public List<AdvanceDepositDetail> getAdvanceDeposits() {
        return advanceDeposits;
    }

    /**
     * Sets the advance deposits list.
     * 
     * @param advanceDeposits
     */
    public void setAdvanceDeposits(List<AdvanceDepositDetail> advanceDeposits) {
        this.advanceDeposits = advanceDeposits;
    }

    /**
     * Adds a new advance deposit to the list.
     * 
     * @param advanceDepositDetail
     */
    public void addAdvanceDeposit(AdvanceDepositDetail advanceDepositDetail) {
        // these three make up the primary key for an advance deposit detail record
        prepareNewAdvanceDeposit(advanceDepositDetail);

        // add the new detail record to the list
        this.advanceDeposits.add(advanceDepositDetail);

        // increment line number
        this.nextAdvanceDepositLineNumber++;

        // update the overall amount
        this.totalAdvanceDepositAmount = this.totalAdvanceDepositAmount.add(advanceDepositDetail.getFinancialDocumentAdvanceDepositAmount());
    }

    /**
     * This is a helper method that automatically populates document specfic information into the advance deposit
     * (AdvanceDepositDetail) instance.
     * 
     * @param advanceDepositDetail
     */
    public final void prepareNewAdvanceDeposit(AdvanceDepositDetail advanceDepositDetail) {
        advanceDepositDetail.setFinancialDocumentLineNumber(this.nextAdvanceDepositLineNumber);
        advanceDepositDetail.setFinancialDocumentColumnTypeCode(Constants.AdvanceDepositConstants.CASH_RECEIPT_ADVANCE_DEPOSIT_COLUMN_TYPE_CODE);
        advanceDepositDetail.setFinancialDocumentNumber(this.getFinancialDocumentNumber());
        advanceDepositDetail.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(this.getClass()));
    }

    /**
     * Retrieve a particular advance deposit at a given index in the list of advance deposits.
     * 
     * @param index
     * @return AdvanceDepositDetail
     */
    public AdvanceDepositDetail getAdvanceDepositDetail(int index) {
        while (this.advanceDeposits.size() <= index) {
            advanceDeposits.add(new AdvanceDepositDetail());
        }
        return advanceDeposits.get(index);
    }

    /**
     * This method removes an advance deposit from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeAdvanceDeposit(int index) {
        AdvanceDepositDetail advanceDepositDetail = advanceDeposits.remove(index);
        this.totalAdvanceDepositAmount = this.totalAdvanceDepositAmount.subtract(advanceDepositDetail.getFinancialDocumentAdvanceDepositAmount());
    }

    /**
     * @return Integer
     */
    public Integer getNextAdvanceDepositLineNumber() {
        return nextAdvanceDepositLineNumber;
    }

    /**
     * @param nextAdvanceDepositLineNumber
     */
    public void setNextAdvanceDepositLineNumber(Integer nextAdvanceDepositLineNumber) {
        this.nextAdvanceDepositLineNumber = nextAdvanceDepositLineNumber;
    }

    /**
     * This method returns the overall total of the document - the advance deposit total.
     * 
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getSumTotalAmount() {
        return this.totalAdvanceDepositAmount;
    }
    
    /**
     * Overrides super to call super and then also add in the new list of advance deposits that have to be managed.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getAdvanceDeposits());

        return managedLists;
    }
}