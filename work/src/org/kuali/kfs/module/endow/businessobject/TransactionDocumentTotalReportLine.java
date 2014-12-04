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

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class TransactionDocumentTotalReportLine extends TransactionDocumentForReportLineBase {

    protected int totalNumberOfTransactionLines = 0;;

    public TransactionDocumentTotalReportLine() {
        this("", "", "");
    }
    
    public TransactionDocumentTotalReportLine(String documentType, String documentId, String securityId) {
        this.documentType = documentType;
        this.documentId = documentId;
        this.securityId = securityId;        
    }
    
    /**
     * Gets the totalNumberOfTransactionLines attribute. 
     * @return Returns the totalNumberOfTransactionLines.
     */
    public Integer getTotalNumberOfTransactionLines() {
        return totalNumberOfTransactionLines;
    }
     
    /**
     * Sets the totalNumberOfTransactionLines attribute value.
     * @param totalNumberOfTransactionLines The totalNumberOfTransactionLines to set.
     */
    public void setTotalNumberOfTransactionLines(int totalNumberOfTransactionLines) {
        this.totalNumberOfTransactionLines = totalNumberOfTransactionLines;
    }

    /**
     * Adds a income amount to the current income total
     * @param incomeAmount the income amount to add to the income total
     */
    public void addIncomeAmount(KualiDecimal incomeAmount) {
        this.incomeAmount = this.incomeAmount.add(incomeAmount);       
        this.totalNumberOfTransactionLines++;
        
    }
  
    /**
     * Adds a principal amount to the current principal total
     * @param principalAmount the principal amount to add to the principal total
     */
    public void addPrincipalAmount(KualiDecimal principalAmount) {
        this.principalAmount = this.principalAmount.add(principalAmount); 
        this.totalNumberOfTransactionLines++;
    }
}
