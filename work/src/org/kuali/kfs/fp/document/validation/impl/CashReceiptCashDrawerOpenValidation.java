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
package org.kuali.module.financial.document.validation.impl;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashReceiptService;

/**
 * Validation for the cash receipt document that verifies that the cash drawer is open at approval.
 */
public class CashReceiptCashDrawerOpenValidation extends GenericValidation {
    private CashReceiptFamilyBase cashReceiptDocumentForValidation;
    private CashReceiptService cashReceiptService;
    private CashDrawerService cashDrawerService;

    /**
     * Makes sure that the cash drawer for the verification unit associated with this CR doc is
     * open. If it's not, the the rule fails.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        String unitName = getCashReceiptService().getCashReceiptVerificationUnitForCampusCode(getCashReceiptDocumentForValidation().getCampusLocationCode());
        CashDrawer cd = getCashDrawerService().getByWorkgroupName(unitName, false);
        if (cd == null) {
            throw new IllegalStateException("There is no cash drawer associated with unitName '" + unitName + "' from cash receipt " + getCashReceiptDocumentForValidation().getDocumentNumber());
        }
        else if (cd.isClosed() && !getCashReceiptDocumentForValidation().getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.CashReceipt.MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED, cd.getWorkgroupName());
            return false;
        }
        return true;
    }

    /**
     * Gets the cashReceiptDocumentForValidation attribute. 
     * @return Returns the cashReceiptDocumentForValidation.
     */
    public CashReceiptFamilyBase getCashReceiptDocumentForValidation() {
        return cashReceiptDocumentForValidation;
    }

    /**
     * Sets the cashReceiptDocumentForValidation attribute value.
     * @param cashReceiptDocumentForValidation The cashReceiptDocumentForValidation to set.
     */
    public void setCashReceiptDocumentForValidation(CashReceiptFamilyBase cashReceiptFamilyDocumentForValidation) {
        this.cashReceiptDocumentForValidation = cashReceiptFamilyDocumentForValidation;
    }

    /**
     * Gets the cashDrawerService attribute. 
     * @return Returns the cashDrawerService.
     */
    public CashDrawerService getCashDrawerService() {
        return cashDrawerService;
    }

    /**
     * Sets the cashDrawerService attribute value.
     * @param cashDrawerService The cashDrawerService to set.
     */
    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }

    /**
     * Gets the cashReceiptService attribute. 
     * @return Returns the cashReceiptService.
     */
    public CashReceiptService getCashReceiptService() {
        return cashReceiptService;
    }

    /**
     * Sets the cashReceiptService attribute value.
     * @param cashReceiptService The cashReceiptService to set.
     */
    public void setCashReceiptService(CashReceiptService cashReceiptService) {
        this.cashReceiptService = cashReceiptService;
    }
}
