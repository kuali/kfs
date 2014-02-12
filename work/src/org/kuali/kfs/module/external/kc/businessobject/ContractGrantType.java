/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsContractGrantType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

public class ContractGrantType implements ContractsAndGrantsContractGrantType, MutableInactivatable {

    private String contractGrantTypeCode;
    private String contractGrantTypeDescription;

    @Override
    public void refresh() { }

    @Override
    public void setActive(boolean active) { }

    @Override
    /**
     * Always returns true as KC's AwardType doesn't have an active flag
     * @see org.kuali.rice.core.api.mo.common.active.Inactivatable#isActive()
     */
    public boolean isActive() {
        return true;
    }

    @Override
    public String getContractGrantTypeCode() {
        return contractGrantTypeCode;
    }

    public void setContractGrantTypeCode(String contractGrantTypeCode) {
        this.contractGrantTypeCode = contractGrantTypeCode;
    }

    @Override
    public String getContractGrantTypeDescription() {
        return contractGrantTypeDescription;
    }

    public void setContractGrantTypeDescription(String contractGrantTypeDescription) {
        this.contractGrantTypeDescription = contractGrantTypeDescription;
    }

}
