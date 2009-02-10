/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * Evaluates business rules for editing or creation of a new bank record.
 */
public class BankRule extends MaintenanceDocumentRuleBase {
    private Bank oldBank;
    private Bank newBank;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        return super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean valid = true;

        valid &= checkPartiallyFilledOutReferences();
        valid &= validateFieldsForBankOffsetEntries();

        return valid;
    }

    /**
     * Sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and old objects
     * contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all
     * sub-objects from the DB by their primary keys, if available.
     */
    public void setupConvenienceObjects() {
        oldBank = (Bank) super.getOldBo();
        newBank = (Bank) super.getNewBo();
    }

    /**
     * Checks for partially filled out objects.
     * 
     * @return true if there are no partially filled out references
     */
    private boolean checkPartiallyFilledOutReferences() {
        boolean valid = true;

        valid &= checkForPartiallyFilledOutReferenceForeignKeys(KFSPropertyConstants.CASH_OFFSET_ACCOUNT);
        valid &= checkForPartiallyFilledOutReferenceForeignKeys(KFSPropertyConstants.CASH_OFFSET_OBJECT);

        return valid;
    }

    /**
     * Checks system parameter to determine if the bank code functionality is enabled. If so verifies the cash offset fields needed
     * to create the additional bank entries were given.
     * 
     * @return true if all cash offset fields needed have value
     */
    private boolean validateFieldsForBankOffsetEntries() {
        boolean valid = true;

        if (!SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            return valid;
        }

        if (StringUtils.isBlank(newBank.getCashOffsetAccountNumber())) {
            this.putFieldError(KFSPropertyConstants.CASH_OFFSET_ACCOUNT_NUMBER, KFSKeyConstants.Bank.ERROR_MISSING_CASH_ACCOUNT_NUMBER);
            valid = false;
        }

        if (StringUtils.isBlank(newBank.getCashOffsetObjectCode())) {
            this.putFieldError(KFSPropertyConstants.CASH_OFFSET_OBJECT_CODE, KFSKeyConstants.Bank.ERROR_MISSING_CASH_OBJECT_CODE);
            valid = false;
        }

        return valid;
    }

}
