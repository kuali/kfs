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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountSalaryDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionAccountSalaryDetailReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.BudgetConstructionOrgPositionFundingDetailReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionPositionFunding;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.service.BudgetConstructionAccountSalaryDetailReportService;
import org.kuali.module.budget.service.BudgetConstructionReportsServiceHelper;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountSalaryDetailReportServiceImpl implements BudgetConstructionAccountSalaryDetailReportService {

    private KualiConfigurationService kualiConfigurationService;
    private BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    
    /**
     * @see org.kuali.module.budget.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionAccountSalaryDetailReport> buildReports(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        Collection<BudgetConstructionAccountSalaryDetailReport> reportSet = new ArrayList();

        BudgetConstructionAccountSalaryDetailReport accountSalaryDetailReport;
        // build searchCriteria
        Map searchCriteria = buildSearchCriteria(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber);
        
        // build order list
        List<String> orderList = buildOrderByList();
        Collection<PendingBudgetConstructionAppointmentFunding> pendingAppointmentFundingList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(PendingBudgetConstructionAppointmentFunding.class, searchCriteria, orderList);

        List<PendingBudgetConstructionAppointmentFunding> listForTotal = BudgetConstructionReportHelper.deleteDuplicated((List) pendingAppointmentFundingList, fieldsForTotal());

        // Calculate Total Section
        Collection<BudgetConstructionAccountSalaryDetailReportTotal> fundingDetailTotalPerson = calculateTotal(pendingAppointmentFundingList, listForTotal);


/*        for (BudgetConstructionMonthly bcMonthly : budgetConstructionMonthlyList) {
            accountSalaryDetailReport = new BudgetConstructionAccountSalaryDetailReport();
            buildReportsHeader(bcMonthly, accountSalaryDetailReport);
            buildReportsBody(bcMonthly, accountSalaryDetailReport);
            
            reportSet.add(accountSalaryDetailReport);
        }
*/
        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    private void buildReportsHeader(BudgetConstructionMonthly bcMonthly, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport) {
        accountMonthlyDetailReport.setFiscalYear(bcMonthly.getUniversityFiscalYear().toString());
        accountMonthlyDetailReport.setChartOfAccountsCode(bcMonthly.getChartOfAccountsCode());
        accountMonthlyDetailReport.setAccountNumber(bcMonthly.getAccountNumber());
        accountMonthlyDetailReport.setSubAccountNumber(bcMonthly.getSubAccountNumber());
        accountMonthlyDetailReport.setAccountName(bcMonthly.getAccount().getAccountName());
        try {
            accountMonthlyDetailReport.setSubAccountName(bcMonthly.getSubAccount().getSubAccountName());
            } catch (Exception e){
                accountMonthlyDetailReport.setSubAccountName("");
            }
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    private void buildReportsBody(BudgetConstructionMonthly bcMonthly, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport) {
        /*accountMonthlyDetailReport.setFinancialObjectCode(bcMonthly.getFinancialObjectCode());
        accountMonthlyDetailReport.setFinancialSubObjectCode(bcMonthly.getFinancialSubObjectCode());
        accountMonthlyDetailReport.setFinancialObjectCodeShortName(bcMonthly.getFinancialObject().getFinancialObjectCodeShortName());
        accountMonthlyDetailReport.setObjCodeSubObjCode(accountMonthlyDetailReport.getFinancialObjectCode() + accountMonthlyDetailReport.getFinancialSubObjectCode());
        
        Integer financialDocumentMonth1LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth1LineAmount());
        Integer financialDocumentMonth2LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth2LineAmount());
        Integer financialDocumentMonth3LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth3LineAmount());
        Integer financialDocumentMonth4LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth4LineAmount());
        Integer financialDocumentMonth5LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth5LineAmount());
        Integer financialDocumentMonth6LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth6LineAmount());
        Integer financialDocumentMonth7LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth7LineAmount());
        Integer financialDocumentMonth8LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth8LineAmount());
        Integer financialDocumentMonth9LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth9LineAmount());
        Integer financialDocumentMonth10LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth10LineAmount());
        Integer financialDocumentMonth11LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth11LineAmount());
        Integer financialDocumentMonth12LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth12LineAmount());
        
        Integer annualAmount = financialDocumentMonth1LineAmount + financialDocumentMonth2LineAmount + financialDocumentMonth3LineAmount
                             + financialDocumentMonth4LineAmount + financialDocumentMonth5LineAmount + financialDocumentMonth6LineAmount
                             + financialDocumentMonth7LineAmount + financialDocumentMonth8LineAmount + financialDocumentMonth9LineAmount
                             + financialDocumentMonth10LineAmount + financialDocumentMonth11LineAmount +financialDocumentMonth12LineAmount;
        
        accountMonthlyDetailReport.setAnnualAmount(annualAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth1LineAmount(financialDocumentMonth1LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth2LineAmount(financialDocumentMonth2LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth3LineAmount(financialDocumentMonth3LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth4LineAmount(financialDocumentMonth4LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth5LineAmount(financialDocumentMonth5LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth6LineAmount(financialDocumentMonth6LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth7LineAmount(financialDocumentMonth7LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth8LineAmount(financialDocumentMonth8LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth9LineAmount(financialDocumentMonth9LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth10LineAmount(financialDocumentMonth10LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth11LineAmount(financialDocumentMonth11LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth12LineAmount(financialDocumentMonth12LineAmount);
*/        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private Collection<BudgetConstructionAccountSalaryDetailReportTotal> calculateTotal(Collection<PendingBudgetConstructionAppointmentFunding> pendingAppointmentFundingList, List<PendingBudgetConstructionAppointmentFunding> listForTotal) {
        Collection<BudgetConstructionAccountSalaryDetailReportTotal> returnCollection = new ArrayList();
        Integer totalPersonPositionCsfAmount = new Integer(0);
        Integer totalPersonAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalPersonPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalPersonAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer personSortCode = new Integer(0);
        PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding = null;
        BudgetConstructionAccountSalaryDetailReportTotal budgetConstructionAccountSalaryDetailReportTotal = new BudgetConstructionAccountSalaryDetailReportTotal();
        for (PendingBudgetConstructionAppointmentFunding totalEntry : listForTotal){
            for (PendingBudgetConstructionAppointmentFunding appointmentFundingEntry : pendingAppointmentFundingList) {
                if (BudgetConstructionReportHelper.isSameEntry(totalEntry, appointmentFundingEntry, fieldsForTotal())) {
//                    pendingAppointmentFunding = appointmentFundingEntry.getPendingAppointmentFunding();
//                    if (pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
//                        BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().get(0);
//                        totalPersonPositionCsfAmount = totalPersonPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
//                        totalPersonPositionCsfFteQuantity = totalPersonPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
//                    }
//                    if (pendingAppointmentFunding != null) {
//                        totalPersonAppointmentRequestedAmount = totalPersonAppointmentRequestedAmount + new Integer(pendingAppointmentFunding.getAppointmentRequestedAmount().intValue());
//                        totalPersonAppointmentRequestedFteQuantity = totalPersonAppointmentRequestedFteQuantity.add(pendingAppointmentFunding.getAppointmentRequestedFteQuantity());
//                    }
               }
            }
//            

            returnCollection.add(budgetConstructionAccountSalaryDetailReportTotal);
            // set all values to zero, after the entry was added to collection
            totalPersonPositionCsfAmount = new Integer(0);
            totalPersonAppointmentRequestedAmount = new Integer(0);
            totalPersonPositionCsfFteQuantity = BigDecimal.ZERO;
            totalPersonAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            personSortCode = new Integer(0);
        }
        return returnCollection;
    }
    
    
    
    
    
    
    private List<String> fieldsForTotal() {

        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        return fieldList;
    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    private List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.POSITION_NUMBER);
        returnList.add(KFSPropertyConstants.EMPLID);
        return returnList;
    }

    private Map buildSearchCriteria(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber){
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        return searchCriteria;
    }
    
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

}
