/*
 * Copyright 2009 The Kuali Foundation.
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
import java.sql.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.module.endow.util.ValidateLastDayOfMonth;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class PooledFundValueRule extends MaintenanceDocumentRuleBase {

    protected static Logger LOG = org.apache.log4j.Logger.getLogger(SecurityRule.class);
    private PooledFundValue newPooledFundValue;

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
            PooledFundValue newPooledFundValue = (PooledFundValue) document.getNewMaintainableObject().getBusinessObject();
            isValid &= checkCustomRequiredFields(newPooledFundValue);
        }

        return isValid;
    }

    protected boolean checkCustomRequiredFields(PooledFundValue newPooledFundValue) {
        boolean isValid = true;

        Date valuationDate = newPooledFundValue.getValuationDate();
        String pooledSecurityID = newPooledFundValue.getPooledSecurityID();
        BigDecimal unitValue = newPooledFundValue.getUnitValue();

        isValid &= isValuationDateLastDayOfMonth(valuationDate);
        isValid &= isValuationDateTheLatest(pooledSecurityID, valuationDate);
        isValid &= isUnitValuePositive(unitValue);
        BigDecimal zero = new BigDecimal(0);
        BigDecimal incomeDistributionPerUnit = newPooledFundValue.getIncomeDistributionPerUnit();
        Date distributeIncomeOnDate = newPooledFundValue.getDistributeIncomeOnDate();
        if (ObjectUtils.isNotNull(incomeDistributionPerUnit) && (incomeDistributionPerUnit.compareTo(zero) == 1)) {
            isValid &= isIncomeDistributionPerUnitNotNegative(incomeDistributionPerUnit);
            isValid &= isDateRequiredField(distributeIncomeOnDate, EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE, EndowKeyConstants.PooledFundValueConstants.ERROR_DISTRIBUTE_INCOME_ON_DATE_IS_REQUIRED_FIELD);
        }

        BigDecimal longTermGainLossDistributionPerUnit = newPooledFundValue.getLongTermGainLossDistributionPerUnit();
        Date distributeLongTermGainLossOnDate = newPooledFundValue.getDistributeLongTermGainLossOnDate();
        if (ObjectUtils.isNotNull(longTermGainLossDistributionPerUnit) && (longTermGainLossDistributionPerUnit.compareTo(zero) == 1)) {
            isValid &= isDateRequiredField(distributeLongTermGainLossOnDate, EndowPropertyConstants.DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE, EndowKeyConstants.PooledFundValueConstants.ERROR_DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE_IS_REQUIRED_FIELD);
        }

        BigDecimal shortTermGainLossDistributionPerUnit = newPooledFundValue.getShortTermGainLossDistributionPerUnit();
        Date distributeShortTermGainLossOnDate = newPooledFundValue.getDistributeShortTermGainLossOnDate();
        if (ObjectUtils.isNotNull(shortTermGainLossDistributionPerUnit) && (shortTermGainLossDistributionPerUnit.compareTo(zero) == 1)) {
            isValid &= isDateRequiredField(distributeShortTermGainLossOnDate, EndowPropertyConstants.DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE, EndowKeyConstants.PooledFundValueConstants.ERROR_DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE_IS_REQUIRED_FIELD);
        }


        return isValid;
    }

    /**
     * 
     * 
     */
    protected boolean isDateRequiredField(Date theDate, String errorField, String errorKey) {
        boolean isRequiredField = true;
        if (ObjectUtils.isNull(theDate)) {
            putFieldError(errorField, errorKey);
            isRequiredField = false;
        }
        return isRequiredField;
    }

    /**
     * Checks if the value of the valuation date is the last day of the month. If yes, return true; otherwise, return false.
     * 
     * @param valuationDate
     * @return true if valuationDate is the last day of the month, false otherwise
     */
    protected boolean isValuationDateLastDayOfMonth(Date valuationDate) {
        boolean isLastDay = true;
        if (!ValidateLastDayOfMonth.validateLastDayOfMonth(valuationDate)) {
            putFieldError(EndowPropertyConstants.VALUATION_DATE, EndowKeyConstants.PooledFundValueConstants.ERROR_VALUATION_DATE_IS_NOT_THE_END_OF_MONTH);
            isLastDay = false;
        }
        return isLastDay;
    }

    /**
     * The rule is "A new record cannot have a VAL_DT that is earlier than the record with the most recent VAL_DT".
     * 
     * @param pooledSecurityID
     * @param theValuationDate
     * @return true if valuationDate is the latest one for the specified pooledSecurityID, false otherwise.
     */
    protected boolean isValuationDateTheLatest(String pooledSecurityID, Date theValuationDate) {
        PooledFundValueService pooledFundValueService = SpringContext.getBean(PooledFundValueService.class);
        boolean isLatest = pooledFundValueService.isValuationDateTheLatest(pooledSecurityID, theValuationDate);
        if (!isLatest) {
            putFieldError(EndowPropertyConstants.VALUATION_DATE, EndowKeyConstants.PooledFundValueConstants.ERROR_VALUATION_DATE_IS_NOT_LATEST_ONE);
        }
        return isLatest;
    }

    /**
     * The rule is unitValue can only be positive because unitValue is a required field.
     * 
     * @param unitValue
     * @return true if unitValue is a positive value, false otherwise
     */
    protected boolean isUnitValuePositive(BigDecimal unitValue) {
        boolean isPositive = true;
        if (unitValue.compareTo(new BigDecimal(0)) != 1) {
            putFieldError(EndowPropertyConstants.SECURITY_UNIT_VALUE, EndowKeyConstants.PooledFundValueConstants.ERROR_UNIT_VALUE_IS_NOT_POSITIVE);
            isPositive = false;
        }
        return isPositive;
    }

    /**
     * The rule is incomeDistributionPerUnit can't be less than zero. Either zero or positive value is an accepted/valid input
     * because incomeDistributionPerUnit is not a required field.
     * 
     * @param incomeDistributionPerUnit
     * @return true if incomeDistributionPerUnit is zero or bigger than zero, false otherwise
     */
    protected boolean isIncomeDistributionPerUnitNotNegative(BigDecimal incomeDistributionPerUnit) {
        boolean isNotNegative = true;
        if ((incomeDistributionPerUnit.compareTo(new BigDecimal(0)) == 1) || (incomeDistributionPerUnit.compareTo(new BigDecimal(0)) == 0)) {
            return isNotNegative;
        }
        else {
            putFieldError(EndowPropertyConstants.INCOME_DISTRIBUTION_PER_UNIT, EndowKeyConstants.PooledFundValueConstants.ERROR_INCOME_DISTRIBUTION_PER_UNIT_IS_NOT_POSITIVE);
            return false;
        }
    }

}