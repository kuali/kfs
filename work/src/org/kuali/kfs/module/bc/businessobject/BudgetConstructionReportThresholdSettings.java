/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Holds threshold settings for object code report selection screen.
 */
public class BudgetConstructionReportThresholdSettings extends TransientBusinessObjectBase {
    private boolean lockThreshold;
    private boolean useThreshold;
    private KualiDecimal thresholdPercent;
    private boolean useGreaterThanOperator = true;

    /**
     * Constructs a BudgetConstructionReportThresholdSettings.java.
     */
    public BudgetConstructionReportThresholdSettings() {
        super();
    }

    /**
     * Gets the lockThreshold attribute.
     * 
     * @return Returns the lockThreshold.
     */
    public boolean isLockThreshold() {
        return lockThreshold;
    }

    /**
     * Sets the lockThreshold attribute value.
     * 
     * @param lockThreshold The lockThreshold to set.
     */
    public void setLockThreshold(boolean lockThreshold) {
        this.lockThreshold = lockThreshold;
    }

    /**
     * Gets the thresholdPercent attribute.
     * 
     * @return Returns the thresholdPercent.
     */
    public KualiDecimal getThresholdPercent() {
        return thresholdPercent;
    }

    /**
     * Sets the thresholdPercent attribute value.
     * 
     * @param thresholdPercent The thresholdPercent to set.
     */
    public void setThresholdPercent(KualiDecimal thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    /**
     * Gets the useGreaterThanOperator attribute.
     * 
     * @return Returns the useGreaterThanOperator.
     */
    public boolean isUseGreaterThanOperator() {
        return useGreaterThanOperator;
    }

    /**
     * Sets the useGreaterThanOperator attribute value.
     * 
     * @param useGreaterThanOperator The useGreaterThanOperator to set.
     */
    public void setUseGreaterThanOperator(boolean useGreaterThanOperator) {
        this.useGreaterThanOperator = useGreaterThanOperator;
    }

    /**
     * Gets the useThreshold attribute.
     * 
     * @return Returns the useThreshold.
     */
    public boolean isUseThreshold() {
        return useThreshold;
    }

    /**
     * Sets the useThreshold attribute value.
     * 
     * @param useThreshold The useThreshold to set.
     */
    public void setUseThreshold(boolean useThreshold) {
        this.useThreshold = useThreshold;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("useThreshold", this.useThreshold);
        m.put("thresholdPercent", this.thresholdPercent);
        m.put("useGreaterThanOperator", this.useGreaterThanOperator);
        return m;
    }


}
