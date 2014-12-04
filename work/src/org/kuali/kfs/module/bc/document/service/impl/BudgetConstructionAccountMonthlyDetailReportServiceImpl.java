/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountMonthlyDetailReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountMonthlyDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountMonthlyDetailReportServiceImpl implements BudgetConstructionAccountMonthlyDetailReportService {

    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionAccountMonthlyDetailReport> buildReports(String documentNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        Collection<BudgetConstructionAccountMonthlyDetailReport> reportSet = new ArrayList();

        BudgetConstructionAccountMonthlyDetailReport accountMonthlyDetailReport;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionMonthly> budgetConstructionMonthlyList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionMonthly.class, searchCriteria, orderList);


        for (BudgetConstructionMonthly bcMonthly : budgetConstructionMonthlyList) {
            accountMonthlyDetailReport = new BudgetConstructionAccountMonthlyDetailReport();
            buildReportsHeader(bcMonthly, accountMonthlyDetailReport);
            buildReportsBody(bcMonthly, accountMonthlyDetailReport);
            
            reportSet.add(accountMonthlyDetailReport);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    protected void buildReportsHeader(BudgetConstructionMonthly bcMonthly, BudgetConstructionAccountMonthlyDetailReport accountMonthlyDetailReport) {
        accountMonthlyDetailReport.setUniversityFiscalYear(bcMonthly.getUniversityFiscalYear());
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
    protected void buildReportsBody(BudgetConstructionMonthly bcMonthly, BudgetConstructionAccountMonthlyDetailReport accountMonthlyDetailReport) {
        accountMonthlyDetailReport.setFinancialObjectCode(bcMonthly.getFinancialObjectCode());
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
        
    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    protected List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

}
