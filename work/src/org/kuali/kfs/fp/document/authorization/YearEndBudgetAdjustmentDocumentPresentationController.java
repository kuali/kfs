/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.fp.document.authorization;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

/**
 * Document Authorizer for the Year End Budget Adjustment document.
 */
public class YearEndBudgetAdjustmentDocumentPresentationController extends BudgetAdjustmentDocumentPresentationController {

    /**
     * Checks whether the BA document is active for the year end posting year.
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.rice.krad.bo.user.KualiUser)
     */
    @Override
    public boolean canInitiate(String documentTypeName) {
        List allowedYears = SpringContext.getBean(FiscalYearFunctionControlService.class).getBudgetAdjustmentAllowedYears();
        Integer previousPostingYear = new Integer(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue() - 1);
        boolean previousActive = false;
		if (allowedYears != null) {
            for (Iterator iter = allowedYears.iterator(); iter.hasNext();) {
                FiscalYearFunctionControl fyControl = (FiscalYearFunctionControl) iter.next();
                if (fyControl.getUniversityFiscalYear().equals(previousPostingYear)) {
                    previousActive = true;
                }
            }
        }
        return super.canInitiate(documentTypeName) && previousActive;
    }
}

