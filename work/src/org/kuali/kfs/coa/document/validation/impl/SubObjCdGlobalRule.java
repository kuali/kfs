/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.DelegateGlobal;
import org.kuali.module.chart.bo.DelegateGlobalDetail;
import org.kuali.module.chart.bo.SubObjCdGlobalDetail;
import org.kuali.module.chart.bo.SubObjCdGlobal;

public class SubObjCdGlobalRule extends GlobalDocumentRuleBase {
    private SubObjCdGlobal subObjCdGlobal;
    
    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    @Override
    public void setupConvenienceObjects() {

        // setup subObjCdGlobal convenience objects,
        // make sure all possible sub-objects are populated
        subObjCdGlobal = (SubObjCdGlobal) super.getNewBo();

        // forces refreshes on all the sub-objects in the lists
        for (SubObjCdGlobalDetail objectCodeGlobalDetail : subObjCdGlobal.getSubObjCdGlobalDetails()) {
            objectCodeGlobalDetail.refreshNonUpdateableReferences();
        }
        for (AccountGlobalDetail accountGlobalDetail : subObjCdGlobal.getAccountGlobalDetails()) {
            accountGlobalDetail.refreshNonUpdateableReferences();
        }
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects();
        // check simple rules
        success &= checkSimpleRulesAllLines();

        success &= checkOnlyOneChartErrorWrapper(subObjCdGlobal.getAccountGlobalDetails());

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects();
        // check simple rules
        success &= checkSimpleRulesAllLines();

        success &= checkAccountDetails( subObjCdGlobal.getAccountGlobalDetails() );

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        // check simple rules
        checkSimpleRulesAllLines();

        return true;
    }
    
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo ) {
        boolean success = true;
        if ( bo instanceof AccountGlobalDetail ) {
            AccountGlobalDetail detail = (AccountGlobalDetail)bo;
            // make sure that both primary keys are available for this object
            if (!checkEmptyValue(detail.getAccountNumber())) {
                // put an error about accountnumber
                GlobalVariables.getErrorMap().putError( "accountNumber", KFSKeyConstants.ERROR_REQUIRED, "Account Number");
                success &= false;
            }
            if (!checkEmptyValue(detail.getChartOfAccountsCode())) {
                // put an error about chart code
                GlobalVariables.getErrorMap().putError( "chartOfAccountsCode", KFSKeyConstants.ERROR_REQUIRED, "Chart of Accounts Code");
                success &= false;
            }
            success &= checkAccountDetails( detail );
        } else if ( bo instanceof SubObjCdGlobalDetail ) {
            SubObjCdGlobalDetail detail = (SubObjCdGlobalDetail)bo;
            if (!checkEmptyValue(detail.getChartOfAccountsCode())) {
                // put an error about accountnumber
                GlobalVariables.getErrorMap().putError( "chartOfAccountsCode", KFSKeyConstants.ERROR_REQUIRED, "Chart of Accounts Code");
                success &= false;
            }
            if (!checkEmptyValue(detail.getFinancialObjectCode())) {
                // put an error about financial object code
                GlobalVariables.getErrorMap().putError( "financialObjectCode", KFSKeyConstants.ERROR_REQUIRED, "Financial Object Code");
                success &= false;
            }
            if (!checkEmptyValue(detail.getUniversityFiscalYear())) {
                // put an error about financial object code
                GlobalVariables.getErrorMap().putError( "universityFiscalYear", KFSKeyConstants.ERROR_REQUIRED, "University Fiscal Year");
                success &= false;
            }
            success &= checkSubObjectCodeDetails(detail);       
        }
        return success;
    }
    
    public boolean checkAccountDetails( List<AccountGlobalDetail> details ) {
        boolean success = true;
        
        // check if there are any accounts
        if ( details.size() == 0 ) {
            putFieldError( KFSConstants.MAINTENANCE_ADD_PREFIX + "accountGlobalDetails.accountNumber", 
                    KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_NO_ACCOUNTS);
            success = false;
        } else {
            // check each account
            int index = 0;
            for (AccountGlobalDetail dtl : details) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + "accountGlobalDetails[" + index + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                success &= checkAccountDetails( dtl );
                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                index++;
            }
            success &= checkOnlyOneChartErrorWrapper( details );
        }
        
        return success;
    }
    
    public boolean checkAccountDetails( AccountGlobalDetail dtl ) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);
        if ( StringUtils.isNotBlank( dtl.getAccountNumber() ) && StringUtils.isNotBlank( dtl.getChartOfAccountsCode() ) ) {
            dtl.refreshReferenceObject( "account" );
            if ( dtl.getAccount() == null ) {
                GlobalVariables.getErrorMap().putError( "accountNumber", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { dtl.getChartOfAccountsCode(), dtl.getAccountNumber() } );
            }
        }
        success &= GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;
        
        return success;
    }
    
    public boolean checkSubObjectCodeDetails( SubObjCdGlobalDetail dtl ) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);
        if (StringUtils.isNotBlank( dtl.getFinancialObjectCode() ) &&
                StringUtils.isNotBlank( dtl.getChartOfAccountsCode() ) &&
                 dtl.getUniversityFiscalYear() > 0) {
            dtl.refreshReferenceObject( "financialObject" );
            dtl.refreshReferenceObject("universityFiscal");
            dtl.refreshReferenceObject("chartOfAccounts");
            if ( dtl.getChartOfAccounts() == null || 
                 dtl.getUniversityFiscal() == null ||
                 dtl.getFinancialObject() == null) 
            {
                GlobalVariables.getErrorMap().putError( "financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_INVALID_OBJECT_CODE, new String[] { dtl.getFinancialObjectCode(), dtl.getChartOfAccountsCode(), dtl.getUniversityFiscalYear().toString() } );
            }
        }
        success &= GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;
        
        return success;
    }
    
    /**
     * 
     * This method checks the simple rules for all lines at once and gets called on save, submit, etc. but not on add
     * 
     * @return
     */
    protected boolean checkSimpleRulesAllLines() {
        boolean success = true;
        // check if there are any object codes and accounts, if either fails this should fail
        if ( !checkForSubObjCdGlobalDetails(subObjCdGlobal.getSubObjCdGlobalDetails()) 
                && !checkForAccountGlobalDetails(subObjCdGlobal.getAccountGlobalDetails()) ) {
            success = false;
        } else {
            //check object codes
            success &= checkFiscalYearAllLines(subObjCdGlobal);
            
            //check chart code
            success &= checkChartAllLines(subObjCdGlobal);
            
        }
        return success;
    }

    protected boolean checkForSubObjCdGlobalDetails(List<SubObjCdGlobalDetail> subObjCdGlobalDetails) {
        if(subObjCdGlobalDetails == null || subObjCdGlobalDetails.size() == 0) {
            putFieldError( KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, 
                    KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_NO_OBJECT_CODE);
            return false;
        }
        return true;
    }
    
    protected boolean checkForAccountGlobalDetails(List<AccountGlobalDetail> acctChangeDetails) {
        if(acctChangeDetails == null || acctChangeDetails.size() == 0) {
            putFieldError( KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.ACCOUNT_CHANGE_DETAILS + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, 
                    KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_NO_ACCOUNT);
            return false;
        }
        return true;
    }

    protected boolean checkFiscalYearAllLines(SubObjCdGlobal socChangeDocument) {
        boolean success = true;
        int i = 0;
        for (SubObjCdGlobalDetail subObjCdGlobal : socChangeDocument.getSubObjCdGlobalDetails()) {

            //check fiscal year first
            success &= checkFiscalYear(socChangeDocument, subObjCdGlobal, i, false);

            // increment counter for sub object changes list
            i++;
        }
        
        return success;
    }
    
    protected boolean checkChartAllLines(SubObjCdGlobal socChangeDocument) {
        boolean success = true;
        int i = 0;
        for (SubObjCdGlobalDetail subObjCdGlobal : socChangeDocument.getSubObjCdGlobalDetails()) {

            //check chart
            success &= checkChartOnSubObjCodeDetails(socChangeDocument, subObjCdGlobal, i, false);
            // increment counter for sub object changes list
            i++;
        }
        
        // check each account change
        i = 0;
        for (AccountGlobalDetail acctChangeDetail : socChangeDocument.getAccountGlobalDetails()) {

            //check chart
            success &= checkChartOnAccountDetails(socChangeDocument, acctChangeDetail, i, false);
            // increment counter for account changes list
            i++;
        }
        
        return success;
    }
    
    /**
     * 
     * This method checks to make sure that a given 
     * @param socChangeDocument
     * @return
     */
    protected boolean checkFiscalYear(SubObjCdGlobal socChangeDocument, SubObjCdGlobalDetail socChangeDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        //first must have an actual fiscal year
        if(socChangeDetail.getUniversityFiscal() == null) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "." + "universityFiscalYear";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_FISCAL_YEAR_MUST_EXIST);
            }
            else {
                errorPath = KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "[" + lineNum + "]." + "universityFiscalYear";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_FISCAL_YEAR_MUST_EXIST);
            }
            success &= false;
            return success;
        }
        
        //the two fiscal years from the document and detail must match
        if(!socChangeDocument.getUniversityFiscal().equals(socChangeDetail.getUniversityFiscal())) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "." + "universityFiscalYear";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_FISCAL_YEAR_MUST_BE_SAME);
            }
            else {
                errorPath = KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "[" + lineNum + "]." + "universityFiscalYear";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_FISCAL_YEAR_MUST_BE_SAME);
            }
            success &= false;
            return success;
        }
        
        return success;
    }

    protected boolean checkChartOnSubObjCodeDetails(SubObjCdGlobal socChangeDocument, SubObjCdGlobalDetail socChangeDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        //first must have an actual fiscal year
        if(socChangeDetail.getChartOfAccounts() == null) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_EXIST);
            }
            else {
                errorPath = KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_EXIST);
            }
            success &= false;
            return success;
        }
        
        //the two fiscal years from the document and detail must match
        if(!socChangeDocument.getChartOfAccounts().equals(socChangeDetail.getChartOfAccounts())) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_BE_SAME);
            }
            else {
                errorPath = KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_BE_SAME);
            }
            success &= false;
            return success;
        }
        
        return success;
    }
    
    protected boolean checkChartOnAccountDetails(SubObjCdGlobal socChangeDocument, AccountGlobalDetail acctDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        //first must have an actual fiscal year
        if(acctDetail.getChartOfAccounts() == null) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_EXIST);
            }
            else {
                errorPath = KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_EXIST);
            }
            success &= false;
            return success;
        }
        
        //the two fiscal years from the document and detail must match
        if(!socChangeDocument.getChartOfAccounts().equals(acctDetail.getChartOfAccounts())) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.ACCOUNT_CHANGE_DETAILS + "." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_BE_SAME);
            }
            else {
                errorPath = KFSPropertyConstants.ACCOUNT_CHANGE_DETAILS + "[" + lineNum + "]." + "chartOfAccountsCode";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_BE_SAME);
            }
            success &= false;
            return success;
        }
        
        return success;
    }

    
}
