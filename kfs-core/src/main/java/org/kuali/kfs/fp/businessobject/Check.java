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
