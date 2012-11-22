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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.document.validation.event.AddDistributionAccountingLineValidationEvent;
import org.kuali.kfs.module.tem.document.validation.event.AssignDistributionAccountingLinesEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.util.RiceKeyConstants;

public class TravelDocumentDistributionAccountingLinesValidation extends GenericValidation {

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

        List errors = GlobalVariables.getMessageMap().getErrorPath();
        if (event instanceof AddDistributionAccountingLineValidationEvent){
            AddDistributionAccountingLineValidationEvent distributionEvent = (AddDistributionAccountingLineValidationEvent) event;
            TravelMvcWrapperBean wrapper = distributionEvent.getTravelForm();
            //Check data dictionary validations
            success = getDictionaryValidationService().isBusinessObjectValid(wrapper.getAccountDistributionnewSourceLine(), "");
            TemDistributionAccountingLine line = wrapper.getAccountDistributionnewSourceLine();
            List<TemDistributionAccountingLine> lines = wrapper.getAccountDistributionsourceAccountingLines();
            if (success){
                //Does account exist?
                Map<String,String> fieldValues = new HashMap<String, String>();
                fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, line.getChartOfAccountsCode());
                fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, line.getAccountNumber());
                Account account = (Account) getBusinessObjectService().findByPrimaryKey(Account.class, fieldValues);
                if (account == null){
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, 
                            RiceKeyConstants.ERROR_EXISTENCE, "Account Number");
                    return false;

                }
                
                
                if (line.getAccountLinePercent() == null){
                    line.setAccountLinePercent(new BigDecimal(0));
                }
                if (line.getAmount() == null){
                    line.setAmount(KualiDecimal.ZERO);
                }
                if (line.getAmount().isLessThan(KualiDecimal.ZERO)){
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AMOUNT, 
                            TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_AMOUNT_OR_PERCENT, "Amount");
                    success = false;
                }
                if (line.getAccountLinePercent().doubleValue() < 0){
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACCOUNT_LINE_PERCENT, 
                            TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_AMOUNT_OR_PERCENT, "Percent");
                    success = false;
                }
                if (line.getAccountLinePercent().doubleValue() == 0 && line.getAmount().isLessThan(KualiDecimal.ZERO)){
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AMOUNT, 
                            TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_AMOUNT_OR_PERCENT, "Amount");
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACCOUNT_LINE_PERCENT, 
                            TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_AMOUNT_OR_PERCENT, "Percent");
                    return false;
                }
                
                /*KualiDecimal totalAmount = KualiDecimal.ZERO;
                BigDecimal totalPercent = new BigDecimal(0);
                for (TemDistributionAccountingLine distributionLine : wrapper.getAccountDistributionsourceAccountingLines()){
                    totalAmount = totalAmount.add(distributionLine.getAmount());
                    totalPercent = totalPercent.add(distributionLine.getAccountLinePercent());
                }
                                
                if (line.getAccountLinePercent().add(totalPercent).doubleValue() > 100){
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACCOUNT_LINE_PERCENT, 
                            TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_TOTAL);
                    return false;
                }
                else if (line.getAmount().add(totalAmount).isGreaterThan(wrapper.getDistributionRemainingAmount(true))){
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACCOUNT_LINE_PERCENT, 
                            TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_TOTAL);
                    return false;
                }
*/                  
            }
            
        }
        else if (event instanceof AssignDistributionAccountingLinesEvent){
            GlobalVariables.getMessageMap().clearErrorPath();
            AssignDistributionAccountingLinesEvent distributionEvent = (AssignDistributionAccountingLinesEvent) event;
            TravelMvcWrapperBean wrapper = distributionEvent.getTravelForm();
            
            
            if (!getAccountingDistributionService().getTotalAmount(wrapper.getAccountDistributionsourceAccountingLines()).equals(wrapper.getDistributionRemainingAmount(true))
                    || !getAccountingDistributionService().getTotalPercent(wrapper.getAccountDistributionsourceAccountingLines()).equals(new BigDecimal(100))){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACCOUNT_DISTRIBUTION_SRC_LINES + "[0]." + TemPropertyConstants.ACCOUNT_LINE_PERCENT, 
                        TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_TOTAL, wrapper.getDistributionRemainingAmount(true).toString());
                success = false;
            }
        }

        return success;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return SpringContext.getBean(DictionaryValidationService.class);
    }
    
    public AccountingDistributionService getAccountingDistributionService() {
        return SpringContext.getBean(AccountingDistributionService.class);
    }
    
    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
}
