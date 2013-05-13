/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class HoldingHistoryValueAdjustmentDocumentRules extends TransactionalDocumentRuleBase {

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
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
        // check the valuation method for Unit value and make sure market value is not entered.
        isValid &= this.checkValuationMethodForUnitOrSecurityValue(holdingHistoryValueAdjustmentDocument);
        // check if the unit value is a positive value
        isValid &= this.isUnitValuePositive(holdingHistoryValueAdjustmentDocument);
        // check if the market value is a positive value
        isValid &= this.isMarketValuePositive(holdingHistoryValueAdjustmentDocument);

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
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_REQUIRED);
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
        Security security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(document.getSecurityId());
        document.setSecurity(security);

        if (ObjectUtils.isNull(security)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_INVALID);
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
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_INACTIVE);
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
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_NOT_LIABILITY);
            }
        }

        return isValid;
    }

    /**
     * Checks if the unit value entered is positive value
     *
     * @param document
     * @return true if positive, else false
     */
    protected boolean isUnitValuePositive(HoldingHistoryValueAdjustmentDocument document) {
        if (ObjectUtils.isNotNull(document.getSecurityUnitValue()) && document.getSecurityUnitValue().compareTo(BigDecimal.ZERO) <= 0) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE_NOT_POSITIVE);
            return false;
        }

        return true;
    }

    /**
     * Checks if the market value entered is positive value
     *
     * @param document
     * @return true if positive, else false
     */
    protected boolean isMarketValuePositive(HoldingHistoryValueAdjustmentDocument document) {
        // reset Market value if unit valuation method is U (Unit value)
        resetMarketValueToNullWhenUnitValueEntered(document);

        if (ObjectUtils.isNotNull(document.getSecurityMarketValue()) && document.getSecurityMarketValue().compareTo(BigDecimal.ZERO) <= 0) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE_NOT_POSITIVE);
            return false;
        }

        return true;
    }

    /**
     * Checks if security valuation method is Unit value and if so make sure nothing entered for Market Value
     *
     * @param document
     * @return true if only Unit Value is entered, else false
     */
    protected boolean checkValuationMethodForUnitOrSecurityValue(HoldingHistoryValueAdjustmentDocument document) {
        String valuationMethodCode = document.getSecurity().getClassCode().getSecurityValuationMethod().getCode();

        // check if the valuation method is U (unit value) and if so, then make sure no value is entered for market value.
        if (EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_VALUATION_METHOD_FOR_UNIT_VALUE.equals(valuationMethodCode)) {
            if (ObjectUtils.isNull(document.getSecurityUnitValue())) {
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE_REQUIRED);
                return false;
            }

            if (ObjectUtils.isNotNull(document.getSecurityMarketValue())) {
                document.setSecurityMarketValue(null);
                return true;
            }
        }

        // check if the valuation method is M (market value) and if so, then make sure no value is entered for unit value.
        if (EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_VALUATION_METHOD_FOR_MARKET_VALUE.equals(valuationMethodCode)) {
            if (ObjectUtils.isNull(document.getSecurityMarketValue())) {
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS + EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE, EndowKeyConstants.HoldingHistoryValueAdjustmentConstants.ERROR_HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE_REQUIRED);
                return false;
            }

            if (ObjectUtils.isNotNull(document.getSecurityMarketValue())) {
                // calculate Unit value as per 5.6.2.1.2 in KEM Adjustment_Transactions+v.1.3 document...
                document.setSecurityUnitValue(calculateUnitValueWhenMarketValueEntered(document));
                return true;
            }
        }

        return true;
    }

    /**
     * Reset the value for Market Value that the user entered if the valuation method is U (Unit Value)
     *
     * @param document
     */
    protected void resetMarketValueToNullWhenUnitValueEntered(HoldingHistoryValueAdjustmentDocument document) {
        String valuationMethodCode = document.getSecurity().getClassCode().getSecurityValuationMethod().getCode();

        // check if the valuation method is U (unit value) and if so, then make sure no value is entered for market value.
        if (EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_VALUATION_METHOD_FOR_UNIT_VALUE.equals(valuationMethodCode)) {
            if (ObjectUtils.isNotNull(document.getSecurityMarketValue())) {
                document.setSecurityMarketValue(null);
            }
        }
    }

    /**
     * Calculates unit value when security id's valuation method is M and market value is entered.
     */
    protected BigDecimal calculateUnitValueWhenMarketValueEntered(HoldingHistoryValueAdjustmentDocument document) {
        BigDecimal unitValue = BigDecimal.ZERO;
        BigDecimal totalUnits = BigDecimal.ZERO;

        BigDecimal marketValue = document.getSecurityMarketValue();

        Collection<HoldingHistory> holdingHistoryRecords = SpringContext.getBean(HoldingHistoryService.class).getHoldingHistoryBySecuritIdAndMonthEndId(document.getSecurityId(), document.getHoldingMonthEndDate());
        for (HoldingHistory holdingHistory : holdingHistoryRecords) {
            totalUnits = totalUnits.add(holdingHistory.getUnits()); // sum up the units and store it
        }

        ClassCode classCode = document.getSecurity().getClassCode();

        if (ObjectUtils.isNotNull(classCode) && ObjectUtils.isNotNull(totalUnits) && totalUnits.compareTo(BigDecimal.ZERO) != 0) {
            if (EndowConstants.ClassCodeTypes.BOND.equalsIgnoreCase(classCode.getClassCodeType())) {
                unitValue = KEMCalculationRoundingHelper.divide((marketValue.multiply(new BigDecimal(100))), totalUnits, EndowConstants.Scale.SECURITY_UNIT_VALUE);
            }
            else {
                unitValue = KEMCalculationRoundingHelper.divide(marketValue, totalUnits, EndowConstants.Scale.SECURITY_UNIT_VALUE);
            }
        }

        return unitValue;
    }

}
