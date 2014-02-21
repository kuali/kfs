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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * Overrides maintainable so that if the threshhold is not null, turns receipted required to true (if it isn't)
 */
public class ExpenseTypeObjectCodeMaintainable extends FinancialSystemMaintainable {

    /**
     * Overridden to turn receipt required to true if the receipt required threshold is not null
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#processAfterPost(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
        // let's be careful and get the new maintainable and business object from the maint doc directly
        ExpenseTypeObjectCode etoc = (ExpenseTypeObjectCode)document.getNewMaintainableObject().getBusinessObject();
        if (!etoc.isReceiptRequired() && etoc.getReceiptRequirementThreshold() != null && (etoc.getReceiptRequirementThreshold().isPositive() || etoc.getReceiptRequirementThreshold().isZero())) {
            etoc.setReceiptRequired(true);
        }
    }

}
