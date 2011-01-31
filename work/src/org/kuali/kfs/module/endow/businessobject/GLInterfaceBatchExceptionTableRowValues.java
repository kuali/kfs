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

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

public class GLInterfaceBatchExceptionTableRowValues extends TransientBusinessObjectBase {
    private String documentType;
    private String eDocNumber;
    private String kEMID;
    private KualiDecimal incomeAmount = KualiDecimal.ZERO;
    private KualiDecimal principalAmount = KualiDecimal.ZERO;
    private KualiDecimal securityCost = KualiDecimal.ZERO;
    private KualiDecimal longTermGainLoss = KualiDecimal.ZERO;    
    private KualiDecimal shortTermGainLoss = KualiDecimal.ZERO;        
    
    public GLInterfaceBatchExceptionTableRowValues() {
        documentType = " ";
        eDocNumber = " ";
        kEMID = " ";
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
    public String getEDocNumber() {
        return eDocNumber;
    }

    /**
     * Sets the eDocNumber attribute. 
     * @return Returns the eDocNumber.
     */    
    public void setEDocNumber(String eDocNumber) {
        this.eDocNumber = eDocNumber;
    }

    /**
     * Gets the kEMID attribute. 
     * @return Returns the kEMID.
     */    
    public String getKEMID() {
        return kEMID;
    }

    /**
     * Sets the kEMID attribute. 
     * @return Returns the kEMID.
     */    
    public void setKEMID(String kEMID) {
        this.kEMID = kEMID;
    }

    /**
     * Gets the incomeAmount attribute. 
     * @return Returns the incomeAmount.
     */
    public KualiDecimal getIncomeAmount() {
        return incomeAmount;
    }

    /**
     * Sets the incomeAmount attribute value.
     * @param incomeAmount The incomeAmount to set.
     */
    public void setIncomeAmount(KualiDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    /**
     * Gets the principalAmount attribute. 
     * @return Returns the principalAmount.
     */
    public KualiDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * Sets the principalAmount attribute value.
     * @param principalAmount The principalAmount to set.
     */
    public void setPrincipalAmountt(KualiDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * Gets the securityCost attribute. 
     * @return Returns the securityCost.
     */
    public KualiDecimal getSecurityCost() {
        return securityCost;
    }

    /**
     * Sets the securityCost attribute value.
     * @param securityCost The securityCost to set.
     */
    public void setSecurityCost(KualiDecimal securityCost) {
        this.securityCost = securityCost;
    }
    
    /**
     * Gets the longTermGainLoss attribute. 
     * @return Returns the longTermGainLoss.
     */   
    public KualiDecimal getLongTermGainLoss() {
        return longTermGainLoss;
    }

    /**
     * Sets the longTermGainLoss attribute. 
     * @return Returns the longTermGainLoss.
     */
    public void setLongTermGainLoss(KualiDecimal longTermGainLoss) {
        this.longTermGainLoss = longTermGainLoss;
    }

    /**
     * Gets the longTermGainLoss attribute. 
     * @return Returns the longTermGainLoss.
     */   
    public KualiDecimal getShortTermGainLoss() {
        return shortTermGainLoss;
    }

    /**
     * Sets the shortTermGainLoss attribute. 
     * @return Returns the shortTermGainLoss.
     */
    public void setShortTermGainLoss(KualiDecimal shortTermGainLoss) {
        this.shortTermGainLoss = shortTermGainLoss;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("documentType",this.getDocumentType());
        pks.put("eDocNumber",this.getEDocNumber());
        pks.put("kEMID",this.getKEMID());
        pks.put("incomeAmount",this.getIncomeAmount());
        pks.put("principalAmount",this.getPrincipalAmount());
        pks.put("securityCost",this.getSecurityCost());
        pks.put("longTermGainLoss",this.getLongTermGainLoss());
        pks.put("shortTermGainLoss",this.getShortTermGainLoss());        
        
        return pks;
    }
}
