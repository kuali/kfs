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
