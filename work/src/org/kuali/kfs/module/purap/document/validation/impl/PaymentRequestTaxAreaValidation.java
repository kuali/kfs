/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class PaymentRequestTaxAreaValidation extends GenericValidation {
    
    private BusinessObjectService businessObjectService;    
    
    /** Map for allowed federal and state tax rates based on income class. *
    private static HashMap<String, ArrayList<BigDecimal>> federalTaxRates;
    private static HashMap<String, ArrayList<BigDecimal>> stateTaxRates;    

    //TODO these rates shall be kept in DB tables or as parameter
    // set up the tax rate maps
    static {
        federalTaxRates = new HashMap<String, ArrayList<BigDecimal>>();
        stateTaxRates = new HashMap<String, ArrayList<BigDecimal>>();
        
        ArrayList<BigDecimal> fedrates = new ArrayList<BigDecimal>();
        fedrates.add(new BigDecimal(30));
        fedrates.add(new BigDecimal(14));
        fedrates.add(new BigDecimal(0));
        federalTaxRates.put("F", fedrates);

        fedrates = new ArrayList<BigDecimal>();
        fedrates.add(new BigDecimal(30));
        fedrates.add(new BigDecimal(15));
        fedrates.add(new BigDecimal(10));
        fedrates.add(new BigDecimal(5));
        fedrates.add(new BigDecimal(0));
        federalTaxRates.put("R", fedrates);

        fedrates = new ArrayList<BigDecimal>();
        fedrates.add(new BigDecimal(30));
        fedrates.add(new BigDecimal(0));
        federalTaxRates.put("I", fedrates);
        federalTaxRates.put("A", fedrates);
        federalTaxRates.put("O", fedrates);

        ArrayList<BigDecimal> strates = new ArrayList<BigDecimal>();
        strates.add(new BigDecimal("3.40"));
        strates.add(new BigDecimal(0));
        stateTaxRates.put("F", strates);
        stateTaxRates.put("A", strates);
        stateTaxRates.put("O", strates);

        strates = new ArrayList<BigDecimal>();
        strates.add(new BigDecimal(0));
        stateTaxRates.put("I", strates);
        stateTaxRates.put("R", strates);
    }
    */

    /**
     * Process business rules applicable to tax area data before calculating the withholding tax on payment request.
     * @param paymentRequest - payment request document
     * @return true if all business rules applicable passes; false otherwise.
     */
    public boolean validate(AttributedDocumentEvent event) {        
        PaymentRequestDocument preq = (PaymentRequestDocument)event.getDocument();
        
        // do this validation only at route level of awaiting tax review
        if ( ! StringUtils.equals(preq.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW))
            return true;
        
        MessageMap errorMap = GlobalVariables.getMessageMap();        
        errorMap.clearErrorPath();
        //errorMap.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        errorMap.addToErrorPath(PurapConstants.PAYMENT_REQUEST_TAX_TAB_ERRORS);

        boolean valid = true;        
        valid &= validateTaxIncomeClass(preq);        
        valid &= validateTaxRates(preq);    
        valid &= validateTaxIndicators(preq);

        errorMap.clearErrorPath();
        return valid;
    }

    /**
     * Validates tax income class: when Non-Reportable income class is chosen, all other fields shall be left blank; 
     * It assumed that the input tax income class code is valid (existing and active in the system) since it's chosen from drop-down list.
     * otherwise tax rates and country are required;
     * @param paymentRequest - payment request document
     * @return true if this validation passes; false otherwise.
     */
    protected boolean validateTaxIncomeClass(PaymentRequestDocument preq) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();

        // TaxClassificationCode is required field
        if (StringUtils.isEmpty(preq.getTaxClassificationCode())) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED, PurapPropertyConstants.TAX_CLASSIFICATION_CODE);           
        }
        // If TaxClassificationCode is N (Non_Reportable, then other fields shall be blank.
        else if (StringUtils.equalsIgnoreCase(preq.getTaxClassificationCode(), "N")) {
            if (preq.getTaxFederalPercent() != null && preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
            }
            if (preq.getTaxStatePercent() != null && preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_STATE_PERCENT);
            }
            if (!StringUtils.isEmpty(preq.getTaxCountryCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_COUNTRY_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_COUNTRY_CODE);
            }
            if (!StringUtils.isEmpty(preq.getTaxNQIId())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_NQI_ID, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_NQI_ID);           
            }
            if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new BigDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
            }               
            if (ObjectUtils.equals(preq.getTaxExemptTreatyIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
            }                         
            if (ObjectUtils.equals(preq.getTaxGrossUpIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxForeignSourceIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxOtherExemptIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR);
            }               
        }
        else {
            // If TaxClassificationCode is not N (Non_Reportable, then the federal/state tax percent and country are required.
            if (preq.getTaxFederalPercent() == null) {       
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_FEDERAL_PERCENT);           
            }
            if (preq.getTaxStatePercent() == null) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_STATE_PERCENT);           
            }
            if (StringUtils.isEmpty(preq.getTaxCountryCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_COUNTRY_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_COUNTRY_CODE);           
            }
        }

        return valid;
    }

    /**
     * Validates federal and state tax rates based on each other and the income class.
     * Validation will be bypassed if income class is empty or N, or tax rates are null.
     * @param paymentRequest - payment request document
     * @return true if this validation passes; false otherwise.
     */
    protected boolean validateTaxRates(PaymentRequestDocument preq) {
        boolean valid = true;
        String code = preq.getTaxClassificationCode();
        BigDecimal fedrate = preq.getTaxFederalPercent();
        BigDecimal strate = preq.getTaxStatePercent();
        MessageMap errorMap = GlobalVariables.getMessageMap();

        // only test the cases when income class and tax rates aren't empty/N
        if (StringUtils.isEmpty(code) || StringUtils.equalsIgnoreCase(code, "N") || fedrate == null || strate == null)
            return true;

        // validate that the federal and state tax rates are among the allowed set
        ArrayList<BigDecimal> fedrates = retrieveTaxRates(code, "F"); //(ArrayList<BigDecimal>) federalTaxRates.get(code);
        if (!listContainsValue(fedrates, fedrate)) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_FEDERAL_PERCENT);                      
        }
        ArrayList<BigDecimal> strates = retrieveTaxRates(code, "S"); //(ArrayList<BigDecimal>) stateTaxRates.get(code);
        if (!listContainsValue(strates, strate)) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_STATE_PERCENT);                      
        }

        // validate that the federal and state tax rate abide to certain relationship
        if (fedrate.compareTo(new BigDecimal(0)) == 0 && strate.compareTo(new BigDecimal(0)) != 0) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapPropertyConstants.TAX_STATE_PERCENT);                      
        } 
        boolean hasstrate = code.equalsIgnoreCase("F") || code.equalsIgnoreCase("A") || code.equalsIgnoreCase("O");
        if (fedrate.compareTo(new BigDecimal(0)) > 0 && strate.compareTo(new BigDecimal(0)) <= 0 && hasstrate) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_NOT_ZERO_IF, PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapPropertyConstants.TAX_STATE_PERCENT);                      
        } 

        return valid;
    }

    /**
     * Validates rules among tax treaty, gross up, foreign source, USAID, other exempt, and Special W-4.
     * @param paymentRequest - payment request document
     * @return true if this validation passes; false otherwise.
     */
    protected boolean validateTaxIndicators(PaymentRequestDocument preq) {
        boolean valid = true;     
        MessageMap errorMap = GlobalVariables.getMessageMap();

        // if choose tax treaty, cannot choose any of the other above 
        if (ObjectUtils.equals(preq.getTaxExemptTreatyIndicator(), true)) {
            if (ObjectUtils.equals(preq.getTaxGrossUpIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxForeignSourceIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxOtherExemptIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR);
            }               
            if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
            }
            if (preq.getTaxFederalPercent() != null && preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
            }
            if (preq.getTaxStatePercent() != null && preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapPropertyConstants.TAX_STATE_PERCENT);
            }
        }

        // if choose gross up, cannot choose any other above, and fed tax rate cannot be zero
        if (ObjectUtils.equals(preq.getTaxGrossUpIndicator(), true)) {
            if (ObjectUtils.equals(preq.getTaxExemptTreatyIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxForeignSourceIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxOtherExemptIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR);
            }               
            if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
            }               
            if (preq.getTaxFederalPercent() == null || preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) == 0 ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_NOT_ZERO_IF, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
            }
        }

        // if choose foreign source, cannot choose any other above, and tax rates shall be zero
        if (ObjectUtils.equals(preq.getTaxForeignSourceIndicator(), true)) {
            if (ObjectUtils.equals(preq.getTaxExemptTreatyIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxGrossUpIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxUSAIDPerDiemIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxOtherExemptIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR);
            }               
            if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
            }               
            if (preq.getTaxFederalPercent() != null && preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
            }
            if (preq.getTaxStatePercent() != null && preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapPropertyConstants.TAX_STATE_PERCENT);
            }
        }

        // if choose USAID per diem, cannot choose any other above except other exempt code, which must be checked; income class shall be fellowship with tax rates 0
        if (ObjectUtils.equals(preq.getTaxUSAIDPerDiemIndicator(), true)) {
            if (ObjectUtils.equals(preq.getTaxExemptTreatyIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxGrossUpIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxForeignSourceIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR);
            }               
            if (!ObjectUtils.equals(preq.getTaxOtherExemptIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR);
            }               
            if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
            }               
            if (StringUtils.isEmpty(preq.getTaxClassificationCode()) || !StringUtils.equalsIgnoreCase(preq.getTaxClassificationCode(), "F")) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_CLASSIFICATION_CODE);
            }
            if (preq.getTaxFederalPercent() != null && preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0 ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
            }
            if (preq.getTaxStatePercent() != null && preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0 ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_USAID_PER_DIEM_INDICATOR, PurapPropertyConstants.TAX_STATE_PERCENT);
            }
        }

        // if choose exempt under other code, cannot choose any other above except USAID, and tax rates shall be zero
        if (ObjectUtils.equals(preq.getTaxOtherExemptIndicator(), true)) {
            if (ObjectUtils.equals(preq.getTaxExemptTreatyIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxGrossUpIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxForeignSourceIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR);
            }   
            if (preq.getTaxFederalPercent() != null && preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0 ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
            }
            if (preq.getTaxStatePercent() != null && preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0 ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapPropertyConstants.TAX_STATE_PERCENT);
            }
        }

        // if choose Special W-4, cannot choose tax treaty, gross up, and foreign source; income class shall be fellowship with tax rates 0
        if (preq.getTaxSpecialW4Amount() != null && preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) != 0 ) {
            if (ObjectUtils.equals(preq.getTaxExemptTreatyIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_EXEMPT_TREATY_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxGrossUpIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_GROSS_UP_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_GROSS_UP_INDICATOR);
            }               
            if (ObjectUtils.equals(preq.getTaxForeignSourceIndicator(), true)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_FOREIGN_SOURCE_INDICATOR);
            }   
            //if Exempt Under Other Code box is not checked then validation error...
            //KFSMI-8415..
            if (ObjectUtils.equals(preq.getTaxOtherExemptIndicator(), false)) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR, PurapKeyConstants.ERROR_PAYMENT_REQUEST_EXEMPT_UNDER_OTHER_CODE_MUST_EXIST, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_OTHER_EXEMPT_INDICATOR);
            }   
            if (preq.getTaxSpecialW4Amount().compareTo(new KualiDecimal(0)) < 0) { 
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_MUST_NOT_NEGATIVE, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT);
            }                          
            if (StringUtils.isEmpty(preq.getTaxClassificationCode()) || !StringUtils.equalsIgnoreCase(preq.getTaxClassificationCode(), "F")) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_CLASSIFICATION_CODE);
            }
            if (preq.getTaxFederalPercent() != null && preq.getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0 ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
            }
            if (preq.getTaxStatePercent() != null && preq.getTaxStatePercent().compareTo(new BigDecimal(0)) != 0 ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_SPECIAL_W4_AMOUNT, PurapPropertyConstants.TAX_STATE_PERCENT);
            }
        }

        return valid;
    }

    /**
     * Initiates the federal and state tax rate maps for the purpose of validation on tax rates.
     *
    private void loadTaxRates() {        
        Collection<TaxIncomeClassCode> incomeClasses = retrieveAllTaxIncomeClasses();
        for (TaxIncomeClassCode incomeClass : incomeClasses) {
            String incomeCode = incomeClass.getCode();
            ArrayList<BigDecimal> fedrates = retrieveTaxRates(incomeCode, "F"); // federal rates
            federalTaxRates.put(incomeCode, fedrates);
            ArrayList<BigDecimal> strates = retrieveTaxRates(incomeCode, "S"); // state rates
            federalTaxRates.put(incomeCode, strates);
        }        
    }
    */
    
    /**
     * Retrieves all valid tax income classes in the system.
     *
    public Collection<TaxIncomeClassCode> retrieveAllTaxIncomeClasses() {
        return businessObjectService.findAll(TaxIncomeClassCode.class);
    }
    */
    
    /**
     * Retrieve active NonResidentAlien tax rate percent from database based on the specified income class and federal/state tax type.
     * @param incomeClassCode The specified income class type code.
     * @param incomeTaxTypeCode The specified income tax type code.
     * @return The array list containing the tax rates retrieved.
     */
    public ArrayList<BigDecimal> retrieveTaxRates(String incomeClassCode, String incomeTaxTypeCode) {
        ArrayList<BigDecimal> rates = new ArrayList<BigDecimal>();
        Map<String, String> criterion = new HashMap<String, String>();
        criterion.put("incomeClassCode", incomeClassCode);
        criterion.put("incomeTaxTypeCode", incomeTaxTypeCode);
        criterion.put("active", "Y"); // only retrieve active tax percents
        List<NonResidentAlienTaxPercent> percents = (List<NonResidentAlienTaxPercent>)businessObjectService.findMatching(NonResidentAlienTaxPercent.class, criterion);
        
        for (NonResidentAlienTaxPercent percent : percents) {
            rates.add(percent.getIncomeTaxPercent().bigDecimalValue());
        }        
        return rates;
    }
    
    /**
     * Returns true if the specified ArrayList contains the specified BigDecimal value.
     * @param list the specified ArrayList
     * @param value the specified BigDecimal
     */
    protected boolean listContainsValue(ArrayList<BigDecimal> list, BigDecimal value) {
        if (list == null || value == null)
            return false;
        for (BigDecimal val : list) {
            if (val.compareTo(value) == 0)
                return true;
        }
        return false;     
    }
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }    
}
