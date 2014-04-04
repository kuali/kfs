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

import static org.kuali.kfs.module.tem.TemPropertyConstants.TravelAgencyAuditReportFields.ACCOUNTING_INFO;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TravelAgencyAuditReportFields.LODGING_NUMBER;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TravelAgencyAuditReportFields.TRAVELER_DATA;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.service.AgencyStagingDataRuleHelper;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rules validation for the Travel Agency Audit and Correction using the UCD method of importing
 * by traveler
 */
public class AgencyStagingDataRuleByTraveler implements AgencyStagingDataRuleHelper {
    public static final String MAINTAINABLE_ERROR_PREFIX = KRADConstants.MAINTENANCE_NEW_MAINTAINABLE;
    public static final String ADD_LINE_ERROR_PREFIX = KRADConstants.MAINTENANCE_ADD_PREFIX;

    protected ExpenseImportByTravelerService expenseImportByTravelerService;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = processCustomDocumentBusinessRules(document);
        //check validation in order to display error messages, but return true on a save
        return true;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return processCustomDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomApproveDocumentBusinessRules(final MaintenanceDocument document) {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AgencyStagingDataRuleHelper#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean result = true;

        //validate accounting line before it's added to the document
        if (collectionName.equals(TemPropertyConstants.TravelAgencyAuditReportFields.ACCOUNTING_INFO)) {
            TripAccountingInformation accountingLine = (TripAccountingInformation)line;

            Map<String,ErrorMessage> errorMap = getExpenseImportByTravelerService().validateAccountingInfoLine(accountingLine);

            if(!errorMap.isEmpty()) {
                for(String errorProperty : errorMap.keySet()) {
                    ErrorMessage error = errorMap.get(errorProperty);
                    if (ObjectUtils.isNotNull(error)) {
                        putFieldError(ADD_LINE_ERROR_PREFIX + ACCOUNTING_INFO +"."+ errorProperty, error.getErrorKey(), error.getMessageParameters());
                    }
                }
                result &= false;
            }
        }

        return result;
    }

    /**
     * Gets the expenseImportByTravelerService attribute.
     * @return Returns the expenseImportByTravelerService.
     */
    public ExpenseImportByTravelerService getExpenseImportByTravelerService() {
        return expenseImportByTravelerService;
    }

    /**
     * Sets the expenseImportByTravelerService attribute value.
     * @param expenseImportByTravelerService The expenseImportByTravelerService to set.
     */
    public void setExpenseImportByTravelerService(final ExpenseImportByTravelerService expenseImportByTravelerService) {
        this.expenseImportByTravelerService = expenseImportByTravelerService;
    }

    /**
     *
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     *
     */
    protected void putFieldError(String propertyName, String errorConstant, String... errorParameters) {
        if (!errorAlreadyExists(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant, errorParameters);
        }
    }

    /**
     *
     * Convenience method to determine whether the field already has the message indicated.
     *
     * This is useful if you want to suppress duplicate error messages on the same field.
     *
     * @param propertyName - propertyName you want to test on
     * @param errorConstant - errorConstant you want to test
     * @return returns True if the propertyName indicated already has the errorConstant indicated, false otherwise
     *
     */
    protected boolean errorAlreadyExists(String propertyName, String errorConstant) {
        return GlobalVariables.getMessageMap().fieldHasMessage(propertyName, errorConstant);
    }

    /**
     * This method is used by processCustomSaveDocumentBusinessRules and processCustomRouteDocumentBusinessRules in order to
     * have common validation checking
     *
     * @param document - document needing to be validated
     * @return returns true if all validations passed, false otherwise
     */
    protected boolean processCustomDocumentBusinessRules(final MaintenanceDocument document) {
        boolean result = true;
        final AgencyStagingData data = (AgencyStagingData) document.getNewMaintainableObject().getBusinessObject();
        if (data.isActive()) {

            List<ErrorMessage> errorMessages = getExpenseImportByTravelerService().validateMandatoryFieldsPresent(data);
            if (!errorMessages.isEmpty()) {
                result &= false;
            }

            errorMessages = getExpenseImportByTravelerService().validateTraveler(data);
            if (!errorMessages.isEmpty()) {
                putFieldError(TRAVELER_DATA, TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRAVELER, data.getTravelerId());
                result &= false;
            }

            int i = 0;
            for(TripAccountingInformation account : data.getTripAccountingInformation()) {
                Map<String,ErrorMessage> errorMap = getExpenseImportByTravelerService().validateAccountingInfoLine(account);

                if(!errorMap.isEmpty()) {
                    for(String errorProperty : errorMap.keySet()) {
                        ErrorMessage error = errorMap.get(errorProperty);
                        if (ObjectUtils.isNotNull(error)) {
                            putFieldError(ACCOUNTING_INFO +"["+i+"]."+ errorProperty, error.getErrorKey(), error.getMessageParameters());
                            result &= false;
                        }
                    }
                }

                i++;
            }

            if (getExpenseImportByTravelerService().isTripDataMissing(data)) {
                putFieldError(LODGING_NUMBER, TemKeyConstants.MESSAGE_AGENCY_DATA_MISSING_TRIP_DATA);
                result &= false;
            }

            //only check for duplicate data if other fields have been correctly validated
            if (result) {
                errorMessages = getExpenseImportByTravelerService().validateDuplicateData(data);
                if (!errorMessages.isEmpty()) {
                    if (isErrorListContainsErrorKey(errorMessages, TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS) ||
                            isErrorListContainsErrorKey(errorMessages, TemKeyConstants.MESSAGE_AGENCY_DATA_AIR_LODGING_RENTAL_MISSING)) {
                        result &= false;
                    }
                    else {

                        putFieldError(TRAVELER_DATA, TemKeyConstants.MESSAGE_AGENCY_DATA_TRAVELER_DUPLICATE_RECORD, data.getTravelerId(), data.getItineraryDataString(),
                                data.getCreditCardOrAgencyCode(), data.getTransactionPostingDate().toString(), data.getTripExpenseAmount().toString(), data.getTripInvoiceNumber());
                        result &= false;
                    }
                }
            }

        }

        return result;
    }

    protected boolean isErrorListContainsErrorKey(List<ErrorMessage> errors, String errorKey) {
        for(ErrorMessage error : errors) {
            if (error.getErrorKey().equals(errorKey)) {
                return true;
            }
        }
        return false;
    }

}
