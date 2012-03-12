/*
 * Copyright 2009 The Kuali Foundation
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
