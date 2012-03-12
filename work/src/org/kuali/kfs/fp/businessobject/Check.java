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

import java.sql.Date;

import org.apache.ojb.broker.PersistenceBrokerAware;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;


/**
 * This class represents a check is cashiering related documents
 */
public interface Check extends PersistableBusinessObject, PersistenceBrokerAware {

    /**
     * Gets the checkDate attribute.
     * 
     * @return Returns the checkDate.
     */
    public Date getCheckDate();

    /**
     * Sets the checkDate attribute value.
     * 
     * @param checkDate The checkDate to set.
     */
    public void setCheckDate(Date checkDate);

    /**
     * Gets the checkNumber attribute.
     * 
     * @return Returns the checkNumber.
     */
    public String getCheckNumber();

    /**
     * Sets the checkNumber attribute value.
     * 
     * @param checkNumber The checkNumber to set.
     */
    public void setCheckNumber(String checkNumber);

    /**
     * Gets the description attribute.
     * 
     * @return Returns the description.
     */
    public String getDescription();

    /**
     * Sets the description attribute value.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description);


    /**
     * Gets the sequenceId attribute.
     * 
     * @return Returns the sequenceId.
     */
    public Integer getSequenceId();

    /**
     * Sets the sequenceId attribute value.
     * 
     * @param sequenceId The sequenceId to set.
     */
    public void setSequenceId(Integer sequenceId);


    /**
     * Gets the amount attribute.
     * 
     * @return Returns the amount.
     */
    public KualiDecimal getAmount();

    /**
     * Sets the amount attribute value.
     * 
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount);

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber();

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber);


    /**
     * Sets the financialDocumentDepositLineNumber.
     * 
     * @param financialDocumentDepositLineNumber
     */
    public void setFinancialDocumentDepositLineNumber(Integer financialDocumentDepositLineNumber);

    /**
     * Gets the financialDocumentDepositLineNumber attribute.
     * 
     * @return Returns the financialDocumentDepositLineNumber.
     */
    public Integer getFinancialDocumentDepositLineNumber();

    /**
     * Returns the document type that created this record.
     * 
     * @return the document type of the creating document.
     */
    public String getFinancialDocumentTypeCode();

    /**
     * Sets the kind of document that created this check
     * 
     * @param financialDocumentTypeCode
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode);

    /**
     * Gets the code that describes where this cashiering record came from
     * 
     * @return the cashiering record source
     */
    public String getCashieringStatus();

    /**
     * Sets the source of this cashiering record.
     * 
     * @param cashieringStatus
     */
    public void setCashieringStatus(String cashieringStatus);

    /**
     * Returns true if this check has the same primitive field values as the given one
     * 
     * @param other
     * @return true if this Check has the same primitive field values as the given one
     */
    public boolean isLike(Check other);
}
