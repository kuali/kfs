/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.pdp.businessobject;

import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCode;
import org.kuali.rice.krad.service.BusinessObjectService;

public class ACHPayee extends DisbursementPayee implements MutableInactivatable {
    private String entityId;

    public ACHPayee() {
        super();
    }

    /**
     * Gets the entityId attribute.
     * 
     * @return Returns the entityId.
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Sets the entityId attribute value.
     * 
     * @param entityId The entityId to set.
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * @see org.kuali.kfs.fp.businessobject.DisbursementPayee#getPayeeTypeDescription()
     */
    @Override
    public String getPayeeTypeDescription() {
        KualiCode payeeType = (KualiCode) SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(PayeeType.class, this.getPayeeTypeCode());

        return payeeType.getName();
    }

}
