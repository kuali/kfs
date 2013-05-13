/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.ProcurementCardCreateDocumentsStep;
import org.kuali.kfs.fp.businessobject.ProcurementCardDefault;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * This class represents business rules for the procurement cardholder maintenance document
 */

public class ProcurementCardDefaultRule extends MaintenanceDocumentRuleBase {
    protected static final String WARNING_CARDHOLDER_LAST_ACTIVE_MEMBER = "warning.document.procurementcardholderdetail.cardholder.last.active";
    protected static ParameterService parameterService;

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
        validateCardHolderDefault(newProcurementCardDefault);
        validateAccountingDefault(newProcurementCardDefault);

        return true;
    }

    /**
     * @return true if use of card holder defaults is turned on via parameter, false if it is turned off
     */
    protected boolean isCardHolderDefaultTurnedOn() {
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
    protected synchronized ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

}
