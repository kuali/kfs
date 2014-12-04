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

public class GLInterfaceBatchStatisticsTableRowValues extends TransientBusinessObjectBase {
    private String documenTypeColumn1;
    private String chartColumn2;
    private String objectColumn3;
    
    public GLInterfaceBatchStatisticsTableRowValues() {
        documenTypeColumn1 = " ";
        chartColumn2 = " ";
        objectColumn3 = " ";
    }
    
    /**
     * Gets the documenTypeColumn1 attribute. 
     * @return Returns the documenTypeColumn1.
     */   
    public String getDocumenTypeColumn1() {
        return documenTypeColumn1;
    }

    /**
     * Sets the documenTypeColumn1 attribute. 
     * @return Returns the documenTypeColumn1.
     */
    public void setDocumenTypeColumn1(String documenTypeColumn1) {
        this.documenTypeColumn1 = documenTypeColumn1;
    }

    /**
     * Gets the chartColumn2 attribute. 
     * @return Returns the chartColumn2.
     */    
    public String getChartColumn2() {
        return chartColumn2;
    }

    /**
     * Sets the chartColumn2 attribute. 
     * @return Returns the chartColumn2.
     */    
    public void setChartColumn2(String chartColumn2) {
        this.chartColumn2 = chartColumn2;
    }

    /**
     * Gets the objectColumn3 attribute. 
     * @return Returns the objectColumn3.
     */    
    public String getObjectColumn3() {
        return objectColumn3;
    }

    /**
     * Sets the objectColumn3 attribute. 
     * @return Returns the objectColumn3.
     */    
    public void setObjectColumn3(String objectColumn3) {
        this.objectColumn3 = objectColumn3;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("documenTypeColumn1",this.getDocumenTypeColumn1());
        pks.put("chartColumn2",this.getChartColumn2());
        pks.put("objectColumn3",this.getObjectColumn3());
        return pks;
    }
}
