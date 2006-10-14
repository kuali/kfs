/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.gl.service;

import org.kuali.module.chart.bo.ObjectCode;

public interface OrganizationReversionCategoryLogic {
    /**
     * Determine if this object code is contained in this category
     * 
     * @param oc Object Code
     * @return true if object code is in this category, false if not
     */
    public boolean containsObjectCode(ObjectCode oc);

    /**
     * Name of this code
     * 
     * @return code
     */
    public String getCode();

    /**
     * Name of this category
     * 
     * @return name
     */
    public String getName();

    /**
     * Is this category an expense?
     * 
     * @return true if expense, false if not
     */
    public boolean isExpense();
}
