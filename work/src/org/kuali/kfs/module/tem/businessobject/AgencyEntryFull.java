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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import org.springframework.beans.BeanUtils;

public class AgencyEntryFull extends AgencyStagingData {

    private Integer entryId;

    public AgencyEntryFull(AgencyStagingData agency, Integer entryId) {
        BeanUtils.copyProperties(agency, this);
        this.entryId = entryId;
    }

    public AgencyEntryFull() {
        super();
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the entryId attribute.
     * @return Returns the entryId.
     */
    public Integer getEntryId() {
        return entryId;
    }

    /**
     * Sets the entryId attribute value.
     * @param entryId The entryId to set.
     */
    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

}
