/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class BillingFrequencyMapping extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String frequency;
    private String gracePeriodDays;
    private String kcFrequencyCode;
    private boolean active;

    public String getFrequency() {
        return frequency;
    }

    public String getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(String gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getKcFrequencyCode() {
        return kcFrequencyCode;
    }

    public void setKcFrequencyCode(String kcFrequencyCode) {
        this.kcFrequencyCode = kcFrequencyCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

}
