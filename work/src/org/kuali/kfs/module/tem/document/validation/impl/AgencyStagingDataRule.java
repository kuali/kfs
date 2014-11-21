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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.document.service.AgencyStagingDataRuleHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * Business rules validation for the Travel Agency Audit and Correction
 */
public class AgencyStagingDataRule extends MaintenanceDocumentRuleBase {

    protected AgencyStagingDataRuleHelper validationByTripHelper;
    protected AgencyStagingDataRuleHelper validationByTravelerHelper;

    public AgencyStagingDataRule() {
        setValidationByTripHelper(SpringContext.getBean(AgencyStagingDataRuleByTrip.class));
        setValidationByTravelerHelper(SpringContext.getBean(AgencyStagingDataRuleByTraveler.class));
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomSaveDocumentBusinessRules(document);
        result &= getValidationHelper().processCustomSaveDocumentBusinessRules(document);
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);
        result &= getValidationHelper().processCustomRouteDocumentBusinessRules(document);
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomApproveDocumentBusinessRules(document);
        result &= getValidationHelper().processCustomApproveDocumentBusinessRules(document);
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean result = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        result &= getValidationHelper().processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        return result;
    }

    protected AgencyStagingDataRuleHelper getValidationHelper() {
        final AgencyStagingData data = (AgencyStagingData) getNewBo();
        if (data.getImportBy().equals(ExpenseImportTypes.IMPORT_BY_TRAVELLER)) {
            return getValidationByTravelerHelper();
        }
        return getValidationByTripHelper();
    }

    /**
     * Gets the validationByTravelerHelper attribute.
     * @return Returns the validationByTravelerHelper.
     */
    public AgencyStagingDataRuleHelper getValidationByTravelerHelper() {
        return validationByTravelerHelper;
    }

    /**
     * Sets the validationByTravelerHelper attribute value.
     * @param validationByTravelerHelper The validationByTravelerHelper to set.
     */
    public void setValidationByTravelerHelper(final AgencyStagingDataRuleHelper validationByTravelerHelper) {
        this.validationByTravelerHelper = validationByTravelerHelper;
    }

    /**
     * Gets the validationByTripHelper attribute.
     * @return Returns the validationByTripHelper.
     */
    public AgencyStagingDataRuleHelper getValidationByTripHelper() {
        return validationByTripHelper;
    }

    /**
     * Sets the validationByTripHelper attribute value.
     * @param validationByTripHelper The validationByTripHelper to set.
     */
    public void setValidationByTripHelper(final AgencyStagingDataRuleHelper validationByTripHelper) {
        this.validationByTripHelper = validationByTripHelper;
    }

}
