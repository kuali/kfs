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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class TransactioneDocPostingDocumentTotalReportLine extends TransientBusinessObjectBase {

    private String documentNumber;
    private String documentName;
    
    private KualiDecimal totalPrincipleCash;
    private KualiDecimal totalHoldingCost;
    private KualiDecimal totalIncomeCash;
    private KualiDecimal totalUnits;
    
    /**
     * 
     * Constructs a TransactioneDocPostingDocumentTotalReportLine.java.
     */
    public TransactioneDocPostingDocumentTotalReportLine() {
        this.totalPrincipleCash = KualiDecimal.ZERO;
        this.totalHoldingCost   = KualiDecimal.ZERO;
        this.totalIncomeCash    = KualiDecimal.ZERO;
        this.totalUnits         = KualiDecimal.ZERO;
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
     * Gets the documentName attribute. 
     * @return Returns the documentName.
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Sets the documentName attribute value.
     * @param documentName The documentName to set.
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * Gets the totalPrincipleCash attribute. 
     * @return Returns the totalPrincipleCash.
     */
    public KualiDecimal getTotalPrincipleCash() {
        return totalPrincipleCash;
    }

    /**
     * Sets the totalPrincipleCash attribute value.
     * @param totalPrincipleCash The totalPrincipleCash to set.
     */
    public void setTotalPrincipleCash(KualiDecimal totalPrincipleCash) {
        this.totalPrincipleCash = totalPrincipleCash;
    }

    /**
     * Gets the totalHoldingCost attribute. 
     * @return Returns the totalHoldingCost.
     */
    public KualiDecimal getTotalHoldingCost() {
        return totalHoldingCost;
    }

    /**
     * Sets the totalHoldingCost attribute value.
     * @param totalHoldingCost The totalHoldingCost to set.
     */
    public void setTotalHoldingCost(KualiDecimal totalHoldingCost) {
        this.totalHoldingCost = totalHoldingCost;
    }

    /**
     * Gets the totalIncomeCash attribute. 
     * @return Returns the totalIncomeCash.
     */
    public KualiDecimal getTotalIncomeCash() {
        return totalIncomeCash;
    }

    /**
     * Sets the totalIncomeCash attribute value.
     * @param totalIncomeCash The totalIncomeCash to set.
     */
    public void setTotalIncomeCash(KualiDecimal totalIncomeCash) {
        this.totalIncomeCash = totalIncomeCash;
    }

    /**
     * Gets the totalUnits attribute. 
     * @return Returns the totalUnits.
     */
    public KualiDecimal getTotalUnits() {
        return totalUnits;
    }

    /**
     * Sets the totalUnits attribute value.
     * @param totalUnits The totalUnits to set.
     */
    public void setTotalUnits(KualiDecimal totalUnits) {
        this.totalUnits = totalUnits;
    }

}
