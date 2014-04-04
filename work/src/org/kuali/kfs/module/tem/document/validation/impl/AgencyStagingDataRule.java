/*
 * Copyright 2011 The Kuali Foundation.
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
