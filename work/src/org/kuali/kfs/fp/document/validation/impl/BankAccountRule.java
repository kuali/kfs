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
package org.kuali.module.financial.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.financial.bo.BankAccount;

/**
 * This is the rules class that is used for the default implementation of the Bank Account maintenance document.
 * 
 * 
 */
public class BankAccountRule extends MaintenanceDocumentRuleBase {

    BankAccount oldBankAccount;
    BankAccount newBankAccount;

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;

        success &= checkPartiallyFilledOutReferences();
        success &= checkSubAccountNumberExistence();
        success &= checkSubObjectCodeExistence();

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;

        success &= checkPartiallyFilledOutReferences();
        success &= checkSubAccountNumberExistence();
        success &= checkSubObjectCodeExistence();

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkPartiallyFilledOutReferences();
        checkSubAccountNumberExistence();
        checkSubObjectCodeExistence();

        return true;
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    public void setupConvenienceObjects() {
        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldBankAccount = (BankAccount) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newBankAccount = (BankAccount) super.getNewBo();
    }

    /**
     * This method checks for partially filled out objects.
     * 
     * @param document
     * @return
     */
    private boolean checkPartiallyFilledOutReferences() {

        boolean success = true;

        success &= checkForPartiallyFilledOutReferenceForeignKeys("cashOffsetAccount");
        return success;
    }

    /**
     * This method validates that the Cash Offset subAccountNumber exists, if it is entered
     * 
     * @param document
     * @return
     */
    private boolean checkSubAccountNumberExistence() {

        boolean success = true;

        // if all of the relevant values arent entered, don't bother
        if (StringUtils.isBlank(newBankAccount.getCashOffsetFinancialChartOfAccountCode())) {
            return success;
        }
        if (StringUtils.isBlank(newBankAccount.getCashOffsetAccountNumber())) {
            return success;
        }
        if (StringUtils.isBlank(newBankAccount.getCashOffsetSubAccountNumber())) {
            return success;
        }

        // setup the map to search on
        Map pkMap = new HashMap();
        pkMap.put("chartOfAccountsCode", newBankAccount.getCashOffsetFinancialChartOfAccountCode());
        pkMap.put("accountNumber", newBankAccount.getCashOffsetAccountNumber());
        pkMap.put("subAccountNumber", newBankAccount.getCashOffsetSubAccountNumber());

        // do the search
        SubAccount testSubAccount = (SubAccount) getBoService().findByPrimaryKey(SubAccount.class, pkMap);

        // fail if the subAccount isnt found
        if (testSubAccount == null) {
            putFieldError("cashOffsetSubAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, getDdService().getAttributeLabel(BankAccount.class, "cashOffsetSubAccountNumber"));
            success &= false;
        }

        return success;
    }

    /**
     * This method validates that the Cash Offset subObjectCode exists, if it is entered
     * 
     * @param document
     * @return
     */
    private boolean checkSubObjectCodeExistence() {

        boolean success = true;

        // if all of the relevant values arent entered, dont bother
        if (StringUtils.isBlank(newBankAccount.getCashOffsetFinancialChartOfAccountCode())) {
            return success;
        }
        if (StringUtils.isBlank(newBankAccount.getCashOffsetAccountNumber())) {
            return success;
        }
        if (StringUtils.isBlank(newBankAccount.getCashOffsetObjectCode())) {
            return success;
        }
        if (StringUtils.isBlank(newBankAccount.getCashOffsetSubObjectCode())) {
            return success;
        }

        // setup the map to search on
        Map pkMap = new HashMap();
        pkMap.put("universityFiscalYear", SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear());
        pkMap.put("chartOfAccountsCode", newBankAccount.getCashOffsetFinancialChartOfAccountCode());
        pkMap.put("accountNumber", newBankAccount.getCashOffsetAccountNumber());
        pkMap.put("financialObjectCode", newBankAccount.getCashOffsetObjectCode());
        pkMap.put("financialSubObjectCode", newBankAccount.getCashOffsetSubObjectCode());

        // do the search
        SubObjCd testSubObjCd = (SubObjCd) getBoService().findByPrimaryKey(SubObjCd.class, pkMap);

        // fail if the subObjectCode isnt found
        if (testSubObjCd == null) {
            putFieldError("cashOffsetSubObjectCode", KFSKeyConstants.ERROR_EXISTENCE, getDdService().getAttributeLabel(BankAccount.class, "cashOffsetSubObjectCode"));
            success &= false;
        }

        return success;
    }

    /**
     * This method has been taken out of service to be replaced by the defaultExistenceChecks happening through the
     * BankAccountMaintenanceDocument.xml
     * 
     * @param document
     * @return
     */
    private boolean checkSubObjectExistence(MaintenanceDocument document) {
        // default to success
        boolean success = true;
        BankAccount newBankAcct = (BankAccount) document.getNewMaintainableObject().getBusinessObject();
        newBankAcct.refresh();

        // existence check on bank code


        if (StringUtils.isNotEmpty(newBankAcct.getFinancialDocumentBankCode())) {
            if (ObjectUtils.isNull(newBankAcct.getBank())) {
                success &= false;
                putFieldError("financialDocumentBankCode", KFSKeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INVALID_BANK);
            }
        }

        return success;
    }
}