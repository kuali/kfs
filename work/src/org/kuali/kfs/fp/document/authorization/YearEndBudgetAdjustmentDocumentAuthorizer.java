/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.document.authorization;

import java.util.Iterator;
import java.util.List;


import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.exceptions.InactiveDocumentTypeAuthorizationException;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;

/**
 * Document Authorizer for the Year End Budget Adjustment document.
 * 
 * 
 */
public class YearEndBudgetAdjustmentDocumentAuthorizer extends BudgetAdjustmentDocumentAuthorizer {

    /**
     * Checks whether the BA document is active for the year end posting year.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        List allowedYears = SpringServiceLocator.getKeyValuesService().getBudgetAdjustmentAllowedYears();
        Integer previousPostingYear = new Integer(SpringServiceLocator.getDateTimeService().getCurrentFiscalYear().intValue() - 1);

        // if previous fiscal year not active, BA document is not allowed to be initiated
        if (allowedYears == null || allowedYears.isEmpty()) {
            throw new InactiveDocumentTypeAuthorizationException("initiate", "KualiBudgetAdjustmentDocument");
        }
        
        boolean previousActive = false;
        for (Iterator iter = allowedYears.iterator(); iter.hasNext();) {
            FiscalYearFunctionControl fyControl = (FiscalYearFunctionControl) iter.next();
            if (fyControl.getUniversityFiscalYear().equals(previousPostingYear)) {
                previousActive = true;
            }
        }
        
        if (!previousActive) {
            throw new InactiveDocumentTypeAuthorizationException("initiate", "KualiBudgetAdjustmentDocument");
        }

        super.canInitiate(documentTypeName, user);
    }
}