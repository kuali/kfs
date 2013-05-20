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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * SourceOfFunds under Contracts and Grants section.
 */

public class SourceOfFunds extends PersistableBusinessObjectBase {

    private String sourceOfFunds;
    private String sourceOfFundDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public SourceOfFunds() {
    }


    public String getSourceOfFunds() {
        return sourceOfFunds;
    }


    public void setSourceOfFunds(String sourceOfFunds) {
        this.sourceOfFunds = sourceOfFunds;
    }


    public String getSourceOfFundDescription() {
        return sourceOfFundDescription;
    }


    public void setSourceOfFundDescription(String sourceOfFundDescription) {
        this.sourceOfFundDescription = sourceOfFundDescription;
    }


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("sourceOfFunds", this.sourceOfFunds);

        return m;
    }
}
