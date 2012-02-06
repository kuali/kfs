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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Recurring Payment Frequency Business Object.
 */
public class RecurringPaymentFrequency extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String recurringPaymentFrequencyCode;
    private String recurringPaymentFrequencyDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public RecurringPaymentFrequency() {

    }

    public String getRecurringPaymentFrequencyCode() {
        return recurringPaymentFrequencyCode;
    }

    public void setRecurringPaymentFrequencyCode(String recurringPaymentFrequencyCode) {
        this.recurringPaymentFrequencyCode = recurringPaymentFrequencyCode;
    }

    public String getRecurringPaymentFrequencyDescription() {
        return recurringPaymentFrequencyDescription;
    }

    public void setRecurringPaymentFrequencyDescription(String recurringPaymentFrequencyDescription) {
        this.recurringPaymentFrequencyDescription = recurringPaymentFrequencyDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("recurringPaymentFrequencyCode", this.recurringPaymentFrequencyCode);
        return m;
    }

}
