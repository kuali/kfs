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
package org.kuali.kfs.sec.businessobject.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.gl.businessobject.lookup.AccountBalanceByConsolidationLookupableHelperServiceImpl;
import org.kuali.kfs.gl.service.AccountBalanceService;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sec.util.SecUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * Override of AccountBalanceByConsolidation lookup helper to integrate access security
 */
public class AccessSecurityAccountBalanceByConsolidationLookupableHelperServiceImpl extends AccountBalanceByConsolidationLookupableHelperServiceImpl {
    protected AccessSecurityService accessSecurityService;
    protected ObjectTypeService objectTypeService;
    protected ConfigurationService kualiConfigurationService;

    /**
     * Checks security on the detail balance records, if user does not have access to view any of those records they are removed and total lines
     * are updated
     * 
     * @see org.kuali.kfs.gl.businessobject.lookup.AccountBalanceByConsolidationLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map fieldValues) {
        AccountBalanceByConsolidationLookupableHelperServiceImpl helperServiceImpl = new AccountBalanceByConsolidationLookupableHelperServiceImpl();
        helperServiceImpl.setAccountBalanceService(SpringContext.getBean(AccountBalanceService.class));
        List<? extends BusinessObject> results = helperServiceImpl.getSearchResults(fieldValues);

        // first 7 items of results are total lines, so we need to check any detail lines after than
        if (results.size() > 7) {
            List details = results.subList(7, results.size());

            int resultSizeBeforeRestrictions = details.size();
            accessSecurityService.applySecurityRestrictionsForGLInquiry(details, GlobalVariables.getUserSession().getPerson());

            accessSecurityService.compareListSizeAndAddMessageIfChanged(resultSizeBeforeRestrictions, details, SecKeyConstants.MESSAGE_BALANCE_INQUIRY_RESULTS_RESTRICTED);

            // if details have changed we need to update totals
            if (resultSizeBeforeRestrictions != details.size()) {
                String subAccountNumber = (String) fieldValues.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
                String fiscalYear = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
                Integer universityFiscalYear = new Integer(Integer.parseInt(fiscalYear));

                // Dashes means no sub account number
                if (KFSConstants.getDashSubAccountNumber().equals(subAccountNumber)) {
                    subAccountNumber = "";
                }

                TransientBalanceInquiryAttributes dbo = ((AccountBalance) results.get(0)).getDummyBusinessObject();

                List totals = buildAccountBalanceTotals(details, universityFiscalYear, subAccountNumber, dbo);
                totals.addAll(details);

                return new CollectionIncomplete(totals, new Long(totals.size()));
            }
        }

        return results;
    }

    /**
     * Rebuilds the account balance total lines, logic mostly duplicated from AccountBalanceServiceImpl:findAccountBalanceByConsolidation
     * 
     * @param balanceDetails List of AccountBalance detail lines
     * @param universityFiscalYear Fiscal Year being searched
     * @param subAccountNumber Sub Account number being searched 
     * @param dbo TransientBalanceInquiryAttributes object that will be set on total lines
     * @return List of AccountBalance total lines
     */
    protected List buildAccountBalanceTotals(List balanceDetails, Integer universityFiscalYear, String subAccountNumber, TransientBalanceInquiryAttributes dbo) {
        List totals = new ArrayList();

        List incomeObjectTypes = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear);
        String incomeTransferObjectType = objectTypeService.getIncomeTransferObjectType(universityFiscalYear);
        List expenseObjectTypes = objectTypeService.getBasicExpenseObjectTypes(universityFiscalYear);
        String expenseTransferObjectType = objectTypeService.getExpenseTransferObjectType(universityFiscalYear);

        AccountBalance income = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME));
        AccountBalance incomeTransfers = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS));
        AccountBalance incomeTotal = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_TOTAL));
        AccountBalance expense = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE));
        AccountBalance expenseTransfers = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS));
        AccountBalance expenseTotal = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_TOTAL));
        AccountBalance total = new AccountBalance(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.TOTAL));

        totals.add(income);
        totals.add(incomeTransfers);
        totals.add(incomeTotal);
        totals.add(expense);
        totals.add(expenseTransfers);
        totals.add(expenseTotal);
        totals.add(total);

        // set the dummy business object that was built in super lookupable
        for (Iterator iterator = totals.iterator(); iterator.hasNext();) {
            AccountBalance totalBalance = (AccountBalance) iterator.next();
            total.setDummyBusinessObject(dbo);
        }

        boolean subAccountBlank = StringUtils.isBlank(subAccountNumber);

        // iterate over details and update total line based on object type
        for (Iterator iterator = balanceDetails.iterator(); iterator.hasNext();) {
            AccountBalance detail = (AccountBalance) iterator.next();
            String objectType = detail.getFinancialObject().getFinancialObjectTypeCode();

            if (incomeObjectTypes.contains(objectType)) {
                String transferExpenseCode = detail.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObject().getFinConsolidationObjectCode();
                if (!subAccountBlank && transferExpenseCode.equals(GeneralLedgerConstants.INCOME_OR_EXPENSE_TRANSFER_CONSOLIDATION_CODE)) {
                    incomeTransfers.add(detail);
                }
                else {
                    income.add(detail);
                }

                incomeTotal.add(detail);
            }

            if (incomeTransferObjectType.equals(objectType)) {
                incomeTransfers.add(detail);
                incomeTotal.add(detail);
            }

            if (expenseObjectTypes.contains(objectType)) {
                String transferExpenseCode = detail.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObject().getFinConsolidationObjectCode();
                if (!subAccountBlank && transferExpenseCode.equals(GeneralLedgerConstants.INCOME_OR_EXPENSE_TRANSFER_CONSOLIDATION_CODE)) {
                    expenseTransfers.add(detail);
                }
                else {
                    expense.add(detail);
                }

                expenseTotal.add(detail);
            }

            if (expenseTransferObjectType.equals(objectType)) {
                expenseTransfers.add(detail);
                expenseTransfers.add(detail);
            }
        }

        // Add up variances
        income.getDummyBusinessObject().setGenericAmount(income.getAccountLineActualsBalanceAmount().add(income.getAccountLineEncumbranceBalanceAmount()).subtract(income.getCurrentBudgetLineBalanceAmount()));
        incomeTransfers.getDummyBusinessObject().setGenericAmount(incomeTransfers.getAccountLineActualsBalanceAmount().add(incomeTransfers.getAccountLineEncumbranceBalanceAmount()).subtract(incomeTransfers.getCurrentBudgetLineBalanceAmount()));
        incomeTotal.getDummyBusinessObject().setGenericAmount(income.getDummyBusinessObject().getGenericAmount().add(incomeTransfers.getDummyBusinessObject().getGenericAmount()));

        expense.getDummyBusinessObject().setGenericAmount(expense.getCurrentBudgetLineBalanceAmount().subtract(expense.getAccountLineActualsBalanceAmount()).subtract(expense.getAccountLineEncumbranceBalanceAmount()));
        expenseTransfers.getDummyBusinessObject().setGenericAmount(expenseTransfers.getCurrentBudgetLineBalanceAmount().subtract(expenseTransfers.getAccountLineActualsBalanceAmount()).subtract(expenseTransfers.getAccountLineEncumbranceBalanceAmount()));
        expenseTotal.getDummyBusinessObject().setGenericAmount(expense.getDummyBusinessObject().getGenericAmount().add(expenseTransfers.getDummyBusinessObject().getGenericAmount()));

        total.getDummyBusinessObject().setGenericAmount(incomeTotal.getDummyBusinessObject().getGenericAmount().add(expenseTotal.getDummyBusinessObject().getGenericAmount()));

        return totals;
    }

    /**
     * Sets the accessSecurityService attribute value.
     * 
     * @param accessSecurityService The accessSecurityService to set.
     */
    public void setAccessSecurityService(AccessSecurityService accessSecurityService) {
        this.accessSecurityService = accessSecurityService;
    }

    /**
     * Sets the objectTypeService attribute value.
     * 
     * @param objectTypeService The objectTypeService to set.
     */
    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
