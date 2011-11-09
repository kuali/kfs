/*
 * Copyright 2006 The Kuali Foundation
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
