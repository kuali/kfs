/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountBalance;
import org.kuali.module.budget.bo.BudgetConstructionAccountObjectDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionAccountObjectDetailReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionBalanceByAccount;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountObjectDetailReportTotal;
import org.kuali.module.budget.dao.BudgetConstructionDocumentAccountObjectDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionDocumentAccountObjectDetailReportService;
import org.kuali.module.budget.service.BudgetConstructionReportsServiceHelper;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionDocumentAccountObjectDetailReportServiceImpl implements BudgetConstructionDocumentAccountObjectDetailReportService {
    private BudgetConstructionDocumentAccountObjectDetailReportDao budgetConstructionDocumentAccountObjectDetailReportDao;
    private KualiConfigurationService kualiConfigurationService;
    private BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;

    public void updateDocumentAccountObjectDetailReportTable(String personUserIdentifier, String documentNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        budgetConstructionDocumentAccountObjectDetailReportDao.updateDocumentAccountObjectDetailReportTable(personUserIdentifier, documentNumber, universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionAccountObjectDetailReport> buildReports(String personUserIdentifier) {
        Collection<BudgetConstructionAccountObjectDetailReport> reportSet = new ArrayList();

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionBalanceByAccount> balanceByAccountList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionBalanceByAccount.class, personUserIdentifier, orderList);

        
        
        
        List listForCalculateObject = BudgetConstructionReportHelper.deleteDuplicated((List) balanceByAccountList, fieldsForObject());
        List listForCalculateLevel = BudgetConstructionReportHelper.deleteDuplicated((List) balanceByAccountList, fieldsForLevel());
        List listForCalculateType = BudgetConstructionReportHelper.deleteDuplicated((List) balanceByAccountList, fieldsForType());
        
        // Calculate Total Section
        List<BudgetConstructionAccountObjectDetailReportTotal> accountObjectDetailTotalObjectList;
        List<BudgetConstructionAccountObjectDetailReportTotal> accountObjectDetailTotalLevelList;
        List<BudgetConstructionAccountObjectDetailReportTotal> accountObjectDetailTotalTypeList;
        
        accountObjectDetailTotalObjectList = calculateObjectTotal((List) balanceByAccountList, listForCalculateObject);
        accountObjectDetailTotalLevelList = calculateLevelTotal((List) balanceByAccountList, listForCalculateLevel);
        accountObjectDetailTotalTypeList = calculateTypeTotal((List) balanceByAccountList, listForCalculateType);
        
        
        
        

        for (BudgetConstructionBalanceByAccount balanceByAccount : balanceByAccountList) {
            BudgetConstructionAccountObjectDetailReport accountObjectDetailReport = new BudgetConstructionAccountObjectDetailReport();
            buildReportsHeader(balanceByAccount, accountObjectDetailReport);
            buildReportsBody(balanceByAccount, accountObjectDetailReport);

            reportSet.add(accountObjectDetailReport);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    private void buildReportsHeader(BudgetConstructionBalanceByAccount balanceByAccount, BudgetConstructionAccountObjectDetailReport accountObjectDetailReport) {
        Integer prevFiscalyear = balanceByAccount.getUniversityFiscalYear() - 1;
        accountObjectDetailReport.setFiscalYear(prevFiscalyear.toString() + " - " + balanceByAccount.getUniversityFiscalYear().toString().substring(2, 4));
        
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        accountObjectDetailReport.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        accountObjectDetailReport.setReqFy(prevFiscalyear.toString() + " - " + balanceByAccount.getUniversityFiscalYear().toString().substring(2, 4));
        
        accountObjectDetailReport.setAccountNumber(balanceByAccount.getAccountNumber());
        accountObjectDetailReport.setSubAccountNumber(balanceByAccount.getSubAccountNumber());
        accountObjectDetailReport.setChartOfAccountsCode(balanceByAccount.getChartOfAccountsCode());
        accountObjectDetailReport.setOrganizationCode(balanceByAccount.getAccount().getOrganizationCode());
        
        
        String orgName = null;
        try {
            orgName = balanceByAccount.getAccount().getOrganization().getOrganizationName();
        }
        catch (PersistenceBrokerException e) {
        }
        String accountName = balanceByAccount.getAccount().getAccountName();
        String fundGroupCode = balanceByAccount.getAccount().getSubFundGroup().getFundGroupCode();
        String fundGroupName = balanceByAccount.getAccount().getSubFundGroup().getFundGroup().getName();
        String subAccountName = balanceByAccount.getSubAccount().getSubAccountName();
        
        if (orgName == null) {
            accountObjectDetailReport.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            accountObjectDetailReport.setOrganizationName(orgName);
        }
        
        if (fundGroupCode == null) {
            accountObjectDetailReport.setFundGroupCode(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_CODE));
        }
        else {
            accountObjectDetailReport.setFundGroupCode(fundGroupCode);
        }
        
        if (fundGroupName == null) {
            accountObjectDetailReport.setFundGroupName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            accountObjectDetailReport.setFundGroupName(fundGroupName);
        }
        if (subAccountName == null) {
            accountObjectDetailReport.setSubAccountName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION));
        }
        else {
            accountObjectDetailReport.setSubAccountName(subAccountName);
        }
        
        if (accountName == null) {
            accountObjectDetailReport.setAccountName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION));
        }
        else {
            accountObjectDetailReport.setAccountName(accountName);
        }
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    private void buildReportsBody(BudgetConstructionBalanceByAccount balanceByAccount, BudgetConstructionAccountObjectDetailReport accountObjectDetailReport) {

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private List calculateObjectTotal(List<BudgetConstructionBalanceByAccount> balanceByAccountList, List<BudgetConstructionBalanceByAccount> simpleList) {

        BigDecimal totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalObjectAccountLineAnnualBalanceAmount = new Integer(0);
        Integer totalObjectAmountChange = new Integer(0);
        BigDecimal totalObjectPercentChange = BigDecimal.ZERO;
        
        List returnList = new ArrayList();
        
        for (BudgetConstructionBalanceByAccount simpleBalanceByAccountEntry : simpleList) {
            
            BudgetConstructionAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionAccountObjectDetailReportTotal();
            for (BudgetConstructionBalanceByAccount balanceByAccountEntry : balanceByAccountList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBalanceByAccountEntry, balanceByAccountEntry, fieldsForObject())) {
                    totalObjectFinancialBeginningBalanceLineAmount += new Integer(balanceByAccountEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalObjectAccountLineAnnualBalanceAmount += new Integer(balanceByAccountEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity.add(balanceByAccountEntry.getPositionCsfLeaveFteQuantity());
                    totalObjectCsfFullTimeEmploymentQuantity = totalObjectCsfFullTimeEmploymentQuantity.add(balanceByAccountEntry.getCsfFullTimeEmploymentQuantity());
                    totalObjectAppointmentRequestedCsfFteQuantity = totalObjectAppointmentRequestedCsfFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedCsfFteQuantity());
                    totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBudgetConstructionBalanceByAccount(simpleBalanceByAccountEntry);
            bcObjectTotal.setTotalObjectPositionCsfLeaveFteQuantity(totalObjectPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalObjectCsfFullTimeEmploymentQuantity(totalObjectCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTotalObjectFinancialBeginningBalanceLineAmount(totalObjectFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalObjectAppointmentRequestedCsfFteQuantity(totalObjectAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalObjectAppointmentRequestedFteQuantity(totalObjectAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalObjectAccountLineAnnualBalanceAmount(totalObjectAccountLineAnnualBalanceAmount);

            totalObjectAmountChange = totalObjectAccountLineAnnualBalanceAmount - totalObjectFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setTotalObjectAmountChange(totalObjectAmountChange);
            returnList.add(bcObjectTotal);

            totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalObjectCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
            totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalObjectAccountLineAnnualBalanceAmount = new Integer(0);
            totalObjectAmountChange = new Integer(0);
            totalObjectPercentChange = BigDecimal.ZERO;

        }
        return returnList;
        
    }

    
    
    
    private List calculateLevelTotal(List<BudgetConstructionBalanceByAccount> balanceByAccountList, List<BudgetConstructionBalanceByAccount> simpleList) {

        BigDecimal totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
        Integer totalLevelAmountChange = new Integer(0);
        BigDecimal totalLevelPercentChange = BigDecimal.ZERO;
        
        List returnList = new ArrayList();
        
        for (BudgetConstructionBalanceByAccount simpleBcosEntry : simpleList) {
            
            BudgetConstructionAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionAccountObjectDetailReportTotal();
            for (BudgetConstructionBalanceByAccount balanceByAccountEntry : balanceByAccountList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, balanceByAccountEntry, fieldsForLevel())) {
                    totalLevelFinancialBeginningBalanceLineAmount += new Integer(balanceByAccountEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalLevelAccountLineAnnualBalanceAmount += new Integer(balanceByAccountEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity.add(balanceByAccountEntry.getPositionCsfLeaveFteQuantity());
                    totalLevelCsfFullTimeEmploymentQuantity = totalLevelCsfFullTimeEmploymentQuantity.add(balanceByAccountEntry.getCsfFullTimeEmploymentQuantity());
                    totalLevelAppointmentRequestedCsfFteQuantity = totalLevelAppointmentRequestedCsfFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedCsfFteQuantity());
                    totalLevelAppointmentRequestedFteQuantity = totalLevelAppointmentRequestedFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBudgetConstructionBalanceByAccount(simpleBcosEntry);
            bcObjectTotal.setTotalLevelPositionCsfLeaveFteQuantity(totalLevelPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalLevelCsfFullTimeEmploymentQuantity(totalLevelCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTotalLevelFinancialBeginningBalanceLineAmount(totalLevelFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalLevelAppointmentRequestedCsfFteQuantity(totalLevelAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalLevelAppointmentRequestedFteQuantity(totalLevelAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalLevelAccountLineAnnualBalanceAmount(totalLevelAccountLineAnnualBalanceAmount);
            totalLevelAmountChange = totalLevelAccountLineAnnualBalanceAmount - totalLevelFinancialBeginningBalanceLineAmount;

            bcObjectTotal.setTotalLevelAmountChange(totalLevelAmountChange);

            returnList.add(bcObjectTotal);

            totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalLevelCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
            totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
            totalLevelAmountChange = new Integer(0);
            totalLevelPercentChange = BigDecimal.ZERO;

        }
        return returnList;
        
    }

    
    private List calculateTypeTotal(List<BudgetConstructionBalanceByAccount> balanceByAccountList, List<BudgetConstructionBalanceByAccount> simpleList) {

        BigDecimal typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal typeCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer typeFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer typeAccountLineAnnualBalanceAmount = new Integer(0);
        Integer typeAmountChange = new Integer(0);
        BigDecimal typePercentChange = BigDecimal.ZERO;

        List returnList = new ArrayList();
        for (BudgetConstructionBalanceByAccount simpleBcosEntry : simpleList) {
            BudgetConstructionAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionAccountObjectDetailReportTotal();
            for (BudgetConstructionBalanceByAccount balanceByAccountEntry : balanceByAccountList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, balanceByAccountEntry, fieldsForType())) {

                    typeFinancialBeginningBalanceLineAmount += new Integer(balanceByAccountEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    typeAccountLineAnnualBalanceAmount += new Integer(balanceByAccountEntry.getAccountLineAnnualBalanceAmount().intValue());
                    typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity.add(balanceByAccountEntry.getPositionCsfLeaveFteQuantity());
                    typeCsfFullTimeEmploymentQuantity = typeCsfFullTimeEmploymentQuantity.add(balanceByAccountEntry.getCsfFullTimeEmploymentQuantity());
                    typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedCsfFteQuantity());
                    typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedFteQuantity());
                }
            }
            
            bcObjectTotal.setBudgetConstructionBalanceByAccount(simpleBcosEntry);
            
            bcObjectTotal.setTypePositionCsfLeaveFteQuantity(typePositionCsfLeaveFteQuantity);
            bcObjectTotal.setTypeCsfFullTimeEmploymentQuantity(typeCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTypeFinancialBeginningBalanceLineAmount(typeFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTypeAppointmentRequestedCsfFteQuantity(typeAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTypeAppointmentRequestedFteQuantity(typeAppointmentRequestedFteQuantity);
            bcObjectTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);

            typeAmountChange = typeAccountLineAnnualBalanceAmount - typeFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setTypeAmountChange(typeAmountChange);

            
            returnList.add(bcObjectTotal);
            
            typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            typeCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            typeFinancialBeginningBalanceLineAmount = new Integer(0);
            typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            typeAccountLineAnnualBalanceAmount = new Integer(0);
            typeAmountChange = new Integer(0);
            typePercentChange = BigDecimal.ZERO;
        }

        return returnList;
    }

    
    
    
    
    

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    private List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.TYPE_FINANCIAL_REPORT_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.LEVEL_FINANCIAL_REPORT_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }
    
    /**
     * builds list of fields for comparing entry of Object
     * @return List<String>
     */
    private List<String> fieldsForObject() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForLevel());
        fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        fieldList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return fieldList;
    }    
    
    /**
     * builds list of fields for comparing entry of Level
     * @return List<String>
     */
    private List<String> fieldsForLevel() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForType());
        fieldList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        return fieldList;
    }    

    /**
     * builds list of fields for comparing entry of GexpAndType
     * @return List<String>
     */
    private List<String> fieldsForType() {
        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.TYPE_FINANCIAL_REPORT_SORT_CODE);
        return fieldList;
    }
    
    /*private List<String> commonFields(){
        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.TYPE_FINANCIAL_REPORT_SORT_CODE);
        fieldList.add(KFSPropertyConstants.LEVEL_FINANCIAL_REPORT_SORT_CODE);
        fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        fieldList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.TYPE_FINANCIAL_REPORT_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.LEVEL_FINANCIAL_REPORT_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        
        
        return fieldList;
    }

*/    

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

    public void setBudgetConstructionDocumentAccountObjectDetailReportDao(BudgetConstructionDocumentAccountObjectDetailReportDao budgetConstructionDocumentAccountObjectDetailReportDao) {
        this.budgetConstructionDocumentAccountObjectDetailReportDao = budgetConstructionDocumentAccountObjectDetailReportDao;
    }

}
