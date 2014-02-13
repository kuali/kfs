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

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * BillingFrequency under Contracts and Grants section.
 */

public class BillingFrequency extends PersistableBusinessObjectBase implements ContractsAndGrantsBillingFrequency, MutableInactivatable {

    private String frequency;
    private String frequencyDescription;
    private String gracePeriodDays;
    private boolean active;

    /**
     * Default constructor.
     */
    public BillingFrequency() {
    }



    @Override
    public String getFrequency() {
        return frequency;
    }


    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }




    @Override
    public String getFrequencyDescription() {
        return frequencyDescription;
    }


    public void setFrequencyDescription(String frequencyDescription) {
        this.frequencyDescription = frequencyDescription;
    }

    /**
     * Gets the gracePeriodDays attribute.
     *
     * @return Returns the gracePeriodDays.
     */


    @Override
    public String getGracePeriodDays() {
        return gracePeriodDays;
    }


    /**
     * Sets the gracePeriodDays attribute value.
     *
     * @param gracePeriodDays The gracePeriodDays to set.
     */
    public void setGracePeriodDays(String gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }




    @Override
    public boolean isActive() {
        return active;
    }



    @Override
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("frequency", this.frequency);
        m.put("frequencyDescription", frequencyDescription);
        m.put("gracePeriodDays", gracePeriodDays);
        m.put(KFSPropertyConstants.ACTIVE, active);

        return m;
    }
}
