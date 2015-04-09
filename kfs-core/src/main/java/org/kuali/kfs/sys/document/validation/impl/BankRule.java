/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.document.validation.impl;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * Evaluates business rules for editing or creation of a new bank record.
 */
public class BankRule extends MaintenanceDocumentRuleBase {
    protected Bank oldBank;
    protected Bank newBank;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean valid = true;

        valid &= checkPartiallyFilledOutReferences();
        valid &= validateFieldsForBankOffsetEntries();
        valid &= validateBankAccountNumber();

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
    protected boolean checkPartiallyFilledOutReferences() {
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
    protected boolean validateFieldsForBankOffsetEntries() {
        boolean valid = true;

        if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {

            if (StringUtils.isBlank(newBank.getCashOffsetAccountNumber())) {
                putFieldError(KFSPropertyConstants.CASH_OFFSET_ACCOUNT_NUMBER, KFSKeyConstants.Bank.ERROR_MISSING_CASH_ACCOUNT_NUMBER);
                valid = false;
            }
    
            if (StringUtils.isBlank(newBank.getCashOffsetObjectCode())) {
                putFieldError(KFSPropertyConstants.CASH_OFFSET_OBJECT_CODE, KFSKeyConstants.Bank.ERROR_MISSING_CASH_OBJECT_CODE);
                valid = false;
            }
        }

        return valid;
    }
    
    /**
     * Bank account number must be unique.
     * 
     * @return
     */
    protected boolean validateBankAccountNumber() {
        // if the new bank is not blank *AND* has been changed
        // (I.e, never fire this edit if the account has not been changed)
        if ( StringUtils.isNotBlank(newBank.getBankAccountNumber() )
                && (oldBank == null ||
                         !StringUtils.equals(oldBank.getBankAccountNumber(), newBank.getBankAccountNumber())) ) {
            @SuppressWarnings("rawtypes")
            Collection existingBanks = getBoService().findMatching(Bank.class, Collections.singletonMap(KFSPropertyConstants.BANK_ACCOUNT_NUMBER, newBank.getBankAccountNumber()));
            if ( existingBanks != null && !existingBanks.isEmpty() ) {
                putFieldError(KFSPropertyConstants.BANK_ACCOUNT_NUMBER, KFSKeyConstants.Bank.ERROR_ACCOUNT_NUMBER_NOT_UNIQUE);
                return false;
            }
        }
        return true;        
    }
}
