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
