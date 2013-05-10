/*
 * Copyright 2012 The Kuali Foundation.
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
