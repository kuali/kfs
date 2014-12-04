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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.ProcurementCardCreateDocumentsStep;
import org.kuali.kfs.fp.businessobject.ProcurementCardDefault;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class represents business rules for the procurement cardholder maintenance document
 */

public class ProcurementCardDefaultRule extends MaintenanceDocumentRuleBase {
    private static volatile ParameterService parameterService;
    private static volatile BusinessObjectService businessObjectService;

    /**
     * Returns true procurement card defaults maintenance document is routed successfully
     *
     * @param document submitted procurement card defaults maintenance document
     * @return true if chart/account/organization is valid
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean continueRouting = super.processCustomRouteDocumentBusinessRules(document);
        final ProcurementCardDefault newProcurementCardDefault = (ProcurementCardDefault)getNewBo();

        continueRouting &= validateCreditCardNumberUniqueness(newProcurementCardDefault);
        continueRouting &= validateCardHolderDefault(newProcurementCardDefault);
        continueRouting &= validateAccountingDefault(newProcurementCardDefault);

        return continueRouting;
    }
    /**
     * Always returns true; provides user feedback on procurement card defaults maintenance document
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        super.processCustomSaveDocumentBusinessRules(document);

        final ProcurementCardDefault newProcurementCardDefault = (ProcurementCardDefault)getNewBo();
        validateCreditCardNumberUniqueness(newProcurementCardDefault);
        validateCardHolderDefault(newProcurementCardDefault);
        validateAccountingDefault(newProcurementCardDefault);

        return true;
    }

    /**
     * Determines if the given procurement card default has a unique credit card number
     * @param procurementCardDefault the procurement card default to check
     * @return true if the procurement card default credit card number is unique, false otherwise
     */
    public boolean validateCreditCardNumberUniqueness(ProcurementCardDefault procurementCardDefault) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.CREDIT_CARD_NUMBER, procurementCardDefault.getCreditCardNumber());
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        Collection<ProcurementCardDefault> matchingPcardDefaults = getBusinessObjectService().findMatching(ProcurementCardDefault.class, fieldValues);
        if (CollectionUtils.isEmpty(matchingPcardDefaults)) {
            return true; // no other pcard defaults with the same credit card, so we must be safe
        }
        for (ProcurementCardDefault pcardDefault : matchingPcardDefaults) {
            if (!procurementCardDefault.getId().equals(pcardDefault.getId())) {
                putFieldError(KFSPropertyConstants.CREDIT_CARD_NUMBER, KFSKeyConstants.ERROR_PROCUREMENT_CARD_DEFAULT_CREDIT_CARD_NUMBER_NOT_UNIQUE);
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if use of card holder defaults is turned on via parameter, false if it is turned off
     */
    protected boolean isCardHolderDefaultTurnedOn() {
        // phrasing much?
        return getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_CARD_HOLDER_DEFAULT_PARAMETER_NAME);
    }

    /**
     * @return true if use of accounting defaults is turned on via parameter, false if it is turned off
     */
    protected boolean isAccountDefaultTurnedOn() {
        return getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME);
    }

    /**
     * Validates the card holder information
     * @return true if the card holder is valid, false otherwise
     */
    protected boolean validateCardHolderDefault(ProcurementCardDefault newProcurementCardDefault) {
        boolean valid = true;
        if (isCardHolderDefaultTurnedOn()) {
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderLine1Address())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_LINE1_ADDRESS, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderCityName())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_CITY_NAME, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderStateCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_STATE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderZipCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_ZIP_CODE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderWorkPhoneNumber())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_WORK_PHONE_NUMBER, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (newProcurementCardDefault.getCardLimit() == null) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_LIMIT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (newProcurementCardDefault.getCardCycleAmountLimit() == null) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_CYCLE_AMOUNT_LIMIT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (newProcurementCardDefault.getCardCycleVolumeLimit() == null) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_CYCLE_VOLUME_LIMIT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardStatusCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_STATUS_CODE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardNoteText())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_NOTE_TEXT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Validates the accounting default information
     * @return true if the accounting default information is valid, false otherwise
     */
    protected boolean validateAccountingDefault(ProcurementCardDefault newProcurementCardDefault) {
        boolean valid = true;
        if (isAccountDefaultTurnedOn() || isCardHolderDefaultTurnedOn()) {
            if (StringUtils.isBlank(newProcurementCardDefault.getChartOfAccountsCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getAccountNumber())) {
                putFieldErrorWithLabel(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getFinancialObjectCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Adds a property-specific error to the global errors list, with the DD short label as the single argument.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     */
    protected void putFieldErrorWithLabel(String propertyName, String errorConstant) {
        final String label = getDataDictionaryService().getAttributeLabel(getNewBo().getClass(), propertyName);
        putFieldError(propertyName, errorConstant, label);
    }

    /**
     * @return the default implementation of the ParameterService
     */
    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
}
