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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Security Attribute represents attributes that can used within the security system to define restrictions.
 */
public class SecurityAttribute extends PersistableBusinessObjectBase implements MutableInactivatable {
    private KualiInteger id;
    private String name;
    private boolean active;

    public SecurityAttribute() {
        super();
    }

    /**
     * Gets the id attribute.
     * 
     * @return Returns the id.
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * 
     * @param id The id to set.
     */
    public void setId(KualiInteger id) {
        this.id = id;
    }

    /**
     * Gets the name attribute.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute value.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
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

        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }
}
