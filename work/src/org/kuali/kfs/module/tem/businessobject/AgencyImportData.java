/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.businessobject;

import java.util.ArrayList;
import java.util.List;

public class AgencyImportData {

    public String importBy;
    public List<AgencyStagingData> agencies;
    
    public AgencyImportData() {
        agencies = new ArrayList<AgencyStagingData>();
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
    public List<AgencyStagingData> getAgencies() {
        return agencies;
    }

    /**
     * Sets the agencies attribute value.
     * @param agencies The agencies to set.
     */
    public void setAgencies(List<AgencyStagingData> agencies) {
        this.agencies = agencies;
    }
    
    /**
     * 
     * This method adds the {@link AgencyStagingData} to the list.
     * @param agency
     */
    public void addAgency(AgencyStagingData agency) {
        this.getAgencies().add(agency);
    }
}
