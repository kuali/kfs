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
package org.kuali.kfs.fp.batch.service.impl;

import org.kuali.kfs.fp.document.YearEndDistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.GeneralLedgerInputTypeService;
import org.kuali.rice.kim.bo.Person;

/**
 * An implementation of ElectronicPaymentClaimingHelper for YearEndDisbursementOfIncomeAndExpense documents.  Most of the behaviors have been inherited from
 * DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.
 */
public class YearEndDistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl extends DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private final static String YEDI_WORKFLOW_DOC_TYPE = "YearEndDistributionOfIncomeAndExpenseDocument";

    /**
     * Returns the YearEndDistributionOfIncomeAndExpenseDocument class.
     * @see org.kuali.kfs.fp.batch.service.impl.DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl#getClaimingDocumentClass()
     */
    @Override
    public String getClaimingDocumentWorkflowDocumentType() {
        return YearEndDistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.YEDI_WORKFLOW_DOC_TYPE;
    }

    /**
     * This uses the parent's userMayUseToClaim method, but then also checks that the YearEndDistributionOfIncomeAndExpenseDocument is actually currently active within the system.
     * @see org.kuali.kfs.fp.batch.service.impl.DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl#userMayUseToClaim(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean userMayUseToClaim(Person claimingUser) {
        boolean userMayUse = super.userMayUseToClaim(claimingUser);
        if (userMayUse) {
            userMayUse = SpringContext.getBean(GeneralLedgerInputTypeService.class).getGeneralLedgerInputTypeByDocumentName(getClaimingDocumentWorkflowDocumentType()).isDocumentTypeActiveIndicator();
        }
        return userMayUse;
    }

    /**
     * @see org.kuali.kfs.fp.batch.service.impl.DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl#getDocumentTypeName()
     */
    public String getDocumentTypeName() {
        return YearEndDistributionOfIncomeAndExpenseDocument.class.getSimpleName();
    }
}

