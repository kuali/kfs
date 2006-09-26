/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team ()
 */
public class DepositCashReceiptControl extends BusinessObjectBase {
    private String financialDocumentDepositNumber;
    private Integer financialDocumentDepositLineNumber;
    private String financialDocumentCashReceiptNumber;

    private Timestamp financialSystemsCashReceiptProcessingTimestamp;
    private String financialSystemsProcessingOperatorIdentifier;

    private Deposit deposit;
    private CashReceiptHeader cashReceiptHeader;


    /**
     * Default constructor.
     */
    public DepositCashReceiptControl() {

    }


    /**
     * @return current value of cashReceiptHeader.
     */
    public CashReceiptHeader getCashReceiptHeader() {
        return cashReceiptHeader;
    }

    /**
     * Sets the cashReceiptHeader attribute value.
     * 
     * @param cashReceiptHeader The cashReceiptHeader to set.
     */
    public void setCashReceiptHeader(CashReceiptHeader cashReceiptHeader) {
        this.cashReceiptHeader = cashReceiptHeader;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentDepositNumber", getFinancialDocumentDepositNumber());
        m.put("financialDocumentDepositLineNumber", getFinancialDocumentDepositLineNumber());
        m.put("financialDocumentCashReceiptNumber", getFinancialDocumentCashReceiptNumber());
        return m;
    }
}
