/*
 * Copyright 2010 The Kuali Foundation.
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
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityValuationMethod;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.ObjectUtils;

public class HoldingHistoryValueAdjustmentDocumentRules extends EndowmentTransactionalDocumentBaseRule {

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        
        HoldingHistoryValueAdjustmentDocument holdingHistoryValueAdjustmentDocument = (HoldingHistoryValueAdjustmentDocument) document;
        
        boolean isValid = true;
        
        if (this.isSecurityCodeEmpty(holdingHistoryValueAdjustmentDocument)) {
            return false;
        }
        if (!this.validateSecurityCode(holdingHistoryValueAdjustmentDocument)) {
            return false;
        }

        // Checks if Security is Active
        isValid &= this.isSecurityActive(holdingHistoryValueAdjustmentDocument);
        // check if it is Liability class type code for the given security id
        isValid &= this.validateSecurityClassCodeTypeNotLiability(holdingHistoryValueAdjustmentDocument);
        // check if the unit value is a positve value
        isValid &= this.isUnitValuePositive(holdingHistoryValueAdjustmentDocument);
        // check if the market value is a positive value
        isValid &= this.isMarketValuePositive(holdingHistoryValueAdjustmentDocument);   
        // check the valuation method for Unit value and make sure market value is not entered.
        isValid &= this.checkValuationMethodForUnitOrSecurityValue(holdingHistoryValueAdjustmentDocument);        
        
        return isValid;
    }

    /**
     * Validates the Security code to make sure the value is not empty.
     * 
     * @param tranSecurity
     * @return true if security code is empty, else false.
     */
    protected boolean isSecurityCodeEmpty(HoldingHistoryValueAdjustmentDocument document) {

        if (StringUtils.isEmpty(document.getSecurityId())) {
            putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_REQUIRED);
            return true;
        }

        return false;
    }

    /**
     * Validates the Security code by trying to create a Security object from the code.
     * 
     * @param document
     * @return true if security code is valid one, false otherwise
     */
    protected boolean validateSecurityCode(HoldingHistoryValueAdjustmentDocument document) {
        Security security = (Security) SpringContext.getBean(SecurityService.class).getByPrimaryKey(document.getSecurityId());
        document.setSecurity(security);
        
        if (ObjectUtils.isNull(security)) {
            putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_INVALID);
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if the Security is Active.
     * 
     * @param document
     * @return true if the security is active, else false
     */
    protected boolean isSecurityActive(HoldingHistoryValueAdjustmentDocument document) {
        Security security = document.getSecurity();
        
        if (!security.isActive()) {
            putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_INACTIVE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates that the security class code type is not Liability.
     * 
     * @param document
     * @return true is valid, false otherwise
     */
    protected boolean validateSecurityClassCodeTypeNotLiability(HoldingHistoryValueAdjustmentDocument document) {
        boolean isValid = true;
        
        Security security = document.getSecurity();
        
        ClassCode classCode = security.getClassCode();
        if (ObjectUtils.isNotNull(classCode)) {
            String classCodeType = classCode.getClassCodeType();
            if (EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCodeType)) {
                isValid = false;
                putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_NOT_LIABILITY);
            }
        }

        return isValid;

    }
    
    /**
     * Checks if the unit value entered is positive value
     * @param document
     * @return true if positive, else false
     */
    protected boolean isUnitValuePositive(HoldingHistoryValueAdjustmentDocument document) {
        
        if (ObjectUtils.isNotNull(document.getSecurityUnitValue()) && document.getSecurityUnitValue().compareTo(BigDecimal.ZERO) != 1) {
            putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE_NOT_POSITIVE);            
            return false;
        }
        
        return true;
    }

    /**
     * Checks if the market value entered is positive value
     * @param document
     * @return true if positive, else false
     */
    protected boolean isMarketValuePositive(HoldingHistoryValueAdjustmentDocument document) {
        
        if (ObjectUtils.isNotNull(document.getSecurityMarketValue()) && document.getSecurityMarketValue().compareTo(BigDecimal.ZERO) != 1) {
            putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE_NOT_POSITIVE);            
            return false;
        }
        
        return true;
    }
    

    /**
     * Checks if security valuation method is Unit value and if so make sure nothing entered for Market Value
     * @param document
     * @return true if only Unit Value is entered, else false
     */
    protected boolean checkValuationMethodForUnitOrSecurityValue(HoldingHistoryValueAdjustmentDocument document) {
        // check if the valuation method is U (unit value) and if so, then make sure no value is entered for market value.
        if (EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_VALUATION_METHOD_FOR_UNIT_VALUE.equals(document.getSecurityValuationMethod())) {
            if (ObjectUtils.isNotNull(document.getSecurityMarketValue())) {
                putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE_ENTERED_WHEN_VALUATION_METHOD_IS_UNIT_VALUE);
                return false;
            }
        }

        // check if the valuation method is M (market value) and if so, then make sure no value is entered for unit value.        
        if (EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_VALUATION_METHOD_FOR_MARKET_VALUE.equals(document.getSecurityValuationMethod())) {
            if (ObjectUtils.isNotNull(document.getSecurityUnitValue())) {
                putFieldError(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE_ENTERED_WHEN_VALUATION_METHOD_IS_MARKET_VALUE);
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        return isValid;
    }
}
