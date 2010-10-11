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

public abstract class TransactionDocumentForReportLineBase extends TransientBusinessObjectBase {
    protected  String documentType;
    protected  String documentId;
    protected  String securityId;
    protected  KualiDecimal incomeAmount = KualiDecimal.ZERO;
    protected  KualiDecimal incomeUnits = KualiDecimal.ZERO;
    protected  KualiDecimal principalAmount = KualiDecimal.ZERO;
    protected  KualiDecimal principalUnits = KualiDecimal.ZERO;

    /**
     * Gets the documentType attribute. 
     * @return Returns the documentType.
     */
    public String getDocumentType() {
        return documentType;
    }
    
    /**
     * Gets the documentId attribute. 
     * @return Returns the documentId.
     */
    public String getDocumentId() {
        return documentId;
    }
    
    /**
     * Gets the securityId attribute. 
     * @return Returns the securityId.
     */
    public String getSecurityId() {
        return securityId;
    }
    
    /**
     * Sets the securityId attribute. 
     * @param the securityId.
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }
    
    /**
     * Gets the incomeAmount attribute. 
     * @return Returns the incomeAmount.
     */
    public KualiDecimal getIncomeAmount() {
        return incomeAmount;
    }
    
    /**
     * Sets the incomeAmount attribute. 
     * @param the incomeAmount.
     */
    public void setIncomeAmount(KualiDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }
    
    /**
     * Gets the incomeUnits attribute. 
     * @return Returns the incomeUnits.
     */
    public KualiDecimal getIncomeUnits() {
        return incomeUnits;
    }
    
    /**
     * Sets the incomeUnits attribute. 
     * @param the incomeUnits.
     */
    public void setIncomeUnits(KualiDecimal incomeUnits) {
        this.incomeUnits = incomeUnits;
    }
    
    /**
     * Gets the principalAmount attribute. 
     * @return Returns the principalAmount.
     */
    public KualiDecimal getPrincipalAmount() {
        return principalAmount;
    }
    
    /**
     * Sets the principalAmount attribute. 
     * @param the principalAmount.
     */
    public void setPrincipalAmount(KualiDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
    
    /**
     * Gets the principalUnits attribute. 
     * @return Returns the principalUnits.
     */
    public KualiDecimal getPrincipalUnits() {
        return principalUnits;
    }  

    /**
     * Sets the principalUnits attribute. 
     * @param the principalUnits.
     */
    public void setPrincipalUnits(KualiDecimal principalUnits) {
        this.principalUnits = principalUnits;
    }   
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        return new LinkedHashMap();
    }

}
