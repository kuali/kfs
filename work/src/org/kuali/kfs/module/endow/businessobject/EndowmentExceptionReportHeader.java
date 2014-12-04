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
