/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.lookup.keyvalues.PaymentReasonValuesFinder;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.rules.RulesUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.uidraw.KeyLabelPair;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel;
import org.kuali.module.financial.bo.DisbursementVoucherWireTransfer;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

/**
 * Checks warnings and prompt conditions for dv document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
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

        String[] travelNonEmplPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM);

        if (hasNonEmployeeTravelValues(dvNonEmplTrav) && !RulesUtils.makeSet(travelNonEmplPaymentReasonCodes).contains(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            PaymentReasonValuesFinder payReasonValues = new PaymentReasonValuesFinder();
            List<KeyLabelPair> reasons = payReasonValues.getKeyValues();
            String nonEmplTravReasonStr = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();

            for (KeyLabelPair r : reasons) {
                if (r.getKey().equals(travelNonEmplPaymentReasonCodes[0])) {
                    nonEmplTravReasonStr = r.getLabel();
                }
            }

            Object[] args = { "payment reason", "'" + dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonName() + "'", "Non-Employee Travel", "'" + nonEmplTravReasonStr + "'" };
            questionText = MessageFormat.format(questionText, args);

            boolean clearTab = super.askOrAnalyzeYesNoQuestion(Constants.DisbursementVoucherDocumentConstants.CLEAR_NON_EMPLOYEE_TAB_QUESTION_ID, questionText);
            if (clearTab) {
                DisbursementVoucherNonEmployeeTravel blankDvNonEmplTrav = new DisbursementVoucherNonEmployeeTravel();
                blankDvNonEmplTrav.setFinancialDocumentNumber(dvNonEmplTrav.getFinancialDocumentNumber());
                blankDvNonEmplTrav.setVersionNumber(dvNonEmplTrav.getVersionNumber());
                dvDocument.setDvNonEmployeeTravel(blankDvNonEmplTrav);
            }
            else {
                // return to document if the user doesn't want to clear the Non Employee Travel tab
                super.event.setActionForwardName(Constants.MAPPING_BASIC);
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
            String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            Object[] args = { "payment method", dvDocument.getDisbVchrPaymentMethodCode(), "Foreign Draft", DisbursementVoucherRuleConstants.PAYMENT_METHOD_DRAFT };
            questionText = MessageFormat.format(questionText, args);

            boolean clearTab = super.askOrAnalyzeYesNoQuestion(Constants.DisbursementVoucherDocumentConstants.CLEAR_FOREIGN_DRAFT_TAB_QUESTION_ID, questionText);
            if (clearTab) {
                // NOTE: Can't replace with new instance because Wire Transfer uses same object
                clearForeignDraftValues(dvForeignDraft);
            }
            else {
                // return to document if the user doesn't want to clear the Wire Transfer tab
                super.event.setActionForwardName(Constants.MAPPING_BASIC);
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
            String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);

            Object[] args = { "payment method", dvDocument.getDisbVchrPaymentMethodCode(), "Wire Transfer", DisbursementVoucherRuleConstants.PAYMENT_METHOD_WIRE };
            questionText = MessageFormat.format(questionText, args);

            boolean clearTab = super.askOrAnalyzeYesNoQuestion(Constants.DisbursementVoucherDocumentConstants.CLEAR_WIRE_TRANSFER_TAB_QUESTION_ID, questionText);
            if (clearTab) {
                // NOTE: Can't replace with new instance because Foreign Draft uses same object
                clearWireTransferValues(dvWireTransfer);
            }
            else {
                // return to document if the user doesn't want to clear the Wire Transfer tab
                super.event.setActionForwardName(Constants.MAPPING_BASIC);
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