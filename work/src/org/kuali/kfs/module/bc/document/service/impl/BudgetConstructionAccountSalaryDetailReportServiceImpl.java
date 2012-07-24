/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBrokerException;
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
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountSalaryDetailReportServiceImpl implements BudgetConstructionAccountSalaryDetailReportService {

    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected SalarySettingService salarySettingService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    @Override
    public Collection<BudgetConstructionAccountSalaryDetailReport> buildReports(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        Collection<BudgetConstructionAccountSalaryDetailReport> reportSet = new ArrayList<BudgetConstructionAccountSalaryDetailReport>();

        Map<String, Object> searchCriteria = buildSearchCriteria(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber);
        List<String> orderList = buildOrderByList();

        Collection<PendingBudgetConstructionAppointmentFunding> pendingAppointmentFundingList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(PendingBudgetConstructionAppointmentFunding.class, searchCriteria, orderList);
        List<PendingBudgetConstructionAppointmentFunding> listForTotal = BudgetConstructionReportHelper.deleteDuplicated((List) pendingAppointmentFundingList, fieldsForTotal());

        Collection<BudgetConstructionAccountSalaryDetailReportTotal> accountSalaryDetailTotal = calculateTotal(pendingAppointmentFundingList, listForTotal);
        for (PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding : pendingAppointmentFundingList) {
            BudgetConstructionAccountSalaryDetailReport accountSalaryDetailReport = new BudgetConstructionAccountSalaryDetailReport();

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
    protected void buildReportsHeader(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding, BudgetConstructionAccountSalaryDetailReport accountSalaryDetailReport) {

        Integer prevFiscalyear = universityFiscalYear - 1;
        accountSalaryDetailReport.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));


        accountSalaryDetailReport.setAccountNumber(pendingAppointmentFunding.getAccountNumber());
        accountSalaryDetailReport.setSubAccountNumber(pendingAppointmentFunding.getSubAccountNumber());
        accountSalaryDetailReport.setChartOfAccountsCode(pendingAppointmentFunding.getChartOfAccountsCode());
        accountSalaryDetailReport.setOrganizationCode(pendingAppointmentFunding.getAccount().getOrganizationCode());

        String chartOfAccountDescription = "";
        if (pendingAppointmentFunding.getChartOfAccounts() != null) {
            try {
                chartOfAccountDescription = pendingAppointmentFunding.getChartOfAccounts().getFinChartOfAccountDescription();
            }
            catch (PersistenceBrokerException e) {
                chartOfAccountDescription = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION);
            }
        }
        else {
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
            accountSalaryDetailReport.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            accountSalaryDetailReport.setOrganizationName(orgName);
        }

        if (fundGroupCode == null) {
            accountSalaryDetailReport.setFundGroupCode(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_CODE));
        }
        else {
            accountSalaryDetailReport.setFundGroupCode(fundGroupCode);
        }

        if (fundGroupName == null) {
            accountSalaryDetailReport.setFundGroupName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            accountSalaryDetailReport.setFundGroupName(fundGroupName);
        }

        if (accountName == null) {
            accountSalaryDetailReport.setAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION));
        }
        else {
            accountSalaryDetailReport.setAccountName(accountName);
        }

        String subAccountName = "";

        if (!pendingAppointmentFunding.getSubAccountNumber().equals(KFSConstants.getDashSubAccountNumber())) {
            try {
                subAccountName = pendingAppointmentFunding.getSubAccount().getSubAccountName();
            }
            catch (PersistenceBrokerException e) {
                subAccountName = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION);
            }
        }
        accountSalaryDetailReport.setSubAccountName(subAccountName);


    }

    /**
     * builds report body
     *
     * @param BudgetConstructionLevelSummary bcas
     */
    protected void buildReportsBody(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport) {
        Integer amountChange = new Integer(0);
        BigDecimal percentChange = BigDecimal.ZERO;

        BudgetConstructionIntendedIncumbent intendedIncumbent = budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(pendingAppointmentFunding);
        BudgetConstructionAdministrativePost administrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(pendingAppointmentFunding);
        BudgetConstructionPosition position = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, pendingAppointmentFunding);
        BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = pendingAppointmentFunding.getEffectiveCSFTracker();

        // from PendingBudgetConstructionAppointmentFunding
        accountMonthlyDetailReport.setFinancialSubObjectCode(pendingAppointmentFunding.getFinancialSubObjectCode());

        // from BudgetConstructionIntendedIncumbent
        if (intendedIncumbent != null) {
            accountMonthlyDetailReport.setIuClassificationLevel(intendedIncumbent.getIuClassificationLevel());
        }

        // from BudgetConstructionAdministrativePost
        if (administrativePost != null) {
            accountMonthlyDetailReport.setAdministrativePost(administrativePost.getAdministrativePost());
        }

        // from BudgetConstructionPosition
        if (position != null) {
            accountMonthlyDetailReport.setPositionNumber(position.getPositionNumber());
            accountMonthlyDetailReport.setPositionSalaryPlanDefault(position.getPositionSalaryPlanDefault());
            accountMonthlyDetailReport.setPositionGradeDefault(position.getPositionGradeDefault());
            accountMonthlyDetailReport.setNormalWorkMonthsAndiuPayMonths(position.getIuNormalWorkMonths() + "/" + position.getIuPayMonths());
        }

        // from BudgetConstructionCalculatedSalaryFoundationTracker
        if (csfTracker != null) {
            accountMonthlyDetailReport.setPositionCsfAmount(csfTracker.getCsfAmount().intValue());
            accountMonthlyDetailReport.setCsfTimePercent(BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfTimePercent(), 2, false));
            accountMonthlyDetailReport.setPositionCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfFullTimeEmploymentQuantity(), 5, false));
            accountMonthlyDetailReport.setPositionCsfFundingStatusCode(csfTracker.getCsfFundingStatusCode());

            BigDecimal csfFte = BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfFullTimeEmploymentQuantity(), 5, false);
            BigDecimal reqFte = BudgetConstructionReportHelper.setDecimalDigit(pendingAppointmentFunding.getAppointmentRequestedFteQuantity(), 5, false);
            if (reqFte.compareTo(csfFte) == 0) {
                amountChange = pendingAppointmentFunding.getAppointmentRequestedAmount().subtract(csfTracker.getCsfAmount()).intValue();

                if (!csfTracker.getCsfAmount().equals(KualiInteger.ZERO)) {
                    percentChange = BudgetConstructionReportHelper.calculatePercent(amountChange, csfTracker.getCsfAmount().intValue());
                }
            }
        }

        // from PendingBudgetConstructionAppointmentFunding
        accountMonthlyDetailReport.setAppointmentFundingMonth(pendingAppointmentFunding.getAppointmentFundingMonth());
        if (salarySettingService.isHourlyPaidObject(pendingAppointmentFunding.getUniversityFiscalYear(), pendingAppointmentFunding.getChartOfAccountsCode(), pendingAppointmentFunding.getFinancialObjectCode())){
            accountMonthlyDetailReport.setAppointmentRequestedPayRate(pendingAppointmentFunding.getAppointmentRequestedPayRate());
        }

        accountMonthlyDetailReport.setAppointmentRequestedAmount(pendingAppointmentFunding.getAppointmentRequestedAmount().intValue());
        accountMonthlyDetailReport.setAppointmentRequestedTimePercent(BudgetConstructionReportHelper.setDecimalDigit(pendingAppointmentFunding.getAppointmentRequestedTimePercent(), 2, false));
        accountMonthlyDetailReport.setAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(pendingAppointmentFunding.getAppointmentRequestedFteQuantity(), 5, false));
        accountMonthlyDetailReport.setAppointmentRequestedCsfAmount(pendingAppointmentFunding.getAppointmentRequestedCsfAmount().intValue());
        accountMonthlyDetailReport.setAppointmentRequestedCsfTimePercent(BudgetConstructionReportHelper.setDecimalDigit(pendingAppointmentFunding.getAppointmentRequestedCsfTimePercent(), 2, false));
        accountMonthlyDetailReport.setAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(pendingAppointmentFunding.getAppointmentRequestedCsfFteQuantity(), 5, false));
        accountMonthlyDetailReport.setAppointmentFundingDurationCode(pendingAppointmentFunding.getAppointmentFundingDurationCode());
        accountMonthlyDetailReport.setAppointmentTotalIntendedAmount(pendingAppointmentFunding.getAppointmentTotalIntendedAmount().intValue());
        accountMonthlyDetailReport.setAppointmentTotalIntendedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(pendingAppointmentFunding.getAppointmentTotalIntendedFteQuantity(), 5, false));

        accountMonthlyDetailReport.setFinancialObjectCode(pendingAppointmentFunding.getFinancialObjectCode());
        accountMonthlyDetailReport.setFinancialObjectCodeName(pendingAppointmentFunding.getFinancialObject().getFinancialObjectCodeName());

        String deleteBox = pendingAppointmentFunding.isAppointmentFundingDeleteIndicator() ? BCConstants.Report.DELETE_MARK : BCConstants.Report.BLANK;
        accountMonthlyDetailReport.setDeleteBox(deleteBox);

        if (pendingAppointmentFunding.getEmplid().equals(BCConstants.Report.VACANT)){
            accountMonthlyDetailReport.setName(BCConstants.Report.VACANT);
        } else {
            int nameLength = intendedIncumbent.getName().length();
            accountMonthlyDetailReport.setName(intendedIncumbent.getName().substring(0, (nameLength > 35) ? 35 : nameLength));

        }

        accountMonthlyDetailReport.setAmountChange(amountChange);
        accountMonthlyDetailReport.setPercentChange(percentChange);
    }

    protected void buildReportsTotal(PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport, Collection<BudgetConstructionAccountSalaryDetailReportTotal> accountSalaryDetailTotal) {

        for (BudgetConstructionAccountSalaryDetailReportTotal totalEntry : accountSalaryDetailTotal) {
            if (BudgetConstructionReportHelper.isSameEntry(totalEntry.getPendingBudgetConstructionAppointmentFunding(), pendingAppointmentFunding, fieldsForTotal())) {

                String objectCodeName = StringUtils.EMPTY;
                if (pendingAppointmentFunding.getFinancialObject() != null) {
                    try {
                        objectCodeName = pendingAppointmentFunding.getFinancialObject().getFinancialObjectCodeName();
                    }
                    catch (PersistenceBrokerException e) {
                        objectCodeName = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_OBJECT_NAME);
                    }
                }
                else {
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


    protected Collection<BudgetConstructionAccountSalaryDetailReportTotal> calculateTotal(Collection<PendingBudgetConstructionAppointmentFunding> pendingAppointmentFundingList, List<PendingBudgetConstructionAppointmentFunding> listForTotal) {
        Collection<BudgetConstructionAccountSalaryDetailReportTotal> reportTotals = new ArrayList<BudgetConstructionAccountSalaryDetailReportTotal>();

        for (PendingBudgetConstructionAppointmentFunding totalEntry : listForTotal) {
            KualiInteger totalBaseAmount = KualiInteger.ZERO;
            BigDecimal totalBaseFte = BigDecimal.ZERO;
            Integer totalRequestAmount = new Integer(0);
            BigDecimal totalRequestFte = BigDecimal.ZERO;

            BudgetConstructionAccountSalaryDetailReportTotal budgetConstructionAccountSalaryDetailReportTotal = new BudgetConstructionAccountSalaryDetailReportTotal();
            for (PendingBudgetConstructionAppointmentFunding appointmentFundingEntry : pendingAppointmentFundingList) {
                if (BudgetConstructionReportHelper.isSameEntry(totalEntry, appointmentFundingEntry, fieldsForTotal())) {
                    BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFundingEntry.getEffectiveCSFTracker();

                    if (csfTracker != null) {
                        totalBaseAmount = totalBaseAmount.add(csfTracker.getCsfAmount());
                        totalBaseFte = totalBaseFte.add(csfTracker.getCsfFullTimeEmploymentQuantity());
                    }

                    totalRequestAmount = totalRequestAmount + new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue());
                    totalRequestFte = totalRequestFte.add(appointmentFundingEntry.getAppointmentRequestedFteQuantity());
                }
            }

            budgetConstructionAccountSalaryDetailReportTotal.setTotalBaseAmount(totalBaseAmount.intValue());
            budgetConstructionAccountSalaryDetailReportTotal.setTotalBaseFte(totalBaseFte);
            budgetConstructionAccountSalaryDetailReportTotal.setTotalRequestAmount(totalRequestAmount);
            budgetConstructionAccountSalaryDetailReportTotal.setTotalRequestFte(totalRequestFte);
            budgetConstructionAccountSalaryDetailReportTotal.setPendingBudgetConstructionAppointmentFunding(totalEntry);

            reportTotals.add(budgetConstructionAccountSalaryDetailReportTotal);
        }

        return reportTotals;
    }


    protected List<String> fieldsForTotal() {
        List<String> fieldList = new ArrayList<String>();
        fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        return fieldList;
    }

    /**
     * builds orderByList for sort order.
     *
     * @return returnList
     */
    protected List<String> buildOrderByList() {
        List<String> returnList = new ArrayList<String>();
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.POSITION_NUMBER);
        returnList.add(KFSPropertyConstants.EMPLID);

        return returnList;
    }

    protected Map<String, Object> buildSearchCriteria(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);

        return searchCriteria;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the budgetConstructionReportsServiceHelper attribute value.
     *
     * @param budgetConstructionReportsServiceHelper The budgetConstructionReportsServiceHelper to set.
     */
    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

    /**
     * Sets the salarySettingService attribute value.
     * @param salarySettingService The salarySettingService to set.
     */
    public void setSalarySettingService(SalarySettingService salarySettingService) {
        this.salarySettingService = salarySettingService;
    }
}
