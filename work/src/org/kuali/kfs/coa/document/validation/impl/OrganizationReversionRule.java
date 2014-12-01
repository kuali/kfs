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

import java.util.List;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionDetail;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class implements the business rules specific to the {@link OrganizationReversion} Maintenance Document.
 */
public class OrganizationReversionRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionRule.class);

    protected OrganizationReversion oldOrgReversion;
    protected OrganizationReversion newOrgReversion;

    /**
     * No-Args Constructor for an OrganizationReversionRule.
     */
    public OrganizationReversionRule() {

    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link OrganizationReversionRule#validateDetailBusinessObjects(OrganizationReversion)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        // make sure its a valid organization reversion MaintenanceDocument
        if (!isCorrectMaintenanceClass(document, OrganizationReversion.class)) {
            throw new IllegalArgumentException("Maintenance Document passed in was of the incorrect type.  Expected " + "'" + OrganizationReversion.class.toString() + "', received " + "'" + document.getOldMaintainableObject().getBoClass().toString() + "'.");
        }

        // get the real business object
        newOrgReversion = (OrganizationReversion) document.getNewMaintainableObject().getBusinessObject();

        // add check to validate document recursively to get to the collection attributes
        success &= validateDetailBusinessObjects(newOrgReversion);

        return success;
    }

    /**
     * Tests each option attached to the main business object and validates its properties.
     * 
     * @param orgReversion
     * @return false if any of the detail objects fail with their validation
     */
    protected boolean validateDetailBusinessObjects(OrganizationReversion orgReversion) {
        GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");
        List<OrganizationReversionDetail> details = orgReversion.getOrganizationReversionDetail();
        int index = 0;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        for (OrganizationReversionDetail dtl : details) {
            String errorPath = "organizationReversionDetail[" + index + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            validateOrganizationReversionDetail(dtl);
            validateOrganizationReversionCode(orgReversion, dtl);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            index++;
        }
        GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");
        return GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;
    }
    
    /**
     * 
     * This checks to make sure that the organization reversion object on the detail object actually exists
     * @param detail
     * @return false if the organization reversion object doesn't exist
     */
    protected boolean validateOrganizationReversionDetail(OrganizationReversionDetail detail) {
        
        // let's assume this detail will pass the rule
        boolean result = true;
        
        // 1. makes sure the financial object code exists
        detail.refreshReferenceObject("organizationReversionObject");
        if (ObjectUtils.isNull(detail.getOrganizationReversionObject())) {
            LOG.debug("organization reversion finanical object = null");
            result = false;
            GlobalVariables.getMessageMap().putError("organizationReversionObjectCode", KFSKeyConstants.ERROR_EXISTENCE, new String[] { "Financial Object Code: " + detail.getOrganizationReversionObjectCode() });
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("organization reversion finanical object = " + detail.getOrganizationReversionObject().getName());            
            }
        }
        return result;
    }

    /**
     * 
     * Verifies that a reversion code exists when the 
     * "Carry Forward by Object Code" indicator is selected.  If this indicator
     * isn't selected, then the reversion codes isn't required.
     * 
     * @param reversion OrganizationReversion object
     * @param detail OrganizationReversionDetail object
     * 
     * @return true for successful validation
     */
    protected boolean validateOrganizationReversionCode(OrganizationReversion reversion, OrganizationReversionDetail detail) {
        
        //
        // Assume it will pass!
        //
        boolean result = true;
        
        //
        // Only need to verify that organization reversion code exists if the
        // "Carry Forward by Object Code Indicator" is not selected.
        //
        if (reversion.isCarryForwardByObjectCodeIndicator()) {
            if (ObjectUtils.isNull(detail.getOrganizationReversionCode())) {
                result = false;
                GlobalVariables.getMessageMap().putError("organizationReversionCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_NO_REVERSION_CODE);
            }
        }
        return result;
    }
}
