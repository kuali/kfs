/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountGlobalDetail;
import org.kuali.kfs.coa.businessobject.SubObjectCodeGlobal;
import org.kuali.kfs.coa.businessobject.SubObjectCodeGlobalDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This class implements the business rules specific to the {@link SubObjCdGlobal} Maintenance Document.
 */
public class SubObjCdGlobalRule extends GlobalDocumentRuleBase {
    protected SubObjectCodeGlobal subObjCdGlobal;

    /**
     * This method sets the convenience objects like subObjCdGlobal and all the detail objects, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available. This also loops through each detail item (SubObjCdGlobalDetail and AccountGlobalDetail)
     * are refreshed
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    @Override
    public void setupConvenienceObjects() {

        // setup subObjCdGlobal convenience objects,
        // make sure all possible sub-objects are populated
        subObjCdGlobal = (SubObjectCodeGlobal) super.getNewBo();

        // forces refreshes on all the sub-objects in the lists
        for (SubObjectCodeGlobalDetail objectCodeGlobalDetail : subObjCdGlobal.getSubObjCdGlobalDetails()) {
            objectCodeGlobalDetail.refreshNonUpdateableReferences();
        }
        for (AccountGlobalDetail accountGlobalDetail : subObjCdGlobal.getAccountGlobalDetails()) {
            accountGlobalDetail.refreshNonUpdateableReferences();
        }
    }

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link SubObjCdGlobalRule#checkSimpleRulesAllLines()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
     * This performs rules checks on document route
     * <ul>
     * <li>{@link SubObjCdGlobalRule#checkSimpleRulesAllLines()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects();
        // check simple rules
        success &= checkSimpleRulesAllLines();

        success &= checkAccountDetails(subObjCdGlobal.getAccountGlobalDetails());

        return success;
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link SubObjCdGlobalRule#checkSimpleRulesAllLines()}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        // check simple rules
        checkSimpleRulesAllLines();

        return true;
    }

    /**
     * Before adding either a {@link AccountGlobalDetail} or {@link SubObjCdGlobalDetail} this checks to make sure
     * that the account and chart are filled in, in the case of SubObjCdGlobalDetail it also checks
     * that the object code and fiscal year are filled in
     * If any of these fail, it fails to add the new line to the collection
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;
        if (bo instanceof AccountGlobalDetail) {
            AccountGlobalDetail detail = (AccountGlobalDetail) bo;
            // make sure that both primary keys are available for this object
            if (!checkEmptyValue(detail.getAccountNumber())) {
                // put an error about accountnumber
                GlobalVariables.getMessageMap().putError("accountNumber", KFSKeyConstants.ERROR_REQUIRED, "Account Number");
                success &= false;
            }
            if (!checkEmptyValue(detail.getChartOfAccountsCode())) {
                // put an error about chart code
                GlobalVariables.getMessageMap().putError("chartOfAccountsCode", KFSKeyConstants.ERROR_REQUIRED, "Chart of Accounts Code");
                success &= false;
            }
            success &= checkAccountDetails(detail);
        }
        else if (bo instanceof SubObjectCodeGlobalDetail) {
            SubObjectCodeGlobalDetail detail = (SubObjectCodeGlobalDetail) bo;
            if (!checkEmptyValue(detail.getChartOfAccountsCode())) {
                // put an error about accountnumber
                GlobalVariables.getMessageMap().putError("chartOfAccountsCode", KFSKeyConstants.ERROR_REQUIRED, "Chart of Accounts Code");
                success &= false;
            }
            if (!checkEmptyValue(detail.getFinancialObjectCode())) {
                // put an error about financial object code
                GlobalVariables.getMessageMap().putError("financialObjectCode", KFSKeyConstants.ERROR_REQUIRED, "Financial Object Code");
                success &= false;
            }
            if (!checkEmptyValue(detail.getUniversityFiscalYear())) {
                // put an error about financial object code
                GlobalVariables.getMessageMap().putError("universityFiscalYear", KFSKeyConstants.ERROR_REQUIRED, "University Fiscal Year");
                success &= false;
            }
            success &= checkSubObjectCodeDetails(detail);
        }
        return success;
    }

    /**
     * 
     * This calls the {@link SubObjCdGlobalRule#checkAccountDetails(AccountGlobalDetail)} on each AccountGlobalDetail as well as calling
     * {@link SubObjCdGlobalRule#checkOnlyOneChartErrorWrapper(List)} to ensure there is just one chart
     * @param details
     * @return false if any of the detail objects fail they're sub-rule
     */
    public boolean checkAccountDetails(List<AccountGlobalDetail> details) {
        boolean success = true;

        // check if there are any accounts
        if (details.size() == 0) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + "accountGlobalDetails.accountNumber", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_NO_ACCOUNTS);
            success = false;
        }
        else {
            // check each account
            int index = 0;
            for (AccountGlobalDetail dtl : details) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + "accountGlobalDetails[" + index + "]";
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                success &= checkAccountDetails(dtl);
                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
                index++;
            }
            success &= checkOnlyOneChartErrorWrapper(details);
        }

        return success;
    }

    /**
     * 
     * This checks that if the account and chart are entered that the  account associated with the AccountGlobalDetail is valid
     * @param dtl - the AccountGlobalDetail we are dealing with
     * @return false if any of the fields are found to be invalid
     */
    public boolean checkAccountDetails(AccountGlobalDetail dtl) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);
        if (StringUtils.isNotBlank(dtl.getAccountNumber()) && StringUtils.isNotBlank(dtl.getChartOfAccountsCode())) {
            dtl.refreshReferenceObject("account");
            if (ObjectUtils.isNull(dtl.getAccount())) {
                GlobalVariables.getMessageMap().putError("accountNumber", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { dtl.getChartOfAccountsCode(), dtl.getAccountNumber() });
            }
        }
        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * 
     * This checks that if the object code, chart code, and fiscal year are entered it is a valid Object Code, chart, and Fiscal Year
     * associated with this SubObjectCode
     * @param dtl - the SubObjCdGlobalDetail we are checking
     * @return false if any of the fields are found to be invalid
     */
    public boolean checkSubObjectCodeDetails(SubObjectCodeGlobalDetail dtl) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);
        if (StringUtils.isNotBlank(dtl.getFinancialObjectCode()) && StringUtils.isNotBlank(dtl.getChartOfAccountsCode()) && dtl.getUniversityFiscalYear() > 0) {
            dtl.refreshReferenceObject("financialObject");
            dtl.refreshReferenceObject("universityFiscal");
            dtl.refreshReferenceObject("chartOfAccounts");
            if (ObjectUtils.isNull(dtl.getChartOfAccounts()) || ObjectUtils.isNull(dtl.getUniversityFiscal()) || ObjectUtils.isNull(dtl.getFinancialObject())) {
                GlobalVariables.getMessageMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_INVALID_OBJECT_CODE, new String[] { dtl.getFinancialObjectCode(), dtl.getChartOfAccountsCode(), dtl.getUniversityFiscalYear().toString() });
            }
        }
        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * This method checks the simple rules for all lines at once and gets called on save, submit, etc. but not on add
     * <ul>
     * <li>{@link SubObjCdGlobalRule#checkForSubObjCdGlobalDetails(List)}</li>
     * <li>{@link SubObjCdGlobalRule#checkForAccountGlobalDetails(List)}</li>
     * <li>{@link SubObjCdGlobalRule#checkFiscalYearAllLines(SubObjCdGlobal)}</li>
     * <li>{@link SubObjCdGlobalRule#checkChartAllLines(SubObjCdGlobal)}</li>
     * </ul>
     * @return
     */
    protected boolean checkSimpleRulesAllLines() {
        boolean success = true;
        // check if there are any object codes and accounts, if either fails this should fail
        if (!checkForSubObjCdGlobalDetails(subObjCdGlobal.getSubObjCdGlobalDetails()) && !checkForAccountGlobalDetails(subObjCdGlobal.getAccountGlobalDetails())) {
            success = false;
        }
        else {
            // check object codes
            success &= checkFiscalYearAllLines(subObjCdGlobal);

            // check chart code
            success &= checkChartAllLines(subObjCdGlobal);

        }
        return success;
    }

    /**
     * 
     * This checks that the SubObjCdGlobalDetail list isn't empty or null
     * @param subObjCdGlobalDetails
     * @return false if the list is null or empty
     */
    protected boolean checkForSubObjCdGlobalDetails(List<SubObjectCodeGlobalDetail> subObjCdGlobalDetails) {
        if (subObjCdGlobalDetails == null || subObjCdGlobalDetails.size() == 0) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.SUB_OBJ_CODE_CHANGE_DETAILS + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_NO_OBJECT_CODE);
            return false;
        }
        return true;
    }

    /**
     * 
     * This checks that the AccountGlobalDetail list isn't empty or null
     * @param acctChangeDetails
     * @return false if the list is null or empty
     */
    protected boolean checkForAccountGlobalDetails(List<AccountGlobalDetail> acctChangeDetails) {
        if (acctChangeDetails == null || acctChangeDetails.size() == 0) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.ACCOUNT_CHANGE_DETAILS + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_NO_ACCOUNT);
            return false;
        }
        return true;
    }

    /**
     * 
     * This checks that the fiscal year is the same on the doc and all SubObjCdGlobalDetails
     * @param socChangeDocument
     * @return false if the fiscal year is not the same on the doc and any of the SubObjCdGlobalDetails
     */
    protected boolean checkFiscalYearAllLines(SubObjectCodeGlobal socChangeDocument) {
        boolean success = true;
        int i = 0;
        for (SubObjectCodeGlobalDetail subObjCdGlobal : socChangeDocument.getSubObjCdGlobalDetails()) {

            // check fiscal year first
            success &= checkFiscalYear(socChangeDocument, subObjCdGlobal, i, false);

            // increment counter for sub object changes list
            i++;
        }

        return success;
    }

    /**
     * 
     * This checks that the chart is the same on the document, SubObjCdGlobalDetails and AccountGlobalDetails
     * @param socChangeDocument
     * @return false if the chart is missing or not the same on the doc, or the detail lists
     */
    protected boolean checkChartAllLines(SubObjectCodeGlobal socChangeDocument) {
        boolean success = true;
        int i = 0;
        for (SubObjectCodeGlobalDetail subObjCdGlobal : socChangeDocument.getSubObjCdGlobalDetails()) {

            // check chart
            success &= checkChartOnSubObjCodeDetails(socChangeDocument, subObjCdGlobal, i, false);
            // increment counter for sub object changes list
            i++;
        }

        // check each account change
        i = 0;
        for (AccountGlobalDetail acctChangeDetail : socChangeDocument.getAccountGlobalDetails()) {

            // check chart
            success &= checkChartOnAccountDetails(socChangeDocument, acctChangeDetail, i, false);
            // increment counter for account changes list
            i++;
        }

        return success;
    }

    /**
     * This checks to make sure that the fiscal year on the {@link SubObjCdGlobalDetail} is not empty and
     * the document's fiscal year matches the detail's fiscal year
     * 
     * @param socChangeDocument
     * @return false if the fiscal year is missing or is not the same between the doc and the detail
     */
    protected boolean checkFiscalYear(SubObjectCodeGlobal socChangeDocument, SubObjectCodeGlobalDetail socChangeDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        // first must have an actual fiscal year
        if (ObjectUtils.isNull(socChangeDetail.getUniversityFiscal())) {
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

        // the two fiscal years from the document and detail must match
        if (!socChangeDocument.getUniversityFiscal().equals(socChangeDetail.getUniversityFiscal())) {
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

    /**
     * 
     * This checks to make sure that the chart of accounts on the {@link SubObjCdGlobalDetail} is not empty and 
     * the document's chart matches the detail's chart
     * @param socChangeDocument
     * @param socChangeDetail
     * @param lineNum
     * @param add
     * @return false if the chart is missing or is not the same between the doc and the detail
     */
    protected boolean checkChartOnSubObjCodeDetails(SubObjectCodeGlobal socChangeDocument, SubObjectCodeGlobalDetail socChangeDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        
        if (StringUtils.isBlank(socChangeDetail.getChartOfAccountsCode())) {
            return success; // just return, the existence check will balk at empty details
        }
        
        // first must have an actual fiscal year
        if (socChangeDetail.getChartOfAccounts() == null) {
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

        // the two fiscal years from the document and detail must match
        if (!socChangeDocument.getChartOfAccounts().equals(socChangeDetail.getChartOfAccounts())) {
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

    /**
     * 
     * This checks that the chart of accounts on the {@link AccountGlobalDetail} is not empty and matches
     * the document's chart matches the detail's chart
     * @param socChangeDocument
     * @param acctDetail
     * @param lineNum
     * @param add
     * @return false if the chart is missing or is not the same between the doc and the detail
     */
    protected boolean checkChartOnAccountDetails(SubObjectCodeGlobal socChangeDocument, AccountGlobalDetail acctDetail, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;
        // first must have an actual fiscal year
        if (acctDetail.getChartOfAccounts() == null) {
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

        // the two fiscal years from the document and detail must match
        if (!socChangeDocument.getChartOfAccounts().equals(acctDetail.getChartOfAccounts())) {
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
