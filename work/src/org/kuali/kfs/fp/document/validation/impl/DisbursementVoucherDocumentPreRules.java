/*
 * Copyright 2005 The Kuali Foundation
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherWireTransfer;
import org.kuali.kfs.fp.businessobject.options.PaymentReasonValuesFinder;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.service.AccountingDocumentPreRuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Checks warnings and prompt conditions for dv document.
 */
public class DisbursementVoucherDocumentPreRules extends PromptBeforeValidationBase implements DisbursementVoucherConstants {

    /**
     * Executes pre-rules for Disbursement Voucher Document
     *
     * @param document submitted document
     * @return true if pre-rules execute successfully
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        checkSpecialHandlingIndicator(dvDocument);

        preRulesOK &= checkNonEmployeeTravelTabState(dvDocument);

        preRulesOK &= checkWireTransferTabState(dvDocument);

        preRulesOK &= checkForeignDraftTabState(dvDocument);

        preRulesOK &= checkBankCodeActive(dvDocument);

        preRulesOK &= SpringContext.getBean(AccountingDocumentPreRuleService.class).expiredAccountOverrideQuestion((AccountingDocumentBase) document, this, this.event);


        return preRulesOK;
    }

    /**
     * If the special handling name and address 1 fields have value, this will mark the special handling indicator for the user.
     *
     * @param dvDocument submitted disbursement voucher document
     */
    protected void checkSpecialHandlingIndicator(DisbursementVoucherDocument dvDocument) {
        if (StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrSpecialHandlingPersonName()) && StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrSpecialHandlingLine1Addr()) && allowTurningOnOfSpecialHandling(dvDocument)) {
            dvDocument.setDisbVchrSpecialHandlingCode(true);
        }
    }

    /**
     * Allows the automatic turning on of special handling indicator - which will not be allowed at the Campus route level
     * @param dvDocument the document to allow turning on of special handling for
     * @return true if special handling can be automatically turned on, false otherwise
     */
    protected boolean allowTurningOnOfSpecialHandling(DisbursementVoucherDocument dvDocument) {
        Set<String> currentNodes = dvDocument.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
        return CollectionUtils.isNotEmpty(currentNodes) && !(currentNodes.contains(DisbursementVoucherConstants.RouteLevelNames.CAMPUS));
    }

    /**
     * This method checks non-employee travel tab state is valid
     *
     * @param dvDocument submitted disbursement voucher document
     * @return true if the state of all the tabs is valid, false otherwise.
     */
    protected boolean checkNonEmployeeTravelTabState(DisbursementVoucherDocument dvDocument) {
        boolean tabStatesOK = true;

        DisbursementVoucherNonEmployeeTravel dvNonEmplTrav = dvDocument.getDvNonEmployeeTravel();
        if (!hasNonEmployeeTravelValues(dvNonEmplTrav)) {
            return true;
        }

        String paymentReasonCode = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        List<String> nonEmpltravelPaymentReasonCodes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(DisbursementVoucherDocument.class, NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM) );

        if (nonEmpltravelPaymentReasonCodes == null || !nonEmpltravelPaymentReasonCodes.contains(paymentReasonCode) || dvDocument.getDvPayeeDetail().isEmployee()) {
            String nonEmplTravReasonStr = getValidPaymentReasonsAsString(nonEmpltravelPaymentReasonCodes);

            String paymentReasonName = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonName();
            Object[] args = { "payment reason", "'" + paymentReasonName + "'", "Non-Employee Travel", nonEmplTravReasonStr };

            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);
            questionText = MessageFormat.format(questionText, args);

            boolean clearTab = super.askOrAnalyzeYesNoQuestion(KFSConstants.DisbursementVoucherDocumentConstants.CLEAR_NON_EMPLOYEE_TAB_QUESTION_ID, questionText);
            if (clearTab) {
                DisbursementVoucherNonEmployeeTravel blankDvNonEmplTrav = new DisbursementVoucherNonEmployeeTravel();
                blankDvNonEmplTrav.setDocumentNumber(dvNonEmplTrav.getDocumentNumber());
                blankDvNonEmplTrav.setVersionNumber(dvNonEmplTrav.getVersionNumber());
                dvDocument.setDvNonEmployeeTravel(blankDvNonEmplTrav);
            }
            else {
                // return to document if the user doesn't want to clear the Non Employee Travel tab
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                tabStatesOK = false;
            }
        }

        return tabStatesOK;
    }

    /**
     * Returns true if non-employee travel tab contains any data in any of its fields
     *
     * @param dvNonEmplTrav disbursement voucher non employee travel object
     * @return True if non employee travel tab contains any data in any fields.
     */
    protected boolean hasNonEmployeeTravelValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        boolean hasValues = false;

        // Checks each explicit field in the tab for user entered values
        hasValues = hasNonEmployeeTravelGeneralValues(dvNonEmplTrav);

        // Checks per diem section for values
        if (!hasValues) {
            hasValues = hasNonEmployeeTravelPerDiemValues(dvNonEmplTrav);
        }

        if (!hasValues) {
            hasValues = hasNonEmployeeTravelPersonalVehicleValues(dvNonEmplTrav);
        }

        if (!hasValues) {
            hasValues = dvNonEmplTrav.getDvNonEmployeeExpenses().size() > 0;
        }

        if (!hasValues) {
            hasValues = dvNonEmplTrav.getDvPrePaidEmployeeExpenses().size() > 0;
        }

        return hasValues;
    }

    /**
     * Returns true if any values are not blank on non employee travel tab
     *
     * @param dvNonEmplTrav disbursement voucher non employee travel object
     * @return True if any values are found in the non employee travel tab
     */
    protected boolean hasNonEmployeeTravelGeneralValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrNonEmpTravelerName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrServicePerformedDesc()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvServicePerformedLocName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvServiceRegularEmprName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelFromCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelFromStateCode()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvTravelFromCountryCode()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDvPerdiemStartDttmStamp()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToStateCode()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToCountryCode()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDvPerdiemEndDttmStamp());
    }

    /**
     * Returns true if non employee travel tab contains data in any of the fields in the per diem section
     *
     * @param dvNonEmplTrav disbursement voucher non employee travel object
     * @return True if non employee travel tab contains data in any of the fields in the per diem section
     */
    protected boolean hasNonEmployeeTravelPerDiemValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrPerdiemCategoryName()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPerdiemRate()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPerdiemCalculatedAmt()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPerdiemActualAmount()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvPerdiemChangeReasonText());
    }

    /**
     * Returns true if non employee travel tab contains data in any of the fields in the personal vehicle section
     *
     * @param dvNonEmplTrav disbursement voucher non employee travel object
     * @return True if non employee travel tab contains data in any of the fields in the personal vehicle section
     */
    protected boolean hasNonEmployeeTravelPersonalVehicleValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoFromCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoFromStateCode()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoToCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoToStateCode()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrMileageCalculatedAmt()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPersonalCarAmount());
    }

    /**
     * Returns true if the state of all the tabs is valid, false otherwise.
     *
     * @param dvDocument submitted disbursemtn voucher document
     * @return true if the state of all the tabs is valid, false otherwise.
     */
    protected boolean checkForeignDraftTabState(DisbursementVoucherDocument dvDocument) {
        boolean tabStatesOK = true;

        DisbursementVoucherWireTransfer dvForeignDraft = dvDocument.getDvWireTransfer();

        // if payment method is CHECK and wire tab contains data, ask user to clear tab
        if ((StringUtils.equals(DisbursementVoucherConstants.PAYMENT_METHOD_CHECK, dvDocument.getDisbVchrPaymentMethodCode()) || StringUtils.equals(DisbursementVoucherConstants.PAYMENT_METHOD_WIRE, dvDocument.getDisbVchrPaymentMethodCode())) && hasForeignDraftValues(dvForeignDraft)) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            Object[] args = { "payment method", dvDocument.getDisbVchrPaymentMethodCode(), "Foreign Draft", DisbursementVoucherConstants.PAYMENT_METHOD_DRAFT };
            questionText = MessageFormat.format(questionText, args);

            boolean clearTab = super.askOrAnalyzeYesNoQuestion(KFSConstants.DisbursementVoucherDocumentConstants.CLEAR_FOREIGN_DRAFT_TAB_QUESTION_ID, questionText);
            if (clearTab) {
                // NOTE: Can't replace with new instance because Wire Transfer uses same object
                clearForeignDraftValues(dvForeignDraft);
            }
            else {
                // return to document if the user doesn't want to clear the Wire Transfer tab
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                tabStatesOK = false;
            }
        }

        return tabStatesOK;
    }

    /**
     * Returns true if foreign draft tab contains any data in any fields. NOTE: Currently does not validate based on only required
     * fields. Checks all fields within tab for data.
     *
     * @param dvForeignDraft disbursement foreign draft object
     * @return True if foreign draft tab contains any data in any fields.
     */
    protected boolean hasForeignDraftValues(DisbursementVoucherWireTransfer dvForeignDraft) {
        boolean hasValues = false;

        // Checks each explicit field in the tab for user entered values
        hasValues |= StringUtils.isNotBlank(dvForeignDraft.getDisbursementVoucherForeignCurrencyTypeCode());
        hasValues |= StringUtils.isNotBlank(dvForeignDraft.getDisbursementVoucherForeignCurrencyTypeName());

        return hasValues;
    }

    /**
     * This method sets foreign currency type code and name to null for passed in disbursement foreign draft object
     *
     * @param dvForeignDraft disbursement foreign draft object
     */
    protected void clearForeignDraftValues(DisbursementVoucherWireTransfer dvForeignDraft) {
        dvForeignDraft.setDisbursementVoucherForeignCurrencyTypeCode(null);
        dvForeignDraft.setDisbursementVoucherForeignCurrencyTypeName(null);
    }

    /**
     * This method returns true if the state of all the tabs is valid, false otherwise.
     *
     * @param dvDocument submitted disbursement voucher document
     * @return Returns true if the state of all the tabs is valid, false otherwise.
     */
    protected boolean checkWireTransferTabState(DisbursementVoucherDocument dvDocument) {
        boolean tabStatesOK = true;

        DisbursementVoucherWireTransfer dvWireTransfer = dvDocument.getDvWireTransfer();

        // if payment method is CHECK and wire tab contains data, ask user to clear tab
        if ((StringUtils.equals(DisbursementVoucherConstants.PAYMENT_METHOD_CHECK, dvDocument.getDisbVchrPaymentMethodCode()) || StringUtils.equals(DisbursementVoucherConstants.PAYMENT_METHOD_DRAFT, dvDocument.getDisbVchrPaymentMethodCode())) && hasWireTransferValues(dvWireTransfer)) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            Object[] args = { "payment method", dvDocument.getDisbVchrPaymentMethodCode(), "Wire Transfer", DisbursementVoucherConstants.PAYMENT_METHOD_WIRE };
            questionText = MessageFormat.format(questionText, args);

            boolean clearTab = super.askOrAnalyzeYesNoQuestion(KFSConstants.DisbursementVoucherDocumentConstants.CLEAR_WIRE_TRANSFER_TAB_QUESTION_ID, questionText);
            if (clearTab) {
                // NOTE: Can't replace with new instance because Foreign Draft uses same object
                clearWireTransferValues(dvWireTransfer);
            }
            else {
                // return to document if the user doesn't want to clear the Wire Transfer tab
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                tabStatesOK = false;
            }
        }

        return tabStatesOK;
    }

    /**
     * If bank specification is enabled, prompts user to use the continuation bank code when the given bank code is inactive
     *
     * @param dvDocument document containing bank code
     * @return true
     */
    protected boolean checkBankCodeActive(DisbursementVoucherDocument dvDocument) {
        boolean continueRules = true;

        // if bank specification is not enabled, no need to validate bank
        if (!SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            return continueRules;
        }

        // refresh bank reference so continuation bank can be checked for active status
        dvDocument.refreshReferenceObject(KFSPropertyConstants.BANK);
        Bank bank = dvDocument.getBank();

        // if bank is inactive and continuation is active, prompt user to use continuation bank
        if (bank != null && !bank.isActive() && bank.getContinuationBank().isActive()) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_BANK_INACTIVE);
            questionText = MessageFormat.format(questionText, dvDocument.getDisbVchrBankCode(), bank.getContinuationBankCode());

            boolean useContinuation = super.askOrAnalyzeYesNoQuestion(KFSConstants.USE_CONTINUATION_BANK_QUESTION, questionText);
            if (useContinuation) {
                dvDocument.setDisbVchrBankCode(bank.getContinuationBankCode());
            }
        }

        return continueRules;
    }

    /**
     * Returns true if wire transfer tab contains any data in any fields.
     *
     * @param dvWireTransfer disbursement voucher wire transfer
     * @return true if wire transfer tab contains any data in any fields.
     */
    protected boolean hasWireTransferValues(DisbursementVoucherWireTransfer dvWireTransfer) {
        boolean hasValues = false;

        // Checks each explicit field in the tab for user entered values
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbursementVoucherAutomatedClearingHouseProfileNumber());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbursementVoucherBankName());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrBankRoutingNumber());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrBankCityName());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrBankStateCode());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrBankCountryCode());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrPayeeAccountNumber());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrAttentionLineText());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrCurrencyTypeName());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbVchrAdditionalWireText());
        hasValues |= StringUtils.isNotBlank(dvWireTransfer.getDisbursementVoucherPayeeAccountName());

        return hasValues;
    }

    /**
     * This method sets all values in the passed in disbursement wire transfer object to null
     *
     * @param dvWireTransfer
     */
    protected void clearWireTransferValues(DisbursementVoucherWireTransfer dvWireTransfer) {
        dvWireTransfer.setDisbursementVoucherAutomatedClearingHouseProfileNumber(null);
        dvWireTransfer.setDisbursementVoucherBankName(null);
        dvWireTransfer.setDisbVchrBankRoutingNumber(null);
        dvWireTransfer.setDisbVchrBankCityName(null);
        dvWireTransfer.setDisbVchrBankStateCode(null);
        dvWireTransfer.setDisbVchrBankCountryCode(null);
        dvWireTransfer.setDisbVchrPayeeAccountNumber(null);
        dvWireTransfer.setDisbVchrAttentionLineText(null);
        dvWireTransfer.setDisbVchrCurrencyTypeName(null);
        dvWireTransfer.setDisbVchrAdditionalWireText(null);
        dvWireTransfer.setDisbursementVoucherPayeeAccountName(null);
    }

    /**
     * put the valid payment reason codes along with their description together
     *
     * @param validPaymentReasonCodes the given valid payment reason codes
     * @return the valid payment reason codes along with their description as a string
     */
    protected String getValidPaymentReasonsAsString(List<String> validPaymentReasonCodes) {
        List<String> payementReasonString = new ArrayList<String>();

        if (validPaymentReasonCodes == null || validPaymentReasonCodes.isEmpty()) {
            return StringUtils.EMPTY;
        }

        List<KeyValue> reasons = new PaymentReasonValuesFinder().getKeyValues();
        for (KeyValue reason : reasons) {
            if (validPaymentReasonCodes.contains(reason.getKey())) {
                payementReasonString.add(reason.getValue());
            }
        }

        return payementReasonString.toString();
    }
}
