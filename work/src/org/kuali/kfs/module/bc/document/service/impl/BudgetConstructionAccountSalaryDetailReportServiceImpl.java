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
package org.kuali.kfs.module.bc.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSalaryDetailReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSalaryDetailReportTotal;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountSalaryDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountSalaryDetailReportServiceImpl implements BudgetConstructionAccountSalaryDetailReportService {

    private KualiConfigurationService kualiConfigurationService;
    private BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
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
        Collection<BudgetConstructionAccountSalaryDetailReportTotal> accountSalaryDetailTotal = calculateTotal(pendingAppointmentFundingList, listForTotal);


        for (PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding : pendingAppointmentFundingList) {
            accountSalaryDetailReport = new BudgetConstructionAccountSalaryDetailReport();
            buildReportsHeader(universityFiscalYear, pendingAppointmentFunding, accountSalaryDetailReport);
            buildReportsBody(universityFiscalYear, pendingAppointmentFunding, accountSalaryDetailReport);
            buildReportsTotal(pendingAppointmentFunding, accountSalaryDetailReport, accountSalaryDetailTotal);
            reportSet.add(accountSalaryDetailReport);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    private void buildReportsHeader(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding, BudgetConstructionAccountSalaryDetailReport accountSalaryDetailReport) {

        Integer prevFiscalyear = universityFiscalYear - 1;
        accountSalaryDetailReport.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));


        accountSalaryDetailReport.setAccountNumber(pendingAppointmentFunding.getAccountNumber());
        accountSalaryDetailReport.setSubAccountNumber(pendingAppointmentFunding.getSubAccountNumber());
        accountSalaryDetailReport.setChartOfAccountsCode(pendingAppointmentFunding.getChartOfAccountsCode());
        accountSalaryDetailReport.setOrganizationCode(pendingAppointmentFunding.getAccount().getOrganizationCode());

        String chartOfAccountDescription = "";
        if (pendingAppointmentFunding.getChartOfAccounts() != null){
            try {
                chartOfAccountDescription = pendingAppointmentFunding.getChartOfAccounts().getFinChartOfAccountDescription();
            }
            catch (PersistenceBrokerException e) {
                chartOfAccountDescription = kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION);
            }
        } else {
            chartOfAccountDescription = BCConstants.Report.CHART + BCConstants.Report.NOT_DEFINED;
        }
        
        accountSalaryDetailReport.setChartOfAccountDescription(chartOfAccountDescription);

        String orgName = null;
        try {
            orgName = pendingAppointmentFunding.getAccount().getOrganization().getOrganizationName();
        }
        catch (PersistenceBrokerException e) {
        }
        String accountName = pendingAppointmentFunding.getAccount().getAccountName();
        String fundGroupCode = pendingAppointmentFunding.getAccount().getSubFundGroup().getFundGroupCode();
        String fundGroupName = pendingAppointmentFunding.getAccount().getSubFundGroup().getFundGroup().getName();
        
        if (orgName == null) {
            accountSalaryDetailReport.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            accountSalaryDetailReport.setOrganizationName(orgName);
        }

        if (fundGroupCode == null) {
            accountSalaryDetailReport.setFundGroupCode(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_CODE));
        }
        else {
            accountSalaryDetailReport.setFundGroupCode(fundGroupCode);
        }

        if (fundGroupName == null) {
            accountSalaryDetailReport.setFundGroupName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            accountSalaryDetailReport.setFundGroupName(fundGroupName);
        }
        
        if (accountName == null) {
            accountSalaryDetailReport.setAccountName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION));
        }
        else {
            accountSalaryDetailReport.setAccountName(accountName);
        }
        
        String subAccountName = "";
        
        if (!pendingAppointmentFunding.getSubAccountNumber().equals(BCConstants.Report.DASHES_SUB_ACCOUNT_CODE)){
            try {
                subAccountName = pendingAppointmentFunding.getSubAccount().getSubAccountName();
            }
            catch (PersistenceBrokerException e) {
                subAccountName = kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION);
            }
        }
        accountSalaryDetailReport.setSubAccountName(subAccountName);


    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    private void buildReportsBody(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport) {
        Integer amountChange = new Integer(0);
        BigDecimal percentChange = BigDecimal.ZERO;

        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(pendingAppointmentFunding);
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(pendingAppointmentFunding);
        BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, pendingAppointmentFunding);
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker = pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().get(0);


        // from PendingBudgetConstructionAppointmentFunding
        accountMonthlyDetailReport.setFinancialSubObjectCode(pendingAppointmentFunding.getFinancialSubObjectCode());
        
        // from BudgetConstructionIntendedIncumbent
        if (budgetConstructionIntendedIncumbent != null){
            accountMonthlyDetailReport.setIuClassificationLevel(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
        }
        
        // from BudgetConstructionAdministrativePost
        if (budgetConstructionAdministrativePost != null){
            accountMonthlyDetailReport.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }
        

        // from BudgetConstructionPosition
        if (budgetConstructionPosition != null){
            accountMonthlyDetailReport.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            accountMonthlyDetailReport.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            accountMonthlyDetailReport.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
            accountMonthlyDetailReport.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
        }
        
        // from BudgetConstructionCalculatedSalaryFoundationTracker
        if (budgetConstructionCalculatedSalaryFoundationTracker != null){
            accountMonthlyDetailReport.setPositionCsfAmount(new Integer(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue()));
            accountMonthlyDetailReport.setPositionCsfFullTimeEmploymentQuantity(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
            accountMonthlyDetailReport.setPositionCsfFundingStatusCode(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFundingStatusCode());
        }

        // from PendingBudgetConstructionAppointmentFunding
        accountMonthlyDetailReport.setAppointmentFundingMonth(pendingAppointmentFunding.getAppointmentFundingMonth());
        accountMonthlyDetailReport.setAppointmentRequestedPayRate(pendingAppointmentFunding.getAppointmentRequestedPayRate());
        accountMonthlyDetailReport.setAppointmentRequestedAmount(new Integer(pendingAppointmentFunding.getAppointmentRequestedAmount().intValue()));
        accountMonthlyDetailReport.setAppointmentRequestedFteQuantity(pendingAppointmentFunding.getAppointmentRequestedFteQuantity());
        accountMonthlyDetailReport.setAppointmentRequestedCsfAmount(new Integer(pendingAppointmentFunding.getAppointmentRequestedCsfAmount().intValue()));
        accountMonthlyDetailReport.setAppointmentFundingDurationCode(pendingAppointmentFunding.getAppointmentFundingDurationCode());
        accountMonthlyDetailReport.setAppointmentTotalIntendedAmount(new Integer(pendingAppointmentFunding.getAppointmentTotalIntendedAmount().intValue()));
        accountMonthlyDetailReport.setAppointmentTotalIntendedFteQuantity(pendingAppointmentFunding.getAppointmentTotalIntendedFteQuantity());

        accountMonthlyDetailReport.setFinancialObjectCode(pendingAppointmentFunding.getFinancialObjectCode());
        accountMonthlyDetailReport.setFinancialObjectCodeName(pendingAppointmentFunding.getFinancialObject().getFinancialObjectCodeName());

        if (pendingAppointmentFunding.getAppointmentFundingDurationCode().equals(BCConstants.Report.YES)) {
            accountMonthlyDetailReport.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            accountMonthlyDetailReport.setDeleteBox(BCConstants.Report.BLANK);
        }

        if (pendingAppointmentFunding.getEmplid().equals(BCConstants.Report.VACANT)) {
            accountMonthlyDetailReport.setPersonName(BCConstants.Report.VACANT);
        }
        else {
            accountMonthlyDetailReport.setPersonName(budgetConstructionIntendedIncumbent.getPersonName());
        }

        if (pendingAppointmentFunding.getAppointmentRequestedFteQuantity().equals(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity())) {
            amountChange = new Integer(pendingAppointmentFunding.getAppointmentRequestedAmount().subtract(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount()).intValue());
        }
        accountMonthlyDetailReport.setAmountChange(amountChange);

        if (!budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().equals(KualiInteger.ZERO)) {
            percentChange = BudgetConstructionReportHelper.calculatePercent(amountChange, new Integer(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue()));
        }
        accountMonthlyDetailReport.setPercentChange(percentChange);


        /*
         * 
         * 24-1 if (fin_object_cd.hobject != fin_object_cd.ld_pndbc_apptfnd_t) 25-2 if (fin_object_cd.hobject != "") ;ff if not
         * first header 26-2 eject 27-2 endif 28-1 print_break "hobject" 29-1 endif 30 compare (fin_object_cd) from
         * "ld_pndbc_apptfnd_t" 31-1 if ($result < 1) 32-2 if (fin_object_cd.ld_pndbc_apptfnd_t != %\
         * $next(fin_object_cd.ld_pndbc_apptfnd_t)) 33-2 call lp_set_und 34-2 endif
         */


    }

    private void buildReportsTotal(PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport, Collection<BudgetConstructionAccountSalaryDetailReportTotal> accountSalaryDetailTotal) {

        for (BudgetConstructionAccountSalaryDetailReportTotal totalEntry : accountSalaryDetailTotal) {
            if (BudgetConstructionReportHelper.isSameEntry(totalEntry.getPendingBudgetConstructionAppointmentFunding(), pendingAppointmentFunding, fieldsForTotal())) {
                
                String objectCodeName = "";
                if (pendingAppointmentFunding.getFinancialObject() != null){
                    try {
                        objectCodeName = pendingAppointmentFunding.getFinancialObject().getFinancialObjectCodeName();
                    }
                    catch (PersistenceBrokerException e) {
                        objectCodeName = kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_OBJECT_NAME);
                    }
                } else {
                    objectCodeName = BCConstants.Report.OBJECT + BCConstants.Report.NOT_DEFINED;
                }
                accountMonthlyDetailReport.setTotalDescription(objectCodeName);
                
                accountMonthlyDetailReport.setTotalBaseAmount(totalEntry.getTotalBaseAmount());
                accountMonthlyDetailReport.setTotalBaseFte(totalEntry.getTotalBaseFte());
                accountMonthlyDetailReport.setTotalRequestAmount(totalEntry.getTotalRequestAmount());
                accountMonthlyDetailReport.setTotalRequestFte(totalEntry.getTotalRequestFte());
                
                accountMonthlyDetailReport.setTotalAmountChange(totalEntry.getTotalRequestAmount() - totalEntry.getTotalBaseAmount());
                accountMonthlyDetailReport.setTotalPercentChange(BudgetConstructionReportHelper.calculatePercent(accountMonthlyDetailReport.getTotalAmountChange(), totalEntry.getTotalBaseAmount()));
            }
        }
    }


    private Collection<BudgetConstructionAccountSalaryDetailReportTotal> calculateTotal(Collection<PendingBudgetConstructionAppointmentFunding> pendingAppointmentFundingList, List<PendingBudgetConstructionAppointmentFunding> listForTotal) {
        Collection<BudgetConstructionAccountSalaryDetailReportTotal> returnCollection = new ArrayList();
        Integer totalBaseAmount = new Integer(0);
        BigDecimal totalBaseFte = BigDecimal.ZERO;
        Integer totalRequestAmount = new Integer(0);
        BigDecimal totalRequestFte = BigDecimal.ZERO;


        
        for (PendingBudgetConstructionAppointmentFunding totalEntry : listForTotal) {
            BudgetConstructionAccountSalaryDetailReportTotal budgetConstructionAccountSalaryDetailReportTotal = new BudgetConstructionAccountSalaryDetailReportTotal();
            for (PendingBudgetConstructionAppointmentFunding appointmentFundingEntry : pendingAppointmentFundingList) {
                if (BudgetConstructionReportHelper.isSameEntry(totalEntry, appointmentFundingEntry, fieldsForTotal())) {
                    totalBaseAmount = totalBaseAmount + new Integer(appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfAmount().intValue());
                    totalBaseFte = totalBaseFte.add(appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfFullTimeEmploymentQuantity());
                    totalRequestAmount = totalRequestAmount + new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue());
                    totalRequestFte = totalRequestFte.add(appointmentFundingEntry.getAppointmentRequestedFteQuantity());
                }
            }
            budgetConstructionAccountSalaryDetailReportTotal.setTotalBaseAmount(totalBaseAmount);
            budgetConstructionAccountSalaryDetailReportTotal.setTotalBaseFte(totalBaseFte);
            budgetConstructionAccountSalaryDetailReportTotal.setTotalRequestAmount(totalRequestAmount);
            budgetConstructionAccountSalaryDetailReportTotal.setTotalRequestFte(totalRequestFte);
            budgetConstructionAccountSalaryDetailReportTotal.setPendingBudgetConstructionAppointmentFunding(totalEntry);
            returnCollection.add(budgetConstructionAccountSalaryDetailReportTotal);
            // set all values to zero, after the entry was added to collection
            totalBaseAmount = new Integer(0);
            totalBaseFte = BigDecimal.ZERO;
            totalRequestAmount = new Integer(0);
            totalRequestFte = BigDecimal.ZERO;
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

    private Map buildSearchCriteria(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
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
