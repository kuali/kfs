/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel;
import org.kuali.module.financial.bo.DisbursementVoucherWireTransfer;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.lookup.keyvalues.PaymentReasonValuesFinder;

/**
 * Checks warnings and prompt conditions for dv document.
 * 
 * 
 */
public class DisbursementVoucherDocumentPreRules extends PreRulesContinuationBase implements DisbursementVoucherRuleConstants {
    private KualiConfigurationService kualiConfiguration;


    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.MaintenanceDocument)
     */
    public boolean doRules(Document document) {
        boolean preRulesOK = true;

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        checkSpecialHandlingIndicator(dvDocument);

        preRulesOK &= checkNonEmployeeTravelTabState(dvDocument);

        preRulesOK &= checkWireTransferTabState(dvDocument);

        preRulesOK &= checkForeignDraftTabState(dvDocument);

        return preRulesOK;
    }

    /**
     * If the special handling name and address 1 fields have value, this will mark the special handling indicator for the user.
     * 
     * @param dvDocument
     */
    private void checkSpecialHandlingIndicator(DisbursementVoucherDocument dvDocument) {
        if (StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrRemitPersonName()) && StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrRemitLine1Addr())) {
            dvDocument.setDisbVchrSpecialHandlingCode(true);
        }
    }

    /**
     * 
     * This method...
     * 
     * @param dvDocument
     * @return Returns true if the state of all the tabs is valid, false otherwise.
     */
    private boolean checkNonEmployeeTravelTabState(DisbursementVoucherDocument dvDocument) {
        boolean tabStatesOK = true;

        DisbursementVoucherNonEmployeeTravel dvNonEmplTrav = dvDocument.getDvNonEmployeeTravel();

        List<String> travelNonEmplPaymentReasonCodes = SpringContext.getBean(KualiConfigurationService.class).getParameterValuesAsList(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM);

        if (hasNonEmployeeTravelValues(dvNonEmplTrav) && !travelNonEmplPaymentReasonCodes.contains(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            PaymentReasonValuesFinder payReasonValues = new PaymentReasonValuesFinder();
            List<KeyLabelPair> reasons = payReasonValues.getKeyValues();
            String nonEmplTravReasonStr = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();

            for (KeyLabelPair r : reasons) {
                if (r.getKey().equals(travelNonEmplPaymentReasonCodes.get(0))) {
                    nonEmplTravReasonStr = r.getLabel();
                }
            }

            Object[] args = { "payment reason", "'" + dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonName() + "'", "Non-Employee Travel", "'" + nonEmplTravReasonStr + "'" };
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
     * 
     * This method...
     * 
     * @param dvNonEmplTrav
     * @return True if non employee travel tab contains any data in any fields.
     */
    private boolean hasNonEmployeeTravelValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
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
     * This method...
     * 
     * @param dvNonEmplTrav
     * @return True if any values are found in the non employee travel tab
     */
    private boolean hasNonEmployeeTravelGeneralValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrNonEmpTravelerName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrServicePerformedDesc()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvServicePerformedLocName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvServiceRegularEmprName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelFromCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelFromStateCode()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvTravelFromCountryCode()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDvPerdiemStartDttmStamp()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToStateCode()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToCountryCode()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDvPerdiemEndDttmStamp());
    }

    /**
     * 
     * This method...
     * 
     * @param dvNonEmplTrav
     * @return True if non employee travel tab contains data in any of the fields in the per diem section
     */
    private boolean hasNonEmployeeTravelPerDiemValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrPerdiemCategoryName()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPerdiemRate()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPerdiemCalculatedAmt()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPerdiemActualAmount()) || StringUtils.isNotBlank(dvNonEmplTrav.getDvPerdiemChangeReasonText());
    }

    /**
     * 
     * This method...
     * 
     * @param dvNonEmplTrav
     * @return True if non employee travel tab contains data in any of the fields in the personal vehicle section
     */
    private boolean hasNonEmployeeTravelPersonalVehicleValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoFromCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoFromStateCode()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoToCityName()) || StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrAutoToStateCode()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrMileageCalculatedAmt()) || ObjectUtils.isNotNull(dvNonEmplTrav.getDisbVchrPersonalCarAmount());
    }

    /**
     * 
     * This method...
     * 
     * @param dvDocument
     * @return Returns true if the state of all the tabs is valid, false otherwise.
     */
    private boolean checkForeignDraftTabState(DisbursementVoucherDocument dvDocument) {
        boolean tabStatesOK = true;

        DisbursementVoucherWireTransfer dvForeignDraft = dvDocument.getDvWireTransfer();

        // if payment method is CHECK and wire tab contains data, ask user to clear tab
        if ((StringUtils.equals(DisbursementVoucherRuleConstants.PAYMENT_METHOD_CHECK, dvDocument.getDisbVchrPaymentMethodCode()) || StringUtils.equals(DisbursementVoucherRuleConstants.PAYMENT_METHOD_WIRE, dvDocument.getDisbVchrPaymentMethodCode())) && hasForeignDraftValues(dvForeignDraft)) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            Object[] args = { "payment method", dvDocument.getDisbVchrPaymentMethodCode(), "Foreign Draft", DisbursementVoucherRuleConstants.PAYMENT_METHOD_DRAFT };
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
     * 
     * This method...
     * 
     * NOTE: Currently does not validate based on only required fields. Checks all fields within tab for data.
     * 
     * @param dvForeignDraft
     * @return True if foreign draft tab contains any data in any fields.
     */
    private boolean hasForeignDraftValues(DisbursementVoucherWireTransfer dvForeignDraft) {
        boolean hasValues = false;

        // Checks each explicit field in the tab for user entered values
        hasValues |= StringUtils.isNotBlank(dvForeignDraft.getDisbursementVoucherForeignCurrencyTypeCode());
        hasValues |= StringUtils.isNotBlank(dvForeignDraft.getDisbursementVoucherForeignCurrencyTypeName());

        return hasValues;
    }

    /**
     * 
     * This method...
     * 
     * @param dvForeignDraft
     */
    private void clearForeignDraftValues(DisbursementVoucherWireTransfer dvForeignDraft) {
        dvForeignDraft.setDisbursementVoucherForeignCurrencyTypeCode(null);
        dvForeignDraft.setDisbursementVoucherForeignCurrencyTypeName(null);
    }

    /**
     * 
     * This method...
     * 
     * @param dvDocument
     * @return Returns true if the state of all the tabs is valid, false otherwise.
     */
    private boolean checkWireTransferTabState(DisbursementVoucherDocument dvDocument) {
        boolean tabStatesOK = true;

        DisbursementVoucherWireTransfer dvWireTransfer = dvDocument.getDvWireTransfer();

        // if payment method is CHECK and wire tab contains data, ask user to clear tab
        if ((StringUtils.equals(DisbursementVoucherRuleConstants.PAYMENT_METHOD_CHECK, dvDocument.getDisbVchrPaymentMethodCode()) || StringUtils.equals(DisbursementVoucherRuleConstants.PAYMENT_METHOD_DRAFT, dvDocument.getDisbVchrPaymentMethodCode())) && hasWireTransferValues(dvWireTransfer)) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            Object[] args = { "payment method", dvDocument.getDisbVchrPaymentMethodCode(), "Wire Transfer", DisbursementVoucherRuleConstants.PAYMENT_METHOD_WIRE };
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
     * 
     * This method...
     * 
     * NOTE: Currently does not validate based on only required fields. Checks all fields within tab for data.
     * 
     * @param dvWireTransfer
     * @return True if wire transfer tab contains any data in any fields.
     */
    private boolean hasWireTransferValues(DisbursementVoucherWireTransfer dvWireTransfer) {
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
     * 
     * This method...
     * 
     * @param dvWireTransfer
     */
    private void clearWireTransferValues(DisbursementVoucherWireTransfer dvWireTransfer) {
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

}