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

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class FeeProcessingTotalsProcessedReportHeader extends TransientBusinessObjectBase {
    private String columnHeading1;
    private String columnHeading2;
    private String columnHeading3;
    private String columnHeading4;
    private String columnHeading5;

    public FeeProcessingTotalsProcessedReportHeader() {
        columnHeading1 = "";
        columnHeading2 = "";
        columnHeading3 = "";
        columnHeading4 = "";
        columnHeading5 = "";
    }
    
    /**
     * Gets the columnHeading1 attribute. 
     * @return Returns the columnHeading1.
     */   
    public String getColumnHeading1() {
        return columnHeading1;
    }

    /**
     * Sets the columnHeading1 attribute. 
     * @return Returns the columnHeading1.
     */
    public void setColumnHeading1(String columnHeading1) {
        this.columnHeading1 = columnHeading1;
    }

    /**
     * Gets the columnHeading2 attribute. 
     * @return Returns the columnHeading2.
     */    
    public String getColumnHeading2() {
        return columnHeading2;
    }

    /**
     * Sets the columnHeading2 attribute. 
     * @return Returns the columnHeading2.
     */    
    public void setColumnHeading2(String columnHeading2) {
        this.columnHeading2 = columnHeading2;
    }

    /**
     * Gets the columnHeading3 attribute. 
     * @return Returns the columnHeading3.
     */    
    public String getColumnHeading3() {
        return columnHeading3;
    }

    /**
     * Sets the columnHeading3 attribute. 
     * @return Returns the columnHeading3.
     */    
    public void setColumnHeading3(String columnHeading3) {
        this.columnHeading3 = columnHeading3;
    }

    /**
     * Gets the columnHeading4 attribute. 
     * @return Returns the columnHeading4.
     */    
    public String getColumnHeading4() {
        return columnHeading4;
    }

    /**
     * Sets the columnHeading4 attribute. 
     * @return Returns the columnHeading4.
     */    
    public void setColumnHeading4(String columnHeading4) {
        this.columnHeading4 = columnHeading4;
    }

    /**
     * Gets the columnHeading5 attribute. 
     * @return Returns the columnHeading5.
     */
    public String getColumnHeading5() {
        return columnHeading5;
    }

    /**
     * Sets the columnHeading5 attribute value.
     * @param columnHeading5 The columnHeading5 to set.
     */
    public void setColumnHeading5(String columnHeading5) {
        this.columnHeading5 = columnHeading5;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("columnHeading1",this.getColumnHeading1());
        pks.put("columnHeading2",this.getColumnHeading2());
        pks.put("columnHeading3",this.getColumnHeading3());
        pks.put("columnHeading4",this.getColumnHeading4());
        pks.put("columnHeading5",this.getColumnHeading5());
        
        return pks;
    }
}
