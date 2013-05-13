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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class ClassCodeRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClassCodeRule.class);

    private ClassCode newClassCode;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        newClassCode = (ClassCode) super.getNewBo();
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

            isValid = validateClassCodeType(newClassCode);
            isValid = validateTransactionTypeCode(newClassCode);
            isValid = validateIncomneTransactionPostTypeCode(newClassCode);
            isValid = validateValuationMethodForPooledInvestments(newClassCode);
        }

        return isValid;
    }

    /**
     * This method validates that if the Class Code Type is Liability, the Security Transaction Code must be a Liability Type Code.
     * Also it checks if endowment transaction type code is Asset or Liability. Also
     * 
     * @param classCode
     * @return true if endowment transaction type code is Asset or Liability, false otherwise
     */
    public boolean validateTransactionTypeCode(ClassCode classCode) {
        boolean success = true;
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        classCode.refreshReferenceObject(EndowPropertyConstants.CLASS_CODE_ENDOWMENT_TRANSACTION);

        if (ObjectUtils.isNotNull(classCode.getEndowmentTransactionCode())) {

            String endTransactionTypeCode = classCode.getEndowmentTransactionCode().getEndowmentTransactionTypeCode();

            // If the Class Code is a Liability, the ETRAN code must be a Liability Type Code.
            if (EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCode.getClassCodeType())) {
                if (ObjectUtils.isNotNull(classCode.getEndowmentTransactionCode()) && !(EndowConstants.EndowmentTransactionTypeCodes.LIABILITY_TYPE_CODE.equalsIgnoreCase(classCode.getEndowmentTransactionCode().getEndowmentTransactionTypeCode()))) {
                    putFieldError(EndowPropertyConstants.CLASS_CODE_SEC_END_TRANSACTION_CODE, EndowKeyConstants.ClassCodeConstants.ERROR_CLASS_CODE_TYPE_LIABILITY_MUST_HAVE_SEC_ETRAN_TYPE_LIABILITY);
                    success = false;
                }
            }
            else if (!(EndowConstants.EndowmentTransactionTypeCodes.ASSET_TYPE_CODE.equals(endTransactionTypeCode) || EndowConstants.EndowmentTransactionTypeCodes.LIABILITY_TYPE_CODE.equals(endTransactionTypeCode))) {
                putFieldError(EndowPropertyConstants.CLASS_CODE_SEC_END_TRANSACTION_CODE, EndowKeyConstants.ClassCodeConstants.ERROR_ENDOWMENT_TRANSACTION_TYPE_ASSET_OR_LIABILITY);
                success = false;
            }
        }

        return success;
    }

    /**
     * This method checks if income endowment transaction post type code is Income.
     * 
     * @param classCode
     * @return true if income endowment transaction post type code is Income, false otherwise
     */
    public boolean validateIncomneTransactionPostTypeCode(ClassCode classCode) {
        boolean success = false;
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        classCode.refreshReferenceObject(EndowPropertyConstants.CLASS_CODE_INCOME_ENDOWMENT_TRANSACTION_POST);

        if (ObjectUtils.isNotNull(classCode.getIncomeEndowmentTransactionPost())) {

            String incomeEndTransactionPostTypeCode = classCode.getIncomeEndowmentTransactionPost().getEndowmentTransactionTypeCode();

            if (EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE.equals(incomeEndTransactionPostTypeCode)) {

                success = true;
            }
            else {

                putFieldError(EndowPropertyConstants.CLASS_CODE_SEC_INCOME_END_TRANSACTION_CODE, EndowKeyConstants.ClassCodeConstants.ERROR_INCOME_ENDOWMENT_TRANSACTION_POST_TYPE_INCOME);
            }
        }

        return success;

    }

    /**
     * This method checks that security reporting group is Cash Equivalents when class code type is Cash Equivalents.
     * 
     * @param classCode
     * @return true if security reporting group is Cash Equivalents when class code type is Cash Equivalents, false otherwise.
     */
    public boolean validateClassCodeType(ClassCode classCode) {
        boolean success = true;

        if (EndowConstants.ClassCodeTypes.CASH_EQUIVALENTS.equalsIgnoreCase(classCode.getClassCodeType())) {
            if (!EndowConstants.SecurityReportingGroups.CASH_EQUIVALENTS.equalsIgnoreCase(classCode.getSecurityReportingGrp())) {

                putFieldError(EndowPropertyConstants.CLASS_CODE_SEC_REPORTING_GRP, EndowKeyConstants.ClassCodeConstants.ERROR_CLASS_CODE_TYPE_CASH_EQ_REP_GRPCASH_EQ);
                success = false;
            }
        }

        // If the class Code is C (Cash Equivalents) or L (Liabilities), the tax lot indicator must be No.
        if (EndowConstants.ClassCodeTypes.CASH_EQUIVALENTS.equalsIgnoreCase(classCode.getClassCodeType()) || EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCode.getClassCodeType())) {
            if (classCode.isTaxLotIndicator()) {

                putFieldError(EndowPropertyConstants.CLASS_CODE_TAX_LOT_INDICATOR, EndowKeyConstants.ClassCodeConstants.ERROR_CLASS_CODE_TYPE_CASHEQ_OR_LIABILITY_MUST_HAVE_TAX_LOT_IND_NO);
                success = false;
            }
        }
        return success;
    }

    /**
     * This method validates that the valuation method is Unit for class code with class code type P=Pooled Investment or B=Bond or
     * L=Liability.
     * 
     * @param classCode
     * @return true if valuation method is Unit for class code type P, false otherwise
     */
    public boolean validateValuationMethodForPooledInvestments(ClassCode classCode) {
        boolean success = true;

        // If class code type ="P" or "B" or "L", the value of END_SEC_VLTN_MTHD.VLTN_MTHD must be "U"
        if (EndowConstants.ClassCodeTypes.POOLED_INVESTMENT.equalsIgnoreCase(classCode.getClassCodeType()) || EndowConstants.ClassCodeTypes.BOND.equalsIgnoreCase(classCode.getClassCodeType()) || EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCode.getClassCodeType())) {
            if (!EndowConstants.ValuationMethod.UNITS.equalsIgnoreCase(classCode.getValuationMethod())) {

                putFieldError(EndowPropertyConstants.CLASS_CODE_VALUATION_METHOD, EndowKeyConstants.ClassCodeConstants.ERROR_CLASS_CODE_TYPE_POOLED_INVESTMENT_MUST_HAVE_VLTN_MTHD_UNITS);
                success = false;
            }
        }
        return success;
    }
}
