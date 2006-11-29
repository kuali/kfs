/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/CashReceiptHeader.java,v $
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

package org.kuali.module.financial.bo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class CashReceiptHeader extends BusinessObjectBase {

    private String documentNumber;
    private String explanation;
    private Integer nextCheckSequenceId;
    private Integer nextCreditCardCashReceiptLineNumber;
    private Integer nextCreditCardCashieringDocumentLineNumber;
    private Integer nextRevolvingFundLineNumber;
    private Integer nextAdvanceDepositLineNumber;
    private String workgroupName;
    private Date depositDate;

    private CashReceiptDocument cashReceiptDocument;
    private List depositCashReceiptControl;

    /**
     * Default constructor.
     */
    public CashReceiptHeader() {
        depositCashReceiptControl = new ArrayList();

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the explanation attribute.
     * 
     * @return Returns the explanation
     * 
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Sets the explanation attribute.
     * 
     * @param explanation The explanation to set.
     * 
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }


    /**
     * Gets the nextCheckSequenceId attribute.
     * 
     * @return Returns the nextCheckSequenceId
     * 
     */
    public Integer getNextCheckSequenceId() {
        return nextCheckSequenceId;
    }

    /**
     * Sets the nextCheckSequenceId attribute.
     * 
     * @param nextCheckSequenceId The nextCheckSequenceId to set.
     * 
     */
    public void setNextCheckSequenceId(Integer nextCheckSequenceId) {
        this.nextCheckSequenceId = nextCheckSequenceId;
    }


    /**
     * Gets the nextCreditCardCashReceiptLineNumber attribute.
     * 
     * @return Returns the nextCreditCardCashReceiptLineNumber
     * 
     */
    public Integer getNextCreditCardCashReceiptLineNumber() {
        return nextCreditCardCashReceiptLineNumber;
    }

    /**
     * Sets the nextCreditCardCashReceiptLineNumber attribute.
     * 
     * @param nextCreditCardCashReceiptLineNumber The nextCreditCardCashReceiptLineNumber to set.
     * 
     */
    public void setNextCreditCardCashReceiptLineNumber(Integer nextCreditCardCashReceiptLineNumber) {
        this.nextCreditCardCashReceiptLineNumber = nextCreditCardCashReceiptLineNumber;
    }


    /**
     * Gets the nextCreditCardCashieringDocumentLineNumber attribute.
     * 
     * @return Returns the nextCreditCardCashieringDocumentLineNumber
     * 
     */
    public Integer getNextCreditCardCashieringDocumentLineNumber() {
        return nextCreditCardCashieringDocumentLineNumber;
    }

    /**
     * Sets the nextCreditCardCashieringDocumentLineNumber attribute.
     * 
     * @param nextCreditCardCashieringDocumentLineNumber The nextCreditCardCashieringDocumentLineNumber to set.
     * 
     */
    public void setNextCreditCardCashieringDocumentLineNumber(Integer nextCreditCardCashieringDocumentLineNumber) {
        this.nextCreditCardCashieringDocumentLineNumber = nextCreditCardCashieringDocumentLineNumber;
    }


    /**
     * Gets the nextRevolvingFundLineNumber attribute.
     * 
     * @return Returns the nextRevolvingFundLineNumber
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
     * Gets the nextAdvanceDepositLineNumber attribute.
     * 
     * @return Returns the nextAdvanceDepositLineNumber
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
     * Gets the workgroupName attribute.
     * 
     * @return Returns the workgroupName
     * 
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute.
     * 
     * @param workgroupName The workgroupName to set.
     * 
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }


    /**
     * Gets the depositDate attribute.
     * 
     * @return Returns the depositDate
     * 
     */
    public Date getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the depositDate attribute.
     * 
     * @param depositDate The depositDate to set.
     * 
     */
    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }


    /**
     * Gets the cashReceiptDocument attribute.
     * 
     * @return Returns the cashReceiptDocument
     * 
     */
    public CashReceiptDocument getCashReceiptDocument() {
        return cashReceiptDocument;
    }

    /**
     * Sets the cashReceiptDocument attribute.
     * 
     * @param cashReceiptDocument The cashReceiptDocument to set.
     * @deprecated
     */
    public void setCashReceiptDocument(CashReceiptDocument cashReceiptDocument) {
        this.cashReceiptDocument = cashReceiptDocument;
    }

    /**
     * Gets the depositCashReceiptControl list.
     * 
     * @return Returns the depositCashReceiptControl list
     * 
     */
    public List getDepositCashReceiptControl() {
        return depositCashReceiptControl;
    }

    /**
     * Sets the depositCashReceiptControl list.
     * 
     * @param depositCashReceiptControl The depositCashReceiptControl list to set.
     * 
     */
    public void setDepositCashReceiptControl(List depositCashReceiptControl) {
        this.depositCashReceiptControl = depositCashReceiptControl;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
