/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

public class SecurityRule extends MaintenanceDocumentRuleBase {

    protected static Logger LOG = org.apache.log4j.Logger.getLogger(SecurityRule.class);
    private Security newSecurity;
    private Security oldSecurity;

    /**
     * This method initializes the old and new security.
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newSecurity == null) {
            newSecurity = (Security) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldSecurity == null) {
            oldSecurity = (Security) document.getOldMaintainableObject().getBusinessObject();
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();

        if (isValid) {

            initializeAttributes(document);
            isValid &= checkClassCode();
            isValid &= checkCustomRequiredFields();
            isValid &= checkUnitValue();
            isValid &= checkValuesBasedOnValuationMethod();
            isValid &= checkIncomeFrequencyCodeWhenPooledFundClassCodeUsed();            
        }

        return isValid;
    }

    /**
     * Checks the required fields based on user input data. If the Security Class Code for the new security record has a Security
     * Accrual Method of 3,6,B,M or T the following fields are required input: 1. Issue Date 2. Maturity Date 3. Interest Rate or
     * Amount 4. Income Pay Frequency. If the Class Code Type is an Alternative Investment, then the Commitment Amount field is
     * required.
     * 
     * @param newSecurity
     * @return true if required fields entered, false otherwise
     */
    private boolean checkCustomRequiredFields() {

        boolean isValid = true;
        ClassCode classCode = newSecurity.getClassCode();
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

        if (ObjectUtils.isNotNull(classCode) && StringUtils.isNotEmpty(classCode.getSecurityAccrualMethod())) {

            String accrualMethod = classCode.getSecurityAccrualMethod();

            // if accrual method is 3,6,B,M,T
            if (accrualMethod.equalsIgnoreCase(EndowConstants.AccrualMethod.MORTGAGE_30) || accrualMethod.equalsIgnoreCase(EndowConstants.AccrualMethod.MORTGAGE_60) || accrualMethod.equalsIgnoreCase(EndowConstants.AccrualMethod.DISCOUNT_BONDS) || accrualMethod.equalsIgnoreCase(EndowConstants.AccrualMethod.TIME_DEPOSITS) || accrualMethod.equalsIgnoreCase(EndowConstants.AccrualMethod.TREASURY_NOTES_AND_BONDS)) {

                // maturity date required
                if (ObjectUtils.isNull(newSecurity.getMaturityDate())) {

                    String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Security.class.getName()).getAttributeDefinition(EndowPropertyConstants.SECURITY_MATURITY_DATE).getLabel();
                    putFieldError(EndowPropertyConstants.SECURITY_MATURITY_DATE, KFSKeyConstants.ERROR_REQUIRED, label);
                    isValid = false;
                }

                // income pay frequency required
                if (StringUtils.isEmpty(newSecurity.getIncomePayFrequency())) {

                    String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Security.class.getName()).getAttributeDefinition(EndowPropertyConstants.SECURITY_INCOME_PAY_FREQUENCY).getLabel();
                    putFieldError(EndowPropertyConstants.SECURITY_INCOME_PAY_FREQUENCY, KFSKeyConstants.ERROR_REQUIRED, label);
                    isValid = false;
                }

                // income rate required
                if (ObjectUtils.isNull(newSecurity.getIncomeRate())) {

                    String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Security.class.getName()).getAttributeDefinition(EndowPropertyConstants.SECURITY_INCOME_RATE).getLabel();
                    putFieldError(EndowPropertyConstants.SECURITY_INCOME_RATE, KFSKeyConstants.ERROR_REQUIRED, label);
                    isValid = false;
                }

                // issue date required
                if (ObjectUtils.isNull(newSecurity.getIssueDate())) {

                    String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Security.class.getName()).getAttributeDefinition(EndowPropertyConstants.SECURITY_ISSUE_DATE).getLabel();
                    putFieldError(EndowPropertyConstants.SECURITY_ISSUE_DATE, KFSKeyConstants.ERROR_REQUIRED, label);
                    isValid = false;
                }

            }

            // if security class code type is Alternative Investment the Commitment Amount is required
            if (ObjectUtils.isNotNull(classCode) && EndowConstants.ClassCodeTypes.ALTERNATIVE_INVESTMENT.equalsIgnoreCase(classCode.getClassCodeType())) {
                if (ObjectUtils.isNull(newSecurity.getCommitmentAmount()) || newSecurity.getCommitmentAmount().compareTo(BigDecimal.ZERO) == 0) {

                    String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Security.class.getName()).getAttributeDefinition(EndowPropertyConstants.SECURITY_COMMITMENT_AMOUNT).getLabel();
                    putFieldError(EndowPropertyConstants.SECURITY_COMMITMENT_AMOUNT, KFSKeyConstants.ERROR_REQUIRED, label);
                    isValid = false;
                }
            }

        }
        return isValid;
    }

    /**
     * This method checks if security unit value is valid. The unit value must always be greater than or equal to zero EXCEPT for
     * liabilities which must always be less than or equal to zero.
     * 
     * @return true if valid, false otherwise
     */
    private boolean checkUnitValue() {
        boolean isValid = true;
        ClassCode classCode = newSecurity.getClassCode();

        if (ObjectUtils.isNotNull(classCode)) {

            BigDecimal unitValue = newSecurity.getUnitValue();

            if (ObjectUtils.isNotNull(unitValue) && EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCode.getClassCodeType())) {

                if (unitValue.compareTo(BigDecimal.ZERO) == 1) {
                    putFieldError(EndowPropertyConstants.SECURITY_UNIT_VALUE, EndowKeyConstants.SecurityConstants.ERROR_SECURITY_UNIT_VALUE_LESS_THAN_OR_EQ_ZERO_FOR_LIABILITIES);
                    isValid = false;
                }
            }
            else if (ObjectUtils.isNotNull(unitValue) && unitValue.compareTo(BigDecimal.ZERO) == -1) {
                putFieldError(EndowPropertyConstants.SECURITY_UNIT_VALUE, EndowKeyConstants.SecurityConstants.ERROR_SECURITY_UNIT_VALUE_LESS_THAN_OR_EQ_ZERO_FOR_NON_LIABILITIES);
                isValid = false;
            }

        }
        return isValid;
    }

    /**
     * This method checks that the old and new Class Codes belong to the same Class Code Type.
     * 
     * @return true if they belong to the same class code type, false otherwise.
     */
    private boolean checkClassCode() {
        boolean isValid = true;

        newSecurity.refreshReferenceObject(EndowPropertyConstants.SECURITY_CLASS_CODE_REF);

        if (ObjectUtils.isNotNull(oldSecurity) && ObjectUtils.isNotNull(oldSecurity.getClassCode()) && ObjectUtils.isNotNull(newSecurity.getClassCode())) {
            String oldClassCodeType = oldSecurity.getClassCode().getClassCodeType();
            if (!oldClassCodeType.equalsIgnoreCase(newSecurity.getClassCode().getClassCodeType())) {
                putFieldError(EndowPropertyConstants.SECURITY_CLASS_CODE, EndowKeyConstants.SecurityConstants.EROR_NEW_SECURITY_CLASS_CODE_TYPE_MUST_EQUAL_OLD_SEC_CLASS_CODE_TYPE);
            }
        }

        return isValid;
    }

    /**
     * Checks the following two rules: 5. If the class code for the security has a valuation method of U (Unit Value), the user can
     * only enter a value in the SEC_UNIT_VAL. No entry is allowed in the SEC_VAL_BY_MKT field. 8. If the class code for the
     * security has a valuation method of M (Market Value), the user can only enter a value in the SEC_ SEC_VAL_BY_MKT field. No
     * entry is allowed in the SEC_UNIT_VAL field.
     * 
     * @return
     */
    private boolean checkValuesBasedOnValuationMethod() {

        boolean isValid = true;

        newSecurity.refreshReferenceObject(EndowPropertyConstants.SECURITY_CLASS_CODE_REF);
        ClassCode classCode = newSecurity.getClassCode();

        // If the class code for the security has a valuation method of U (Unit Value), the user can only enter a value in the
        // SEC_UNIT_VAL. No entry is allowed in the SEC_VAL_BY_MKT field.
        if (classCode != null && EndowConstants.ValuationMethod.UNITS.equalsIgnoreCase((classCode.getValuationMethod()))) {
            if (newSecurity.getMarketValue() != null) {
                putFieldError(EndowPropertyConstants.SECURITY_VALUE_BY_MARKET, EndowKeyConstants.SecurityConstants.ERROR_SECURITY_VAL_BY_MKT_MUST_BE_EMPTY_WHEN_VAL_MTHD_UNITS);
            }
        }
        // If the class code for the security has a valuation method of M (Market Value), the user can only enter a value in the
        // SEC_ SEC_VAL_BY_MKT field. No entry is allowed in the SEC_UNIT_VAL field.
        if (classCode != null && EndowConstants.ValuationMethod.MARKET.equalsIgnoreCase((classCode.getValuationMethod()))) {
            if (newSecurity.getUnitValue() != null) {
                putFieldError(EndowPropertyConstants.SECURITY_UNIT_VALUE, EndowKeyConstants.SecurityConstants.ERROR_SECURITY_UNIT_VAL_MUST_BE_EMPTY_WHEN_VAL_MTHD_MARKET);
            }
        }

        return isValid;
    }

    protected boolean checkIncomeFrequencyCodeWhenPooledFundClassCodeUsed() {

        boolean isValid = true;
        
        newSecurity.refreshReferenceObject(EndowPropertyConstants.SECURITY_CLASS_CODE_REF);
        ClassCode classCode = newSecurity.getClassCode();

        if (classCode.getClassCodeType() != null && classCode.getClassCodeType().equalsIgnoreCase(EndowConstants.ClassCodeTypes.POOLED_INVESTMENT)) {
            String incomePayFrequencyCode = newSecurity.getIncomePayFrequency();
            
            if (StringUtils.isEmpty(incomePayFrequencyCode)) {
                isValid = false;
                putFieldError(EndowPropertyConstants.SECURITY_INCOME_PAY_FREQUENCY, EndowKeyConstants.SecurityConstants.ERROR_SECURITY_INCOME_PAY_FREQUENCY_CODE_NOT_ENTERED);
            }
        }
        
        return isValid;
    }
}
