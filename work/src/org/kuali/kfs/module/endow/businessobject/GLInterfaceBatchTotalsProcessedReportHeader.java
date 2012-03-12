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

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;

public class GLInterfaceBatchTotalsProcessedReportHeader extends FeeProcessingTotalsProcessedReportHeader {
    private String columnHeading6;

    public GLInterfaceBatchTotalsProcessedReportHeader() {
        columnHeading6 = "";
    }
    
    /**
     * Gets the columnHeading6 attribute. 
     * @return Returns the columnHeading6.
     */   
    public String getColumnHeading6() {
        return columnHeading6;
    }

    /**
     * Sets the columnHeading6 attribute. 
     * @return Returns the columnHeading6.
     */
    public void setColumnHeading6(String columnHeading6) {
        this.columnHeading6 = columnHeading6;
    }

    /**
     * get max length for column1
     */
    public int getColumn1MaxLength() {
        return SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(this.getClass(), "columnHeading1");
    }
    
    /**
     * get max length for column2
     */
    public int getColumn2MaxLength() {
        return SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(this.getClass(), "columnHeading2");
    }

    
    /**
     * get max length for column3
     */
    public int getColumn3MaxLength() {
        return SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(this.getClass(), "columnHeading3");
    }
    
    /**
     * get max length for column4
     */
    public int getColumn4MaxLength() {
        return SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(this.getClass(), "columnHeading4");
    }

    /**
     * get max length for column5
     */
    public int getColumn5MaxLength() {
        return SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(this.getClass(), "columnHeading5");
    }

    /**
     * get max length for column6
     */
    public int getColumn6MaxLength() {
        return SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(this.getClass(), "columnHeading6");
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
        return pks;
    }
}
