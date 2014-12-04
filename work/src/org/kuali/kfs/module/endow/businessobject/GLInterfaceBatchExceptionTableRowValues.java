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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GLInterfaceBatchExceptionTableRowValues extends TransientBusinessObjectBase {
    protected String documentType;
    protected String documentNumber;
    protected String kemid;
    protected BigDecimal incomeAmount = BigDecimal.ZERO;
    protected BigDecimal principalAmount = BigDecimal.ZERO;
    protected BigDecimal securityCost = BigDecimal.ZERO;
    protected BigDecimal longTermGainLoss = BigDecimal.ZERO;
    protected BigDecimal shortTermGainLoss = BigDecimal.ZERO;

    public GLInterfaceBatchExceptionTableRowValues() {
        documentType = " ";
        documentNumber = " ";
        kemid = " ";
    }

    /**
     * Gets the documentType attribute.
     * @return Returns the documentType.
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute.
     * @return Returns the documentType.
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the eDocNumber attribute.
     * @return Returns the eDocNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the eDocNumber attribute.
     * @return Returns the eDocNumber.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the kemid attribute.
     * @return Returns the kemid.
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid attribute.
     * @return Returns the kemid.
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the incomeAmount attribute.
     * @return Returns the incomeAmount.
     */
    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    /**
     * Sets the incomeAmount attribute value.
     * @param incomeAmount The incomeAmount to set.
     */
    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    /**
     * Gets the principalAmount attribute.
     * @return Returns the principalAmount.
     */
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * Sets the principalAmount attribute value.
     * @param principalAmount The principalAmount to set.
     */
    public void setPrincipalAmountt(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * Gets the securityCost attribute.
     * @return Returns the securityCost.
     */
    public BigDecimal getSecurityCost() {
        return securityCost;
    }

    /**
     * Sets the securityCost attribute value.
     * @param securityCost The securityCost to set.
     */
    public void setSecurityCost(BigDecimal securityCost) {
        this.securityCost = securityCost;
    }

    /**
     * Gets the longTermGainLoss attribute.
     * @return Returns the longTermGainLoss.
     */
    public BigDecimal getLongTermGainLoss() {
        return longTermGainLoss;
    }

    /**
     * Sets the longTermGainLoss attribute.
     * @return Returns the longTermGainLoss.
     */
    public void setLongTermGainLoss(BigDecimal longTermGainLoss) {
        this.longTermGainLoss = longTermGainLoss;
    }

    /**
     * Gets the longTermGainLoss attribute.
     * @return Returns the longTermGainLoss.
     */
    public BigDecimal getShortTermGainLoss() {
        return shortTermGainLoss;
    }

    /**
     * Sets the shortTermGainLoss attribute.
     * @return Returns the shortTermGainLoss.
     */
    public void setShortTermGainLoss(BigDecimal shortTermGainLoss) {
        this.shortTermGainLoss = shortTermGainLoss;
    }

}
