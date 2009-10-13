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
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.coa.businessobject.ObjectCode;

/**
 * An interface that represents the logic of a category associated with the Organization Reversion Process
 */
public interface OrganizationReversionCategoryLogic {
    
    /**
     * Given an object code, determins if balances with the object code should be added to the bucket
     * for this category or not
     * 
     * @param oc the object code of a balance
     * @return true if object code indicates that the balance should be added to this category, false if not
     */
    public boolean containsObjectCode(ObjectCode oc);

    /**
     * Returns the code of the organization reversion category this logic is associated with
     * 
     * @return the category code of this organization reversion category
     */
    public String getCode();

    /**
     * Returns the name of the organization reversion category this logic is associated with?
     * 
     * @return the name of this organization reversion category
     */
    public String getName();

    /**
     * Does this category represent an expense?
     * 
     * @return true if it represents an expense, false if not
     */
    public boolean isExpense();
}
