/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.document.validation.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class AccountRule extends org.kuali.kfs.coa.document.validation.impl.AccountRule {

    public boolean processAccountRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;
        oldAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        // validate the embedded AccountGuideline object
        success &= checkAccountGuidelinesValidation(newAccount.getAccountGuideline());
        success &= checkEmptyValues(document);
        success &= checkGeneralRules(document);
        success &= checkCloseAccount(document);
        success &= checkContractsAndGrants(document);
        success &= checkExpirationDate(document);
        success &= checkFundGroup(document);
        success &= checkSubFundGroup(document);
        success &= checkIncomeStreamAccountRule();
        success &= checkUniqueAccountNumber(document);
        
        return success;
    }
    
    /**
     * This method checks the basic rules for empty values in an account and associated objects with this account If guidelines are
     * required for this Business Object it checks to make sure that it is filled out It also checks for partially filled out
     * reference keys on the following: continuationAccount incomeStreamAccount endowmentIncomeAccount reportsToAccount
     * contractControlAccount indirectCostRecoveryAcct
     * 
     * @param maintenanceDocument
     * @return false if any of these are empty
     */
    protected boolean checkEmptyValues(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkEmptyValues called");

        boolean success = true;

        // guidelines are always required, except when the expirationDate is set, and its
        // earlier than today
        boolean guidelinesRequired = areGuidelinesRequired((Account) maintenanceDocument.getNewMaintainableObject().getBusinessObject());

        // confirm that required guidelines are entered, if required
        if (guidelinesRequired) {
            success &= checkEmptyBOField("accountGuideline.accountExpenseGuidelineText", newAccount.getAccountGuideline().getAccountExpenseGuidelineText(), "Expense Guideline");
            success &= checkEmptyBOField("accountGuideline.accountIncomeGuidelineText", newAccount.getAccountGuideline().getAccountIncomeGuidelineText(), "Income Guideline");
            success &= checkEmptyBOField("accountGuideline.accountPurposeText", newAccount.getAccountGuideline().getAccountPurposeText(), "Account Purpose");
        }

        // this set confirms that all fields which are grouped (ie, foreign keys of a reference
        // object), must either be none filled out, or all filled out.
        /*
        success &= checkForPartiallyFilledOutReferenceForeignKeys("continuationAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("incomeStreamAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("endowmentIncomeAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("reportsToAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("contractControlAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("indirectCostRecoveryAcct");
        */
        return success;
    }
}
