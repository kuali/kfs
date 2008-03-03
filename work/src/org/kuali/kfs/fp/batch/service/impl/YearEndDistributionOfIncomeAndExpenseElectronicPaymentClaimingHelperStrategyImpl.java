/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.kfs.service.ElectronicPaymentClaimingDocument;
import org.kuali.module.financial.document.YearEndDistributionOfIncomeAndExpenseDocument;

/**
 * An implementation of ElectronicPaymentClaimingHelper for YearEndDisbursementOfIncomeAndExpense documents.  Most of the behaviors have been inherited from
 * DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperImpl.
 */
public class YearEndDistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperImpl extends DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperImpl implements ElectronicPaymentClaimingDocument {
    private final static String YEDI_WORKFLOW_DOC_TYPE = "YearEndDistributionOfIncomeAndExpenseDocument";

    /**
     * Returns the YearEndDistributionOfIncomeAndExpenseDocument class.
     * @see org.kuali.module.financial.service.impl.DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperImpl#getClaimingDocumentClass()
     */
    @Override
    public String getClaimingDocumentWorkflowDocumentType() {
        return YearEndDistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperImpl.YEDI_WORKFLOW_DOC_TYPE;
    }

    /**
     * This uses the parent's userMayUseToClaim method, but then also checks that the YearEndDistributionOfIncomeAndExpenseDocument is actually currently active within the system.
     * @see org.kuali.module.financial.service.impl.DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperImpl#userMayUseToClaim(org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public boolean userMayUseToClaim(UniversalUser claimingUser) {
        boolean userMayUse = super.userMayUseToClaim(claimingUser);
        if (userMayUse) {
            userMayUse = getDocumentTypeService().getDocumentTypeByName(getClaimingDocumentWorkflowDocumentType()).isFinDocumentTypeActiveIndicator();
        }
        return userMayUse;
    }
}
