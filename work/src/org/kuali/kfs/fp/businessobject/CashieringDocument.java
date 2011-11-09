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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents a cashiering document
 */
public class CashieringDocument extends PersistableBusinessObjectBase {

    private String documentNumber;
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
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the depositFinancialSystemOriginationCode attribute.
     * 
     * @return Returns the depositFinancialSystemOriginationCode
     */
    public String getDepositFinancialSystemOriginationCode() {
        return depositFinancialSystemOriginationCode;
    }

    /**
     * Sets the depositFinancialSystemOriginationCode attribute.
     * 
     * @param depositFinancialSystemOriginationCode The depositFinancialSystemOriginationCode to set.
     */
    public void setDepositFinancialSystemOriginationCode(String depositFinancialSystemOriginationCode) {
        this.depositFinancialSystemOriginationCode = depositFinancialSystemOriginationCode;
    }


    /**
     * Gets the financialDocumentDepositNumber attribute.
     * 
     * @return Returns the financialDocumentDepositNumber
     */
    public String getFinancialDocumentDepositNumber() {
        return financialDocumentDepositNumber;
    }

    /**
     * Sets the financialDocumentDepositNumber attribute.
     * 
     * @param financialDocumentDepositNumber The financialDocumentDepositNumber to set.
     */
    public void setFinancialDocumentDepositNumber(String financialDocumentDepositNumber) {
        this.financialDocumentDepositNumber = financialDocumentDepositNumber;
    }


    /**
     * Gets the financialDocumentCheckAmount attribute.
     * 
     * @return Returns the financialDocumentCheckAmount
     */
    public KualiDecimal getFinancialDocumentCheckAmount() {
        return financialDocumentCheckAmount;
    }

    /**
     * Sets the financialDocumentCheckAmount attribute.
     * 
     * @param financialDocumentCheckAmount The financialDocumentCheckAmount to set.
     */
    public void setFinancialDocumentCheckAmount(KualiDecimal financialDocumentCheckAmount) {
        this.financialDocumentCheckAmount = financialDocumentCheckAmount;
    }


    /**
     * Gets the financialDocumentAdvanceDepositAmount attribute.
     * 
     * @return Returns the financialDocumentAdvanceDepositAmount
     */
    public KualiDecimal getFinancialDocumentAdvanceDepositAmount() {
        return financialDocumentAdvanceDepositAmount;
    }

    /**
     * Sets the financialDocumentAdvanceDepositAmount attribute.
     * 
     * @param financialDocumentAdvanceDepositAmount The financialDocumentAdvanceDepositAmount to set.
     */
    public void setFinancialDocumentAdvanceDepositAmount(KualiDecimal financialDocumentAdvanceDepositAmount) {
        this.financialDocumentAdvanceDepositAmount = financialDocumentAdvanceDepositAmount;
    }


    /**
     * Gets the financialDocumentRevolvingFundAmount attribute.
     * 
     * @return Returns the financialDocumentRevolvingFundAmount
     */
    public KualiDecimal getFinancialDocumentRevolvingFundAmount() {
        return financialDocumentRevolvingFundAmount;
    }

    /**
     * Sets the financialDocumentRevolvingFundAmount attribute.
     * 
     * @param financialDocumentRevolvingFundAmount The financialDocumentRevolvingFundAmount to set.
     */
    public void setFinancialDocumentRevolvingFundAmount(KualiDecimal financialDocumentRevolvingFundAmount) {
        this.financialDocumentRevolvingFundAmount = financialDocumentRevolvingFundAmount;
    }


    /**
     * Gets the financialDocumentNextCreditCardLineNumber attribute.
     * 
     * @return Returns the financialDocumentNextCreditCardLineNumber
     */
    public Integer getFinancialDocumentNextCreditCardLineNumber() {
        return financialDocumentNextCreditCardLineNumber;
    }

    /**
     * Sets the financialDocumentNextCreditCardLineNumber attribute.
     * 
     * @param financialDocumentNextCreditCardLineNumber The financialDocumentNextCreditCardLineNumber to set.
     */
    public void setFinancialDocumentNextCreditCardLineNumber(Integer financialDocumentNextCreditCardLineNumber) {
        this.financialDocumentNextCreditCardLineNumber = financialDocumentNextCreditCardLineNumber;
    }


    /**
     * Gets the financialDocumentCashAmount attribute.
     * 
     * @return Returns the financialDocumentCashAmount
     */
    public KualiDecimal getFinancialDocumentCashAmount() {
        return financialDocumentCashAmount;
    }

    /**
     * Sets the financialDocumentCashAmount attribute.
     * 
     * @param financialDocumentCashAmount The financialDocumentCashAmount to set.
     */
    public void setFinancialDocumentCashAmount(KualiDecimal financialDocumentCashAmount) {
        this.financialDocumentCashAmount = financialDocumentCashAmount;
    }


    /**
     * Gets the financialDocumentCreditCardAmount attribute.
     * 
     * @return Returns the financialDocumentCreditCardAmount
     */
    public KualiDecimal getFinancialDocumentCreditCardAmount() {
        return financialDocumentCreditCardAmount;
    }

    /**
     * Sets the financialDocumentCreditCardAmount attribute.
     * 
     * @param financialDocumentCreditCardAmount The financialDocumentCreditCardAmount to set.
     */
    public void setFinancialDocumentCreditCardAmount(KualiDecimal financialDocumentCreditCardAmount) {
        this.financialDocumentCreditCardAmount = financialDocumentCreditCardAmount;
    }


    /**
     * Gets the financialDocumentTotalCoinAmount attribute.
     * 
     * @return Returns the financialDocumentTotalCoinAmount
     */
    public KualiDecimal getFinancialDocumentTotalCoinAmount() {
        return financialDocumentTotalCoinAmount;
    }

    /**
     * Sets the financialDocumentTotalCoinAmount attribute.
     * 
     * @param financialDocumentTotalCoinAmount The financialDocumentTotalCoinAmount to set.
     */
    public void setFinancialDocumentTotalCoinAmount(KualiDecimal financialDocumentTotalCoinAmount) {
        this.financialDocumentTotalCoinAmount = financialDocumentTotalCoinAmount;
    }


    /**
     * Gets the financialDocumentChangeOutAmount attribute.
     * 
     * @return Returns the financialDocumentChangeOutAmount
     */
    public KualiDecimal getFinancialDocumentChangeOutAmount() {
        return financialDocumentChangeOutAmount;
    }

    /**
     * Sets the financialDocumentChangeOutAmount attribute.
     * 
     * @param financialDocumentChangeOutAmount The financialDocumentChangeOutAmount to set.
     */
    public void setFinancialDocumentChangeOutAmount(KualiDecimal financialDocumentChangeOutAmount) {
        this.financialDocumentChangeOutAmount = financialDocumentChangeOutAmount;
    }


    /**
     * Gets the nextCheckLineNumber attribute.
     * 
     * @return Returns the nextCheckLineNumber
     */
    public Integer getNextCheckLineNumber() {
        return nextCheckLineNumber;
    }

    /**
     * Sets the nextCheckLineNumber attribute.
     * 
     * @param nextCheckLineNumber The nextCheckLineNumber to set.
     */
    public void setNextCheckLineNumber(Integer nextCheckLineNumber) {
        this.nextCheckLineNumber = nextCheckLineNumber;
    }


    /**
     * Gets the nextAdvanceDepositLineNumber attribute.
     * 
     * @return Returns the nextAdvanceDepositLineNumber
     */
    public Integer getNextAdvanceDepositLineNumber() {
        return nextAdvanceDepositLineNumber;
    }

    /**
     * Sets the nextAdvanceDepositLineNumber attribute.
     * 
     * @param nextAdvanceDepositLineNumber The nextAdvanceDepositLineNumber to set.
     */
    public void setNextAdvanceDepositLineNumber(Integer nextAdvanceDepositLineNumber) {
        this.nextAdvanceDepositLineNumber = nextAdvanceDepositLineNumber;
    }


    /**
     * Gets the nextRevolvingFundLineNumber attribute.
     * 
     * @return Returns the nextRevolvingFundLineNumber
     */
    public Integer getNextRevolvingFundLineNumber() {
        return nextRevolvingFundLineNumber;
    }

    /**
     * Sets the nextRevolvingFundLineNumber attribute.
     * 
     * @param nextRevolvingFundLineNumber The nextRevolvingFundLineNumber to set.
     */
    public void setNextRevolvingFundLineNumber(Integer nextRevolvingFundLineNumber) {
        this.nextRevolvingFundLineNumber = nextRevolvingFundLineNumber;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
