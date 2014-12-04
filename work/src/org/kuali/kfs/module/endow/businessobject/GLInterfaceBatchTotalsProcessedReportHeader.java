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
