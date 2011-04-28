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

import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
/**
 * This class implements the business rules specific to the {@link ProjectCode} Maintenance Document.
 */
public class ProjectCodeRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProjectCodeRule.class);

    protected ProjectCode oldProjectCode;
    protected ProjectCode newProjectCode;

    public ProjectCodeRule() {
        super();
    }

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link ProjectCodeRule#checkExistenceAndActive()}</li>
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
     * <li>{@link ProjectCodeRule#checkExistenceAndActive()}</li>
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
     * <li>{@link ProjectCodeRule#checkExistenceAndActive()}</li>
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
     * This method sets the convenience objects like newProjectCode and oldProjectCode, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldProjectCode = (ProjectCode) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newProjectCode = (ProjectCode) super.getNewBo();
    }

    /**
     * 
     * This method currently doesn't do anything
     * @return true
     */
    protected boolean checkExistenceAndActive() {

        LOG.debug("Entering checkExistenceAndActive()");
        boolean success = true;

        return success;
    }

}
