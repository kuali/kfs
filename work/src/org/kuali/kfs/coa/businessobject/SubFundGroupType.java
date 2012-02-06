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

package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class SubFundGroupType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String subFundGroupTypeCode;
    private String subFundGroupTypeDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public SubFundGroupType() {

    }

    /**
     * Gets the subFundGroupTypeCode attribute.
     * 
     * @return Returns the subFundGroupTypeCode
     */
    public String getSubFundGroupTypeCode() {
        return subFundGroupTypeCode;
    }

    /**
     * Sets the subFundGroupTypeCode attribute.
     * 
     * @param subFundGroupTypeCode The subFundGroupTypeCode to set.
     */
    public void setSubFundGroupTypeCode(String subFundGroupTypeCode) {
        this.subFundGroupTypeCode = subFundGroupTypeCode;
    }


    /**
     * Gets the subFundGroupTypeDescription attribute.
     * 
     * @return Returns the subFundGroupTypeDescription
     */
    public String getSubFundGroupTypeDescription() {
        return subFundGroupTypeDescription;
    }

    /**
     * Sets the subFundGroupTypeDescription attribute.
     * 
     * @param subFundGroupTypeDescription The subFundGroupTypeDescription to set.
     */
    public void setSubFundGroupTypeDescription(String subFundGroupTypeDescription) {
        this.subFundGroupTypeDescription = subFundGroupTypeDescription;
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
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("subFundGroupTypeCode", this.subFundGroupTypeCode);
        return m;
    }
}
