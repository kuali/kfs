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

import java.util.ArrayList;
import java.util.List;

public class AgencyImportData {

    public String importBy;
    public List<AgencyStagingData> agencyStagingData;

    public AgencyImportData() {
        agencyStagingData = new ArrayList<AgencyStagingData>();
    }

    /**
     * Gets the importBy attribute.
     * @return Returns the importBy.
     */
    public String getImportBy() {
        return importBy;
    }

    /**
     * Sets the importBy attribute value.
     * @param importBy The importBy to set.
     */
    public void setImportBy(String importBy) {
        this.importBy = importBy;
    }

    /**
     * Gets the agencies attribute.
     * @return Returns the agencies.
     */
    public List<AgencyStagingData> getAgencyStagingData() {
        return agencyStagingData;
    }

    /**
     * Sets the agencies attribute value.
     * @param agencies The agencies to set.
     */
    public void setAgencyStagingData(List<AgencyStagingData> agencyStagingData) {
        this.agencyStagingData = agencyStagingData;
    }

    /**
     *
     * This method adds the {@link AgencyStagingData} to the list.
     * @param agency
     */
    public void addAgencyStagingData(AgencyStagingData agencyStagingData) {
        this.getAgencyStagingData().add(agencyStagingData);
    }
}
