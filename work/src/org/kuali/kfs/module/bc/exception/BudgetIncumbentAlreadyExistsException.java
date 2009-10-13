/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.exception;

import org.kuali.kfs.module.bc.BCKeyConstants;

/**
 * Thrown when a request to import a new incumbent into budget is called but the incumbent is already in the budget intended incumbent table.
 * 
 * @see org.kuali.kfs.module.bc.service.BudgetConstructionIncumbentService
 */
public class BudgetIncumbentAlreadyExistsException extends RuntimeException {
    private String emplid;

    /**
     * Constructs a BudgetIncumbentAlreadyExistsException.java.
     */
    public BudgetIncumbentAlreadyExistsException(String emplid) {
        super();

        this.emplid = emplid;
    }

    public String getMessageKey() {
        return BCKeyConstants.ERROR_BUDGET_INCUMBENT_ALREADY_EXISTS;
    }

    public String[] getMessageParameters() {
        return new String[] { emplid };
    }
}
