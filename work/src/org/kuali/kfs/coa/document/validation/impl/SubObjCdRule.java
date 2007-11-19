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

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubObjCd;

public class SubObjCdRule extends MaintenanceDocumentRuleBase {

    private static final String ACCOUNT_ORG_RULE_KEY = "SubObjectCode.AccountOrgsAllowingClosedAccounts";

    private SubObjCd oldSubObjectCode;
    private SubObjCd newSubObjectCode;

    public SubObjCdRule() {
        super();
    }

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link SubObjCdRule#checkExistenceAndActive()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");

        // check that all sub-objects whose keys are specified have matching objects in the db
        checkExistenceAndActive();

        return true;
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link SubObjCdRule#checkExistenceAndActive()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        // check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        return success;
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link SubObjCdRule#checkExistenceAndActive()}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        // check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        return success;
    }

    /**
     * This method sets the convenience objects like newSubObjectCode and oldSubObjectCode, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldSubObjectCode = (SubObjCd) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubObjectCode = (SubObjCd) super.getNewBo();
    }

    /**
     * 
     * This checks that the account on the sub object code is not closed
     * @return false if the account is closed
     */
    protected boolean checkExistenceAndActive() {

        LOG.info("Entering checkExistenceAndActive()");
        boolean success = true;

        // disallow closed accounts unless in certain orgs
        if (ObjectUtils.isNotNull(newSubObjectCode.getAccount())) {
            Account account = newSubObjectCode.getAccount();

            // if the account is closed
            if (account.isAccountClosedIndicator()) {
                putFieldError("accountNumber", KFSKeyConstants.ERROR_DOCUMENT_SUBOBJECTMAINT_ACCOUNT_MAY_NOT_BE_CLOSED);
                success &= false;
            }
        }
        return success;
    }

}
