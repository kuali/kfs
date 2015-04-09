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

package org.kuali.kfs.fp.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * This class is used to represent a travel company code business object.
 */
public class TravelCompanyCode extends KualiCodeBase implements MutableInactivatable {
    private TravelExpenseTypeCode travelExpenseTypeCode;
    
    private boolean foreignCompany = false;

    /**
     * Default no-arg constructor.
     */
    public TravelCompanyCode() {

    }

    /**
     * Gets the travelExpenseTypeCode attribute.
     * 
     * @return Returns the travelExpenseTypeCode.
     */
    public TravelExpenseTypeCode getTravelExpenseTypeCode() {
        return travelExpenseTypeCode;
    }

    /**
     * Sets the travelExpenseTypeCode attribute value.
     * 
     * @param travelExpenseTypeCode The travelExpenseTypeCode to set.
     */
    public void setTravelExpenseTypeCode(TravelExpenseTypeCode travelExpenseTypeCode) {
        this.travelExpenseTypeCode = travelExpenseTypeCode;
    }

    public boolean isForeignCompany() {
        return foreignCompany;
    }

    public void setForeignCompany(boolean foreignCompany) {
        this.foreignCompany = foreignCompany;
    }

}
