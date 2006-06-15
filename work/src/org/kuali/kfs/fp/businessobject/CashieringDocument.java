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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashieringDocument extends BusinessObjectBase {

    private String financialDocumentNumber;
    private String depositFinancialSystemOriginationCode;
    private String financialDocumentDepositNumber;
    private KualiDecimal financialDocumentCheckAmount;
    private KualiDecimal financialDocumentAdvanceDepositAmount;
    private KualiDecimal financialDocumentRevolvingFundAmount;
    private Integer financialDocumentNextCreditCardLineNumber;
    private KualiDecimal financialDocumentCashAmount;
    private KualiDecimal financialDocumentCreditCardAmount;
    private KualiDecimal financialDocumentTotalCoinAmount;
    private KualiDecimal financialDocumentChangeOutAmount;
    private Integer nextCheckLineNumber;
    private Integer nextAdvanceDepositLineNumber;
    private Integer nextRevolvingFundLineNumber;

    /**
     * Default constructor.
     */
    public CashieringDocument() {

    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     * 
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     * 
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }


    /**
     * Gets the depositFinancialSystemOriginationCode attribute.
     * 
     * @return - Returns the depositFinancialSystemOriginationCode
     * 
     */
    public String getDepositFinancialSystemOriginationCode() {
        return depositFinancialSystemOriginationCode;
    }

    /**
     * Sets the depositFinancialSystemOriginationCode attribute.
     * 
     * @param depositFinancialSystemOriginationCode The depositFinancialSystemOriginationCode to set.
     * 
     */
    public void setDepositFinancialSystemOriginationCode(String depositFinancialSystemOriginationCode) {
        this.depositFinancialSystemOriginationCode = depositFinancialSystemOriginationCode;
    }


    /**
     * Gets the financialDocumentDepositNumber attribute.
     * 
     * @return - Returns the financialDocumentDepositNumber
     * 
     */
    public String getFinancialDocumentDepositNumber() {
        return financialDocumentDepositNumber;
    }

    /**
     * Sets the financialDocumentDepositNumber attribute.
     * 
     * @param financialDocumentDepositNumber The financialDocumentDepositNumber to set.
     * 
     */
    public void setFinancialDocumentDepositNumber(String financialDocumentDepositNumber) {
        this.financialDocumentDepositNumber = financialDocumentDepositNumber;
    }


    /**
     * Gets the financialDocumentCheckAmount attribute.
     * 
     * @return - Returns the financialDocumentCheckAmount
     * 
     */
    public KualiDecimal getFinancialDocumentCheckAmount() {
        return financialDocumentCheckAmount;
    }

    /**
     * Sets the financialDocumentCheckAmount attribute.
     * 
     * @param financialDocumentCheckAmount The financialDocumentCheckAmount to set.
     * 
     */
    public void setFinancialDocumentCheckAmount(KualiDecimal financialDocumentCheckAmount) {
        this.financialDocumentCheckAmount = financialDocumentCheckAmount;
    }


    /**
     * Gets the financialDocumentAdvanceDepositAmount attribute.
     * 
     * @return - Returns the financialDocumentAdvanceDepositAmount
     * 
     */
    public KualiDecimal getFinancialDocumentAdvanceDepositAmount() {
        return financialDocumentAdvanceDepositAmount;
    }

    /**
     * Sets the financialDocumentAdvanceDepositAmount attribute.
     * 
     * @param financialDocumentAdvanceDepositAmount The financialDocumentAdvanceDepositAmount to set.
     * 
     */
    public void setFinancialDocumentAdvanceDepositAmount(KualiDecimal financialDocumentAdvanceDepositAmount) {
        this.financialDocumentAdvanceDepositAmount = financialDocumentAdvanceDepositAmount;
    }


    /**
     * Gets the financialDocumentRevolvingFundAmount attribute.
     * 
     * @return - Returns the financialDocumentRevolvingFundAmount
     * 
     */
    public KualiDecimal getFinancialDocumentRevolvingFundAmount() {
        return financialDocumentRevolvingFundAmount;
    }

    /**
     * Sets the financialDocumentRevolvingFundAmount attribute.
     * 
     * @param financialDocumentRevolvingFundAmount The financialDocumentRevolvingFundAmount to set.
     * 
     */
    public void setFinancialDocumentRevolvingFundAmount(KualiDecimal financialDocumentRevolvingFundAmount) {
        this.financialDocumentRevolvingFundAmount = financialDocumentRevolvingFundAmount;
    }


    /**
     * Gets the financialDocumentNextCreditCardLineNumber attribute.
     * 
     * @return - Returns the financialDocumentNextCreditCardLineNumber
     * 
     */
    public Integer getFinancialDocumentNextCreditCardLineNumber() {
        return financialDocumentNextCreditCardLineNumber;
    }

    /**
     * Sets the financialDocumentNextCreditCardLineNumber attribute.
     * 
     * @param financialDocumentNextCreditCardLineNumber The financialDocumentNextCreditCardLineNumber to set.
     * 
     */
    public void setFinancialDocumentNextCreditCardLineNumber(Integer financialDocumentNextCreditCardLineNumber) {
        this.financialDocumentNextCreditCardLineNumber = financialDocumentNextCreditCardLineNumber;
    }


    /**
     * Gets the financialDocumentCashAmount attribute.
     * 
     * @return - Returns the financialDocumentCashAmount
     * 
     */
    public KualiDecimal getFinancialDocumentCashAmount() {
        return financialDocumentCashAmount;
    }

    /**
     * Sets the financialDocumentCashAmount attribute.
     * 
     * @param financialDocumentCashAmount The financialDocumentCashAmount to set.
     * 
     */
    public void setFinancialDocumentCashAmount(KualiDecimal financialDocumentCashAmount) {
        this.financialDocumentCashAmount = financialDocumentCashAmount;
    }


    /**
     * Gets the financialDocumentCreditCardAmount attribute.
     * 
     * @return - Returns the financialDocumentCreditCardAmount
     * 
     */
    public KualiDecimal getFinancialDocumentCreditCardAmount() {
        return financialDocumentCreditCardAmount;
    }

    /**
     * Sets the financialDocumentCreditCardAmount attribute.
     * 
     * @param financialDocumentCreditCardAmount The financialDocumentCreditCardAmount to set.
     * 
     */
    public void setFinancialDocumentCreditCardAmount(KualiDecimal financialDocumentCreditCardAmount) {
        this.financialDocumentCreditCardAmount = financialDocumentCreditCardAmount;
    }


    /**
     * Gets the financialDocumentTotalCoinAmount attribute.
     * 
     * @return - Returns the financialDocumentTotalCoinAmount
     * 
     */
    public KualiDecimal getFinancialDocumentTotalCoinAmount() {
        return financialDocumentTotalCoinAmount;
    }

    /**
     * Sets the financialDocumentTotalCoinAmount attribute.
     * 
     * @param financialDocumentTotalCoinAmount The financialDocumentTotalCoinAmount to set.
     * 
     */
    public void setFinancialDocumentTotalCoinAmount(KualiDecimal financialDocumentTotalCoinAmount) {
        this.financialDocumentTotalCoinAmount = financialDocumentTotalCoinAmount;
    }


    /**
     * Gets the financialDocumentChangeOutAmount attribute.
     * 
     * @return - Returns the financialDocumentChangeOutAmount
     * 
     */
    public KualiDecimal getFinancialDocumentChangeOutAmount() {
        return financialDocumentChangeOutAmount;
    }

    /**
     * Sets the financialDocumentChangeOutAmount attribute.
     * 
     * @param financialDocumentChangeOutAmount The financialDocumentChangeOutAmount to set.
     * 
     */
    public void setFinancialDocumentChangeOutAmount(KualiDecimal financialDocumentChangeOutAmount) {
        this.financialDocumentChangeOutAmount = financialDocumentChangeOutAmount;
    }


    /**
     * Gets the nextCheckLineNumber attribute.
     * 
     * @return - Returns the nextCheckLineNumber
     * 
     */
    public Integer getNextCheckLineNumber() {
        return nextCheckLineNumber;
    }

    /**
     * Sets the nextCheckLineNumber attribute.
     * 
     * @param nextCheckLineNumber The nextCheckLineNumber to set.
     * 
     */
    public void setNextCheckLineNumber(Integer nextCheckLineNumber) {
        this.nextCheckLineNumber = nextCheckLineNumber;
    }


    /**
     * Gets the nextAdvanceDepositLineNumber attribute.
     * 
     * @return - Returns the nextAdvanceDepositLineNumber
     * 
     */
    public Integer getNextAdvanceDepositLineNumber() {
        return nextAdvanceDepositLineNumber;
    }

    /**
     * Sets the nextAdvanceDepositLineNumber attribute.
     * 
     * @param nextAdvanceDepositLineNumber The nextAdvanceDepositLineNumber to set.
     * 
     */
    public void setNextAdvanceDepositLineNumber(Integer nextAdvanceDepositLineNumber) {
        this.nextAdvanceDepositLineNumber = nextAdvanceDepositLineNumber;
    }


    /**
     * Gets the nextRevolvingFundLineNumber attribute.
     * 
     * @return - Returns the nextRevolvingFundLineNumber
     * 
     */
    public Integer getNextRevolvingFundLineNumber() {
        return nextRevolvingFundLineNumber;
    }

    /**
     * Sets the nextRevolvingFundLineNumber attribute.
     * 
     * @param nextRevolvingFundLineNumber The nextRevolvingFundLineNumber to set.
     * 
     */
    public void setNextRevolvingFundLineNumber(Integer nextRevolvingFundLineNumber) {
        this.nextRevolvingFundLineNumber = nextRevolvingFundLineNumber;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }
}
