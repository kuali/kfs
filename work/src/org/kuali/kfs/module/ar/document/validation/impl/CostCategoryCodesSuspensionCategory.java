/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Suspension Category that checks to see if cost category codes are setup correctly. An object code might not
 * be assigned or could be assigned to more than one Cost Category. Check by comparing total current expenditure
 * to the sum of account current expenditure.
 */
public class CostCategoryCodesSuspensionCategory extends SuspensionCategoryBase {

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        String billingFrequencyCode = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAward().getBillingFrequencyCode();

        if (!StringUtils.equals(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE, billingFrequencyCode) &&
                !StringUtils.equals(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE, billingFrequencyCode)) {
            ContractsGrantsInvoiceDetail totalCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalCostInvoiceDetail();
            KualiDecimal categoryCumulativeExpenditure = totalCostInvoiceDetail.getCumulative();
            KualiDecimal accountDetailsCumulativeExpenditure = KualiDecimal.ZERO;

            for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
                accountDetailsCumulativeExpenditure = accountDetailsCumulativeExpenditure.add(invoiceAccountDetail.getCumulativeAmount());
            }

            return !categoryCumulativeExpenditure.equals(accountDetailsCumulativeExpenditure);
        }

        return false;
    }

}
