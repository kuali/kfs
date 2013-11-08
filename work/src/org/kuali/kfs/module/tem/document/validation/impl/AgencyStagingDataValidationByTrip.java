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
import static org.kuali.kfs.module.tem.TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_ID;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.document.service.AgencyStagingDataValidationHelper;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Business rules validation for the Travel Agency Audit and Correction using the IU method of
 * importing by trip
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class AgencyStagingDataValidationByTrip implements AgencyStagingDataValidationHelper {
    public static final String MAINTAINABLE_ERROR_PREFIX = KRADConstants.MAINTENANCE_NEW_MAINTAINABLE;
    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String MAINTAINABLE_ERROR_PATH = DOCUMENT_ERROR_PREFIX + "newMaintainableObject";

    protected ExpenseImportByTripService expenseImportByTripService;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(final MaintenanceDocument document) {
        boolean result = processCustomDocumentBusinessRules(document);
        //check validation in order to display error messages, but return true on a save
        return true;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(final MaintenanceDocument document) {
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
     * @see org.kuali.kfs.module.tem.document.service.AgencyStagingDataValidationHelper#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        return true;
    }

    /**
     * Gets the expenseImportByTripService attribute.
     * @return Returns the expenseImportByTripService.
     */
    public ExpenseImportByTripService getExpenseImportByTripService() {
        return expenseImportByTripService;
    }

    /**
     * Sets the expenseImportByTripService attribute value.
     * @param expenseImportByTripService The expenseImportByTripService to set.
     */
    public void setExpenseImportByTripService(final ExpenseImportByTripService expenseImportByTripService) {
        this.expenseImportByTripService = expenseImportByTripService;
    }

    /**
     *
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param errorParameters - list of parameters to include in the error message
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

        if (GlobalVariables.getMessageMap().fieldHasMessage(propertyName, errorConstant)) {
            return true;
        }
        else {
            return false;
        }
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

            List<ErrorMessage> errors = getExpenseImportByTripService().validateMissingAccountingInfo(data);

            if(!errors.isEmpty()) {
                for(ErrorMessage error : errors) {
                    putFieldError(ACCOUNTING_INFO, error.getErrorKey(), error.getMessageParameters());
                }
                result &= false;
            }
            else {
                errors = getExpenseImportByTripService().validateAccountingInfo(data);
                if (!errors.isEmpty()) {
                    for(ErrorMessage error : errors) {
                        putFieldError(ACCOUNTING_INFO, error.getErrorKey(), error.getMessageParameters());
                    }
                    result &= false;
                }
            }

            if(!getExpenseImportByTripService().validateTripId(data).isEmpty()) {
                putFieldError(TRIP_ID, TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID);
                result &= false;
            }

            if (getExpenseImportByTripService().isTripDataMissing(data)) {
                putFieldError(LODGING_NUMBER, TemKeyConstants.MESSAGE_AGENCY_DATA_MISSING_TRIP_DATA);
                result &= false;
            }

            //only check for duplicate data if other fields have been correctly validated
            if (result) {
                errors = getExpenseImportByTripService().validateDuplicateData(data);
                if (!errors.isEmpty()) {
                    if (isErrorListContainsErrorKey(errors, TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS) ||
                            isErrorListContainsErrorKey(errors, TemKeyConstants.MESSAGE_AGENCY_DATA_AIR_LODGING_RENTAL_MISSING)) {
                        result &= false;
                    }
                    else {
                        //figure out which itinerary to display
                        String itineraryData = "";
                        if (StringUtils.isNotEmpty(data.getAirTicketNumber())) {
                            itineraryData = "AIR-"+ data.getAirTicketNumber();
                        }
                        else if (StringUtils.isNotEmpty(data.getLodgingItineraryNumber())) {
                            itineraryData += "LODGING-"+ data.getLodgingItineraryNumber();
                        }
                        else if (StringUtils.isNotEmpty(data.getRentalCarItineraryNumber())) {
                            itineraryData += "RENTAL CAR-"+ data.getRentalCarItineraryNumber();
                        }

                        putFieldError(TRIP_ID, TemKeyConstants.MESSAGE_AGENCY_DATA_TRIP_DUPLICATE_RECORD, data.getTripId(), data.getAgency(), data.getTransactionPostingDate().toString(), data.getTripExpenseAmount().toString(), itineraryData);
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
