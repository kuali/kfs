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
