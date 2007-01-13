/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.financial.bo;

import org.kuali.core.bo.KualiCodeBase;

/**
 * 
 */
public class TravelCompanyCode extends KualiCodeBase {
    private TravelExpenseTypeCode travelExpenseTypeCode;

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

}
