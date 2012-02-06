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
 * Delivery Required Date Reason Business Object. Defines the reason why a delivery date is required.
 */
public class DeliveryRequiredDateReason extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String deliveryRequiredDateReasonCode;
    private String deliveryRequiredDateReasonDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public DeliveryRequiredDateReason() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDeliveryRequiredDateReasonCode() {
        return deliveryRequiredDateReasonCode;
    }

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    public String getDeliveryRequiredDateReasonDescription() {
        return deliveryRequiredDateReasonDescription;
    }

    public void setDeliveryRequiredDateReasonDescription(String deliveryRequiredDateReasonDescription) {
        this.deliveryRequiredDateReasonDescription = deliveryRequiredDateReasonDescription;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("deliveryRequiredDateReasonCode", this.deliveryRequiredDateReasonCode);
        return m;
    }
}
