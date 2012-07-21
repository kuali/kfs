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
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
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
