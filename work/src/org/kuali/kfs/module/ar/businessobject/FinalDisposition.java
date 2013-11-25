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
 * @author jignasha.
 */
public class FinalDisposition extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String dispositionCode;
    private String dispositionDescription;
    private boolean active;

    /**
     * Default constructor
     */
    public FinalDisposition() {
        super();
    }

    /**
     * Gets the dispositionCode attribute.
     *
     * @return Returns the dispositionCode.
     */
    public String getDispositionCode() {
        return dispositionCode;
    }

    /**
     * Sets the dispositionCode attribute value.
     *
     * @param dispositionCode The dispositionCode to set.
     */
    public void setDispositionCode(String dispositionCode) {
        this.dispositionCode = dispositionCode;
    }

    /**
     * Gets the dispositionDescription attribute.
     *
     * @return Returns the dispositionDescription.
     */
    public String getDispositionDescription() {
        return dispositionDescription;
    }

    /**
     * Sets the dispositionDescription attribute value.
     *
     * @param dispositionDescription The dispositionDescription to set.
     */
    public void setDispositionDescription(String dispositionDescription) {
        this.dispositionDescription = dispositionDescription;
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
        m.put("dispositionCode", this.dispositionCode);
        m.put("dispositionDescription", this.dispositionDescription);
        m.put("active", this.active);
        return m;
    }

}
