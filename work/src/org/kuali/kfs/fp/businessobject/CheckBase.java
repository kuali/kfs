/*
 * Copyright 2005-2007 The Kuali Foundation.
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
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.NumberUtils;

/**
 * This class represents a check in the system. It is a generalized check business object that will be used by the Cash Receipts
 * document, the Cashier document, etc.
 */

public class CheckBase extends PersistableBusinessObjectBase implements Check {
    private String checkNumber;
    private Date checkDate;
    private String description;
    private Integer sequenceId;
    private KualiDecimal amount;
    private String documentNumber;
    private String financialDocumentTypeCode;
    private String cashieringRecordSource;
    private Integer financialDocumentDepositLineNumber;

    /**
     * Constructs a CheckBase business object.
     */
    public CheckBase() {
        super();
        this.sequenceId = new Integer(1);
        this.amount = KualiDecimal.ZERO;
    }

    /**
     * Gets the checkDate attribute.
     * 
     * @return Returns the checkDate.
     */
    public Date getCheckDate() {
        return checkDate;
    }

    /**
     * Sets the checkDate attribute value.
     * 
     * @param checkDate The checkDate to set.
     */
    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    /**
     * Gets the checkNumber attribute.
     * 
     * @return Returns the checkNumber.
     */
    public String getCheckNumber() {
        return checkNumber;
    }

    /**
     * Sets the checkNumber attribute value.
     * 
     * @param checkNumber The checkNumber to set.
     */
    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    /**
     * Gets the description attribute.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the sequenceId attribute.
     * 
     * @return Returns the sequenceId.
     */
    public Integer getSequenceId() {
        return sequenceId;
    }

    /**
     * Sets the sequenceId attribute value.
     * 
     * @param sequenceId The sequenceId to set.
     */
    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    /**
     * Gets the amount attribute.
     * 
     * @return Returns the amount.
     */
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount attribute value.
     * 
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the cashieringRecordSource attribute.
     * 
     * @return Returns the cashieringRecordSource.
     */
    public String getCashieringRecordSource() {
        return cashieringRecordSource;
    }

    /**
     * Sets the cashieringRecordSource attribute value.
     * 
     * @param cashieringRecordSource The cashieringRecordSource to set.
     */
    public void setCashieringRecordSource(String financialDocumentColumnTypeCode) {
        this.cashieringRecordSource = financialDocumentColumnTypeCode;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode.
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute value.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the financialDocumentDepositLineNumber attribute.
     * 
     * @return Returns the financialDocumentDepositLineNumber.
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("sequenceId", this.sequenceId);
        m.put("checkNumber", this.checkNumber);
        m.put("amount", this.amount);
        m.put("checkDate", this.checkDate);
        m.put("financialDocumentDepositLineNumber", this.financialDocumentDepositLineNumber);
        m.put("description", this.description);
        m.put("documentHeaderId", this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("cashieringRecordSource", this.cashieringRecordSource);

        return m;
    }


    /**
     * @see org.kuali.module.financial.bo.Check#isLike(org.kuali.module.financial.bo.Check)
     */
    public boolean isLike(Check other) {
        boolean like = false;

        if (StringUtils.equals(checkNumber, other.getCheckNumber())) {
            if (StringUtils.equals(description, other.getDescription())) {
                if (StringUtils.equals(financialDocumentTypeCode, other.getFinancialDocumentTypeCode()) && StringUtils.equals(cashieringRecordSource, other.getCashieringRecordSource())) {
                    if (StringUtils.equals(documentNumber, other.getDocumentNumber())) {
                        if (NumberUtils.equals(sequenceId, other.getSequenceId())) {
                            if (NumberUtils.equals(financialDocumentDepositLineNumber, other.getFinancialDocumentDepositLineNumber())) {

                                if (DateUtils.isSameDay(checkDate, other.getCheckDate())) {
                                    if ((amount != null) && amount.equals(other.getAmount())) {
                                        like = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return like;
    }
}
