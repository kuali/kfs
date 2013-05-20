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

import org.kuali.kfs.integration.cg.ContractsAndGrantsContractGrantType;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Contract Grant Types under Contracts and Grants section.
 */

public class ContractGrantType extends PersistableBusinessObjectBase implements ContractsAndGrantsContractGrantType {

    private String contractGrantTypeCode;
    private String contractGrantTypeDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public ContractGrantType() {
    }

    public String getContractGrantTypeCode() {
        return contractGrantTypeCode;
    }

    public void setContractGrantTypeCode(String contractGrantTypeCode) {
        this.contractGrantTypeCode = contractGrantTypeCode;
    }

    public String getContractGrantTypeDescription() {
        return contractGrantTypeDescription;
    }

    public void setContractGrantTypeDescription(String contractGrantTypeDescription) {
        this.contractGrantTypeDescription = contractGrantTypeDescription;
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
        m.put("contractGrantTypeCode", this.contractGrantTypeCode);
        return m;
    }
}
