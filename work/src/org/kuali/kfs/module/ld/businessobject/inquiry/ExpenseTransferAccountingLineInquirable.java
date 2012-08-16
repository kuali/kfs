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

import java.util.Properties;

import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class ExpenseTransferAccountingLineInquirable extends KfsInquirableImpl {
    protected static final String FRINGE_BENEFIT_METHOD_TO_CALL = "calculateFringeBenefit";
    protected static final String FRINGE_BENEFIT_INQUIRY_PAGE_NAME = "/fringeBenefitInquiry.do";

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if ( businessObject instanceof ExpenseTransferSourceAccountingLine ||
                businessObject instanceof ExpenseTransferTargetAccountingLine  ) {
        if (attributeName.equalsIgnoreCase("fringeBenefitView")) {
            Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);

            Properties parameters = new Properties();
            if(businessObject instanceof ExpenseTransferSourceAccountingLine ){
                ExpenseTransferSourceAccountingLine sourceAccountingLine = (ExpenseTransferSourceAccountingLine) businessObject;
                parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, sourceAccountingLine.getChartOfAccountsCode());
                parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, sourceAccountingLine.getAccountNumber());
                parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, ObjectUtils.isNotNull(sourceAccountingLine.getSubAccountNumber()) ? sourceAccountingLine.getSubAccountNumber() : "");
                parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, sourceAccountingLine.getObjectCode().getFinancialObjectCode());
                parameters.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, sourceAccountingLine.getPayrollEndDateFiscalYear().toString());
                parameters.put(KFSPropertyConstants.AMOUNT, sourceAccountingLine.getAmount().toString());
                parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, FRINGE_BENEFIT_METHOD_TO_CALL);
            }
            else if(businessObject instanceof ExpenseTransferTargetAccountingLine ){
                ExpenseTransferTargetAccountingLine targetAccountingLine = (ExpenseTransferTargetAccountingLine) businessObject;
                parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, targetAccountingLine.getChartOfAccountsCode());
                parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, targetAccountingLine.getAccountNumber());
                parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, ObjectUtils.isNotNull(targetAccountingLine.getSubAccountNumber()) ? targetAccountingLine.getSubAccountNumber() : "");
                parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, targetAccountingLine.getObjectCode().getFinancialObjectCode());
                parameters.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, targetAccountingLine.getPayrollEndDateFiscalYear().toString());
                parameters.put(KFSPropertyConstants.AMOUNT, targetAccountingLine.getAmount().toString());
                parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, FRINGE_BENEFIT_METHOD_TO_CALL);
            }

            String fieldValue = objFieldValue == null ? KFSConstants.EMPTY_STRING : objFieldValue.toString();
           // build out base path for return location, use config service
            String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

            final String url =  UrlFactory.parameterizeUrl(basePath + FRINGE_BENEFIT_INQUIRY_PAGE_NAME, parameters);
            

            return new AnchorHtmlData(url, "");
        }

        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);

    }

}
