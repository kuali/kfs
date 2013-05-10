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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.impl.FrequencyCodeServiceImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class SecurityDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenPropertyNames(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenPropertyNames(businessObject);
        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        Security security = (Security) document.getNewMaintainableObject().getBusinessObject();
        String incomePayFrequencyCode = security.getIncomePayFrequency();
        
        if (StringUtils.isNotEmpty(incomePayFrequencyCode)) {
            FrequencyCodeServiceImpl frequencyCodeServiceImpl = (FrequencyCodeServiceImpl) SpringContext.getBean(FrequencyCodeServiceImpl.class);
            security.setIncomeNextPayDate(frequencyCodeServiceImpl.calculateProcessDate(incomePayFrequencyCode));
        }
        //KFSMI-6674
        //If SEC_INC_PAY_FREQ entered then the SEC_INC_NEXT_PAY_DT is 
        //automatically calculated.
        //if class code type is stocks and SEC_DIV_PAY_DT is entered then 
        //copy the date value to SEC_INC_NEXT_PAY_DT.
        //We do not want to overwrite the date if it already exists.
        if (ObjectUtils.isNotNull(security.getClassCode()) && EndowConstants.ClassCodeTypes.STOCKS.equalsIgnoreCase(security.getClassCode().getClassCodeType())) {
            if (security.getDividendPayDate() != null) {
                security.setIncomeNextPayDate(security.getDividendPayDate());
            }
        }

        // when we create or copy a new Security, only certain fields are displayed; the following code is used to hide the unwanted
        // fields
        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()) || KRADConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {

            // the security ID hidded on creation and a dummy field is used for user input (userEnteredSecurityIDprefix)
            String routeStatus = document.getDocumentHeader().getWorkflowDocument().getStatus().getCode();
            if (KewApiConstants.ROUTE_HEADER_INITIATED_CD.equalsIgnoreCase(routeStatus) || KewApiConstants.ROUTE_HEADER_SAVED_CD.equalsIgnoreCase(routeStatus)) {
                fields.add(EndowPropertyConstants.SECURITY_ID);
            }
            else {
                fields.add(EndowPropertyConstants.SECURITY_USER_ENTERED_ID_PREFIX);
            }

            fields.add(EndowPropertyConstants.SECURITY_UNIT_VALUE);
            fields.add(EndowPropertyConstants.SECURITY_UNITS_HELD);
            fields.add(EndowPropertyConstants.SECURITY_VALUATION_DATE);
            fields.add(EndowPropertyConstants.SECURITY_UNIT_VALUE_SOURCE);
            fields.add(EndowPropertyConstants.SECURITY_PREVIOUS_UNIT_VALUE);
            fields.add(EndowPropertyConstants.SECURITY_PREVIOUS_UNIT_VALUE_DATE);
            fields.add(EndowPropertyConstants.SECURITY_CARRY_VALUE);
            fields.add(EndowPropertyConstants.SECURITY_MARKET_VALUE);
            fields.add(EndowPropertyConstants.SECURITY_LAST_TRANSACTION_DATE);
            fields.add(EndowPropertyConstants.SECURITY_INCOME_NEXT_PAY_DATE);
            fields.add(EndowPropertyConstants.SECURITY_INCOME_CHANGE_DATE);
            fields.add(EndowPropertyConstants.SECURITY_DIVIDEND_RECORD_DATE);
            fields.add(EndowPropertyConstants.SECURITY_EX_DIVIDEND_DATE);
            fields.add(EndowPropertyConstants.SECURITY_DIVIDEND_PAY_DATE);
            fields.add(EndowPropertyConstants.SECURITY_DIVIDEND_AMOUNT);
            fields.add(EndowPropertyConstants.REPORTING_GROUP_DESC);
            fields.add(EndowPropertyConstants.ACCRUAL_METHOD_DESC);
            fields.add(EndowPropertyConstants.SECURITY_NEXT_FISCAL_YEAR_DISTRIBUTION_AMOUNT);
            fields.add(EndowPropertyConstants.SECURITY_VALUE_BY_MARKET);

        }
        // if action is not new or copy the userEnteredSecurityIDprefix shall not be displayed
        else {
            fields.add(EndowPropertyConstants.SECURITY_USER_ENTERED_ID_PREFIX);
        }

        return fields;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {

        Set<String> fields = super.getConditionallyReadOnlyPropertyNames(document);
        Security security = (Security) document.getNewMaintainableObject().getBusinessObject();
        ClassCode classCode = security.getClassCode();

        // If the class code type = "P" -- pooled investment:
        // - the unit value and value date in the security can't be modified through editing that maintenance doc
        // - END_SEC_T: SEC_RT should NOT be modified through edit that maintenance doc
        if (classCode != null && EndowConstants.ClassCodeTypes.POOLED_INVESTMENT.equalsIgnoreCase(classCode.getClassCodeType()) && KRADConstants.MAINTENANCE_EDIT_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
            fields.add(EndowPropertyConstants.SECURITY_UNIT_VALUE);
            fields.add(EndowPropertyConstants.SECURITY_MARKET_VALUE);
            fields.add(EndowPropertyConstants.SECURITY_VALUATION_DATE);
            fields.add(EndowPropertyConstants.SECURITY_INCOME_RATE);
        }

        // The default unit value for a new security is 1 EXCEPT for Liabilities (CLS_CD_T: CLS_CD_TYP = L) which will be negative 1
        // (-1). The unit value for these securities must remain -1. The unit value for these securities cannot be edited.
        if (classCode != null && EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCode.getClassCodeType())) {
            fields.add(EndowPropertyConstants.SECURITY_UNIT_VALUE);
        }

        return fields;
    }

}
