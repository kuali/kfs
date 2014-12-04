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

public class GLInterfaceBatchExceptionReportHeader extends EndowmentExceptionReportHeader {
    private String columnHeading8;
    
    public GLInterfaceBatchExceptionReportHeader() {
        columnHeading8 = "";
    }
    
    /**
     * Gets the columnHeading1 attribute. 
     * @return Returns the columnHeading8.
     */   
    public String getColumnHeading8() {
        return columnHeading8;
    }

    /**
     * Sets the columnHeading8 attribute. 
     * @return Returns the columnHeading8.
     */
    public void setColumnHeading1(String columnHeading8) {
        this.columnHeading8 = columnHeading8;
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
        pks.put("columnHeading8",this.getColumnHeading8());        
        
        return pks;
    }
}
