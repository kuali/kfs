/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/ResponsibilityCenter.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class ResponsibilityCenter extends BusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public ResponsibilityCenter() {

    }

    private String responsibilityCenterCode;
    private String responsibilityCenterName;
    private String responsibilityCenterShortName;
    private boolean responsibilityCenterActiveIndicator;

    /**
     * Gets the responsibilityCenterCode attribute.
     * 
     * @return Returns the responsibilityCenterCode
     * 
     */
    public String getResponsibilityCenterCode() {
        return responsibilityCenterCode;
    }

    /**
     * Sets the responsibilityCenterCode attribute.
     * 
     * @param responsibilityCenterCode The responsibilityCenterCode to set.
     * 
     */
    public void setResponsibilityCenterCode(String responsibilityCenterCode) {
        this.responsibilityCenterCode = responsibilityCenterCode;
    }

    /**
     * Gets the responsibilityCenterName attribute.
     * 
     * @return Returns the responsibilityCenterName
     * 
     */
    public String getResponsibilityCenterName() {
        return responsibilityCenterName;
    }

    /**
     * Sets the responsibilityCenterName attribute.
     * 
     * @param responsibilityCenterName The responsibilityCenterName to set.
     * 
     */
    public void setResponsibilityCenterName(String responsibilityCenterName) {
        this.responsibilityCenterName = responsibilityCenterName;
    }

    /**
     * Gets the responsibilityCenterShortName attribute.
     * 
     * @return Returns the responsibilityCenterShortName
     * 
     */
    public String getResponsibilityCenterShortName() {
        return responsibilityCenterShortName;
    }

    /**
     * Sets the responsibilityCenterShortName attribute.
     * 
     * @param responsibilityCenterShortName The responsibilityCenterShortName to set.
     * 
     */
    public void setResponsibilityCenterShortName(String responsibilityCenterShortName) {
        this.responsibilityCenterShortName = responsibilityCenterShortName;
    }

    /**
     * Gets the responsibilityCenterActiveIndicator attribute.
     * 
     * @return Returns the responsibilityCenterActiveIndicator
     * 
     */
    public boolean getResponsibilityCenterActiveIndicator() {
        return responsibilityCenterActiveIndicator;
    }

    /**
     * Sets the _responsibilityCenterActiveIndicator_ attribute.
     * 
     * @param _responsibilityCenterActiveIndicator_ The _responsibilityCenterActiveIndicator_ to set.
     * 
     */
    public void setResponsibilityCenterActiveIndicator(boolean responsibilityCenterActiveIndicator) {
        this.responsibilityCenterActiveIndicator = responsibilityCenterActiveIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("responsibilityCenterCode", this.responsibilityCenterCode);

        return m;
    }
}
