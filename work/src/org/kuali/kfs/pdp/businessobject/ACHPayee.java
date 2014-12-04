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
