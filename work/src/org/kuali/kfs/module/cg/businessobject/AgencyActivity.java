/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * AgencyActivity under Contracts and Grants section.
 */
public class AgencyActivity extends PersistableBusinessObjectBase {

    private String agencyActivity;
    private String activityDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public AgencyActivity() {
    }

    /**
     * @return
     */
    public String getAgencyActivity() {
        return agencyActivity;
    }


    /**
     * @param agencyActivity
     */
    public void setAgencyActivity(String agencyActivity) {
        this.agencyActivity = agencyActivity;
    }

    /**
     * @return
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     * @param activityDescription
     */
    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    /**
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("agencyActivity", this.agencyActivity);
        m.put("activityDescription", this.activityDescription);
        return m;
    }
}
