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
