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
package org.kuali.kfs.module.ld.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * BO for the Labor Benefit Rate Category Fringe Benefit
 *
 * @author Allan Sonkin
 */
public class LaborBenefitRateCategory extends PersistableBusinessObjectBase implements org.kuali.kfs.integration.ld.LaborBenefitRateCategory, MutableInactivatable {

    private String laborBenefitRateCategoryCode;//the BO code

    private Boolean active = false;     //indicates active status of this BO

    private String codeDesc;                    //description for the BO


    /**
     * Getter method to get the laborBenefitRateCategoryCode
     * @return laborBenefitRateCategoryCode
     */
	@Override
    public String getLaborBenefitRateCategoryCode() {
		return laborBenefitRateCategoryCode;
	}

    /**
     *
     * Method to set the code
     * @param code
     */
    @Override
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode) {
		this.laborBenefitRateCategoryCode = laborBenefitRateCategoryCode;
	}


    /**
     *
     * Getter method for the code's description
     * @return codeDesc
     */
    @Override
    public String getCodeDesc() {
        return codeDesc;
    }

	/**
	 *
	 * Sets the codeDesc
	 * @param codeDesc
	 */
    @Override
    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("laborBenefitRateCategoryCode", this.laborBenefitRateCategoryCode);
        m.put("codeDesc", this.codeDesc);

        return m;
	}

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
