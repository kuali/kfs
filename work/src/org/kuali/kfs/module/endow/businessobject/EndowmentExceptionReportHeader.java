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

public class EndowmentExceptionReportHeader extends TransientBusinessObjectBase {
    private String columnHeading1;
    private String columnHeading2;
    private String columnHeading3;
    private String columnHeading4;
    private String columnHeading5;

    private String columnHeading6; 
    private String columnHeading7;    
    
    public EndowmentExceptionReportHeader() {
        columnHeading1 = "";
        columnHeading2 = "";
        columnHeading3 = "";
        columnHeading4 = "";
        columnHeading5 = "";
        columnHeading6 = ""; 
        columnHeading7 = "";        
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
     * Gets the columnHeading6 attribute. 
     * @return Returns the columnHeading6.
     */
    public String getColumnHeading6() {
        return columnHeading6;
    }

    /**
     * Sets the columnHeading6 attribute value.
     * @param columnHeading6 The columnHeading6 to set.
     */
    public void setColumnHeading6(String columnHeading6) {
        this.columnHeading6 = columnHeading6;
    }

    /**
     * Gets the columnHeading7 attribute. 
     * @return Returns the columnHeading7.
     */
    public String getColumnHeading7() {
        return columnHeading7;
    }

    /**
     * Sets the columnHeading7 attribute value.
     * @param columnHeading7 The columnHeading7 to set.
     */
    public void setColumnHeading7(String columnHeading7) {
        this.columnHeading7 = columnHeading7;
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
        pks.put("columnHeading6",this.getColumnHeading6());
        pks.put("columnHeading7",this.getColumnHeading7());
        
        return pks;
    }
}
