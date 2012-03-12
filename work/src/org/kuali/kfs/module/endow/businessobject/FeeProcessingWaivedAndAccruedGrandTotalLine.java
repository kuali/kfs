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

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class FeeProcessingWaivedAndAccruedGrandTotalLine extends TransientBusinessObjectBase {
    private String total;
    private String kemid;
    private KualiDecimal totalWaivedFees = KualiDecimal.ZERO;
    private KualiDecimal totalAccruedFees = KualiDecimal.ZERO;
    
    public FeeProcessingWaivedAndAccruedGrandTotalLine() {
        total = "Grand Totals";
        kemid = " ";
    }
    
    /**
     * Gets the total attribute. 
     * @return Returns the total.
     */   
    public String getTotal() {
        return total;
    }

    /**
     * Sets the total attribute. 
     * @return Returns the total.
     */
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     * Gets the kemid attribute. 
     * @return Returns the kemid.
     */    
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid attribute. 
     * @return Returns the kemid.
     */    
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the totalWaivedFees attribute. 
     * @return Returns the totalWaivedFees.
     */
    public KualiDecimal getTotalWaivedFees() {
        return totalWaivedFees;
    }

    /**
     * Sets the totalWaivedFees attribute value.
     * @param totalWaivedFees The totalWaivedFees to set.
     */
    public void setTotalWaivedFees(KualiDecimal totalWaivedFees) {
        this.totalWaivedFees = totalWaivedFees;
    }

    /**
     * Gets the totalAccruedFees attribute. 
     * @return Returns the totalAccruedFees.
     */
    public KualiDecimal getTotalAccruedFees() {
        return totalAccruedFees;
    }

    /**
     * Sets the totalAccruedFees attribute value.
     * @param totalAccruedFees The totalAccruedFees to set.
     */
    public void setTotalAccruedFees(KualiDecimal totalAccruedFees) {
        this.totalAccruedFees = totalAccruedFees;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("total",this.getTotal());
        pks.put("kemid",this.getKemid());
        pks.put("totalWaivedFees",this.getTotalWaivedFees());
        pks.put("totalAccruedFees",this.getTotalAccruedFees());
        
        return pks;
    }
}
