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
