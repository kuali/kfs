/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * 
 */
public class ObjectSubType extends KualiCodeBase implements MutableInactivatable {

    /**
     * Default no-arg constructor.
     */
    public ObjectSubType() {
        super.setActive(true); // always active
    }

    /**
     * Gets the financialObjectSubTypeCode attribute.
     * 
     * @return Returns the financialObjectSubTypeCode
     */
    public String getFinancialObjectSubTypeCode() {
        return this.getCode();
    }


    /**
     * Sets the financialObjectSubTypeCode attribute.
     * 
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.setCode(financialObjectSubTypeCode);
    }

    /**
     * Gets the financialObjectSubTypeName attribute.
     * 
     * @return Returns the financialObjectSubTypeName
     */
    public String getFinancialObjectSubTypeName() {
        return this.getName();
    }

    /**
     * Sets the financialObjectSubTypeName attribute.
     * 
     * @param financialObjectSubTypeName The financialObjectSubTypeName to set.
     */
    public void setFinancialObjectSubTypeName(String financialObjectSubTypeName) {
        this.setName(financialObjectSubTypeName);
    }

}
