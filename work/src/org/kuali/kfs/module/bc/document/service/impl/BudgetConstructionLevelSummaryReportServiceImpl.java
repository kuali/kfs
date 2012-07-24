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
import java.util.List;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLevelSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgLevelSummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgLevelSummaryReportTotal;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLevelSummaryReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.module.bc.util.BudgetConstructionUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionLevelSummaryReportServiceImpl implements BudgetConstructionLevelSummaryReportService {

    protected BudgetConstructionLevelSummaryReportDao budgetConstructionLevelSummaryReportDao;
    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetReportsControlListService#updateRepotsLevelSummaryTable(java.lang.String)
     */
    public void updateLevelSummaryReport(String principalName) {
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        String revenueINList = BudgetConstructionUtils.getRevenueINList();
        budgetConstructionLevelSummaryReportDao.cleanReportsLevelSummaryTable(principalName);
        budgetConstructionLevelSummaryReportDao.updateReportsLevelSummaryTable(principalName, expenditureINList, revenueINList);
    }

    /**
     * sets budgetConstructionLevelSummaryReportDao
     * 
     * @param budgetConstructionLevelSummaryReportDao
     */
    public void setBudgetConstructionLevelSummaryReportDao(BudgetConstructionLevelSummaryReportDao budgetConstructionLevelSummaryReportDao) {
        this.budgetConstructionLevelSummaryReportDao = budgetConstructionLevelSummaryReportDao;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionOrgLevelSummaryReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgLevelSummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgLevelSummaryReport orgLevelSummaryReportEntry;
        Collection<BudgetConstructionLevelSummary> levelSummaryList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionLevelSummary.class, principalName, buildOrderByList());


        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List listForCalculateCons = BudgetConstructionReportHelper.deleteDuplicated((List) levelSummaryList, fieldsForCons());
        List listForCalculateGexpAndType = BudgetConstructionReportHelper.deleteDuplicated((List) levelSummaryList, fieldsForGexpAndType());
        List listForCalculateTotal = BudgetConstructionReportHelper.deleteDuplicated((List) levelSummaryList, fieldsForTotal());

        // Calculate Total Section
        List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalConsList = calculateConsTotal((List) levelSummaryList, listForCalculateCons);
        List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalGexpAndTypeList = calculateGexpAndTypeTotal((List) levelSummaryList, listForCalculateGexpAndType);
        List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalList = calculateTotal((List) levelSummaryList, listForCalculateTotal);

        for (BudgetConstructionLevelSummary levelSummaryEntry : levelSummaryList) {
            orgLevelSummaryReportEntry = new BudgetConstructionOrgLevelSummaryReport();
            buildReportsHeader(universityFiscalYear, orgLevelSummaryReportEntry, levelSummaryEntry);
            buildReportsBody(orgLevelSummaryReportEntry, levelSummaryEntry);
            buildReportsTotal(orgLevelSummaryReportEntry, levelSummaryEntry, levelSummaryTotalConsList, levelSummaryTotalGexpAndTypeList, levelSummaryTotalList);
            reportSet.add(orgLevelSummaryReportEntry);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    public void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgLevelSummaryReport orgLevelSummaryReportEntry, BudgetConstructionLevelSummary levelSummary) {
        String orgChartDesc = levelSummary.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = levelSummary.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = levelSummary.getOrganization().getOrganizationName();
        String reportChartDesc = levelSummary.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = levelSummary.getSubFundGroup().getSubFundGroupCode();
        String subFundGroupDes = levelSummary.getSubFundGroup().getSubFundGroupDescription();
        String fundGroupName = levelSummary.getSubFundGroup().getFundGroupCode();
        String fundGroupDes = levelSummary.getSubFundGroup().getFundGroup().getName();

        Integer prevFiscalyear = universityFiscalYear - 1;
        orgLevelSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgLevelSummaryReportEntry.setOrgChartOfAccountsCode(levelSummary.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgLevelSummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgLevelSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgLevelSummaryReportEntry.setOrganizationCode(levelSummary.getOrganizationCode());
        if (orgName == null) {
            orgLevelSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgLevelSummaryReportEntry.setOrganizationName(orgName);
        }

        orgLevelSummaryReportEntry.setChartOfAccountsCode(levelSummary.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgLevelSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgLevelSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgLevelSummaryReportEntry.setFundGroupCode(levelSummary.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgLevelSummaryReportEntry.setFundGroupName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgLevelSummaryReportEntry.setFundGroupName(fundGroupDes);
        }

        orgLevelSummaryReportEntry.setSubFundGroupCode(levelSummary.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgLevelSummaryReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgLevelSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgLevelSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + "-" + prevFiscalyear.toString().substring(2, 4));
        orgLevelSummaryReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgLevelSummaryReportEntry.setHeader1("Object Level Name");
        orgLevelSummaryReportEntry.setHeader2a("Lv. FTE");
        orgLevelSummaryReportEntry.setHeader2("FTE");
        orgLevelSummaryReportEntry.setHeader3("Amount");
        orgLevelSummaryReportEntry.setHeader31("FTE");
        orgLevelSummaryReportEntry.setHeader40("FTE");
        orgLevelSummaryReportEntry.setHeader4("Amount");
        orgLevelSummaryReportEntry.setHeader5(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgLevelSummaryReportEntry.setHeader6(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgLevelSummaryReportEntry.setConsHdr("");

        // For page break for objectLevelCode
        orgLevelSummaryReportEntry.setFinancialObjectLevelCode(levelSummary.getFinancialObjectLevelCode());
        orgLevelSummaryReportEntry.setIncomeExpenseCode(levelSummary.getIncomeExpenseCode());
        orgLevelSummaryReportEntry.setFinancialConsolidationSortCode(levelSummary.getFinancialConsolidationSortCode());

    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    public void buildReportsBody(BudgetConstructionOrgLevelSummaryReport orgLevelSummaryReportEntry, BudgetConstructionLevelSummary levelSummary) {

        if (levelSummary.getFinancialObjectLevel() == null) {
            orgLevelSummaryReportEntry.setFinancialObjectLevelName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_OBJECT_LEVEL_NAME));
        }
        else {
            orgLevelSummaryReportEntry.setFinancialObjectLevelName(levelSummary.getFinancialObjectLevel().getFinancialObjectLevelName());
        }

        orgLevelSummaryReportEntry.setPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelSummary.getPositionCsfLeaveFteQuantity(), 2, true));
        orgLevelSummaryReportEntry.setCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelSummary.getCsfFullTimeEmploymentQuantity(), 2, true));
        orgLevelSummaryReportEntry.setAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelSummary.getAppointmentRequestedCsfFteQuantity(), 2, true));
        orgLevelSummaryReportEntry.setAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelSummary.getAppointmentRequestedFteQuantity(), 2, true));

        if (levelSummary.getAccountLineAnnualBalanceAmount() != null) {
            orgLevelSummaryReportEntry.setAccountLineAnnualBalanceAmount(new Integer(levelSummary.getAccountLineAnnualBalanceAmount().intValue()));
        }

        if (levelSummary.getFinancialBeginningBalanceLineAmount() != null) {
            orgLevelSummaryReportEntry.setFinancialBeginningBalanceLineAmount(new Integer(levelSummary.getFinancialBeginningBalanceLineAmount().intValue()));
        }

        if (levelSummary.getAccountLineAnnualBalanceAmount() != null && levelSummary.getFinancialBeginningBalanceLineAmount() != null) {
            int changeAmount = levelSummary.getAccountLineAnnualBalanceAmount().subtract(levelSummary.getFinancialBeginningBalanceLineAmount()).intValue();
            orgLevelSummaryReportEntry.setAmountChange(new Integer(changeAmount));
        }

        orgLevelSummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgLevelSummaryReportEntry.getAmountChange(), orgLevelSummaryReportEntry.getFinancialBeginningBalanceLineAmount()));
    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionLevelSummary bcas
     * @param List reportTotalList
     */
    public void buildReportsTotal(BudgetConstructionOrgLevelSummaryReport orgLevelSummaryReportEntry, BudgetConstructionLevelSummary levelSummary, List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalConsList, List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalGexpAndTypeList, List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalList) {

        for (BudgetConstructionOrgLevelSummaryReportTotal consTotal : levelSummaryTotalConsList) {
            if (BudgetConstructionReportHelper.isSameEntry(levelSummary, consTotal.getBcls(), fieldsForCons())) {
                orgLevelSummaryReportEntry.setTotalConsolidationDescription(levelSummary.getFinancialConsolidationObject().getFinConsolidationObjectName());

                // The total part shouldn't have null value, so just checking '0'
                orgLevelSummaryReportEntry.setTotalConsolidationPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationPositionCsfLeaveFteQuantity(), 2, true));
                orgLevelSummaryReportEntry.setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationPositionCsfFullTimeEmploymentQuantity(), 2, true));
                orgLevelSummaryReportEntry.setTotalConsolidationFinancialBeginningBalanceLineAmount(consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setTotalConsolidationAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationAppointmentRequestedCsfFteQuantity(), 2, true));
                orgLevelSummaryReportEntry.setTotalConsolidationAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationAppointmentRequestedFteQuantity(), 2, true));
                orgLevelSummaryReportEntry.setTotalConsolidationAccountLineAnnualBalanceAmount(consTotal.getTotalConsolidationAccountLineAnnualBalanceAmount());

                Integer amountChange = consTotal.getTotalConsolidationAccountLineAnnualBalanceAmount() - consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount();
                orgLevelSummaryReportEntry.setTotalConsolidationAmountChange(amountChange);
                orgLevelSummaryReportEntry.setTotalConsolidationPercentChange(BudgetConstructionReportHelper.calculatePercent(amountChange, consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionOrgLevelSummaryReportTotal gexpAndTypeTotal : levelSummaryTotalGexpAndTypeList) {
            if (BudgetConstructionReportHelper.isSameEntry(levelSummary, gexpAndTypeTotal.getBcls(), fieldsForGexpAndType())) {

                orgLevelSummaryReportEntry.setGrossFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setGrossAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount());
                Integer gexpAndTypeAmountChange = gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount() - gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount();
                orgLevelSummaryReportEntry.setGrossAmountChange(gexpAndTypeAmountChange);
                orgLevelSummaryReportEntry.setGrossPercentChange(BudgetConstructionReportHelper.calculatePercent(gexpAndTypeAmountChange, gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount()));

                if (levelSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                    orgLevelSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE));
                }
                else {
                    orgLevelSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR));
                }
                orgLevelSummaryReportEntry.setTypePositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity(), 2, true));
                orgLevelSummaryReportEntry.setTypePositionCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypePositionCsfFullTimeEmploymentQuantity(), 2, true));
                orgLevelSummaryReportEntry.setTypeFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setTypeAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity(), 2, true));
                orgLevelSummaryReportEntry.setTypeAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity(), 2, true));

                orgLevelSummaryReportEntry.setTypeAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount());
                Integer typeAmountChange = gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount() - gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount();
                orgLevelSummaryReportEntry.setTypeAmountChange(typeAmountChange);
                orgLevelSummaryReportEntry.setTypePercentChange(BudgetConstructionReportHelper.calculatePercent(typeAmountChange, gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionOrgLevelSummaryReportTotal total : levelSummaryTotalList) {
            if (BudgetConstructionReportHelper.isSameEntry(levelSummary, total.getBcls(), fieldsForTotal())) {

                orgLevelSummaryReportEntry.setTotalSubFundGroupDesc(levelSummary.getSubFundGroup().getSubFundGroupDescription());
                orgLevelSummaryReportEntry.setRevenueFinancialBeginningBalanceLineAmount(total.getRevenueFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setRevenueAccountLineAnnualBalanceAmount(total.getRevenueAccountLineAnnualBalanceAmount());
                orgLevelSummaryReportEntry.setExpenditureFinancialBeginningBalanceLineAmount(total.getExpenditureFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setExpenditureAccountLineAnnualBalanceAmount(total.getExpenditureAccountLineAnnualBalanceAmount());

                Integer revenueAmountChange = total.getRevenueAccountLineAnnualBalanceAmount() - total.getRevenueFinancialBeginningBalanceLineAmount();
                orgLevelSummaryReportEntry.setRevenueAmountChange(revenueAmountChange);
                orgLevelSummaryReportEntry.setRevenuePercentChange(BudgetConstructionReportHelper.calculatePercent(revenueAmountChange, total.getRevenueFinancialBeginningBalanceLineAmount()));

                Integer expenditureAmountChange = total.getExpenditureAccountLineAnnualBalanceAmount() - total.getExpenditureFinancialBeginningBalanceLineAmount();
                orgLevelSummaryReportEntry.setExpenditureAmountChange(expenditureAmountChange);
                orgLevelSummaryReportEntry.setExpenditurePercentChange(BudgetConstructionReportHelper.calculatePercent(expenditureAmountChange, total.getExpenditureFinancialBeginningBalanceLineAmount()));

                orgLevelSummaryReportEntry.setDifferenceFinancialBeginningBalanceLineAmount(total.getDifferenceFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setDifferenceAccountLineAnnualBalanceAmount(total.getDifferenceAccountLineAnnualBalanceAmount());

                Integer differenceAmountChange = total.getDifferenceAccountLineAnnualBalanceAmount() - total.getDifferenceFinancialBeginningBalanceLineAmount();
                orgLevelSummaryReportEntry.setDifferenceAmountChange(differenceAmountChange);
                orgLevelSummaryReportEntry.setDifferencePercentChange(BudgetConstructionReportHelper.calculatePercent(differenceAmountChange, total.getDifferenceFinancialBeginningBalanceLineAmount()));
            }
        }
    }


    public List calculateConsTotal(List<BudgetConstructionLevelSummary> bclsList, List<BudgetConstructionLevelSummary> simpleList) {

        BigDecimal totalConsolidationPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalConsolidationFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalConsolidationAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionLevelSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgLevelSummaryReportTotal bcLevelTotal = new BudgetConstructionOrgLevelSummaryReportTotal();
            for (BudgetConstructionLevelSummary bclsListEntry : bclsList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBclsEntry, bclsListEntry, fieldsForCons())) {
                    totalConsolidationFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalConsolidationAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalConsolidationPositionCsfLeaveFteQuantity = totalConsolidationPositionCsfLeaveFteQuantity.add(bclsListEntry.getPositionCsfLeaveFteQuantity());
                    totalConsolidationPositionCsfFullTimeEmploymentQuantity = totalConsolidationPositionCsfFullTimeEmploymentQuantity.add(bclsListEntry.getCsfFullTimeEmploymentQuantity());
                    totalConsolidationAppointmentRequestedCsfFteQuantity = totalConsolidationAppointmentRequestedCsfFteQuantity.add(bclsListEntry.getAppointmentRequestedCsfFteQuantity());
                    totalConsolidationAppointmentRequestedFteQuantity = totalConsolidationAppointmentRequestedFteQuantity.add(bclsListEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcLevelTotal.setBcls(simpleBclsEntry);
            bcLevelTotal.setTotalConsolidationPositionCsfLeaveFteQuantity(totalConsolidationPositionCsfLeaveFteQuantity);
            bcLevelTotal.setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(totalConsolidationPositionCsfFullTimeEmploymentQuantity);
            bcLevelTotal.setTotalConsolidationFinancialBeginningBalanceLineAmount(totalConsolidationFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setTotalConsolidationAppointmentRequestedCsfFteQuantity(totalConsolidationAppointmentRequestedCsfFteQuantity);
            bcLevelTotal.setTotalConsolidationAppointmentRequestedFteQuantity(totalConsolidationAppointmentRequestedFteQuantity);
            bcLevelTotal.setTotalConsolidationAccountLineAnnualBalanceAmount(totalConsolidationAccountLineAnnualBalanceAmount);
            returnList.add(bcLevelTotal);

            totalConsolidationPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalConsolidationPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalConsolidationFinancialBeginningBalanceLineAmount = new Integer(0);
            totalConsolidationAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalConsolidationAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalConsolidationAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;
    }

    public List calculateGexpAndTypeTotal(List<BudgetConstructionLevelSummary> bclsList, List<BudgetConstructionLevelSummary> simpleList) {

        Integer grossFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer grossAccountLineAnnualBalanceAmount = new Integer(0);

        BigDecimal typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal typePositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer typeFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer typeAccountLineAnnualBalanceAmount = new Integer(0);
        Integer typeAmountChange = new Integer(0);
        BigDecimal typePercentChange = BigDecimal.ZERO;

        List returnList = new ArrayList();
        for (BudgetConstructionLevelSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgLevelSummaryReportTotal bcLevelTotal = new BudgetConstructionOrgLevelSummaryReportTotal();
            for (BudgetConstructionLevelSummary bclsListEntry : bclsList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBclsEntry, bclsListEntry, fieldsForGexpAndType())) {

                    typeFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    typeAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity.add(bclsListEntry.getPositionCsfLeaveFteQuantity());
                    typePositionCsfFullTimeEmploymentQuantity = typePositionCsfFullTimeEmploymentQuantity.add(bclsListEntry.getCsfFullTimeEmploymentQuantity());
                    typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity.add(bclsListEntry.getAppointmentRequestedCsfFteQuantity());
                    typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity.add(bclsListEntry.getAppointmentRequestedFteQuantity());

                    if (bclsListEntry.getIncomeExpenseCode().equals("B") && !bclsListEntry.getFinancialObjectLevelCode().equals("CORI") && !bclsListEntry.getFinancialObjectLevelCode().equals("TRIN")) {
                        grossFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        grossAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }
            bcLevelTotal.setBcls(simpleBclsEntry);

            bcLevelTotal.setGrossFinancialBeginningBalanceLineAmount(grossFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setGrossAccountLineAnnualBalanceAmount(grossAccountLineAnnualBalanceAmount);

            bcLevelTotal.setTypePositionCsfLeaveFteQuantity(typePositionCsfLeaveFteQuantity);
            bcLevelTotal.setTypePositionCsfFullTimeEmploymentQuantity(typePositionCsfFullTimeEmploymentQuantity);
            bcLevelTotal.setTypeFinancialBeginningBalanceLineAmount(typeFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setTypeAppointmentRequestedCsfFteQuantity(typeAppointmentRequestedCsfFteQuantity);
            bcLevelTotal.setTypeAppointmentRequestedFteQuantity(typeAppointmentRequestedFteQuantity);
            bcLevelTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);


            returnList.add(bcLevelTotal);
            grossFinancialBeginningBalanceLineAmount = new Integer(0);
            grossAccountLineAnnualBalanceAmount = new Integer(0);

            typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            typePositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            typeFinancialBeginningBalanceLineAmount = new Integer(0);
            typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            typeAccountLineAnnualBalanceAmount = new Integer(0);
            typeAmountChange = new Integer(0);
            typePercentChange = BigDecimal.ZERO;
        }
        return returnList;
    }


    public List calculateTotal(List<BudgetConstructionLevelSummary> bclsList, List<BudgetConstructionLevelSummary> simpleList) {

        Integer revenueFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer revenueAccountLineAnnualBalanceAmount = new Integer(0);

        Integer expenditureFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer expenditureAccountLineAnnualBalanceAmount = new Integer(0);

        Integer differenceFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer differenceAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionLevelSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgLevelSummaryReportTotal bcLevelTotal = new BudgetConstructionOrgLevelSummaryReportTotal();
            for (BudgetConstructionLevelSummary bclsListEntry : bclsList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBclsEntry, bclsListEntry, fieldsForTotal())) {

                    if (bclsListEntry.getIncomeExpenseCode().equals("A")) {
                        revenueFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        revenueAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                    else {
                        expenditureFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        expenditureAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }

            bcLevelTotal.setBcls(simpleBclsEntry);

            bcLevelTotal.setRevenueFinancialBeginningBalanceLineAmount(revenueFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setRevenueAccountLineAnnualBalanceAmount(revenueAccountLineAnnualBalanceAmount);

            bcLevelTotal.setExpenditureFinancialBeginningBalanceLineAmount(expenditureFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setExpenditureAccountLineAnnualBalanceAmount(expenditureAccountLineAnnualBalanceAmount);

            differenceFinancialBeginningBalanceLineAmount = revenueFinancialBeginningBalanceLineAmount - expenditureFinancialBeginningBalanceLineAmount;
            differenceAccountLineAnnualBalanceAmount = revenueAccountLineAnnualBalanceAmount - expenditureAccountLineAnnualBalanceAmount;
            bcLevelTotal.setDifferenceFinancialBeginningBalanceLineAmount(differenceFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setDifferenceAccountLineAnnualBalanceAmount(differenceAccountLineAnnualBalanceAmount);

            returnList.add(bcLevelTotal);

            revenueFinancialBeginningBalanceLineAmount = new Integer(0);
            revenueAccountLineAnnualBalanceAmount = new Integer(0);

            expenditureFinancialBeginningBalanceLineAmount = new Integer(0);
            expenditureAccountLineAnnualBalanceAmount = new Integer(0);

            differenceFinancialBeginningBalanceLineAmount = new Integer(0);
            differenceAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;
    }


    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        return returnList;
    }

    protected List<String> fieldsForCons() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForGexpAndType());
        fieldList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        return fieldList;
    }

    protected List<String> fieldsForGexpAndType() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForTotal());
        fieldList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        return fieldList;
    }

    protected List<String> fieldsForTotal() {
        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        fieldList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        fieldList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        return fieldList;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
