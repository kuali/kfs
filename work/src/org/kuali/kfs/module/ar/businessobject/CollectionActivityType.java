/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines collection activity that can be taken on an outstanding debt.
 * 
 * @author mpritmani
 */
public class CollectionActivityType extends PersistableBusinessObjectBase {

    private String activityCode;
    private String activityDescription;
    private boolean active;
    private boolean referralIndicator;
    private boolean dunningProcessIndicator;

    /**
     * Default constructor.
     */
    public CollectionActivityType() {
    }

    /**
     * Gets the activityCode attribute.
     * 
     * @return Returns the activityCode
     */
    public String getActivityCode() {
        return activityCode;
    }

    /**
     * Sets the activityCode attribute.
     * 
     * @param activityCode The activityCode to set.
     */
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    /**
     * Gets the activityDescription attribute.
     * 
     * @return Returns the activityDescription
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     * Sets the activityDescription attribute.
     * 
     * @param activityDescription The activityDescription to set.
     */
    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    /**
     * Gets the referralIndicator attribute.
     * 
     * @return Returns the referralIndicator.
     */
    public boolean isReferralIndicator() {
        return referralIndicator;
    }

    /**
     * Sets the referralIndicator attribute.
     * 
     * @param referralIndicator The referralIndicator to set.
     */
    public void setReferralIndicator(boolean referralIndicator) {
        this.referralIndicator = referralIndicator;
    }

    /**
     * Gets the dunningProcessIndicator attribute.
     * 
     * @return Returns the dunningProcessIndicator.
     */
    public boolean isDunningProcessIndicator() {
        return dunningProcessIndicator;
    }

    /**
     * Sets the dunningProcessIndicator attribute.
     * 
     * @param dunningProcessIndicator The dunningProcessIndicator to set.
     */
    public void setDunningProcessIndicator(boolean dunningProcessIndicator) {
        this.dunningProcessIndicator = dunningProcessIndicator;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("activityCode", this.activityCode);
        m.put("activityDescription", this.activityDescription);
        m.put(KFSPropertyConstants.ACTIVE, this.active);
        return m;
    }

}
