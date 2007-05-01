/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.vendor.service.impl;

import org.kuali.core.bo.BusinessRule;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.FormatException;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorRuleConstants;
import org.kuali.module.vendor.service.TaxNumberService;

public class TaxNumberServiceImpl implements TaxNumberService {
    
    public KualiConfigurationService kualiConfigurationService;
   
    
    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
   
    
    public static BusinessRule taxNumberFormats;
    public static BusinessRule feinNumberFormats;
    public static BusinessRule notAllowedTaxNumbers;
    
        
    public  String formatToDefaultFormat(String taxNbr) throws FormatException {
        String digits = taxNbr.replaceAll("\\D", "");
        
        Integer defaultTaxNumberDigits = 
            new Integer (SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("PurapAdminGroup","PURAP.DEFAULT_TAX_NUM_DIGITS"));

        if (digits.length() < defaultTaxNumberDigits) {
            throw new FormatException("Tax number has fewer than " + defaultTaxNumberDigits + " digits.", KFSKeyConstants.ERROR_CUSTOM, taxNbr);
        }
        else if (digits.length() > defaultTaxNumberDigits) {
            throw new FormatException("Tax number has more than " + defaultTaxNumberDigits + " digits.", KFSKeyConstants.ERROR_CUSTOM, taxNbr);
        }
        else {
            return digits;
        }
    }

    /**
     * A predicate to determine if a String field is all numbers
     * 
     * @param field A String tax number
     * @return True if String is numeric
     */
    public  boolean isStringAllNumbers(String field) {
        if (!isStringEmpty(field)) {
            field = field.trim();
            for (int x = 0; x < field.length(); x++) {
                char c = field.charAt(x);
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * A predicate to determine if a String field is null or empty
     * 
     * @param field A String tax number
     * @return True if String is null or empty
     */
    public  boolean isStringEmpty(String field) {
        if (field == null || field.equals("")) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * A predicate to determine the validity of tax numbers
     * We're using regular expressions stored in the business rules table 
     * to validate whether the tax number is in the correct format.
     * The regular expressions are : (please update this javadoc comment
     * when the regular expressions change)
     * 1. For SSN : (?!000)(?!666)(\d{3})([ \-]?)(?!00)(\d{2})([\-]?)(?!0000)(\d{4})
     * 2. For FEIN : (?!00)(\d{3})([ \-]?)(\d{2})([\-]?)(?!0000)(\d{4})
     * 
     * @param taxNbr     A tax number String (SSN or FEIN)
     * @param taxType    determines SSN or FEIN tax number type
     * @return          True if the tax number is known to be in a valid format
     */
    public  boolean isValidTaxNumber( String taxNbr, String taxType ) {       
        String[] ssnFormats = parseSSNFormats();
        String[] feinFormats = parseFEINFormats();
        Integer defaultTaxNumberDigits = 
            new Integer (SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("PurapAdminGroup","PURAP.DEFAULT_TAX_NUM_DIGITS"));

        if (taxNbr.length() != defaultTaxNumberDigits || 
                !isStringAllNumbers(taxNbr)) {
            return false;
        }
        
        if (taxType.equals(VendorConstants.TAX_TYPE_SSN)) {
        
            for( int i = 0; i < ssnFormats.length; i++ ) {
                if( taxNbr.matches( ssnFormats[i] ) ){
                    return true;
                }
            }
        return false;
        }
        else if (taxType.equals(VendorConstants.TAX_TYPE_FEIN)) {
            for( int i = 0; i < feinFormats.length; i++ ) {
                if( taxNbr.matches( feinFormats[i] ) ){
                    return true;
                }
            }
        return false;
        }
      
        return true;
    }
    
    
    /**
     * Someday we'll have to use the rules table instead of
     * using constants.
     * This method will return true if the tax number is an allowed tax number
     * and return false if it's not allowed.
     * 
     * @param taxNbr The tax number to be processed.
     * @return boolean true if the tax number is allowed and false otherwise.
     */
    public  boolean isAllowedTaxNumber(String taxNbr) {
        String[] notAllowedTaxNumbers = parseNotAllowedTaxNumbers();
        for (int i=0; i<notAllowedTaxNumbers.length; i++) {
            if (taxNbr.matches(notAllowedTaxNumbers[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits the set of tax number formats which are returned from the rule service as a
     * semicolon-delimeted String into a String array.
     * 
     * @return A String array of the tax number format regular expressions.
     */
    public  String[] parseSSNFormats() {
        if( ObjectUtils.isNull( taxNumberFormats ) ) {
            taxNumberFormats = SpringServiceLocator.getKualiConfigurationService().getApplicationRule(
                    "PurapAdminGroup","PURAP.TAX_NUMBER_FORMATS");
        }
        String taxFormats = taxNumberFormats.getRuleText();
        return taxFormats.split(";");
    }
    
    /**
     * Splits the set of tax fein number formats which are returned from the rule service as a
     * semicolon-delimeted String into a String array.
     * 
     * @return A String array of the tax fein number format regular expressions.
     */
    public  String[] parseFEINFormats() {
        if( ObjectUtils.isNull( feinNumberFormats ) ) {
            feinNumberFormats = SpringServiceLocator.getKualiConfigurationService().getApplicationRule(
                    "PurapAdminGroup","PURAP.TAX_FEIN_NUMBER_FORMATS");
        }
        String feinFormats = feinNumberFormats.getRuleText();
        return feinFormats.split(";");
    }

    /**
     * Splits the set of not allowed tax number formats which are returned from the rule service as a
     * semicolon-delimeted String into a String array.
     * 
     * @return A String array of the not allowed tax number format regular expressions.
     */
    public String[] parseNotAllowedTaxNumbers() {
        if( ObjectUtils.isNull( notAllowedTaxNumbers ) ) {
            notAllowedTaxNumbers = SpringServiceLocator.getKualiConfigurationService().getApplicationRule(
                    VendorRuleConstants.PURAP_ADMIN_GROUP,VendorRuleConstants.PURAP_NOT_ALLOWED_TAX_NUMBERS);
        }
        return notAllowedTaxNumbers.getRuleText().split(";");
    }

}
