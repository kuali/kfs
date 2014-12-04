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

package org.kuali.kfs.fp.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents a deposit cash receipt control which contains cash receipt header used for validating receipts
 */
public class DepositCashReceiptControl extends PersistableBusinessObjectBase {
    private String financialDocumentDepositNumber;
    private Integer financialDocumentDepositLineNumber;
    private String financialDocumentCashReceiptNumber;

    private Timestamp financialSystemsCashReceiptProcessingTimestamp;
    private String financialSystemsProcessingOperatorIdentifier;

    private Deposit deposit;
    private CashReceiptDocument cashReceiptDocument;


    /**
     * Default constructor.
     */
    public DepositCashReceiptControl() {

    }

    /**
     * Gets the cashReceiptDocument attribute. 
     * @return Returns the cashReceiptDocument.
     */
    public CashReceiptDocument getCashReceiptDocument() {
        return cashReceiptDocument;
    }

    /**
     * Sets the cashReceiptDocument attribute value.
     * @param cashReceiptDocument The cashReceiptDocument to set.
     */
    public void setCashReceiptDocument(CashReceiptDocument cashReceiptDocument) {
        this.cashReceiptDocument = cashReceiptDocument;
    }



    /**
     * @return current value of deposit.
     */
    public Deposit getDeposit() {
        return deposit;
    }

    /**
     * Sets the deposit attribute value.
     * 
     * @param deposit The deposit to set.
     */
    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }


    /**
     * @return current value of financialDocumentCashReceiptNumber.
     */
    public String getFinancialDocumentCashReceiptNumber() {
        return financialDocumentCashReceiptNumber;
    }

    /**
     * Sets the financialDocumentCashReceiptNumber attribute value.
     * 
     * @param financialDocumentCashReceiptNumber The financialDocumentCashReceiptNumber to set.
     */
    public void setFinancialDocumentCashReceiptNumber(String financialDocumentCashReceiptNumber) {
        this.financialDocumentCashReceiptNumber = financialDocumentCashReceiptNumber;
    }


    /**
     * @return current value of financialDocumentDepositNumber.
     */
    public String getFinancialDocumentDepositNumber() {
        return financialDocumentDepositNumber;
    }

    /**
     * Sets the financialDocumentDepositNumber attribute value.
     * 
     * @param financialDocumentDepositNumber The financialDocumentDepositNumber to set.
     */
    public void setFinancialDocumentDepositNumber(String financialDocumentDepositNumber) {
        this.financialDocumentDepositNumber = financialDocumentDepositNumber;
    }


    /**
     * @return current value of financialDocumentDepositLineNumber.
     */
    public Integer getFinancialDocumentDepositLineNumber() {
        return financialDocumentDepositLineNumber;
    }

    /**
     * Sets the financialDocumentDepositLineNumber attribute value.
     * 
     * @param financialDocumentDepositLineNumber The financialDocumentDepositLineNumber to set.
     */
    public void setFinancialDocumentDepositLineNumber(Integer financialDocumentDepositLineNumber) {
        this.financialDocumentDepositLineNumber = financialDocumentDepositLineNumber;
    }


    /**
     * @return current value of financialSystemsCashReceiptProcessingTimestamp.
     */
    public Timestamp getFinancialSystemsCashReceiptProcessingTimestamp() {
        return financialSystemsCashReceiptProcessingTimestamp;
    }

    /**
     * Sets the financialSystemsCashReceiptProcessingTimestamp attribute value.
     * 
     * @param financialSystemsCashReceiptProcessingTimestamp The financialSystemsCashReceiptProcessingTimestamp to set.
     */
    public void setFinancialSystemsCashReceiptProcessingTimestamp(Timestamp financialSystemsCashReceiptProcessingTimestamp) {
        this.financialSystemsCashReceiptProcessingTimestamp = financialSystemsCashReceiptProcessingTimestamp;
    }


    /**
     * @return current value of financialSystemsProcessingOperatorIdentifier.
     */
    public String getFinancialSystemsProcessingOperatorIdentifier() {
        return financialSystemsProcessingOperatorIdentifier;
    }

    /**
     * Sets the financialSystemsProcessingOperatorIdentifier attribute value.
     * 
     * @param financialSystemsProcessingOperatorIdentifier The financialSystemsProcessingOperatorIdentifier to set.
     */
    public void setFinancialSystemsProcessingOperatorIdentifier(String financialSystemsProcessingOperatorIdentifier) {
        this.financialSystemsProcessingOperatorIdentifier = financialSystemsProcessingOperatorIdentifier;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentDepositNumber", getFinancialDocumentDepositNumber());
        m.put("financialDocumentDepositLineNumber", getFinancialDocumentDepositLineNumber());
        m.put("financialDocumentCashReceiptNumber", getFinancialDocumentCashReceiptNumber());
        return m;
    }
}
