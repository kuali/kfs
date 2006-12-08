/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/document/validation/impl/DelegateRule.java,v $
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.DocumentType;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.module.chart.bo.ChartUser;

/**
 * Validates content of a <code>{@link AccountDelegate}</code> maintenance document upon triggering of a 
 * approve, save, or route event.
 * 
 * 
 */
public class DelegateRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateRule.class);

    private Delegate oldDelegate;
    private Delegate newDelegate;

    /**
     * Constructs a DelegateRule.java.
     * 
     */
    public DelegateRule() {
        super();
    }

    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("Entering processCustomSaveDocumentBusinessRules()");
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
     * 
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomRouteDocumentBusinessRules()");
        setupConvenienceObjects(document);

        // check simple rules
        success &= checkSimpleRules();

        // disallow more than one PrimaryRoute for a given Chart/Account/Doctype
        success &= checkOnlyOnePrimaryRoute(document);

        // delegate user must be Active and Professional
        success &= checkDelegateUserRules(document);

        return success;
    }

    /**
     * This method should be overridden to provide custom rules for processing document approval.
     * 
     * @param document
     * @return booelan
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        // check simple rules
        success &= checkSimpleRules();

        success &= checkOnlyOnePrimaryRoute(document);

        // delegate user must be Active and Professional
        success &= checkDelegateUserRules(document);

        return success;
    }

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
    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldDelegate = (Delegate) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newDelegate = (Delegate) super.getNewBo();
    }


    protected boolean checkSimpleRules() {

        boolean success = true;
        boolean newActive;
        KualiDecimal fromAmount = newDelegate.getFinDocApprovalFromThisAmt();
        KualiDecimal toAmount = newDelegate.getFinDocApprovalToThisAmount();
        newActive = newDelegate.isAccountDelegateActiveIndicator();

        // start date must be greater than or equal to today if active
        if ((ObjectUtils.isNotNull(newDelegate.getAccountDelegateStartDate())) && newActive) {
            Timestamp today = getDateTimeService().getCurrentTimestamp();
            today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime());
            if (newDelegate.getAccountDelegateStartDate().before(today)) {
                putFieldError("accountDelegateStartDate", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_STARTDATE_IN_PAST);
                success &= false;
            }
        }

        // FROM amount must be >= 0 (may not be negative)
        if (ObjectUtils.isNotNull(fromAmount)) {
            if (fromAmount.isLessThan(new KualiDecimal(0))) {
                putFieldError("finDocApprovalFromThisAmt", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE);
                success &= false;
            }
        }

        if (ObjectUtils.isNotNull(fromAmount) && ObjectUtils.isNull(toAmount)) {
            putFieldError("finDocApprovalToThisAmount", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
            success &= false;
        }

        // TO amount must be >= FROM amount or Zero
        if (ObjectUtils.isNotNull(toAmount)) {

            if (ObjectUtils.isNull(fromAmount)) {
                // case if FROM amount is null then TO amount must be zero
                if (!toAmount.equals(new KualiDecimal(0))) {
                    putFieldError("finDocApprovalToThisAmount", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                    success &= false;
                }
            }
            else {
                // case if FROM amount is non-null and positive, disallow TO amount being less
                if (toAmount.isLessThan(fromAmount)) {
                    putFieldError("finDocApprovalToThisAmount", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                    success &= false;
                }
            }
        }

        // the account that has been chosen cannot be closed
        Account account = newDelegate.getAccount();
        if (ObjectUtils.isNotNull(account)) {
            if (account.isAccountClosedIndicator()) {
                putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_ACCT_NOT_CLOSED);
                success &= false;
            }
        }

        return success;
    }


    // checks to see if there is already a record
    protected boolean checkOnlyOnePrimaryRoute(MaintenanceDocument document) {

        boolean success = true;
        boolean checkDb = false;
        boolean newPrimary;
        boolean newActive;
        boolean blockingDocumentExists;
        DocumentType documentType;

        // exit out immediately if this doc is not requesting a primary route
        newPrimary = newDelegate.isAccountsDelegatePrmrtIndicator();
        if (!newPrimary) {
            return success;
        }

        // exit if new document not active
        newActive = newDelegate.isAccountDelegateActiveIndicator();
        if (!newActive) {
            return success;
        }

        // exit if document group corresponding to document type = "EX"
        documentType = newDelegate.getDocumentType();
        if (ObjectUtils.isNotNull(documentType)) {
            if ((documentType.getFinancialDocumentGroupCode()).equals("EX")) {
                return success;
            }
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

        // okay, so if we get here, then the new value wants to be a primary,
        // and if an edit, its changed from the old value. in other words,
        // we need to check it.

        /*
         * check if there is an ALL primary, if so then the rule fails, we're done
         * 
         * check if this doctype is already defined with a primary, if so then the rule fails, we're done
         * 
         */

        // **********************************************
        // TESTING FOR AN ALL PRIMARY IN THE DB
        //
        // WHERE = an ALL primary route for this account/chart
        Map whereMap;
        whereMap = new HashMap();
        whereMap.put("chartOfAccountsCode", newDelegate.getChartOfAccountsCode());
        whereMap.put("accountNumber", newDelegate.getAccountNumber());
        whereMap.put("financialDocumentTypeCode", "ALL");
        whereMap.put("accountsDelegatePrmrtIndicator", Boolean.valueOf(true));
        whereMap.put("accountDelegateActiveIndicator", Boolean.valueOf(true));

        // find all the matching records
        Collection primaryRoutes;
        primaryRoutes = getBoService().findMatching(Delegate.class, whereMap);

        // if there is at least one result, then this business rule is tripped
        if (primaryRoutes.size() > 0) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALL_TYPES_ALREADY_EXISTS);
            success &= false;
            return success; // we're done, no sense in continuing
        }
        //
        // **********************************************

        // **********************************************
        // TESTING FOR ANY PRIMARY IF THIS IS ALL
        //
        if ("ALL".equalsIgnoreCase(newDelegate.getFinancialDocumentTypeCode())) {

            // WHERE = any primary route for this account/chart
            whereMap = new HashMap();
            whereMap.put("chartOfAccountsCode", newDelegate.getChartOfAccountsCode());
            whereMap.put("accountNumber", newDelegate.getAccountNumber());
            whereMap.put("accountsDelegatePrmrtIndicator", Boolean.valueOf(true));
            whereMap.put("accountDelegateActiveIndicator", Boolean.valueOf(true));

            // find all the matching records
            primaryRoutes = getBoService().findMatching(Delegate.class, whereMap);

            // if there is at least one result, then this business rule is tripped
            if (primaryRoutes.size() > 0) {

                // get the docType of the primary route that is blocking this
                String blockingDocType = "";
                blockingDocumentExists = false;
                for (Iterator iter = primaryRoutes.iterator(); iter.hasNext();) {
                    Delegate delegate = (Delegate) iter.next();

                    // don't consider as blocking if document group corresponding to document type = "EX"
                    documentType = delegate.getDocumentType();
                    if (ObjectUtils.isNotNull(documentType)) {
                        if (!(documentType.getFinancialDocumentGroupCode()).equals("EX")) {
                            blockingDocumentExists = true;
                            blockingDocType = delegate.getFinancialDocumentTypeCode();
                        }
                    }
                }

                // add the error if blocking document found

                if (blockingDocumentExists == true) {
                    putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_NEW_ALL, blockingDocType);
                    success &= false;
                    return success; // we're done, no sense in continuing
                }
            }
        }
        //
        // **********************************************

        // **********************************************
        // TESTING FOR SAME DOCTYPE PRIMARY IF THIS IS NOT ALL
        else {

            // WHERE = primary route for this docType for this account/chart
            whereMap = new HashMap();
            whereMap.put("chartOfAccountsCode", newDelegate.getChartOfAccountsCode());
            whereMap.put("accountNumber", newDelegate.getAccountNumber());
            whereMap.put("accountsDelegatePrmrtIndicator", Boolean.valueOf(true));
            whereMap.put("financialDocumentTypeCode", newDelegate.getFinancialDocumentTypeCode());
            whereMap.put("accountDelegateActiveIndicator", Boolean.valueOf(true));

            // find all the matching records
            primaryRoutes = getBoService().findMatching(Delegate.class, whereMap);

            // if there is at least one result, then this business rule is tripped
            if (primaryRoutes.size() > 0) {
                putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE);
                success &= false;
                return success; // we're done, no sense in continuing
            }
        }
        return success;
    }

    protected boolean checkDelegateUserRules(MaintenanceDocument document) {

        boolean success = true;

        // if the user doesnt exist, then do nothing, it'll fail the existence test elsewhere
        if (ObjectUtils.isNull(newDelegate.getAccountDelegate())) {
            return success;
        }
        UniversalUser user = newDelegate.getAccountDelegate();

        // user must be an active kuali user
        if (!user.isActiveForModule( ChartUser.MODULE_ID ) ) {
            success = false;
            putFieldError("accountDelegate.personUserIdentifier", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE_KUALI_USER);
        }
        
        // user must be of the allowable statuses (A - Active)
        if (apcRuleFails(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, Constants.ChartApcParms.DELEGATE_USER_EMP_STATUSES, user.getEmployeeStatusCode())) {
            success = false;
            putFieldError("accountDelegate.personUserIdentifier", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE);
        }
        
        // user must be of the allowable types (P - Professional)
        if (apcRuleFails(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, Constants.ChartApcParms.DELEGATE_USER_EMP_TYPES, user.getEmployeeTypeCode())) {
            success = false;
            putFieldError("accountDelegate.personUserIdentifier", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_PROFESSIONAL);
        }

        return success;
    }
}
