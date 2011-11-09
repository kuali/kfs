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

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GLInterfaceBatchTotalsProcessedTableRowValues extends TransientBusinessObjectBase {
    private String documentType;
    private String chartCode;
    private String objectCode;
    private KualiDecimal debitAmount = KualiDecimal.ZERO;
    private KualiDecimal creditAmount = KualiDecimal.ZERO;
    private long numberOfEntries = 0;    
    
    public GLInterfaceBatchTotalsProcessedTableRowValues() {
        documentType = " ";
        chartCode = " ";
        objectCode = " ";
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
     * Gets the chartCode attribute. 
     * @return Returns the chartCode.
     */    
    public String getChartCode() {
        return chartCode;
    }

    /**
     * Sets the chartCode attribute. 
     * @return Returns the chartCode.
     */    
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * Gets the objectCode attribute. 
     * @return Returns the objectCode.
     */    
    public String getObjectCode() {
        return objectCode;
    }

    /**
     * Sets the objectCode attribute. 
     * @return Returns the objectCode.
     */    
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * Gets the debitAmount attribute. 
     * @return Returns the debitAmount.
     */
    public KualiDecimal getDebitAmount() {
        return debitAmount;
    }

    /**
     * Sets the debitAmount attribute value.
     * @param DebitAmount The debitAmount to set.
     */
    public void setDebitAmount(KualiDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    /**
     * Gets the creditAmount attribute. 
     * @return Returns the creditAmount.
     */
    public KualiDecimal getCreditAmount() {
        return creditAmount;
    }

    /**
     * Sets the creditAmount attribute value.
     * @param creditAmount The creditAmount to set.
     */
    public void setCreditAmount(KualiDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    /**
     * Gets the numberOfEntries attribute. 
     * @return Returns the numberOfEntries.
     */
    public long getNumberOfEntries() {
        return numberOfEntries;
    }

    /**
     * Sets the numberOfEntries attribute value.
     * @param creditAmount The numberOfEntries to set.
     */
    public void setNumberOfEntries(long numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("documentType",this.getDocumentType());
        pks.put("chartCode",this.getChartCode());
        pks.put("objectCode",this.getObjectCode());
        pks.put("debitAmount",this.getDebitAmount());
        pks.put("creditAmount()",this.getCreditAmount());
        pks.put("numberOfEntries", this.getNumberOfEntries());
        return pks;
    }
}
