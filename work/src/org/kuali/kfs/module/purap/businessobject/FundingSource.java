/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Funding Source Business Object.
 */
public class FundingSource extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String fundingSourceCode;
    private String fundingSourceDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public FundingSource() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFundingSourceCode() {
        return fundingSourceCode;
    }

    public void setFundingSourceCode(String fundingSourceCode) {
        this.fundingSourceCode = fundingSourceCode;
    }

    public String getFundingSourceDescription() {
        return fundingSourceDescription;
    }

    public void setFundingSourceDescription(String fundingSourceDescription) {
        this.fundingSourceDescription = fundingSourceDescription;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("fundingSourceCode", this.fundingSourceCode);
        return m;
    }
}
