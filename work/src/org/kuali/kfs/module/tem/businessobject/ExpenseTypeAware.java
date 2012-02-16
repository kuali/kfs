/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;


import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;

/**
 * Aware of expense type codes
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public interface ExpenseTypeAware {
    
    /**
     * Gets the value of travelExpenseTypeCode
     *
     * @return the value of travelExpenseTypeCode
     */
    TemTravelExpenseTypeCode getTravelExpenseTypeCode();
}
