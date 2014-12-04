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
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for Holding Tax Lot table
 */
public class TransactionArchiveSecurity extends PersistableBusinessObjectBase {
    
    // Composite keys:
    private String  documentNumber;
    private Integer lineNumber;
    private String  lineTypeCode;
    
    // Other fields:
    private String securityId;
    private String registrationCode;
    private String etranCode;
    private BigDecimal unitValue;
    private BigDecimal unitsHeld;
    private BigDecimal holdingCost;
    private BigDecimal longTermGainLoss;
    private BigDecimal shortTermGainLoss;
    
    // Referenced objects:
    protected Security security;

    /**
     * Gets the security attribute. 
     * @return Returns the security.
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets the security attribute value.
     * @param security The security to set.
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the lineNumber attribute. 
     * @return Returns the lineNumber.
     */
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber attribute value.
     * @param lineNumber The lineNumber to set.
     */
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the lineTypeCode attribute. 
     * @return Returns the lineTypeCode.
     */
    public String getLineTypeCode() {
        return lineTypeCode;
    }

    /**
     * Sets the lineTypeCode attribute value.
     * @param lineTypeCode The lineTypeCode to set.
     */
    public void setLineTypeCode(String lineTypeCode) {
        this.lineTypeCode = lineTypeCode;
    }

    /**
     * Gets the securityId attribute. 
     * @return Returns the securityId.
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * Sets the securityId attribute value.
     * @param securityId The securityId to set.
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    /**
     * Gets the registrationCode attribute. 
     * @return Returns the registrationCode.
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * Sets the registrationCode attribute value.
     * @param registrationCode The registrationCode to set.
     */
    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    /**
     * Gets the entranCode attribute. 
     * @return Returns the entranCode.
     */
    public String getEtranCode() {
        return etranCode;
    }

    /**
     * Sets the entranCode attribute value.
     * @param entranCode The entranCode to set.
     */
    public void setEtranCode(String etranCode) {
        this.etranCode = etranCode;
    }

    /**
     * Gets the unitValue attribute. 
     * @return Returns the unitValue.
     */
    public BigDecimal getUnitValue() {
        return unitValue;
    }

    /**
     * Sets the unitValue attribute value.
     * @param unitValue The unitValue to set.
     */
    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    /**
     * Gets the unitsHeld attribute. 
     * @return Returns the unitsHeld.
     */
    public BigDecimal getUnitsHeld() {
        return unitsHeld;
    }

    /**
     * Sets the unitsHeld attribute value.
     * @param unitsHeld The unitsHeld to set.
     */
    public void setUnitsHeld(BigDecimal unitsHeld) {
        this.unitsHeld = unitsHeld;
    }

    /**
     * Gets the holdingCost attribute. 
     * @return Returns the holdingCost.
     */
    public BigDecimal getHoldingCost() {
        return holdingCost;
    }

    /**
     * Sets the holdingCost attribute value.
     * @param holdingCost The holdingCost to set.
     */
    public void setHoldingCost(BigDecimal holdingCost) {
        this.holdingCost = holdingCost;
    }

    /**
     * Gets the longTermGainLoss attribute. 
     * @return Returns the longTermGainLoss.
     */
    public BigDecimal getLongTermGainLoss() {
        return longTermGainLoss;
    }

    /**
     * Sets the longTermGainLoss attribute value.
     * @param longTermGainLoss The longTermGainLoss to set.
     */
    public void setLongTermGainLoss(BigDecimal longTermGainLoss) {
        this.longTermGainLoss = longTermGainLoss;
    }

    /**
     * Gets the shortTermGainLoss attribute. 
     * @return Returns the shortTermGainLoss.
     */
    public BigDecimal getShortTermGainLoss() {
        return shortTermGainLoss;
    }

    /**
     * Sets the shortTermGainLoss attribute value.
     * @param shortTermGainLoss The shortTermGainLoss to set.
     */
    public void setShortTermGainLoss(BigDecimal shortTermGainLoss) {
        this.shortTermGainLoss = shortTermGainLoss;
    }
   
}
