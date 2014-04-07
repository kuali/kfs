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
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.document.validation.event.AddDistributionAccountingLineValidationEvent;
import org.kuali.kfs.module.tem.document.validation.event.AssignDistributionAccountingLinesEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelDocumentDistributionAccountingLinesValidation extends GenericValidation {
    protected DictionaryValidationService dictionaryValidationService;
    protected BusinessObjectService businessObjectService;
    protected AccountingDistributionService accountingDistributionService;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

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
                Account account = getBusinessObjectService().findByPrimaryKey(Account.class, fieldValues);
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
            }

        }
        else if (event instanceof AssignDistributionAccountingLinesEvent){
            GlobalVariables.getMessageMap().clearErrorPath();
            AssignDistributionAccountingLinesEvent distributionEvent = (AssignDistributionAccountingLinesEvent) event;
            TravelMvcWrapperBean wrapper = distributionEvent.getTravelForm();

            if (!getAccountingDistributionService().getTotalAmount(wrapper.getAccountDistributionsourceAccountingLines()).equals(wrapper.getDistributionRemainingAmount(true))
                    || getAccountingDistributionService().getTotalPercent(wrapper.getAccountDistributionsourceAccountingLines()).compareTo(new BigDecimal(100)) != 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACCOUNT_DISTRIBUTION_SRC_LINES + "[0]." + TemPropertyConstants.ACCOUNT_LINE_PERCENT,
                        TemKeyConstants.ERROR_TEM_DISTRIBUTION_ACCOUNTING_LINES_TOTAL, wrapper.getDistributionRemainingAmount(true).toString());
                success = false;
            }

            if (wrapper.getDistribution() != null && !wrapper.getDistribution().isEmpty() && wrapper.getTravelDocument().getExpenseLimit() != null && wrapper.getTravelDocument().getExpenseLimit().isLessThan(wrapper.getDistributionRemainingAmount(true))) {
                // we have an expense limit...do we have more than one selected distribution targets
                int distributionTargetCount = 0;
                for (AccountingDistribution distribution : wrapper.getDistribution()) {
                    if (distribution.getSelected()) {
                        distributionTargetCount += 1;
                    }
                }
                if (distributionTargetCount > 1) {
                    GlobalVariables.getMessageMap().putError(TemKeyConstants.TRVL_ACCOUNT_DIST, TemKeyConstants.ERROR_TEM_DISTRIBUTION_TOO_MANY_DISTRIBUTION_TARGETS_WITH_EXPENSE_LIMIT, Integer.toString(distributionTargetCount), wrapper.getTravelDocument().getExpenseLimit().toString());
                    success = false;
                }
            }
        }

        return success;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return SpringContext.getBean(DictionaryValidationService.class);
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setAccountingDistributionService(AccountingDistributionService accountingDistributionService) {
        this.accountingDistributionService = accountingDistributionService;
    }

    public AccountingDistributionService getAccountingDistributionService() {
        return SpringContext.getBean(AccountingDistributionService.class);
    }

    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
}
