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

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

public class GLInterfaceBatchStatisticsTableRowValues extends TransientBusinessObjectBase {
    private String documenTypeColumn1;
    private String chartColumn2;
    private String objectColumn3;
    private KualiDecimal debitAmountColumn4 = KualiDecimal.ZERO;
    private KualiDecimal creditAmountColumn5 = KualiDecimal.ZERO;
    private KualiInteger numberOfEntriesColumn6 = KualiInteger.ZERO;    
    
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
     * Gets the debitAmountColumn4 attribute. 
     * @return Returns the debitAmountColumn4.
     */
    public KualiDecimal getDebitAmountColumn4() {
        return debitAmountColumn4;
    }

    /**
     * Sets the debitAmountColumn4 attribute value.
     * @param DebitAmountColumn4 The debitAmountColumn4 to set.
     */
    public void setDebitAmountColumn4(KualiDecimal debitAmountColumn4) {
        this.debitAmountColumn4 = debitAmountColumn4;
    }

    /**
     * Gets the creditAmountColumn5 attribute. 
     * @return Returns the creditAmountColumn5.
     */
    public KualiDecimal getCreditAmountColumn5() {
        return creditAmountColumn5;
    }

    /**
     * Sets the creditAmountColumn5 attribute value.
     * @param creditAmountColumn5 The creditAmountColumn5 to set.
     */
    public void setCreditAmountColumn5(KualiDecimal creditAmountColumn5) {
        this.creditAmountColumn5 = creditAmountColumn5;
    }

    /**
     * Gets the numberOfEntriesColumn6 attribute. 
     * @return Returns the numberOfEntriesColumn6.
     */
    public KualiInteger getnumberOfEntriesColumn6() {
        return numberOfEntriesColumn6;
    }

    /**
     * Sets the numberOfEntriesColumn6 attribute value.
     * @param creditAmountColumn5 The numberOfEntriesColumn6 to set.
     */
    public void setNumberOfEntriesColumn65(KualiInteger numberOfEntriesColumn6) {
        this.numberOfEntriesColumn6 = numberOfEntriesColumn6;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("documenTypeColumn1",this.getDocumenTypeColumn1());
        pks.put("chartColumn2",this.getChartColumn2());
        pks.put("objectColumn3",this.getObjectColumn3());
        pks.put("debitAmountColumn4",this.getDebitAmountColumn4());
        pks.put("creditAmountColumn5()",this.getCreditAmountColumn5());
        pks.put("numberOfEntriesColumn6", this.getnumberOfEntriesColumn6());
        return pks;
    }
}
