/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeAmountSummation;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.TravelerType;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Rules for the ExpenseTypeObjectCode maintenance document
 */
public class ExpenseTypeObjectCodeRule extends MaintenanceDocumentRuleBase {

    /**
     * Overridden to make further checks on the new ExpenseTypeObjectCode
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        final ExpenseTypeObjectCode bo = (ExpenseTypeObjectCode)document.getNewMaintainableObject().getBusinessObject();
        result &= checkValidValues(bo);
        result &= validMaximumAmount(bo);
        result &= validSummationCode(bo);
        result &= validTripTypeByDocumentType(bo);
        result &= canExpenseTypeObjectCodeBeChosen(bo);
        if (document.isNew()) {
            result &= checkRecordIsUnique(bo);
        }

        return result;
    }

    /**
     * Checks that the expense type object code has valid values.  This is complicated by a couple of things...
     * First, the document type is already validated, but really, we want a harsh subset: TT, TRV, TA, TR, ENT, and RELO
     * Also, the traveler type and trip type allow "All" and normal values, but no values beyond that
     * @param expenseTypeObjectCode
     * @return
     */
    protected boolean checkValidValues(ExpenseTypeObjectCode expenseTypeObjectCode) {
       boolean success = true;
       if (!validDocumentType(expenseTypeObjectCode.getDocumentTypeName())) {
           success = false;
           final String label = getDataDictionaryService().getAttributeLabel(ExpenseTypeObjectCode.class, KFSPropertyConstants.DOCUMENT_TYPE_NAME);
           GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT_TYPE_NAME, KFSKeyConstants.ERROR_EXISTENCE, label);
       }
       if (!validTripType(expenseTypeObjectCode.getTripTypeCode())) {
           success = false;
           final String label = getDataDictionaryService().getAttributeLabel(ExpenseTypeObjectCode.class, TemPropertyConstants.TRIP_TYPE_CODE);
           GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRIP_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
       }
       if (!validTravelerType(expenseTypeObjectCode.getTravelerTypeCode())) {
           success = false;
           final String label = getDataDictionaryService().getAttributeLabel(ExpenseTypeObjectCode.class, TemPropertyConstants.TRAVELER_TYPE_CODE);
           GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRAVELER_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
       }
       return success;
    }

    /**
     * Determines if the given document type name is a valid document type to associate with an ExpenseTypeObjectCode record
     * @param documentTypeName the document type name to validate
     * @return true if the document type name is valid, false otherwise
     */
    protected boolean validDocumentType(String documentTypeName) {
        if (TemConstants.TravelDocTypes.TEM_TRANSACTIONAL_DOCUMENT.equals(documentTypeName)) {
            return true;
        }
        if (TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT.equals(documentTypeName)) {
            return true;
        }
        if (TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT.equals(documentTypeName)) {
            return true;
        }
        if (TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT.equals(documentTypeName)) {
            return true;
        }
        if (TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT.equals(documentTypeName)) {
            return true;
        }
        if (TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(documentTypeName)) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the given trip type code matches with a known TripType record or is "All"
     * @param tripTypeCode the trip type code to validate
     * @return true if the trip type code is valid, false otherwise
     */
    protected boolean validTripType(String tripTypeCode) {
        if (StringUtils.isBlank(tripTypeCode)) {
            return false;
        }
        if (TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE.equals(tripTypeCode)) {
            return true;
        }
        final TripType tripType = getBoService().findBySinglePrimaryKey(TripType.class, tripTypeCode);
        return tripType != null;
    }

    /**
     * Determines if the given traveler type code matches with a known TravelerType record or is "All"
     * @param travelerTypeCode the traveler type code to validate
     * @return true if the traveler type code is valid, false otherwise
     */
    protected boolean validTravelerType(String travelerTypeCode) {
        if (StringUtils.isBlank(travelerTypeCode)) {
            return false;
        }
        if (TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE.equals(travelerTypeCode)) {
            return true;
        }
        final TravelerType travelerType = this.getBoService().findBySinglePrimaryKey(TravelerType.class, travelerTypeCode);
        return travelerType != null;
    }

    /**
     * Validates that the maximum amount is greater than 0
     * @param expenseTypeObjectCode the expense type object code to validate
     * @return true if the maximum amount is valid, false otherwise
     */
    protected boolean validMaximumAmount(ExpenseTypeObjectCode expenseTypeObjectCode) {
        boolean success = true;
        if (expenseTypeObjectCode.getMaximumAmount() != null) {
            if (KualiDecimal.ZERO.isGreaterEqual(expenseTypeObjectCode.getMaximumAmount())) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.MAXIMUM_AMOUNT, TemKeyConstants.ERROR_EXPENSE_TYPE_OBJECT_CODE_INVALID_MAXIMUM_AMOUNT);
            }
        }
        return success;
    }

    /**
     * Validates that the summation code is either not set, or is "O" or "D"
     * @param expenseTypeObjectCode the expense type object code to validate
     * @return true if maximum amount is valid, false otherwise
     */
    protected boolean validSummationCode(ExpenseTypeObjectCode expenseTypeObjectCode) {
        boolean success = true;
        if (!StringUtils.isBlank(expenseTypeObjectCode.getMaximumAmountSummationCode()) && !ExpenseTypeAmountSummation.PER_DAILY.getCode().equals(expenseTypeObjectCode.getMaximumAmountSummationCode()) && !ExpenseTypeAmountSummation.PER_OCCURRENCE.getCode().equals(expenseTypeObjectCode.getMaximumAmountSummationCode())) {
            success = false;
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.MAXIMUM_AMOUNT_SUMMATION_CODE, TemKeyConstants.ERROR_EXPENSE_TYPE_OBJECT_CODE_INVALID_SUMMATION_CODE, new String[] { expenseTypeObjectCode.getMaximumAmountSummationCode() });
        }
        return success;
    }

    /**
     * Validates that if the document type is "ENT" or "RELO", the trip type is "All" - it can be no other value
     * @param expenseTypeObjectCode the expense type object code to validate
     * @return true if the trip type is valid considering the document type, false otherwise
     */
    protected boolean validTripTypeByDocumentType(ExpenseTypeObjectCode expenseTypeObjectCode) {
        boolean success = true;
        if (TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT.equals(expenseTypeObjectCode.getDocumentTypeName()) || TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT.equals(expenseTypeObjectCode.getDocumentTypeName())) {
            if (!TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE.equals(expenseTypeObjectCode.getTripTypeCode())) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRIP_TYPE_CODE, TemKeyConstants.ERROR_EXPENSE_TYPE_OBJECT_CODE_INVALID_TRIP_TYPE_FOR_DOC_TYPE, expenseTypeObjectCode.getDocumentTypeName(), TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE);
            }
        }
        return success;
    }

    /**
     * Determines that the new expense type object code record is unique
     * @param expenseTypeObjectCode the new expense type object code to check
     * @return true if the expense type object code would be unique, false otherwise
     */
    protected boolean checkRecordIsUnique(ExpenseTypeObjectCode expenseTypeObjectCode) {
        boolean success = true;

        Map<String, String> fields = new HashMap<String, String>();
        fields.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME, expenseTypeObjectCode.getDocumentTypeName());
        fields.put(TemPropertyConstants.TRAVELER_TYPE_CODE, expenseTypeObjectCode.getTravelerTypeCode());
        fields.put(TemPropertyConstants.TRIP_TYPE_CODE, expenseTypeObjectCode.getTripTypeCode());
        fields.put(TemPropertyConstants.EXPENSE_TYPE_CODE, expenseTypeObjectCode.getExpenseTypeCode());
        final int count = getBoService().countMatching(ExpenseTypeObjectCode.class, fields);
        if (count > 0) {
            success = false;
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_TYPE_CODE, TemKeyConstants.ERROR_EXPENSE_TYPE_OBJECT_CODE_REOCRD_NOT_UNIQUE, expenseTypeObjectCode.getExpenseTypeCode(), expenseTypeObjectCode.getDocumentTypeName(), expenseTypeObjectCode.getTravelerTypeCode(), expenseTypeObjectCode.getTripTypeCode());
        }

        return success;
    }

    /**
     * Determines if the expenseTypeObjectCode from the document is sufficiently more
     * @param expenseTypeObjectCode
     * @return
     */
    protected boolean canExpenseTypeObjectCodeBeChosen(ExpenseTypeObjectCode expenseTypeObjectCode) {
        return true;
    }
}
