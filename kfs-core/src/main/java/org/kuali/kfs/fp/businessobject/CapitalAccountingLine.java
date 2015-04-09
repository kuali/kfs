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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This transient business object keeps properties for amount method selection
 * drop-down box and create button actions.
 */
public class CapitalAccountingLine extends TransientBusinessObjectBase {
    protected String distributionCode;
    protected boolean canCreateAsset;

    /**
     * Default constructor.
     */
    public CapitalAccountingLine() {
        canCreateAsset = true;
    }
    
    
    /**
     * Gets the distributionCode attribute.
     * 
     * @return Returns the distributionCode
     */
    
    public String getDistributionCode() {
        return distributionCode;
    }

    /** 
     * Sets the distributionCode attribute.
     * 
     * @param distributionCode The distributionCode to set.
     */
    public void setDistributionCode(String distributionCode) {
        this.distributionCode = distributionCode;
    }

    /**
     * Gets the canCreateAsset attribute.
     * 
     * @return Returns the canCreateAsset
     */
    
    public boolean isCanCreateAsset() {
        return canCreateAsset;
    }

    /** 
     * Sets the canCreateAsset attribute.
     * 
     * @param canCreateAsset The canCreateAsset to set.
     */
    public void setCanCreateAsset(boolean canCreateAsset) {
        this.canCreateAsset = canCreateAsset;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    public LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("distributionCode", this.getDistributionCode());
        m.put("canCreateAsset", this.isCanCreateAsset());
        return m;
    }
}
