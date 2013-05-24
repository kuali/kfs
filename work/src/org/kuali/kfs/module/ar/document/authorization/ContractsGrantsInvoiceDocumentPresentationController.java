/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Contracts Grants Invoice Document Presentation Controller class.
 */
public class ContractsGrantsInvoiceDocumentPresentationController extends CustomerInvoiceDocumentPresentationController {

    /**
     * @see org.kuali.kfs.module.ar.document.authorization.ContractsGrantsInvoiceDocumentPresentationController#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)
     */
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        if (StringUtils.isNotBlank(document.getFinancialSystemDocumentHeader().getCorrectedByDocumentId())) {
            return false;
        }
        if (((ContractsGrantsInvoiceDocument) document).isInvoiceReversal()) {
            return false;
        }
        else {
            // a normal invoice can only be error corrected if document is in a final state
            // and no amounts have been applied (excluding discounts)
            return isDocFinalWithNoAppliedAmountsExceptDiscounts((ContractsGrantsInvoiceDocument) document);
        }
    }

    /**
     * This method returns true if Billing Schedule is Milestone.
     *
     * @param document
     * @return
     */
    public boolean isBillingFrequencyMilestone(ContractsGrantsInvoiceDocument document) {
        String milestone = ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE;
        if (milestone.equals(document.getInvoiceGeneralDetail().getBillingFrequency())) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This method returns true if Billing Schedule is Predetermined Billing.
     *
     * @param document
     * @return
     */
    public boolean isBillingFrequencyPredeterminedBillingSchedule(ContractsGrantsInvoiceDocument document) {
        String predetermined = ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE;
        if (predetermined.equals(document.getInvoiceGeneralDetail().getBillingFrequency())) {
            return true;
        }
        else {
            return false;
        }
    }

}
