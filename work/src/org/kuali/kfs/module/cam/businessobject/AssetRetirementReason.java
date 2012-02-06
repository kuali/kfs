/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetRetirementReason extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String retirementReasonCode;
    private String retirementReasonName;
    private boolean active;
    private boolean retirementReasonRestrictionIndicator;

    /**
     * Default constructor.
     */
    public AssetRetirementReason() {

    }

    /**
     * Gets the retirementReasonCode attribute.
     * 
     * @return Returns the retirementReasonCode
     */
    public String getRetirementReasonCode() {
        return retirementReasonCode;
    }

    /**
     * Sets the retirementReasonCode attribute.
     * 
     * @param retirementReasonCode The retirementReasonCode to set.
     */
    public void setRetirementReasonCode(String retirementReasonCode) {
        this.retirementReasonCode = retirementReasonCode;
    }


    /**
     * Gets the retirementReasonName attribute.
     * 
     * @return Returns the retirementReasonName
     */
    public String getRetirementReasonName() {
        return retirementReasonName;
    }

    /**
     * Sets the retirementReasonName attribute.
     * 
     * @param retirementReasonName The retirementReasonName to set.
     */
    public void setRetirementReasonName(String retirementReasonName) {
        this.retirementReasonName = retirementReasonName;
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
     * Gets the retirementReasonRestrictionIndicator attribute.
     * 
     * @return Returns the retirementReasonRestrictionIndicator.
     */
    public boolean isRetirementReasonRestrictionIndicator() {
        return retirementReasonRestrictionIndicator;
    }

    /**
     * Sets the retirementReasonRestrictionIndicator attribute value.
     * 
     * @param retirementReasonRestrictionIndicator The retirementReasonRestrictionIndicator to set.
     */
    public void setRetirementReasonRestrictionIndicator(boolean retirementReasonRestrictionIndicator) {
        this.retirementReasonRestrictionIndicator = retirementReasonRestrictionIndicator;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("retirementReasonCode", this.retirementReasonCode);
        return m;
    }

}
