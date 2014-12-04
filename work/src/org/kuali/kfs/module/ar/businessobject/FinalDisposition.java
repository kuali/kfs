/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    private String dispositionCode;
    private String dispositionDescription;
    private boolean active;

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
     * @param dispositionCode The code to set.
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
