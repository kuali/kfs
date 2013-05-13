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
     * Sets the documentType attribute value.
     * @param documentType The documentType to set.
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    /**
     * Gets the documentId attribute. 
     * @return Returns the documentId.
     */
    public String getDocumentId() {
        return documentId;
    }
    
    /**
     * Sets the documentId attribute value.
     * @param documentId The documentId to set.
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
     * Adds a income amount to the current income total
     * @param incomeAmount the income amount to add to the income total
     */
    public void addIncomeAmount(KualiDecimal incomeAmount) {
        this.incomeAmount = this.incomeAmount.add(incomeAmount);    
        
    }
  
    /**
     * Adds a principal amount to the current principal total
     * @param principalAmount the principal amount to add to the principal total
     */
    public void addPrincipalAmount(KualiDecimal principalAmount) {
        this.principalAmount = this.principalAmount.add(principalAmount); 
    }
    
    
    /**
     * Adds a income units to the current income units total
     * @param incomeUnits the income units to add to the income units total
     */
    public void addIncomeUnits(KualiDecimal incomeUnits) {
        this.incomeUnits = this.incomeUnits.add(incomeUnits);        
    }    
   
    /**
     * Adds a principal units to the current principal units total
     * @param principalUnits the principal units to add to the principal units total
     */
    public void addPrincipalUnits(KualiDecimal principalUnits) {
        this.principalUnits = this.principalUnits.add(principalUnits);        
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return new LinkedHashMap();
    }   

}
