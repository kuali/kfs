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

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used on the Refer to Collections eDoc to record how each invoice as finally disposed.
 *
 */
public class FinalDisposition extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String code;
    private String description;
    private boolean active;

    /**
     * Gets the code attribute.
     *
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code attribute value.
     *
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description attribute.
     *
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("code", this.code);
        m.put("description", this.description);
        m.put("active", this.active);
        return m;
    }

}
