/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class DisbursementVoucherNonResidentAlienInformationValidation extends GenericValidation implements DisbursementVoucherConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherNonResidentAlienInformationValidation.class);

    private AccountingDocument accountingDocumentForValidation;
    private String validationType;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherNonResidentAlienTax nonResidentAlienTax = document.getDvNonResidentAlienTax();
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        Person financialSystemUser = GlobalVariables.getUserSession().getPerson();

        List<String> taxEditMode = this.getTaxEditMode();
        if (!payeeDetail.isDisbVchrAlienPaymentCode() || !this.hasRequiredEditMode(document, financialSystemUser, taxEditMode)) {
            return true;
        }

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        errors.addToErrorPath(KFSPropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);

// ICC SECTION

        /* income class code required */
        if (StringUtils.isBlank(nonResidentAlienTax.getIncomeClassCode())) {
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_REQUIRED, "Income class code");
            return false;
        }

        /* country code required, unless income type is nonreportable */
        if (StringUtils.isBlank(nonResidentAlienTax.getPostalCountryCode()) && !NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(nonResidentAlienTax.getIncomeClassCode())) {
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_REQUIRED, "Country code");
            return false;
        }

        // income class is FELLOWSHIP
        if(nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_FELLOWSHIP) ){
            // Place holder for logic related to the ICC
        }
        // income class is INDEPENDENT CONTRACTOR
        if(nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_INDEPENDENT_CONTRACTOR)){
            // Place holder for logic related to the ICC
        }
        // income class is ROYALTIES
        if(nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_ROYALTIES)){
            // Place holder for logic related to the ICC
        }
        // income class is NON_REPORTABLE
        if(nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_NON_REPORTABLE)){
            if( (nonResidentAlienTax.isForeignSourceIncomeCode()) || (nonResidentAlienTax.isIncomeTaxTreatyExemptCode()) ||
                    (nonResidentAlienTax.isTaxOtherExemptIndicator()) || (nonResidentAlienTax.isIncomeTaxGrossUpCode()) ||
                    (nonResidentAlienTax.isTaxUSAIDPerDiemIndicator()) || (nonResidentAlienTax.getTaxSpecialW4Amount() != null) ||
                    (nonResidentAlienTax.getReferenceFinancialDocumentNumber() != null) || (nonResidentAlienTax.getTaxNQIId() != null) ||
                    (nonResidentAlienTax.getPostalCountryCode() != null) ) {
                String boxCode = "";
                if(nonResidentAlienTax.isForeignSourceIncomeCode())
                {
                    boxCode = "Foreign Source";
                }
                if(nonResidentAlienTax.isIncomeTaxTreatyExemptCode())
                {
                    boxCode = "Treaty Exempt";
                }
                if(nonResidentAlienTax.isTaxOtherExemptIndicator())
                {
                    boxCode = "Exempt Under Other Code";
                }
                if(nonResidentAlienTax.isIncomeTaxGrossUpCode())
                {
                    boxCode = "Gross Up Payment";
                }
                if(nonResidentAlienTax.isTaxUSAIDPerDiemIndicator())
                {
                    boxCode = "USAID Per Diem";
                }
                if(nonResidentAlienTax.getTaxSpecialW4Amount() != null)
                {
                    boxCode = "Special W-4 Amount";
                }
                if(nonResidentAlienTax.getReferenceFinancialDocumentNumber() != null)
                {
                    boxCode = "Reference Doc";
                }
                if(nonResidentAlienTax.getTaxNQIId() != null)
                {
                    boxCode = "NQI Id";
                }
                if(nonResidentAlienTax.getPostalCountryCode() != null)
                {
                    boxCode = "Country Code";
                }
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NON_REPORTABLE_ONLY, boxCode);
                return false;
            }
        }

// TAX RATES SECTION

        /* check tax rates */
        if (((nonResidentAlienTax.getFederalIncomeTaxPercent() == null) || (nonResidentAlienTax.getFederalIncomeTaxPercent().equals(KualiDecimal.ZERO))) && (nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_NON_REPORTABLE)) ) {
            nonResidentAlienTax.setFederalIncomeTaxPercent(KualiDecimal.ZERO);
        }
        else {
            if (nonResidentAlienTax.getFederalIncomeTaxPercent() == null) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_REQUIRED, "Federal tax percent");
                return false;
            }
            else {
                // check Federal tax percent is in non-resident alien tax percent table for income class code
                NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                taxPercent.setIncomeClassCode(nonResidentAlienTax.getIncomeClassCode());
                taxPercent.setIncomeTaxTypeCode(FEDERAL_TAX_TYPE_CODE);
                taxPercent.setIncomeTaxPercent(nonResidentAlienTax.getFederalIncomeTaxPercent());

                NonResidentAlienTaxPercent retrievedPercent = (NonResidentAlienTaxPercent) SpringContext.getBean(BusinessObjectService.class).retrieve(taxPercent);
                if (retrievedPercent == null) {
                    errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_INVALID_FED_TAX_PERCENT, new String[] { nonResidentAlienTax.getFederalIncomeTaxPercent().toString(), nonResidentAlienTax.getIncomeClassCode() });
                    return false;
                }
            }
        }
        if (((nonResidentAlienTax.getStateIncomeTaxPercent() == null) || (nonResidentAlienTax.getStateIncomeTaxPercent().equals(KualiDecimal.ZERO))) && (nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_NON_REPORTABLE)) ) {
            nonResidentAlienTax.setStateIncomeTaxPercent(KualiDecimal.ZERO);
        }
        else {
            if (nonResidentAlienTax.getStateIncomeTaxPercent() == null) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_REQUIRED, "State tax percent");
                return false;
            }
            else {
                // check State tax percent is in non-resident alien tax percent table for income class code
                NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                taxPercent.setIncomeClassCode(nonResidentAlienTax.getIncomeClassCode());
                taxPercent.setIncomeTaxTypeCode(STATE_TAX_TYPE_CODE);
                taxPercent.setIncomeTaxPercent(nonResidentAlienTax.getStateIncomeTaxPercent());

                PersistableBusinessObject retrievedPercent = SpringContext.getBean(BusinessObjectService.class).retrieve(taxPercent);
                if (retrievedPercent == null) {
                    errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_INVALID_STATE_TAX_PERCENT, nonResidentAlienTax.getStateIncomeTaxPercent().toString(), nonResidentAlienTax.getIncomeClassCode());
                    return false;
                }
                else {
                    if ((!document.getDvNonResidentAlienTax().getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_ROYALTIES)) && (!document.getDvNonResidentAlienTax().getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_INDEPENDENT_CONTRACTOR))) {
                        // If fed tax rate is greater than zero, the state tax rate should be greater than zero.
                        if ((document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().isGreaterThan(KualiDecimal.ZERO)) && (document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().isZero())) {
                                    errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_STATE_INCOME_TAX_PERCENT_SHOULD_BE_GREATER_THAN_ZERO );
                                    return false;
                        }
                        // If fed tax rate is zero, the state tax rate should be zero.
                        if ((document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().equals(KualiDecimal.ZERO)) && (!document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().isZero())) {
                                    errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_STATE_TAX_SHOULD_BE_ZERO );
                                    return false;
                        }
                    }
                }
            }
        }

// CHECK BOX SECTION

    /*examine check boxes*/
       
    // the 4 check boxes (Foreign Source, Treaty Exempt, Gross Up Payment, Exempt Under Other Code) shall be mutual exclusive
    if( OneOrLessBoxesChecked(document) ) {

        // if Foreign Source is checked
        if( nonResidentAlienTax.isForeignSourceIncomeCode() ) {
            // Conditions to be met for "Foreign Source" error to be generated
            // Federal and State tax rate should be zero.
            if( (!nonResidentAlienTax.getFederalIncomeTaxPercent().equals(KualiDecimal.ZERO)) || (!nonResidentAlienTax.getStateIncomeTaxPercent().equals(KualiDecimal.ZERO)) ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_FEDERAL_AND_STATE_TAXES_SHOULD_BE_ZERO , "Foreign Source");
                return false;
            }
            // No other items (mutual exclusiveness checking on USAID Per Diem and Special W-4 Amount are optional here since these are also ensured by their validation later)  
            if((nonResidentAlienTax.isTaxUSAIDPerDiemIndicator()) || (nonResidentAlienTax.getTaxSpecialW4Amount() != null) ||
                    (nonResidentAlienTax.getReferenceFinancialDocumentNumber() != null) || (nonResidentAlienTax.getTaxNQIId() != null) ){
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_CANNOT_HAVE_VALUE , "Foreign Source", "NQI Id, Reference Doc, USAID Per Diem, or Special W-4 Amount");
                return false;
            }
        }

        // if Treaty Exempt is checked
        if( nonResidentAlienTax.isIncomeTaxTreatyExemptCode() ) {
            // Conditions to be met for "Treaty Exempt" error to be generated
            // No other items (mutual exclusiveness checking on USAID Per Diem and Special W-4 Amount are optional here since these are also ensured by their validation later)  
            if((nonResidentAlienTax.isTaxUSAIDPerDiemIndicator()) || (nonResidentAlienTax.getTaxSpecialW4Amount() != null) ||
                    (nonResidentAlienTax.getReferenceFinancialDocumentNumber() != null) || (nonResidentAlienTax.getTaxNQIId() != null) ){
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_CANNOT_HAVE_VALUE , "Treaty Exempt", "NQI Id, Reference Doc, USAID Per Diem, or Special W-4 Amount");
                return false;
            }
        }

        // if Gross Up Payment is checked
        if( nonResidentAlienTax.isIncomeTaxGrossUpCode() ) {
            // Conditions to be met for "Gross Up Payment" error to be generated
            // Federal tax rate cannot be zero
            // NOTE: Also, state tax not allowed to be zero for income classes "R" and "I", however, this rule is already checked in the tax rate section, so no need to re-check
            if( (nonResidentAlienTax.getFederalIncomeTaxPercent().equals(KualiDecimal.ZERO))) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_FEDERAL_TAX_CANNOT_BE_ZERO, "Gross Up Payment" );
                return false;
            }
            // No other items (mutual exclusiveness checking on USAID Per Diem and Special W-4 Amount are optional here since these are also ensured by their validation later)  
            if((nonResidentAlienTax.isTaxUSAIDPerDiemIndicator()) || (nonResidentAlienTax.getTaxSpecialW4Amount() != null) ||
                    (nonResidentAlienTax.getReferenceFinancialDocumentNumber() != null) || (nonResidentAlienTax.getTaxNQIId() != null) ){
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_CANNOT_HAVE_VALUE , "Gross Up Payment", "NQI Id, Reference Doc, USAID Per Diem, or Special W-4 Amount");
                return false;
            }
        }

        // if Exempt Under Other Code is checked
        if( nonResidentAlienTax.isTaxOtherExemptIndicator() ) {
            // also exists in PurapPropertyConstants.java as PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR
            // Conditions to be met for "Exempt Under Other Code" error to be generated
            // Federal and State tax rate should be zero.
            if( !(nonResidentAlienTax.getStateIncomeTaxPercent().equals(KualiDecimal.ZERO)) || !(nonResidentAlienTax.getFederalIncomeTaxPercent().equals(KualiDecimal.ZERO)) ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_FEDERAL_AND_STATE_TAXES_SHOULD_BE_ZERO , "Exempt Under Other Code");
                return false;
            }
        }

        // if USAID Per Diem is checked
        if( nonResidentAlienTax.isTaxUSAIDPerDiemIndicator() ) {
            // Conditions to be met for "USAID Per Diem" error to be generated
            // income class code should be fellowship
            if( !nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_FELLOWSHIP)  ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_SHOULD_BE_SELECTED, "USAID Per Diem", "Income Class Code : Fellowship");
                return false;
            }
            // Federal and State tax rate should be zero.
            if( !(nonResidentAlienTax.getStateIncomeTaxPercent().equals(KualiDecimal.ZERO)) || !(nonResidentAlienTax.getFederalIncomeTaxPercent().equals(KualiDecimal.ZERO)) ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_FEDERAL_AND_STATE_TAXES_SHOULD_BE_ZERO , "USAID Per Diem");
                return false;
            }
            // Exempt Under Other Code should be checked; this will ensure the other 3 check boxes not checked due to mutual exclusiveness
            if( !nonResidentAlienTax.isTaxOtherExemptIndicator() ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_SHOULD_BE_SELECTED, "USAID Per Diem", "Exempt Under Other Code");
                return false;
            }
            // Special W-4 Amount shall have no value 
            if( nonResidentAlienTax.getTaxSpecialW4Amount() != null ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_CANNOT_HAVE_VALUE, "USAID Per Diem", "Special W-4 Amount");
                return false;
            }
        }

        // if Special W-4 Amount is entered
        if( nonResidentAlienTax.getTaxSpecialW4Amount() != null ) {
            // Conditions to be met for "Special W-4 Amount" error to be generated
            // income class code should be fellowship
            if( !nonResidentAlienTax.getIncomeClassCode().equals(NRA_TAX_INCOME_CLASS_FELLOWSHIP) ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_SHOULD_BE_SELECTED, "Special W-4 Amount", "Income Class Code : Fellowship" );
                return false;
            }
            // Federal and State tax rate should be zero.
            if( !(nonResidentAlienTax.getStateIncomeTaxPercent().equals(KualiDecimal.ZERO)) || !(nonResidentAlienTax.getFederalIncomeTaxPercent().equals(KualiDecimal.ZERO)) ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_FEDERAL_AND_STATE_TAXES_SHOULD_BE_ZERO , "Special W-4 Amount");
                return false;
            }
            // Exempt Under Other Code should be checked; this will ensure the other 3 check boxes not checked due to mutual exclusiveness
            if( !nonResidentAlienTax.isTaxOtherExemptIndicator() ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_SHOULD_BE_SELECTED, "Special W-4 Amount", "Exempt Under Other Code");
                return false;
            }
            // USAID Per Diem should not be checked (mutual exclusive checking on USAID Per Diem here is optional since this is also ensured by validation on Special W-4 Amount above   
            if( ( nonResidentAlienTax.isTaxUSAIDPerDiemIndicator() ) ) {
                errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_WHEN_CHECKED_CANNOT_BE_SELECTED, "Special W-4 Amount", "USAID Per Diem");
                return false;
            }
        }

        // if NQI Id is entered
        if( nonResidentAlienTax.getTaxNQIId() != null) {

        }

        // if Reference Doc is entered
        if( nonResidentAlienTax.getReferenceFinancialDocumentNumber() != null) {

        }

    }

// RUN FOR SUBMISSION

    if(validationType!="GENERATE") {
        // verify tax lines have been generated
        if ((nonResidentAlienTax.getFederalIncomeTaxPercent().isNonZero() || nonResidentAlienTax.getStateIncomeTaxPercent().isNonZero())) {
            if (StringUtils.isBlank(nonResidentAlienTax.getFinancialDocumentAccountingLineText())) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_NRATAX_TAB_ERRORS, KFSKeyConstants.ERROR_DV_NRA_NO_TAXLINES_GENERATED);
                return false;
            }
        }
    }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }

    private boolean stateAndFederalTaxesNotNull(DisbursementVoucherDocument document) {
        if( (document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() != null) && (document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() != null) ) {
            return true;
        }
        return false;
    }




    /**
     * determine whether the give user has permission to any edit mode defined in the given candidate edit modes
     *
     * @param accountingDocument the given accounting document
     * @param financialSystemUser the given user
     * @param candidateEditEditModes the given candidate edit modes
     * @return true if the give user has permission to any edit mode defined in the given candidate edit modes; otherwise, false
     */
    private boolean hasRequiredEditMode(AccountingDocument accountingDocument, Person financialSystemUser, List<String> candidateEditModes) {
        DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) documentHelperService.getDocumentAuthorizer(accountingDocument);
        TransactionalDocumentPresentationController presentationController = (TransactionalDocumentPresentationController) documentHelperService.getDocumentPresentationController(accountingDocument);

        Set<String> presentationControllerEditModes = presentationController.getEditModes(accountingDocument);
        Set<String> editModes = documentAuthorizer.getEditModes(accountingDocument, financialSystemUser, presentationControllerEditModes);

        for (String editMode : candidateEditModes) {
            if (editModes.contains(editMode)) {
                return true;
            }
        }

        return false;
    }

    /**
     * define the tax edit mode name
     *
     * @return the tax edit mode name
     */
    protected List<String> getTaxEditMode() {
        List<String> candidateEdiModes = new ArrayList<String>();
        candidateEdiModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TAX_ENTRY);

        return candidateEdiModes;
    }

    /**
     * Sets the validationType attribute value.
     *
     * @param validationType The validationType to set.
     */
    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     *
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     *
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    private boolean OneOrLessBoxesChecked(DisbursementVoucherDocument document) {
        MessageMap errors = GlobalVariables.getMessageMap();
        /* If more then one of the four boxes (FS, TE, EUOC, GUP) is checked throw an error. */
        int boxCnt = 0 ;
        if(document.getDvNonResidentAlienTax().isForeignSourceIncomeCode()) {
            boxCnt++;
        }
        if(document.getDvNonResidentAlienTax().isIncomeTaxTreatyExemptCode()) {
            boxCnt++;
        }
        if(document.getDvNonResidentAlienTax().isTaxOtherExemptIndicator()) {
            boxCnt++;
        }
        if(document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode()) {
            boxCnt++;
        }
        if(boxCnt > 1) {
//            errors.putError(KFSPropertyConstants.INCOME_TAX_TREATY_EXEMPT_CODE, KFSKeyConstants.ERROR_DV_ONLY_ONE_SELECTION_ALLOWED );
            errors.putErrorWithoutFullErrorPath("DVNRATaxErrors", KFSKeyConstants.ERROR_DV_NRA_TAX_ONLY_ONE_SELECTION_ALLOWED);
            return false;
        }
        else {
            errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);
            return true;
        }
    }
}
