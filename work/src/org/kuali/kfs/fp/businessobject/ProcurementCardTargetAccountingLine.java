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

import org.kuali.kfs.sys.businessobject.TargetAccountingLine;


/**
 * This class is used to represent a procurement card target accounting line.
 */
public class ProcurementCardTargetAccountingLine extends TargetAccountingLine {
    private Integer financialDocumentTransactionLineNumber;
    protected int transactionContainerIndex;
    
    /**
     * Gets the transactionContainerIndex attribute.
     * 
     * @return Returns the transactionContainerIndex
     */
    
    public int getTransactionContainerIndex() {
        return transactionContainerIndex;
    }

    /**	
     * Sets the transactionContainerIndex attribute.
     * 
     * @param transactionContainerIndex The transactionContainerIndex to set.
     */
    public void setTransactionContainerIndex(int transactionContainerIndex) {
        this.transactionContainerIndex = transactionContainerIndex;
    }

    /**
     * @return Returns the financialDocumentTransactionLineNumber.
     */
    public Integer getFinancialDocumentTransactionLineNumber() {
        return financialDocumentTransactionLineNumber;
    }

    /**
     * @param financialDocumentTransactionLineNumber The financialDocumentTransactionLineNumber to set.
     */
    public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
        this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
    }
}
