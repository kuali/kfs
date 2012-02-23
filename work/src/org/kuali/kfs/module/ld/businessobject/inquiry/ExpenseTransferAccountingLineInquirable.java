/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.businessobject.inquiry;

import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.krad.bo.BusinessObject;

public class ExpenseTransferAccountingLineInquirable extends KfsInquirableImpl {

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if ( businessObject instanceof ExpenseTransferSourceAccountingLine ||
                businessObject instanceof ExpenseTransferTargetAccountingLine  ) {
        if (attributeName.equalsIgnoreCase("fringeBenefitView")) {
            Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
            String parameters = KFSConstants.EMPTY_STRING;

            if(businessObject instanceof ExpenseTransferSourceAccountingLine ){
                ExpenseTransferSourceAccountingLine sourceAccountingLine = (ExpenseTransferSourceAccountingLine) businessObject;
                parameters = "chartOfAccountsCode="+sourceAccountingLine.getChartOfAccountsCode()+
                "&financialObjectCode="+sourceAccountingLine.getObjectCode().getFinancialObjectCode()+
                "&payrollEndDateFiscalYear="+sourceAccountingLine.getPayrollEndDateFiscalYear()+
                "&amount="+sourceAccountingLine.getAmount()+
                "&methodToCall=calculateFringeBenefit";

            }
            else if(businessObject instanceof ExpenseTransferTargetAccountingLine ){
                ExpenseTransferTargetAccountingLine targetAccountingLine = (ExpenseTransferTargetAccountingLine) businessObject;
                parameters = "chartOfAccountsCode="+targetAccountingLine.getChartOfAccountsCode()+
                "&financialObjectCode="+targetAccountingLine.getObjectCode().getFinancialObjectCode()+
                "&payrollEndDateFiscalYear="+targetAccountingLine.getPayrollEndDateFiscalYear()+
                "&amount="+targetAccountingLine.getAmount()+
                "&methodToCall=calculateFringeBenefit";
            }

            String fieldValue = objFieldValue == null ? KFSConstants.EMPTY_STRING : objFieldValue.toString();
            final String url =  SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)+"/fringeBenefitInquiry.do?" + parameters ;

            return new AnchorHtmlData(url, "", "");
        }

        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);

    }

}
