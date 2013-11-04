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
package org.kuali.kfs.integration.ld;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * BO for the Labor Benefit Rate Category Fringe Benefit
 *
 * @author Allan Sonkin
 */
public interface LaborBenefitRateCategory extends  ExternalizableBusinessObject {


    /**
     * Getter method to get the laborBenefitRateCategoryCode
     * @return laborBenefitRateCategoryCode
     */
	public String getLaborBenefitRateCategoryCode();

    /**
     *
     * Method to set the code
     * @param code
     */
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode);

    /**
     *
     * Getter method for the code's description
     * @return codeDesc
     */
    public String getCodeDesc();

    /**
	 *
	 * Sets the codeDesc
	 * @param codeDesc
	 */
    public void setCodeDesc(String codeDesc);

}