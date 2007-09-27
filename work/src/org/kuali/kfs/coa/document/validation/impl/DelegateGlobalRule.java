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
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.DelegateGlobal;
import org.kuali.module.chart.bo.DelegateGlobalDetail;

public class DelegateGlobalRule extends GlobalDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateGlobalRule.class);

    private static final KualiDecimal ZERO = new KualiDecimal(0);
    private DelegateGlobal newDelegateGlobal;
    private static final String DELEGATE_GLOBALS_PREFIX = "delegateGlobals";

    public DelegateGlobalRule() {
        super();
    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     */
    @Override
    public void setupConvenienceObjects() {

        // setup newDelegateGlobal convenience objects,
        // make sure all possible sub-objects are populated
        newDelegateGlobal = (DelegateGlobal) super.getNewBo();

        // forces refreshes on all the sub-objects in the lists
        for (DelegateGlobalDetail delegateGlobal : newDelegateGlobal.getDelegateGlobals()) {
            delegateGlobal.refreshNonUpdateableReferences();
        }
        for (AccountGlobalDetail accountGlobalDetail : newDelegateGlobal.getAccountGlobalDetails()) {
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

        success &= checkOnlyOneChartErrorWrapper(newDelegateGlobal.getAccountGlobalDetails());

        // check for primary routing
        success &= checkForPrimaryDelegateAllLines();
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

        success &= checkAccountDetails( newDelegateGlobal.getAccountGlobalDetails() );

        // check for primary routing
        success &= checkForPrimaryDelegateAllLines();
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

        checkOnlyOneChartErrorWrapper(newDelegateGlobal.getAccountGlobalDetails());

        // check for primary routing
        checkForPrimaryDelegateAllLines();
        return true;
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
    
    /**
     * 
     * This method checks the simple rules for all lines at once and gets called on save, submit, etc. but not on add
     * 
     * @return
     */
    protected boolean checkSimpleRulesAllLines() {

        boolean success = true;
        // check if there are any accounts
        if ( newDelegateGlobal.getDelegateGlobals().size() == 0 ) {
            putFieldError( KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.DELEGATE_GLOBALS + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, 
                    KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_DELEGATE);
            success = false;
        } else {
            // check each delegate
            int i = 0;
            for (DelegateGlobalDetail newDelegateGlobalDetail : newDelegateGlobal.getDelegateGlobals()) {
                KualiDecimal fromAmount = newDelegateGlobalDetail.getApprovalFromThisAmount();
                KualiDecimal toAmount = newDelegateGlobalDetail.getApprovalToThisAmount();
    
                success &= checkDelegateUserRules(newDelegateGlobalDetail, i, false);
    
                // FROM amount must be >= 0 (may not be negative)
                success &= checkDelegateFromAmtGreaterThanEqualZero(fromAmount, i, false);
    
                // to amount cannot be null if from amount is valid
                success &= checkDelegateForNullToAmount(fromAmount, toAmount, i, false);
    
                // TO amount must be >= FROM amount or Zero
                success &= checkDelegateToAmtGreaterThanFromAmt(fromAmount, toAmount, i, false);
    
                // increment counter for delegate changes list
                i++;
            }
        }
        return success;
    }


    /**
     * 
     * This method will check through each delegate referenced in the DelegateGlobal to ensure that there is one and only
     * primary for each account and doctype
     * 
     * @return
     */
    protected boolean checkForPrimaryDelegateAllLines() {
        boolean success = true;
        int i = 0;
        for (DelegateGlobalDetail newDelegateGlobalDetail : newDelegateGlobal.getDelegateGlobals()) {
            success &= checkPrimaryRouteRules(newDelegateGlobal.getDelegateGlobals(), newDelegateGlobalDetail, new Integer(i), false);
            i++;
        }
        return success;
    }

    /**
     * 
     * This method checks to see if the from amount is greater than zero
     * 
     * @param fromAmount
     * @param lineNum
     * @return false if from amount less than zero
     */
    protected boolean checkDelegateFromAmtGreaterThanEqualZero(KualiDecimal fromAmount, int lineNum, boolean add) {
        boolean success = true;
        if (ObjectUtils.isNotNull(fromAmount)) {
            if (fromAmount.isLessThan(ZERO)) {
                String errorPath = KFSConstants.EMPTY_STRING;
                if (add) {
                    errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "approvalFromThisAmount";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE);
                }
                else {
                    errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "approvalFromThisAmount";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE);
                }
                success &= false;
            }
        }
        return success;
    }

    /**
     * 
     * This method checks to see if the from amount is not null and the to amount is null
     * 
     * @param fromAmount
     * @param toAmount
     * @param lineNum
     * @return false if from amount valid and to amount null
     */
    protected boolean checkDelegateForNullToAmount(KualiDecimal fromAmount, KualiDecimal toAmount, int lineNum, boolean add) {
        boolean success = true;
        if (ObjectUtils.isNotNull(fromAmount) && ObjectUtils.isNull(toAmount)) {
            String errorPath = KFSConstants.EMPTY_STRING;
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "approvalToThisAmount";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
            }
            else {
                errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "approvalToThisAmount";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
            }
            success &= false;
        }
        return success;
    }

    /**
     * 
     * This method checks to see if the to Amount is greater than the from amount
     * 
     * @param fromAmount
     * @param toAmount
     * @param lineNum
     * @return false if to amount less than from amount
     */
    protected boolean checkDelegateToAmtGreaterThanFromAmt(KualiDecimal fromAmount, KualiDecimal toAmount, int lineNum, boolean add) {
        boolean success = true;
        if (ObjectUtils.isNotNull(toAmount)) {

            if (ObjectUtils.isNull(fromAmount)) {
                // case if FROM amount is null and TO amount not null then TO amount must be zero
                if (!toAmount.equals(ZERO)) {
                    String errorPath = KFSConstants.EMPTY_STRING;
                    if (add) {
                        errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "approvalToThisAmount";
                        putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                    }
                    else {
                        errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "approvalToThisAmount";

                        putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                    }
                    success &= false;
                }
            }
            else {
                // case if FROM amount is non-null and positive, disallow TO amount being less if it is not ZERO (another indicator of infinity)
                if (!toAmount.equals(ZERO) && toAmount.isLessThan(fromAmount)) {
                    String errorPath = KFSConstants.EMPTY_STRING;
                    if (add) {
                        errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "approvalToThisAmount";
                        putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                    }
                    else {
                        errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "approvalToThisAmount";
                        putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                    }
                    success &= false;
                }
            }
        }
        return success;
    }

    /**
     * 
     * This method validates the rule that says there can be only one PrimaryRoute delegate on a Global Delegate document if the
     * docType is ALL. It checks the delegateGlobalToTest against the list, to determine whether adding this new
     * delegateGlobalToTest would violate any PrimaryRoute business rule violations.
     * 
     * If any of the incoming variables is null or empty, the method will do nothing, and return Null. It will only process the
     * business rules if there is sufficient data to do so.
     * 
     * @param delegateGlobalToTest A delegateGlobal line that you want to test agains the list.
     * @param delegateGlobals A List of delegateGlobal items that is being tested against.
     * @return Null if the business rule passes, or an Integer value greater than zero, representing the line that the new line is
     *         conflicting with
     * 
     */
    protected Integer checkPrimaryRouteOnlyAllowOneAllDocType(DelegateGlobalDetail delegateGlobalToTest, List<DelegateGlobalDetail> delegateGlobals, Integer testLineNum) {

        // exit immediately if the adding line isnt both Primary and ALL docTypes
        if (delegateGlobalToTest == null || delegateGlobals == null || delegateGlobals.isEmpty()) {
            return null;
        }
        if (!delegateGlobalToTest.getAccountDelegatePrimaryRoutingIndicator()) {
            return null;
        }
        if (!"ALL".equalsIgnoreCase(delegateGlobalToTest.getFinancialDocumentTypeCode())) {
            return null;
        }

        // at this point, the delegateGlobal being added is a Primary for ALL docTypes, so we need to
        // test whether any in the existing list are also Primary, regardless of docType
        DelegateGlobalDetail delegateGlobal = null;
        for (int lineNumber = 0; lineNumber < delegateGlobals.size(); lineNumber++) {
            delegateGlobal = delegateGlobals.get(lineNumber);
            if (delegateGlobal.getAccountDelegatePrimaryRoutingIndicator()) {
                if (testLineNum == null) {
                    return new Integer(lineNumber);
                }
                else if (!(testLineNum.intValue() == lineNumber)) {
                    return new Integer(lineNumber);
                }
            }
        }

        return null;
    }

    /**
     * 
     * This method validates the rule that says there can be only one PrimaryRoute delegate for each given docType. It checks the
     * delegateGlobalToTest against the list, to determine whether adding this new delegateGlobalToTest would violate any
     * PrimaryRoute business rule violations.
     * 
     * If any of the incoming variables is null or empty, the method will do nothing, and return Null. It will only process the
     * business rules if there is sufficient data to do so.
     * 
     * @param delegateGlobalToTest A delegateGlobal line that you want to test against the list.
     * @param delegateGlobals A List of delegateGlobal items that is being tested against.
     * @return Null if the business rule passes, or an Integer value greater than zero, representing the line that the new line is
     *         conflicting with
     * 
     */
    protected Integer checkPrimaryRoutePerDocType(DelegateGlobalDetail delegateGlobalToTest, List<DelegateGlobalDetail> delegateGlobals, Integer testLineNum) {

        // exit immediately if the adding line isnt a Primary routing
        if (delegateGlobalToTest == null || delegateGlobals == null || delegateGlobals.isEmpty()) {
            return null;
        }
        if (!delegateGlobalToTest.getAccountDelegatePrimaryRoutingIndicator()) {
            return null;
        }
        if (StringUtils.isBlank(delegateGlobalToTest.getFinancialDocumentTypeCode())) {
            return null;
        }

        // at this point, the delegateGlobal being added is a Primary for ALL docTypes, so we need to
        // test whether any in the existing list are also Primary, regardless of docType
        String docType = delegateGlobalToTest.getFinancialDocumentTypeCode();
        DelegateGlobalDetail delegateGlobal = null;
        for (int lineNumber = 0; lineNumber < delegateGlobals.size(); lineNumber++) {
            delegateGlobal = delegateGlobals.get(lineNumber);
            if (delegateGlobal.getAccountDelegatePrimaryRoutingIndicator()) {
                if (docType.equalsIgnoreCase(delegateGlobal.getFinancialDocumentTypeCode())) {
                    if (testLineNum == null) {
                        return new Integer(lineNumber);
                    }
                    else if (!(testLineNum.intValue() == lineNumber)) {
                        return new Integer(lineNumber);
                    }
                }
            }
        }

        return null;
    }

    protected boolean checkPrimaryRouteRules(List<DelegateGlobalDetail> delegateGlobals, DelegateGlobalDetail delegateGlobalToTest, Integer lineNum, boolean add) {
        boolean success = true;

        String errorPath = "";
        Integer result = null;
        for (DelegateGlobalDetail delegateGlobal : delegateGlobals) {
            
            result = checkPrimaryRoutePerDocType(delegateGlobalToTest, delegateGlobals, lineNum);
            if (result != null) {
                if (add) {
                    errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "financialDocumentTypeCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_DELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE);
                }
                else {
                    errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum.toString() + "]." + "financialDocumentTypeCode";
                    putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_DELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE);
                }
                success &= false;
            }
        }
        return success;
    }

    protected boolean checkDelegateUserRules(DelegateGlobalDetail delegateGlobal, int lineNum, boolean add) {

        boolean success = true;

        String errorPath = KFSConstants.EMPTY_STRING;
        // user must exist
        if (delegateGlobal.getAccountDelegate() == null) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "accountDelegate.personUserIdentifier";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_DOESNT_EXIST);
            }
            else {
                errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "accountDelegate.personUserIdentifier";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_DOESNT_EXIST);
            }
            success &= false;
            return success;
        }
        UniversalUser user = delegateGlobal.getAccountDelegate();

        // user must be of the allowable statuses (A - Active)
        if (apcRuleFails(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ACCOUNT_DELEGATE, KFSConstants.ChartApcParms.DELEGATE_USER_EMP_STATUSES, user.getEmployeeStatusCode())) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "accountDelegate.personUserIdentifier";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE);
            }
            else {
                errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "accountDelegate.personUserIdentifier";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE);
            }
            success &= false;
        }

        // user must be of the allowable types (P - Professional)
        if (apcRuleFails(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ACCOUNT_DELEGATE, KFSConstants.ChartApcParms.DELEGATE_USER_EMP_TYPES, user.getEmployeeTypeCode())) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "accountDelegate.personUserIdentifier";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_PROFESSIONAL);
            }
            else {
                errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "accountDelegate.personUserIdentifier";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_PROFESSIONAL);
            }
            success &= false;
        }

        return success;
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
        } else if ( bo instanceof DelegateGlobalDetail ) {
            DelegateGlobalDetail detail = (DelegateGlobalDetail)bo;
            detail.refreshNonUpdateableReferences();        
            KualiDecimal fromAmount = detail.getApprovalFromThisAmount();
            KualiDecimal toAmount = detail.getApprovalToThisAmount();

            // FROM amount must be >= 0 (may not be negative)
            success &= checkDelegateFromAmtGreaterThanEqualZero(fromAmount, 0, true);

            // from cannot be a valid value and toAmount cannot be null
            success &= checkDelegateForNullToAmount(fromAmount, toAmount, 0, true);

            // TO amount must be >= FROM amount or Zero
            success &= checkDelegateToAmtGreaterThanFromAmt(fromAmount, toAmount, 0, true);

            // check the user that is being added
            // TODO: add back in once the user issues have been fixed
            success &= checkDelegateUserRules(detail, 0, true);

            // check the routing
            success &= checkPrimaryRouteRules(newDelegateGlobal.getDelegateGlobals(), detail, null, true);

        }
        return success;
    }
    
}
