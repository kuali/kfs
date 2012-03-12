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
