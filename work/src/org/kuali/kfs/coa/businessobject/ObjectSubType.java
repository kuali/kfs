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
