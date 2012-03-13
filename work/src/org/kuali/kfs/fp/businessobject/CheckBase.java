/*
 * Copyright 2005 The Kuali Foundation
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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.springframework.util.ObjectUtils;

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
    private String cashieringStatus;
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
    @Override
    public Date getCheckDate() {
        return checkDate;
    }

    /**
     * Sets the checkDate attribute value.
     *
     * @param checkDate The checkDate to set.
     */
    @Override
    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    /**
     * Gets the checkNumber attribute.
     *
     * @return Returns the checkNumber.
     */
    @Override
    public String getCheckNumber() {
        return checkNumber;
    }

    /**
     * Sets the checkNumber attribute value.
     *
     * @param checkNumber The checkNumber to set.
     */
    @Override
    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    /**
     * Gets the description attribute.
     *
     * @return Returns the description.
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     *
     * @param description The description to set.
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the sequenceId attribute.
     *
     * @return Returns the sequenceId.
     */
    @Override
    public Integer getSequenceId() {
        return sequenceId;
    }

    /**
     * Sets the sequenceId attribute value.
     *
     * @param sequenceId The sequenceId to set.
     */
    @Override
    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    /**
     * Gets the amount attribute.
     *
     * @return Returns the amount.
     */
    @Override
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount attribute value.
     *
     * @param amount The amount to set.
     */
    @Override
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }


    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the cashieringStatus attribute.
     *
     * @return Returns the cashieringStatus.
     */
    @Override
    public String getCashieringStatus() {
        return cashieringStatus;
    }

    /**
     * Sets the cashieringStatus attribute value.
     *
     * @param cashieringStatus The cashieringStatus to set.
     */
    @Override
    public void setCashieringStatus(String financialDocumentColumnTypeCode) {
        this.cashieringStatus = financialDocumentColumnTypeCode;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     *
     * @return Returns the financialDocumentTypeCode.
     */
    @Override
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute value.
     *
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    @Override
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the financialDocumentDepositLineNumber attribute.
     *
     * @return Returns the financialDocumentDepositLineNumber.
     */
    @Override
    public Integer getFinancialDocumentDepositLineNumber() {
        return financialDocumentDepositLineNumber;
    }

    /**
     * Sets the financialDocumentDepositLineNumber attribute value.
     *
     * @param financialDocumentDepositLineNumber The financialDocumentDepositLineNumber to set.
     */
    @Override
    public void setFinancialDocumentDepositLineNumber(Integer financialDocumentDepositLineNumber) {
        this.financialDocumentDepositLineNumber = financialDocumentDepositLineNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("sequenceId", this.sequenceId);
        m.put("checkNumber", this.checkNumber);
        m.put("amount", this.amount);
        m.put("checkDate", this.checkDate);
        m.put("financialDocumentDepositLineNumber", this.financialDocumentDepositLineNumber);
        m.put("description", this.description);
        m.put("documentHeaderId", this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("cashieringStatus", this.cashieringStatus);

        return m;
    }


    /**
     * @see org.kuali.kfs.fp.businessobject.Check#isLike(org.kuali.kfs.fp.businessobject.Check)
     */
    @Override
    public boolean isLike(Check other) {
        boolean like = false;

        if (StringUtils.equals(checkNumber, other.getCheckNumber())) {
            if (StringUtils.equals(description, other.getDescription())) {
                if (StringUtils.equals(financialDocumentTypeCode, other.getFinancialDocumentTypeCode()) && StringUtils.equals(cashieringStatus, other.getCashieringStatus())) {
                    if (StringUtils.equals(documentNumber, other.getDocumentNumber())) {
                        if (ObjectUtils.nullSafeEquals(sequenceId, other.getSequenceId())) {
                            if (ObjectUtils.nullSafeEquals(financialDocumentDepositLineNumber, other.getFinancialDocumentDepositLineNumber())) {

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
