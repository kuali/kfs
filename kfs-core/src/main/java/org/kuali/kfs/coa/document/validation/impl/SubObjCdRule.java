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
package org.kuali.kfs.coa.document.validation.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class SubObjCdRule extends MaintenanceDocumentRuleBase {

    protected static final String ACCOUNT_ORG_RULE_KEY = "SubObjectCode.AccountOrgsAllowingClosedAccounts";

    protected SubObjectCode oldSubObjectCode;
    protected SubObjectCode newSubObjectCode;

    public SubObjCdRule() {
        super();
    }

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link SubObjCdRule#checkExistenceAndActive()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.debug("Entering processCustomApproveDocumentBusinessRules()");

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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");

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
        oldSubObjectCode = (SubObjectCode) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubObjectCode = (SubObjectCode) super.getNewBo();
    }

    /**
     * 
     * This checks that the account on the sub object code is not closed
     * @return false if the account is closed
     */
    protected boolean checkExistenceAndActive() {

        LOG.debug("Entering checkExistenceAndActive()");
        boolean success = true;

        // disallow closed accounts unless in certain orgs
        if (ObjectUtils.isNotNull(newSubObjectCode.getAccount())) {
            Account account = newSubObjectCode.getAccount();

            // if the account is closed
            if (!account.isActive()) {
                putFieldError("accountNumber", KFSKeyConstants.ERROR_DOCUMENT_SUBOBJECTMAINT_ACCOUNT_MAY_NOT_BE_CLOSED);
                success &= false;
            }
        }
        return success;
    }

}
