/*
 * Copyright 2006 The Kuali Foundation
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates content of a <code>{@link AccountDelegate}</code> maintenance document upon triggering of a approve, save, or route
 * event.
 */
public class DelegateRule extends KfsMaintenanceDocumentRuleBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateRule.class);

    protected AccountDelegate oldDelegate;
    protected AccountDelegate newDelegate;

    /**
     * This runs specific rules that are called when a document is saved:
     * <ul>
     * <li>{@link DelegateRule#checkSimpleRules()}</li>
     * <li>{@link DelegateRule#checkOnlyOnePrimaryRoute(MaintenanceDocument)}</li>
     * <li>{@link DelegateRule#checkDelegateUserRules(MaintenanceDocument)}</li>
     * </ul>
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     * @return doesn't fail on save, even if sub-rules fail
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        // check simple rules
        checkSimpleRules();

        // disallow more than one PrimaryRoute for a given Chart/Account/Doctype
        checkOnlyOnePrimaryRoute(document);

        // delegate user must be Active and Professional
        checkDelegateUserRules(document);

        return true;
    }

    /**
     * This runs specific rules that are called when a document is routed:
     * <ul>
     * <li>{@link DelegateRule#checkSimpleRules()}</li>
     * <li>{@link DelegateRule#checkOnlyOnePrimaryRoute(MaintenanceDocument)}</li>
     * <li>{@link DelegateRule#checkDelegateUserRules(MaintenanceDocument)}</li>
     * </ul>
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     * @return fails if sub-rules fail
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

        setupConvenienceObjects(document);

        // check simple rules
        boolean success = checkSimpleRules();

        // disallow more than one PrimaryRoute for a given Chart/Account/Doctype
        success &= checkOnlyOnePrimaryRoute(document);

        // delegate user must be Active and Professional
        success &= checkDelegateUserRules(document);

        return success;
    }

    /**
     * This runs specific rules that are called when a document is approved:
     * <ul>
     * <li>{@link DelegateRule#checkSimpleRules()}</li>
     * <li>{@link DelegateRule#checkOnlyOnePrimaryRoute(MaintenanceDocument)}</li>
     * <li>{@link DelegateRule#checkDelegateUserRules(MaintenanceDocument)}</li>
     * </ul>
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomApproveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        // check simple rules
        success &= checkSimpleRules();

        success &= checkOnlyOnePrimaryRoute(document);

        // delegate user must be Active and Professional
        success &= checkDelegateUserRules(document);

        return success;
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     *
     * @param document - the maintenanceDocument being evaluated
     */
    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldDelegate = (AccountDelegate) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newDelegate = (AccountDelegate) super.getNewBo();
    }


    /**
     * This checks to see if
     * <ul>
     * <li>the delegate start date is valid and they are active</li>
     * <li>from amount is >= 0</li>
     * <li>to amount cannot be empty when from amount is filled out</li>
     * <li>to amount is >= from amount</li>
     * <li>account cannot be closed</li>
     * </ul>
     *
     * @return
     */
    protected boolean checkSimpleRules() {
        boolean success = true;

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, newDelegate.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, newDelegate.getAccountNumber());

        int accountExist = getBoService().countMatching(Account.class, fieldValues);
        if (accountExist<=0) {
            putFieldError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, newDelegate.getAccountNumber());
            success &= false;
        }

        // start date must be greater than or equal to today if active
        boolean newActive = newDelegate.isActive();
        if ((ObjectUtils.isNotNull(newDelegate.getAccountDelegateStartDate())) && newActive) {
            Timestamp today = getDateTimeService().getCurrentTimestamp();
            today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime());
            if (newDelegate.getAccountDelegateStartDate().before(today)) {
                putFieldError(KFSPropertyConstants.ACCOUNT_DELEGATE_START_DATE, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_STARTDATE_IN_PAST);
                success &= false;
            }
        }

        // FROM amount must be >= 0 (may not be negative)
        KualiDecimal fromAmount = newDelegate.getFinDocApprovalFromThisAmt();
        if (ObjectUtils.isNotNull(fromAmount)) {
            if (fromAmount.isLessThan(KualiDecimal.ZERO)) {
                putFieldError(KFSPropertyConstants.FIN_DOC_APPROVAL_FROM_THIS_AMT, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE);
                success &= false;
            }
        }

        // TO amount must be >= FROM amount or Zero
        KualiDecimal toAmount = newDelegate.getFinDocApprovalToThisAmount();
        if (ObjectUtils.isNotNull(toAmount) && !toAmount.equals(KualiDecimal.ZERO)) {
            // case if FROM amount is non-null and positive, disallow TO amount being less
            if (fromAmount != null && toAmount.isLessThan(fromAmount)) {
                putFieldError(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                success &= false;
            } else if (toAmount.isLessThan(KualiDecimal.ZERO)) {
                putFieldError(KFSPropertyConstants.FIN_DOC_APPROVAL_TO_THIS_AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                success &= false;
            }
        }

        // do we have a good document type?
        final FinancialSystemDocumentTypeService documentService = SpringContext.getBean(FinancialSystemDocumentTypeService.class);
        if (!documentService.isFinancialSystemDocumentType(newDelegate.getFinancialDocumentTypeCode())) {
            putFieldError(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_INVALID_DOC_TYPE, new String[] { newDelegate.getFinancialDocumentTypeCode(), KFSConstants.ROOT_DOCUMENT_TYPE });
            success = false;
        }

        return success;
    }

    /**
     * This checks to see if there is already a record for the primary route
     *
     * @param document
     * @return false if there is a record
     */
    protected boolean checkOnlyOnePrimaryRoute(MaintenanceDocument document) {

        boolean success = true;
        boolean checkDb = false;
        boolean newPrimary;
        boolean newActive;
        boolean blockingDocumentExists;

        // exit out immediately if this doc is not requesting a primary route
        newPrimary = newDelegate.isAccountsDelegatePrmrtIndicator();
        if (!newPrimary) {
            return success;
        }

        // exit if new document not active
        newActive = newDelegate.isActive();
        if (!newActive) {
            return success;
        }

        // if its a new document, we are only interested if they have chosen this one
        // to be a primary route
        if (document.isNew()) {
            if (newPrimary) {
                checkDb = true;
            }
        }

        // handle an edit, where all we care about is that someone might change it
        // from NOT a primary TO a primary
        if (document.isEdit()) {
            boolean oldPrimary = oldDelegate.isAccountsDelegatePrmrtIndicator();
            if (!oldPrimary && newPrimary) {
                checkDb = true;
            }
        }

        // if we dont want to check the db for another primary, then exit
        if (!checkDb) {
            return success;
        }

        // if a primary already exists for a document type (including ALL), throw an error. However, current business rules
        // should allow a primary for other document types, even if a primary for ALL already exists.

        Map whereMap = new HashMap();
        whereMap.put("chartOfAccountsCode", newDelegate.getChartOfAccountsCode());
        whereMap.put("accountNumber", newDelegate.getAccountNumber());
        whereMap.put("accountsDelegatePrmrtIndicator", Boolean.TRUE);
        whereMap.put("financialDocumentTypeCode", newDelegate.getFinancialDocumentTypeCode());
        whereMap.put("active", Boolean.TRUE);

        // find all the matching records
        Collection primaryRoutes = getBoService().findMatching(AccountDelegate.class, whereMap);

        // if there is at least one result, then this business rule is tripped
        if (primaryRoutes.size() > 0) {
            putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE);
            success &= false;
        }

        return success;
    }

    /**
     * This checks to see if the user is valid and active for this module
     *
     * @param document
     * @return false if this user is not valid or active for being a delegate
     */
    protected boolean checkDelegateUserRules(MaintenanceDocument document) {
        boolean success = true;
        final Person accountDelegate = newDelegate.getAccountDelegate();

        // if the user doesn't exist, then do nothing, it'll fail the existence test elsewhere
        if (ObjectUtils.isNull(accountDelegate)) {
            return success;
        }

        // if the document is inactivating an account delegate, don't bother validating the user
        if (document.getOldMaintainableObject() != null && document.getOldMaintainableObject().getBusinessObject() != null && ((AccountDelegate)document.getOldMaintainableObject().getBusinessObject()).isActive() && !((AccountDelegate)document.getNewMaintainableObject().getBusinessObject()).isActive()) {
            return success;
        }

        if (StringUtils.isBlank(accountDelegate.getEntityId()) || !getDocumentHelperService().getDocumentAuthorizer(document).isAuthorized(document, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.namespace, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.name, accountDelegate.getPrincipalId())) {
            super.putFieldError("accountDelegate.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {accountDelegate.getName(), KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.namespace, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE.name});
            success = false;
        }
        return success;
    }
}
