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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.AccountDelegateGlobal;
import org.kuali.kfs.coa.businessobject.AccountDelegateGlobalDetail;
import org.kuali.kfs.coa.businessobject.AccountGlobalDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class executes specific rules for the {@link DelegateGlobalMaintenanceDocument}
 */
public class DelegateGlobalRule extends GlobalDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateGlobalRule.class);

    protected static final KualiDecimal ZERO = KualiDecimal.ZERO;
    protected AccountDelegateGlobal newDelegateGlobal;
    protected static final String DELEGATE_GLOBALS_PREFIX = "delegateGlobals";
    protected static ParameterService parameterService;

    public DelegateGlobalRule() {
        super();
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     */
    @Override
    public void setupConvenienceObjects() {

        // setup newDelegateGlobal convenience objects,
        // make sure all possible sub-objects are populated
        newDelegateGlobal = (AccountDelegateGlobal) super.getNewBo();

        // forces refreshes on all the sub-objects in the lists
        for (AccountDelegateGlobalDetail delegateGlobal : newDelegateGlobal.getDelegateGlobals()) {
            delegateGlobal.refreshNonUpdateableReferences();
        }
        for (AccountGlobalDetail accountGlobalDetail : newDelegateGlobal.getAccountGlobalDetails()) {
            accountGlobalDetail.refreshNonUpdateableReferences();
        }
    }

    /**
     * This checks some basic rules for document approval Specifically it calls the following:
     * <ul>
     * <li>{@link DelegateGlobalRule#checkSimpleRulesAllLines()}</li>
     * <li>{@link DelegateGlobalRule#checkOnlyOneChartErrorWrapper(List)}</li>
     * <li>{@link DelegateGlobalRule#checkForPrimaryDelegateAllLines()}</li>
     * </ul>
     * fails if any rules fail
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects();
        // check simple rules
        success &= checkSimpleRulesAllLines(document);

        success &= checkOnlyOneChartErrorWrapper(newDelegateGlobal.getAccountGlobalDetails());

        // check for primary routing
        success &= checkForPrimaryDelegateAllLines();
        return success;
    }

    /**
     * This checks some basic rules for document routing Specifically it calls the following:
     * <ul>
     * <li>{@link DelegateGlobalRule#checkSimpleRulesAllLines()}</li>
     * <li>{@link DelegateGlobalRule#checkAccountDetails(List)}</li>
     * <li>{@link DelegateGlobalRule#checkForPrimaryDelegateAllLines()}</li>
     * </ul>
     * fails if any rules fail
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects();
        // check simple rules
        success &= checkSimpleRulesAllLines(document);

        success &= checkAccountDetails(newDelegateGlobal.getAccountGlobalDetails());

        // check for primary routing
        success &= checkForPrimaryDelegateAllLines();
        return success;
    }

    /**
     * This checks some basic rules for document saving Specifically it calls the following:
     * <ul>
     * <li>{@link DelegateGlobalRule#checkSimpleRulesAllLines()}</li>
     * <li>{@link DelegateGlobalRule#checkOnlyOneChartErrorWrapper(List)}</li>
     * <li>{@link DelegateGlobalRule#checkForPrimaryDelegateAllLines()}</li>
     * </ul>
     * fails if any rules fail
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        // check simple rules
        checkSimpleRulesAllLines(document);

        checkOnlyOneChartErrorWrapper(newDelegateGlobal.getAccountGlobalDetails());

        // check for primary routing
        checkForPrimaryDelegateAllLines();
        return true;
    }

    /**
     * This checks to see if there are any accounts in the details collection if there are then it calls
     * {@link DelegateGlobalRule#checkAccountDetails(AccountGlobalDetail)}
     *
     * @param details - collection of {@link AccountGlobalDetail}s
     * @return false if there are no objects in the collection or any one of the {@link AccountGlobalDetail} fail
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
     * This checks to make sure that each {@link AccountGlobalDetail} has a valid {@link Account}
     *
     * @param dtl - the {@link AccountGlobalDetail}
     * @return false if it does not have a valid {@link Account}
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
     * This method checks the simple rules for all lines at once and gets called on save, submit, etc. but not on add Specifically
     * it calls the following:
     * <ul>
     * <li>{@link DelegateGlobalRule#checkDelegateUserRules(DelegateGlobalDetail, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkDelegateForNullToAmount(KualiDecimal, KualiDecimal, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkDelegateToAmtGreaterThanFromAmt(KualiDecimal, KualiDecimal, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkDelegateFromAmtGreaterThanEqualZero(KualiDecimal, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkPrimaryRouteRules(List, DelegateGlobalDetail, Integer, boolean)}</li>
     * </ul>
     *
     * @return
     */
    protected boolean checkSimpleRulesAllLines(MaintenanceDocument document) {

        boolean success = true;
        // check if there are any accounts
        if (newDelegateGlobal.getDelegateGlobals().size() == 0) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.DELEGATE_GLOBALS + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_DELEGATE);
            success = false;
        }
        else {
            // check each delegate
            int i = 0;
            final FinancialSystemDocumentTypeService documentService = SpringContext.getBean(FinancialSystemDocumentTypeService.class);
            for (AccountDelegateGlobalDetail newDelegateGlobalDetail : newDelegateGlobal.getDelegateGlobals()) {
                KualiDecimal fromAmount = newDelegateGlobalDetail.getApprovalFromThisAmount();
                KualiDecimal toAmount = newDelegateGlobalDetail.getApprovalToThisAmount();

                success &= checkDelegateUserRules(document, newDelegateGlobalDetail, i, false);

                // FROM amount must be >= 0 (may not be negative)
                success &= checkDelegateFromAmtGreaterThanEqualZero(fromAmount, i, false);

                // TO amount must be >= FROM amount or Zero
                success &= checkDelegateToAmtGreaterThanFromAmt(fromAmount, toAmount, i, false);

                success &= checkDelegateDocumentTypeCode(newDelegateGlobalDetail.getFinancialDocumentTypeCode(), documentService);

                // increment counter for delegate changes list
                i++;
            }
            //KFSCNTRB-1730- check total number doesn't exceed parameter
            String maxAccountDelegatesString = getParameterService().getParameterValueAsString(AccountDelegateGlobal.class, KFSConstants.ChartApcParms.MAXIMUM_ACCOUNT_DELEGATES);
            if(maxAccountDelegatesString != null && !maxAccountDelegatesString.isEmpty()){
                int maxAccountDelegates = Integer.parseInt(maxAccountDelegatesString);
                int sumAccountDelegates = newDelegateGlobal.getDelegateGlobals().size() * newDelegateGlobal.getAccountGlobalDetails().size();
                if (sumAccountDelegates > maxAccountDelegates) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_MAXIMUM_ACCOUNT_DELEGATES, maxAccountDelegatesString, Integer.toString(sumAccountDelegates));
                    success = false;
                }
            }
        }
        return success;
    }


    /**
     * This method will check through each delegate referenced in the DelegateGlobal to ensure that there is one and only primary
     * for each account and doctype
     *
     * @return false if there is more than one primary delegate
     */
    protected boolean checkForPrimaryDelegateAllLines() {
        boolean success = true;
        int i = 0;
        for (AccountDelegateGlobalDetail newDelegateGlobalDetail : newDelegateGlobal.getDelegateGlobals()) {
            success &= checkPrimaryRouteRules(newDelegateGlobal.getDelegateGlobals(), newDelegateGlobalDetail, new Integer(i), false);
            i++;
        }
        return success;
    }

    /**
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

            if (!ObjectUtils.isNull(fromAmount)) {
                // case if FROM amount is non-null and positive, disallow TO amount being less if it is not ZERO (another indicator
                // of infinity)
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

            if (toAmount.isLessThan(KualiDecimal.ZERO)) {
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
        return success;
    }

    /**
     * Validates the document type code for the delegate, to make sure it is a Financial System document type code
     *
     * @param documentTypeCode the document type code to check
     * @param delegateService a helpful instance of the delegate service, so new ones don't have to be created all the time
     * @return true if the document type code is valid, false otherwise
     */
    protected boolean checkDelegateDocumentTypeCode(String documentTypeCode, FinancialSystemDocumentTypeService documentService) {
        if (!documentService.isFinancialSystemDocumentType(documentTypeCode)) {
            String errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE;
            putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_INVALID_DOC_TYPE, new String[] { documentTypeCode, KFSConstants.ROOT_DOCUMENT_TYPE });

            return false;
        }
        return true;
    }

    /**
     * This method validates the rule that says there can be only one PrimaryRoute delegate for each given docType. It checks the
     * delegateGlobalToTest against the list, to determine whether adding this new delegateGlobalToTest would violate any
     * PrimaryRoute business rule violations. If any of the incoming variables is null or empty, the method will do nothing, and
     * return Null. It will only process the business rules if there is sufficient data to do so.
     *
     * @param delegateGlobalToTest A delegateGlobal line that you want to test against the list.
     * @param delegateGlobals A List of delegateGlobal items that is being tested against.
     * @return Null if the business rule passes, or an Integer value greater than zero, representing the line that the new line is
     *         conflicting with
     */
    protected Integer checkPrimaryRoutePerDocType(AccountDelegateGlobalDetail delegateGlobalToTest, List<AccountDelegateGlobalDetail> delegateGlobals, Integer testLineNum) {

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
        AccountDelegateGlobalDetail delegateGlobal = null;
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

    /**
     * This checks that the primary routing for delegates is correct, specifically that - there is not already a primary route
     * delegate setup for this {@link Account}
     *
     * @param delegateGlobals
     * @param delegateGlobalToTest
     * @param lineNum
     * @param add
     * @return
     */
    protected boolean checkPrimaryRouteRules(List<AccountDelegateGlobalDetail> delegateGlobals, AccountDelegateGlobalDetail delegateGlobalToTest, Integer lineNum, boolean add) {
        boolean success = true;

        String errorPath = "";
        Integer result = null;
        for (AccountDelegateGlobalDetail delegateGlobal : delegateGlobals) {

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

    /**
     * This checks that the delegate for this {@link Account} exists and is valid (active and a professional)
     *
     * @param delegateGlobal
     * @param lineNum
     * @param add
     * @return false if the delegate for the {@link Account} doesn't exist or isn't valid
     */
    protected boolean checkDelegateUserRules(MaintenanceDocument document, AccountDelegateGlobalDetail delegateGlobal, int lineNum, boolean add) {
        boolean success = true;
        String errorPath = KFSConstants.EMPTY_STRING;

        Person accountDelegate = delegateGlobal.getAccountDelegate();
        if (StringUtils.isBlank(delegateGlobal.getAccountDelegateUniversalId()) || ObjectUtils.isNull(accountDelegate)) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "accountDelegate.principalName";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_DOESNT_EXIST);
            }
            else {
                errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "accountDelegate.principalName";
                putFieldError(errorPath, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_DOESNT_EXIST);
            }

            success &= false;
        }
        else if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorized(document, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.namespace, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.name, accountDelegate.getPrincipalId())) {
            if (add) {
                errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + DELEGATE_GLOBALS_PREFIX + "." + "accountDelegate.principalName";
                putFieldError(errorPath, KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] { accountDelegate.getName(), KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.namespace, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.name });
            }
            else {
                errorPath = DELEGATE_GLOBALS_PREFIX + "[" + lineNum + "]." + "accountDelegate.principalName";
                putFieldError(errorPath, KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] { accountDelegate.getName(), KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.namespace, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.name });
            }

            success &= false;
        }

        return success;
    }

    /**
     * This checks that when a new line is added (either {@link AccountGlobalDetail} or {@link DelegateGlobalDetail}) that the
     * appropriate rules are run on the new lines being added on {@link AccountGlobalDetail}: - make sure that the account number
     * and chart are entered
     * <ul>
     * <li>{@link DelegateGlobalRule#checkAccountDetails(AccountGlobalDetail)}</li>
     * </ul>
     * on {@link DelegateGlobalDetail}
     * <ul>
     * <li>{@link DelegateGlobalRule#checkDelegateFromAmtGreaterThanEqualZero(KualiDecimal, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkDelegateForNullToAmount(KualiDecimal, KualiDecimal, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkDelegateToAmtGreaterThanFromAmt(KualiDecimal, KualiDecimal, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkDelegateUserRules(DelegateGlobalDetail, int, boolean)}</li>
     * <li>{@link DelegateGlobalRule#checkPrimaryRouteRules(List, DelegateGlobalDetail, Integer, boolean)}</li>
     * </ul>
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
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
        else if (bo instanceof AccountDelegateGlobalDetail) {
            AccountDelegateGlobalDetail detail = (AccountDelegateGlobalDetail) bo;
            detail.refreshNonUpdateableReferences();
            KualiDecimal fromAmount = detail.getApprovalFromThisAmount();
            KualiDecimal toAmount = detail.getApprovalToThisAmount();

            // FROM amount must be >= 0 (may not be negative)
            success &= checkDelegateFromAmtGreaterThanEqualZero(fromAmount, 0, true);

            // TO amount must be >= FROM amount or Zero
            success &= checkDelegateToAmtGreaterThanFromAmt(fromAmount, toAmount, 0, true);

            // check the user that is being added
            // TODO: add back in once the user issues have been fixed
            success &= checkDelegateUserRules(document, detail, 0, true);

            // check the routing
            success &= checkPrimaryRouteRules(newDelegateGlobal.getDelegateGlobals(), detail, null, true);

            final FinancialSystemDocumentTypeService documentService = SpringContext.getBean(FinancialSystemDocumentTypeService.class);
            success &= checkDelegateDocumentTypeCode(detail.getFinancialDocumentTypeCode(), documentService);

        }
        return success;
    }

    
    public ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
}
