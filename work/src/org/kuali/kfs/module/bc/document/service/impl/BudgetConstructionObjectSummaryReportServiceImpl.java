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

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgObjectSummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgObjectSummaryReportTotal;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionObjectSummaryReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionObjectSummaryReportService;
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
public class BudgetConstructionObjectSummaryReportServiceImpl implements BudgetConstructionObjectSummaryReportService {

    protected BudgetConstructionObjectSummaryReportDao budgetConstructionObjectSummaryReportDao;
    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetReportsControlListService#updateRepotsLevelSummaryTable(java.lang.String)
     */
    public void updateObjectSummaryReport(String principalName) {
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        String revenueINList = BudgetConstructionUtils.getRevenueINList();
        budgetConstructionObjectSummaryReportDao.cleanGeneralLedgerObjectSummaryTable(principalName);
        budgetConstructionObjectSummaryReportDao.updateGeneralLedgerObjectSummaryTable(principalName, revenueINList, expenditureINList);
    }

    /**
     * sets budgetConstructionLevelSummaryReportDao
     * 
     * @param budgetConstructionLevelSummaryReportDao
     */
    public void setBudgetConstructionObjectSummaryReportDao(BudgetConstructionObjectSummaryReportDao budgetConstructionObjectSummaryReportDao) {
        this.budgetConstructionObjectSummaryReportDao = budgetConstructionObjectSummaryReportDao;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionOrgObjectSummaryReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgObjectSummaryReport> reportSet = new ArrayList();
        Collection<BudgetConstructionObjectSummary> objectSummaryList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionObjectSummary.class, principalName, buildOrderByList());

        // 
        List listForCalculateLevel = BudgetConstructionReportHelper.deleteDuplicated((List) objectSummaryList, fieldsForLevel());
        List listForCalculateCons = BudgetConstructionReportHelper.deleteDuplicated((List) objectSummaryList, fieldsForCons());
        List listForCalculateGexpAndType = BudgetConstructionReportHelper.deleteDuplicated((List) objectSummaryList, fieldsForGexpAndType());
        List listForCalculateTotal = BudgetConstructionReportHelper.deleteDuplicated((List) objectSummaryList, fieldsForTotal());

        // Calculate Total Section
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalLevelList = calculateLevelTotal((List) objectSummaryList, listForCalculateLevel);
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalConsList = calculateConsTotal((List) objectSummaryList, listForCalculateCons);
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalGexpAndTypeList = calculateGexpAndTypeTotal((List) objectSummaryList, listForCalculateGexpAndType);
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalList = calculateTotal((List) objectSummaryList, listForCalculateTotal);

        for (BudgetConstructionObjectSummary objectSummaryEntry : objectSummaryList) {
            BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry = new BudgetConstructionOrgObjectSummaryReport();
            buildReportsHeader(universityFiscalYear, orgObjectSummaryReportEntry, objectSummaryEntry);
            buildReportsBody(universityFiscalYear, orgObjectSummaryReportEntry, objectSummaryEntry);
            buildReportsTotal(orgObjectSummaryReportEntry, objectSummaryEntry, objectSummaryTotalLevelList, objectSummaryTotalConsList, objectSummaryTotalGexpAndTypeList, objectSummaryTotalList);
            reportSet.add(orgObjectSummaryReportEntry);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    protected void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry, BudgetConstructionObjectSummary objectSummary) {
        String orgChartDesc = objectSummary.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = objectSummary.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = objectSummary.getOrganization().getOrganizationName();
        String reportChartDesc = objectSummary.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = objectSummary.getSubFundGroup().getSubFundGroupCode();
        String subFundGroupDes = objectSummary.getSubFundGroup().getSubFundGroupDescription();
        String fundGroupName = objectSummary.getSubFundGroup().getFundGroupCode();
        String fundGroupDes = objectSummary.getSubFundGroup().getFundGroup().getName();

        Integer prevFiscalyear = universityFiscalYear - 1;
        orgObjectSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgObjectSummaryReportEntry.setOrgChartOfAccountsCode(objectSummary.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgObjectSummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgObjectSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgObjectSummaryReportEntry.setOrganizationCode(objectSummary.getOrganizationCode());
        if (orgName == null) {
            orgObjectSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgObjectSummaryReportEntry.setOrganizationName(orgName);
        }

        orgObjectSummaryReportEntry.setChartOfAccountsCode(objectSummary.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgObjectSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgObjectSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgObjectSummaryReportEntry.setFundGroupCode(objectSummary.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgObjectSummaryReportEntry.setFundGroupName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgObjectSummaryReportEntry.setFundGroupName(fundGroupDes);
        }

        orgObjectSummaryReportEntry.setSubFundGroupCode(objectSummary.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgObjectSummaryReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgObjectSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgObjectSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + "-" + prevFiscalyear.toString().substring(2, 4));
        orgObjectSummaryReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgObjectSummaryReportEntry.setHeader1("Object Name");
        orgObjectSummaryReportEntry.setHeader2a("Lv. FTE");
        orgObjectSummaryReportEntry.setHeader2("FTE");
        orgObjectSummaryReportEntry.setHeader3("Amount");
        orgObjectSummaryReportEntry.setHeader31("FTE");
        orgObjectSummaryReportEntry.setHeader40("FTE");
        orgObjectSummaryReportEntry.setHeader4("Amount");
        orgObjectSummaryReportEntry.setHeader5(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgObjectSummaryReportEntry.setHeader6(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgObjectSummaryReportEntry.setConsHdr("");

        // For page break for objectObjectCode
        orgObjectSummaryReportEntry.setFinancialObjectLevelCode(objectSummary.getFinancialObjectLevelCode());
        orgObjectSummaryReportEntry.setIncomeExpenseCode(objectSummary.getIncomeExpenseCode());
        orgObjectSummaryReportEntry.setFinancialConsolidationSortCode(objectSummary.getFinancialConsolidationSortCode());
        orgObjectSummaryReportEntry.setFinancialLevelSortCode(objectSummary.getFinancialLevelSortCode());
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    protected void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry, BudgetConstructionObjectSummary objectSummary) {

        orgObjectSummaryReportEntry.setFinancialObjectCode(objectSummary.getFinancialObjectCode());
        // To get ObjectName: There is no universityFiscalyear field in BudgetConstructionObjectSummary,
        // so we can get ObjectName by getting ObjectCode with Primary key.
        ObjectCode objectCode = budgetConstructionReportsServiceHelper.getObjectCode(universityFiscalYear, objectSummary.getChartOfAccountsCode(), objectSummary.getFinancialObjectCode());
        String objectName = null;

        if (objectCode == null) {
            orgObjectSummaryReportEntry.setFinancialObjectName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_OBJECT_CODE));
        }
        else {
            objectName = objectCode.getFinancialObjectCodeName();
            if (objectName == null) {
                orgObjectSummaryReportEntry.setFinancialObjectName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_OBJECT_NAME));
            }
            else {
                orgObjectSummaryReportEntry.setFinancialObjectName(objectName);
            }
        }
        orgObjectSummaryReportEntry.setPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectSummary.getPositionCsfLeaveFteQuantity(), 2, true));
        orgObjectSummaryReportEntry.setCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectSummary.getCsfFullTimeEmploymentQuantity(), 2, true));
        orgObjectSummaryReportEntry.setAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectSummary.getAppointmentRequestedCsfFteQuantity(), 2, true));
        orgObjectSummaryReportEntry.setAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectSummary.getAppointmentRequestedFteQuantity(), 2, true));

        if (objectSummary.getAccountLineAnnualBalanceAmount() != null) {
            orgObjectSummaryReportEntry.setAccountLineAnnualBalanceAmount(new Integer(objectSummary.getAccountLineAnnualBalanceAmount().intValue()));
        }

        if (objectSummary.getFinancialBeginningBalanceLineAmount() != null) {
            orgObjectSummaryReportEntry.setFinancialBeginningBalanceLineAmount(new Integer(objectSummary.getFinancialBeginningBalanceLineAmount().intValue()));
        }

        if (objectSummary.getAccountLineAnnualBalanceAmount() != null && objectSummary.getFinancialBeginningBalanceLineAmount() != null) {
            int changeAmount = objectSummary.getAccountLineAnnualBalanceAmount().subtract(objectSummary.getFinancialBeginningBalanceLineAmount()).intValue();
            orgObjectSummaryReportEntry.setAmountChange(new Integer(changeAmount));
        }

        orgObjectSummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgObjectSummaryReportEntry.getAmountChange(), orgObjectSummaryReportEntry.getFinancialBeginningBalanceLineAmount()));
    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionObjectSummary bcas
     * @param List reportTotalList
     */
    protected void buildReportsTotal(BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry, BudgetConstructionObjectSummary objectSummary, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalLevelList, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalConsList, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalGexpAndTypeList, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalList) {

        for (BudgetConstructionOrgObjectSummaryReportTotal levelTotal : objectSummaryTotalLevelList) {
            if (BudgetConstructionReportHelper.isSameEntry(objectSummary, levelTotal.getBcos(), fieldsForLevel())) {
                orgObjectSummaryReportEntry.setTotalLevelDescription(objectSummary.getFinancialObjectLevel().getFinancialObjectLevelName());

                orgObjectSummaryReportEntry.setTotalLevelPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelPositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelPositionCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelPositionCsfFullTimeEmploymentQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelFinancialBeginningBalanceLineAmount(levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelAppointmentRequestedFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelAccountLineAnnualBalanceAmount(levelTotal.getTotalLevelAccountLineAnnualBalanceAmount());

                Integer totalLevelAmountChange = levelTotal.getTotalLevelAccountLineAnnualBalanceAmount() - levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setTotalLevelAmountChange(totalLevelAmountChange);
                orgObjectSummaryReportEntry.setTotalLevelPercentChange(BudgetConstructionReportHelper.calculatePercent(totalLevelAmountChange, levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionOrgObjectSummaryReportTotal consTotal : objectSummaryTotalConsList) {
            if (BudgetConstructionReportHelper.isSameEntry(objectSummary, consTotal.getBcos(), fieldsForCons())) {
                orgObjectSummaryReportEntry.setTotalConsolidationDescription(objectSummary.getFinancialConsolidationObject().getFinConsolidationObjectName());

                orgObjectSummaryReportEntry.setTotalConsolidationPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationPositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationPositionCsfFullTimeEmploymentQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalConsolidationFinancialBeginningBalanceLineAmount(consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setTotalConsolidationAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalConsolidationAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(consTotal.getTotalConsolidationAppointmentRequestedFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalConsolidationAccountLineAnnualBalanceAmount(consTotal.getTotalConsolidationAccountLineAnnualBalanceAmount());
                Integer totalConsolidationAmountChange = consTotal.getTotalConsolidationAccountLineAnnualBalanceAmount() - consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setTotalConsolidationAmountChange(totalConsolidationAmountChange);
                orgObjectSummaryReportEntry.setTotalConsolidationPercentChange(BudgetConstructionReportHelper.calculatePercent(totalConsolidationAmountChange, consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount()));

            }
        }

        for (BudgetConstructionOrgObjectSummaryReportTotal gexpAndTypeTotal : objectSummaryTotalGexpAndTypeList) {
            if (BudgetConstructionReportHelper.isSameEntry(objectSummary, gexpAndTypeTotal.getBcos(), fieldsForGexpAndType())) {

                orgObjectSummaryReportEntry.setGrossFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setGrossAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount());
                Integer grossAmountChange = gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount() - gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setGrossAmountChange(grossAmountChange);
                orgObjectSummaryReportEntry.setGrossPercentChange(BudgetConstructionReportHelper.calculatePercent(grossAmountChange, gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount()));

                if (objectSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE));
                }
                else {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR));
                }
                orgObjectSummaryReportEntry.setTypePositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTypePositionCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypePositionCsfFullTimeEmploymentQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTypeFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setTypeAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTypeAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTypeAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount());
                Integer typeAmountChange = gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount() - gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setTypeAmountChange(typeAmountChange);
                orgObjectSummaryReportEntry.setTypePercentChange(BudgetConstructionReportHelper.calculatePercent(typeAmountChange, gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionOrgObjectSummaryReportTotal total : objectSummaryTotalList) {
            if (BudgetConstructionReportHelper.isSameEntry(objectSummary, total.getBcos(), fieldsForTotal())) {
                orgObjectSummaryReportEntry.setTotalSubFundGroupDesc(objectSummary.getSubFundGroup().getSubFundGroupDescription());
                orgObjectSummaryReportEntry.setRevenueFinancialBeginningBalanceLineAmount(total.getRevenueFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setRevenueAccountLineAnnualBalanceAmount(total.getRevenueAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setExpenditureFinancialBeginningBalanceLineAmount(total.getExpenditureFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setExpenditureAccountLineAnnualBalanceAmount(total.getExpenditureAccountLineAnnualBalanceAmount());

                Integer revenueAmountChange = total.getRevenueAccountLineAnnualBalanceAmount() - total.getRevenueFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setRevenueAmountChange(revenueAmountChange);
                orgObjectSummaryReportEntry.setRevenuePercentChange(BudgetConstructionReportHelper.calculatePercent(revenueAmountChange, total.getRevenueFinancialBeginningBalanceLineAmount()));

                Integer expenditureAmountChange = total.getExpenditureAccountLineAnnualBalanceAmount() - total.getExpenditureFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setExpenditureAmountChange(expenditureAmountChange);
                orgObjectSummaryReportEntry.setExpenditurePercentChange(BudgetConstructionReportHelper.calculatePercent(expenditureAmountChange, total.getExpenditureFinancialBeginningBalanceLineAmount()));

                orgObjectSummaryReportEntry.setDifferenceFinancialBeginningBalanceLineAmount(total.getDifferenceFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setDifferenceAccountLineAnnualBalanceAmount(total.getDifferenceAccountLineAnnualBalanceAmount());

                Integer differenceAmountChange = total.getDifferenceAccountLineAnnualBalanceAmount() - total.getDifferenceFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setDifferenceAmountChange(differenceAmountChange);
                orgObjectSummaryReportEntry.setDifferencePercentChange(BudgetConstructionReportHelper.calculatePercent(differenceAmountChange, total.getDifferenceFinancialBeginningBalanceLineAmount()));
            }
        }
    }

    protected List calculateLevelTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

        BigDecimal totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalLevelAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();

        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcosListEntry, fieldsForLevel())) {
                    totalLevelFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalLevelAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity.add(bcosListEntry.getPositionCsfLeaveFteQuantity());
                    totalLevelPositionCsfFullTimeEmploymentQuantity = totalLevelPositionCsfFullTimeEmploymentQuantity.add(bcosListEntry.getCsfFullTimeEmploymentQuantity());
                    totalLevelAppointmentRequestedCsfFteQuantity = totalLevelAppointmentRequestedCsfFteQuantity.add(bcosListEntry.getAppointmentRequestedCsfFteQuantity());
                    totalLevelAppointmentRequestedFteQuantity = totalLevelAppointmentRequestedFteQuantity.add(bcosListEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBcos(simpleBcosEntry);
            bcObjectTotal.setTotalLevelPositionCsfLeaveFteQuantity(totalLevelPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalLevelPositionCsfFullTimeEmploymentQuantity(totalLevelPositionCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTotalLevelFinancialBeginningBalanceLineAmount(totalLevelFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalLevelAppointmentRequestedCsfFteQuantity(totalLevelAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalLevelAppointmentRequestedFteQuantity(totalLevelAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalLevelAccountLineAnnualBalanceAmount(totalLevelAccountLineAnnualBalanceAmount);
            returnList.add(bcObjectTotal);

            totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalLevelPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
            totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;

    }

    protected List calculateConsTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

        BigDecimal totalConsolidationPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalConsolidationFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalConsolidationAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcosListEntry, fieldsForCons())) {
                    totalConsolidationFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalConsolidationAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalConsolidationPositionCsfLeaveFteQuantity = totalConsolidationPositionCsfLeaveFteQuantity.add(bcosListEntry.getPositionCsfLeaveFteQuantity());
                    totalConsolidationPositionCsfFullTimeEmploymentQuantity = totalConsolidationPositionCsfFullTimeEmploymentQuantity.add(bcosListEntry.getCsfFullTimeEmploymentQuantity());
                    totalConsolidationAppointmentRequestedCsfFteQuantity = totalConsolidationAppointmentRequestedCsfFteQuantity.add(bcosListEntry.getAppointmentRequestedCsfFteQuantity());
                    totalConsolidationAppointmentRequestedFteQuantity = totalConsolidationAppointmentRequestedFteQuantity.add(bcosListEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBcos(simpleBcosEntry);
            bcObjectTotal.setTotalConsolidationPositionCsfLeaveFteQuantity(totalConsolidationPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(totalConsolidationPositionCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTotalConsolidationFinancialBeginningBalanceLineAmount(totalConsolidationFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalConsolidationAppointmentRequestedCsfFteQuantity(totalConsolidationAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalConsolidationAppointmentRequestedFteQuantity(totalConsolidationAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalConsolidationAccountLineAnnualBalanceAmount(totalConsolidationAccountLineAnnualBalanceAmount);
            returnList.add(bcObjectTotal);

            totalConsolidationPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalConsolidationPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalConsolidationFinancialBeginningBalanceLineAmount = new Integer(0);
            totalConsolidationAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalConsolidationAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalConsolidationAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;
    }

    protected List calculateGexpAndTypeTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

        Integer grossFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer grossAccountLineAnnualBalanceAmount = new Integer(0);

        BigDecimal typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal typePositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer typeFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer typeAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcosListEntry, fieldsForGexpAndType())) {

                    typeFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    typeAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity.add(bcosListEntry.getPositionCsfLeaveFteQuantity());
                    typePositionCsfFullTimeEmploymentQuantity = typePositionCsfFullTimeEmploymentQuantity.add(bcosListEntry.getCsfFullTimeEmploymentQuantity());
                    typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity.add(bcosListEntry.getAppointmentRequestedCsfFteQuantity());
                    typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity.add(bcosListEntry.getAppointmentRequestedFteQuantity());

                    if (bcosListEntry.getIncomeExpenseCode().equals("B") && !bcosListEntry.getFinancialObjectLevelCode().equals("CORI") && !bcosListEntry.getFinancialObjectLevelCode().equals("TRIN")) {
                        grossFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        grossAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }
            bcObjectTotal.setBcos(simpleBcosEntry);

            bcObjectTotal.setGrossFinancialBeginningBalanceLineAmount(grossFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setGrossAccountLineAnnualBalanceAmount(grossAccountLineAnnualBalanceAmount);

            bcObjectTotal.setTypePositionCsfLeaveFteQuantity(typePositionCsfLeaveFteQuantity);
            bcObjectTotal.setTypePositionCsfFullTimeEmploymentQuantity(typePositionCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTypeFinancialBeginningBalanceLineAmount(typeFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTypeAppointmentRequestedCsfFteQuantity(typeAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTypeAppointmentRequestedFteQuantity(typeAppointmentRequestedFteQuantity);
            bcObjectTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);

            returnList.add(bcObjectTotal);
            grossFinancialBeginningBalanceLineAmount = new Integer(0);
            grossAccountLineAnnualBalanceAmount = new Integer(0);

            typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            typePositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            typeFinancialBeginningBalanceLineAmount = new Integer(0);
            typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            typeAccountLineAnnualBalanceAmount = new Integer(0);
        }

        return returnList;
    }


    protected List calculateTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

        Integer revenueFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer revenueAccountLineAnnualBalanceAmount = new Integer(0);

        Integer expenditureFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer expenditureAccountLineAnnualBalanceAmount = new Integer(0);

        Integer differenceFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer differenceAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();

        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcosListEntry, fieldsForTotal())) {

                    if (bcosListEntry.getIncomeExpenseCode().equals("A")) {
                        revenueFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        revenueAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                    else {
                        expenditureFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        expenditureAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }

            bcObjectTotal.setBcos(simpleBcosEntry);

            bcObjectTotal.setRevenueFinancialBeginningBalanceLineAmount(revenueFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setRevenueAccountLineAnnualBalanceAmount(revenueAccountLineAnnualBalanceAmount);

            bcObjectTotal.setExpenditureFinancialBeginningBalanceLineAmount(expenditureFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setExpenditureAccountLineAnnualBalanceAmount(expenditureAccountLineAnnualBalanceAmount);

            differenceFinancialBeginningBalanceLineAmount = revenueFinancialBeginningBalanceLineAmount - expenditureFinancialBeginningBalanceLineAmount;
            differenceAccountLineAnnualBalanceAmount = revenueAccountLineAnnualBalanceAmount - expenditureAccountLineAnnualBalanceAmount;
            bcObjectTotal.setDifferenceFinancialBeginningBalanceLineAmount(differenceFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setDifferenceAccountLineAnnualBalanceAmount(differenceAccountLineAnnualBalanceAmount);

            returnList.add(bcObjectTotal);

            revenueFinancialBeginningBalanceLineAmount = new Integer(0);
            revenueAccountLineAnnualBalanceAmount = new Integer(0);

            expenditureFinancialBeginningBalanceLineAmount = new Integer(0);
            expenditureAccountLineAnnualBalanceAmount = new Integer(0);

            differenceFinancialBeginningBalanceLineAmount = new Integer(0);
            differenceAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;
    }


    protected List<String> fieldsForLevel() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForCons());
        fieldList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        return fieldList;
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
        /*
         * fieldList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
         * fieldList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
         * fieldList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE); fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
         */
        return fieldList;
    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    protected List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        return returnList;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }
}
