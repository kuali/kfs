/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class TransactionDocumentExceptionReportLine extends TransactionDocumentForReportLineBase {

    protected String kemid;

    public TransactionDocumentExceptionReportLine(String documentType, String documentId) {
        this(documentType, documentType, "", "");        
    }
    
    public TransactionDocumentExceptionReportLine(String documentType, String documentId, String securityId) {
        this(documentType, documentType, securityId, "");        
    }
    
    public TransactionDocumentExceptionReportLine(String documentType, String documentId, String securityId, String kemid) {
        this.documentType = documentType;
        this.documentId   = documentId;
        this.securityId   = securityId;
        this.kemid        = kemid;        
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
     * @param the kemid.
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }
    
    /**
     * Sets the securityId attribute. 
     * @param the securityId.
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }
    
    /**
     * Sets the incomeAmount attribute. 
     * @param the incomeAmount.
     */
    public void setIncomeAmount(KualiDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }
    
    /**
     * Sets the incomeUnits attribute. 
     * @param the incomeUnits.
     */
    public void setIncomeUnits(KualiDecimal incomeUnits) {
        this.incomeUnits = incomeUnits;
    }
    
    /**
     * Sets the principalAmount attribute. 
     * @param the principalAmount.
     */
    public void setPrincipalAmount(KualiDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
    
    /**
     * Sets the principalUnits attribute. 
     * @param the principalUnits.
     */
    public void setPrincipalUnits(KualiDecimal principalUnits) {
        this.principalUnits = principalUnits;
    }
}
