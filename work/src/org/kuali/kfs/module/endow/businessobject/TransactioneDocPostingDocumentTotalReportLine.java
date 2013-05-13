/*
 * Copyright 2010 The Kuali Foundation.
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
