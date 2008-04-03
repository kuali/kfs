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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionLevelSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgLevelSummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgLevelSummaryReportTotal;
import org.kuali.module.budget.dao.BudgetConstructionLevelSummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionLevelSummaryReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionLevelSummaryReportServiceImpl implements BudgetConstructionLevelSummaryReportService {

    BudgetConstructionLevelSummaryReportDao budgetConstructionLevelSummaryReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateRepotsLevelSummaryTable(java.lang.String)
     */
    public void updateLevelSummaryReport(String personUserIdentifier) {
        budgetConstructionLevelSummaryReportDao.cleanReportsLevelSummaryTable(personUserIdentifier);
        budgetConstructionLevelSummaryReportDao.updateReportsLevelSummaryTable(personUserIdentifier);
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
     * @see org.kuali.module.budget.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionOrgLevelSummaryReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgLevelSummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgLevelSummaryReport orgLevelSummaryReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionLevelSummary> levelSummaryList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionLevelSummary.class, searchCriteria, orderList);


        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List listForCalculateCons = deleteDuplicated((List) levelSummaryList, 1);
        List listForCalculateGexpAndType = deleteDuplicated((List) levelSummaryList, 2);
        List listForCalculateTotal = deleteDuplicated((List) levelSummaryList, 3);


        // Calculate Total Section
        List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalConsList;
        List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalGexpAndTypeList;
        List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalList;

        levelSummaryTotalConsList = calculateConsTotal((List) levelSummaryList, listForCalculateCons);
        levelSummaryTotalGexpAndTypeList = calculateGexpAndTypeTotal((List) levelSummaryList, listForCalculateGexpAndType);
        levelSummaryTotalList = calculateTotal((List) levelSummaryList, listForCalculateTotal);


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
        orgLevelSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgLevelSummaryReportEntry.setOrgChartOfAccountsCode(levelSummary.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgLevelSummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgLevelSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgLevelSummaryReportEntry.setOrganizationCode(levelSummary.getOrganizationCode());
        if (orgName == null) {
            orgLevelSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgLevelSummaryReportEntry.setOrganizationName(orgName);
        }

        orgLevelSummaryReportEntry.setChartOfAccountsCode(levelSummary.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgLevelSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgLevelSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgLevelSummaryReportEntry.setFundGroupCode(levelSummary.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgLevelSummaryReportEntry.setFundGroupName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgLevelSummaryReportEntry.setFundGroupName(fundGroupDes);
        }

        orgLevelSummaryReportEntry.setSubFundGroupCode(levelSummary.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgLevelSummaryReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgLevelSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgLevelSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        orgLevelSummaryReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgLevelSummaryReportEntry.setHeader1("Object Level Name");
        orgLevelSummaryReportEntry.setHeader2a("Lv. FTE");
        orgLevelSummaryReportEntry.setHeader2("FTE");
        orgLevelSummaryReportEntry.setHeader3("Amount");
        orgLevelSummaryReportEntry.setHeader31("FTE");
        orgLevelSummaryReportEntry.setHeader40("FTE");
        orgLevelSummaryReportEntry.setHeader4("Amount");
        orgLevelSummaryReportEntry.setHeader5(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgLevelSummaryReportEntry.setHeader6(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
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
        if (levelSummary.getFinancialConsolidationObjectCode() == null) {
            // TODO Should changed the error message.
            orgLevelSummaryReportEntry.setFinancialObjectLevelName("error to get blah blah");
        }
        else {
            orgLevelSummaryReportEntry.setFinancialObjectLevelName(levelSummary.getFinancialObjectLevel().getFinancialObjectLevelName());
        }
        if (levelSummary.getPositionCsfLeaveFteQuantity() != null && !levelSummary.getPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
            orgLevelSummaryReportEntry.setPositionCsfLeaveFteQuantity(levelSummary.getPositionCsfLeaveFteQuantity().toString());
        }

        if (levelSummary.getCsfFullTimeEmploymentQuantity() != null && !levelSummary.getCsfFullTimeEmploymentQuantity().equals(BigDecimal.ZERO)) {
            orgLevelSummaryReportEntry.setCsfFullTimeEmploymentQuantity(levelSummary.getCsfFullTimeEmploymentQuantity().toString());
        }

        if (levelSummary.getAppointmentRequestedCsfFteQuantity() != null && !levelSummary.getAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
            orgLevelSummaryReportEntry.setAppointmentRequestedCsfFteQuantity(levelSummary.getAppointmentRequestedCsfFteQuantity().toString());
        }

        if (levelSummary.getAppointmentRequestedFteQuantity() != null && !levelSummary.getAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
            orgLevelSummaryReportEntry.setAppointmentRequestedFteQuantity(levelSummary.getAppointmentRequestedFteQuantity().toString());
        }

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

        BigDecimal decimalAmountChange = new BigDecimal(orgLevelSummaryReportEntry.getAmountChange());
        BigDecimal decimalFinancialBeginningBalanceLineAmount = new BigDecimal(orgLevelSummaryReportEntry.getFinancialBeginningBalanceLineAmount().intValue());

        if (!decimalFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
            orgLevelSummaryReportEntry.setPercentChange(decimalAmountChange.divide(decimalFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }

    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionLevelSummary bcas
     * @param List reportTotalList
     */
    public void buildReportsTotal(BudgetConstructionOrgLevelSummaryReport orgLevelSummaryReportEntry, BudgetConstructionLevelSummary levelSummary, List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalConsList, List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalGexpAndTypeList, List<BudgetConstructionOrgLevelSummaryReportTotal> levelSummaryTotalList) {

        for (BudgetConstructionOrgLevelSummaryReportTotal consTotal : levelSummaryTotalConsList) {
            if (isSameLevelSummaryEntry(levelSummary, consTotal.getBcls())) {
                orgLevelSummaryReportEntry.setTotalConsolidationDescription(levelSummary.getFinancialConsolidationObject().getFinConsolidationObjectName());

                // The total part shouldn't have null value, so just checking '0'
                if (!consTotal.getTotalConsolidationPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTotalConsolidationPositionCsfLeaveFteQuantity(consTotal.getTotalConsolidationPositionCsfLeaveFteQuantity().toString());
                }
                if (!consTotal.getTotalConsolidationPositionCsfFullTimeEmploymentQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(consTotal.getTotalConsolidationPositionCsfFullTimeEmploymentQuantity().toString());
                }
                orgLevelSummaryReportEntry.setTotalConsolidationFinancialBeginningBalanceLineAmount(consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount());

                if (!consTotal.getTotalConsolidationAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTotalConsolidationAppointmentRequestedCsfFteQuantity(consTotal.getTotalConsolidationAppointmentRequestedCsfFteQuantity().toString());
                }
                if (!consTotal.getTotalConsolidationAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTotalConsolidationAppointmentRequestedFteQuantity(consTotal.getTotalConsolidationAppointmentRequestedFteQuantity().toString());
                }
                orgLevelSummaryReportEntry.setTotalConsolidationAccountLineAnnualBalanceAmount(consTotal.getTotalConsolidationAccountLineAnnualBalanceAmount());

                orgLevelSummaryReportEntry.setTotalConsolidationAmountChange(consTotal.getTotalConsolidationAmountChange());
                orgLevelSummaryReportEntry.setTotalConsolidationPercentChange(consTotal.getTotalConsolidationPercentChange());

            }
        }

        for (BudgetConstructionOrgLevelSummaryReportTotal gexpAndTypeTotal : levelSummaryTotalGexpAndTypeList) {
            if (isSameLevelSummaryEntryWithoutSortCode(levelSummary, gexpAndTypeTotal.getBcls())) {

                orgLevelSummaryReportEntry.setGrossFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setGrossAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount());
                orgLevelSummaryReportEntry.setGrossAmountChange(gexpAndTypeTotal.getGrossAmountChange());
                orgLevelSummaryReportEntry.setGrossPercentChange(gexpAndTypeTotal.getGrossPercentChange());

                if (levelSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                    orgLevelSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE));
                }
                else {
                    orgLevelSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR));
                }

                if (!gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTypePositionCsfLeaveFteQuantity(gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity().toString());
                }
                if (!gexpAndTypeTotal.getTypePositionCsfFullTimeEmploymentQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTypePositionCsfFullTimeEmploymentQuantity(gexpAndTypeTotal.getTypePositionCsfFullTimeEmploymentQuantity().toString());
                }
                orgLevelSummaryReportEntry.setTypeFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount());

                if (!gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTypeAppointmentRequestedCsfFteQuantity(gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity().toString());
                }
                if (!gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgLevelSummaryReportEntry.setTypeAppointmentRequestedFteQuantity(gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity().toString());
                }

                orgLevelSummaryReportEntry.setTypeAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount());
                orgLevelSummaryReportEntry.setTypeAmountChange(gexpAndTypeTotal.getTypeAmountChange());
                orgLevelSummaryReportEntry.setTypePercentChange(gexpAndTypeTotal.getTypePercentChange());
            }
        }

        for (BudgetConstructionOrgLevelSummaryReportTotal total : levelSummaryTotalList) {
            if (isSameLevelSummaryEntryWithoutSortCodeAndExpenseCode(levelSummary, total.getBcls())) {
                orgLevelSummaryReportEntry.setTotalSubFundGroupDesc(levelSummary.getSubFundGroup().getSubFundGroupDescription());
                orgLevelSummaryReportEntry.setRevenueFinancialBeginningBalanceLineAmount(total.getRevenueFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setRevenueAccountLineAnnualBalanceAmount(total.getRevenueAccountLineAnnualBalanceAmount());
                orgLevelSummaryReportEntry.setExpenditureFinancialBeginningBalanceLineAmount(total.getExpenditureFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setExpenditureAccountLineAnnualBalanceAmount(total.getExpenditureAccountLineAnnualBalanceAmount());

                orgLevelSummaryReportEntry.setRevenueAmountChange(total.getRevenueAmountChange());
                orgLevelSummaryReportEntry.setRevenuePercentChange(total.getRevenuePercentChange());

                orgLevelSummaryReportEntry.setExpenditureAmountChange(total.getExpenditureAmountChange());
                orgLevelSummaryReportEntry.setExpenditureAmountChange(total.getExpenditureAmountChange());

                orgLevelSummaryReportEntry.setDifferenceFinancialBeginningBalanceLineAmount(total.getDifferenceFinancialBeginningBalanceLineAmount());
                orgLevelSummaryReportEntry.setDifferenceAccountLineAnnualBalanceAmount(total.getDifferenceAccountLineAnnualBalanceAmount());

                orgLevelSummaryReportEntry.setDifferenceAmountChange(total.getDifferenceAmountChange());
                orgLevelSummaryReportEntry.setDifferencePercentChange(total.getDifferencePercentChange());
            }
        }


        /*
         * orgLevelSummaryReportEntry.setTotalRevenueAmountChange(orgLevelSummaryReportEntry.getTotalRevenueReqAmount().subtract(orgLevelSummaryReportEntry.getTotalRevenueBaseAmount()));
         * if (!orgLevelSummaryReportEntry.getTotalRevenueBaseAmount().equals(BigDecimal.ZERO)) {
         * orgLevelSummaryReportEntry.setTotalRevenuePercentChange(orgLevelSummaryReportEntry.getTotalRevenueAmountChange().divide(orgLevelSummaryReportEntry.getTotalRevenueBaseAmount(),
         * 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100))); }
         * orgLevelSummaryReportEntry.setTotalGrossAmountChange(orgLevelSummaryReportEntry.getTotalGrossReqAmount().subtract(orgLevelSummaryReportEntry.getTotalGrossBaseAmount()));
         * if (!orgLevelSummaryReportEntry.getTotalGrossBaseAmount().equals(BigDecimal.ZERO)) {
         * orgLevelSummaryReportEntry.setTotalGrossPercentChange(orgLevelSummaryReportEntry.getTotalGrossAmountChange().divide(orgLevelSummaryReportEntry.getTotalGrossBaseAmount(),
         * 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100))); }
         * orgLevelSummaryReportEntry.setTotalTransferAmountChange(orgLevelSummaryReportEntry.getTotalTransferInReqAmount().subtract(orgLevelSummaryReportEntry.getTotalTransferInBaseAmount()));
         * if (!orgLevelSummaryReportEntry.getTotalTransferInBaseAmount().equals(BigDecimal.ZERO)) {
         * orgLevelSummaryReportEntry.setTotalTransferInPercentChange(orgLevelSummaryReportEntry.getTotalTransferAmountChange().divide(orgLevelSummaryReportEntry.getTotalTransferInBaseAmount(),
         * 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100))); }
         * orgLevelSummaryReportEntry.setTotalNetTransferAmountChange(orgLevelSummaryReportEntry.getTotalNetTransferReqAmount().subtract(orgLevelSummaryReportEntry.getTotalNetTransferBaseAmount()));
         * if (!orgLevelSummaryReportEntry.getTotalNetTransferBaseAmount().equals(BigDecimal.ZERO)) {
         * orgLevelSummaryReportEntry.setTotalNetTransferPercentChange(orgLevelSummaryReportEntry.getTotalNetTransferAmountChange().divide(orgLevelSummaryReportEntry.getTotalNetTransferBaseAmount(),
         * 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100))); }
         * orgLevelSummaryReportEntry.setRevExpDifferenceBaseAmount(orgLevelSummaryReportEntry.getTotalRevenueBaseAmount().subtract(orgLevelSummaryReportEntry.getTotalNetTransferBaseAmount()));
         * orgLevelSummaryReportEntry.setRevExpDifferenceReqAmount(orgLevelSummaryReportEntry.getTotalRevenueReqAmount().subtract(orgLevelSummaryReportEntry.getTotalNetTransferReqAmount()));
         * orgLevelSummaryReportEntry.setRevExpDifferenceAmountChange(orgLevelSummaryReportEntry.getRevExpDifferenceReqAmount().subtract(orgLevelSummaryReportEntry.getRevExpDifferenceBaseAmount()));
         * if (!orgLevelSummaryReportEntry.getRevExpDifferenceBaseAmount().equals(BigDecimal.ZERO)) {
         * orgLevelSummaryReportEntry.setRevExpDifferencePercentChange(orgLevelSummaryReportEntry.getRevExpDifferenceAmountChange().divide(orgLevelSummaryReportEntry.getRevExpDifferenceBaseAmount(),
         * 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100))); }
         */
    }


    public List calculateConsTotal(List<BudgetConstructionLevelSummary> bclsList, List<BudgetConstructionLevelSummary> simpleList) {

        BigDecimal totalConsolidationPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalConsolidationFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalConsolidationAccountLineAnnualBalanceAmount = new Integer(0);
        Integer totalConsolidationAmountChange = new Integer(0);
        BigDecimal totalConsolidationPercentChange = BigDecimal.ZERO;


        List returnList = new ArrayList();


        for (BudgetConstructionLevelSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgLevelSummaryReportTotal bcLevelTotal = new BudgetConstructionOrgLevelSummaryReportTotal();
            for (BudgetConstructionLevelSummary bclsListEntry : bclsList) {
                if (isSameLevelSummaryEntry(simpleBclsEntry, bclsListEntry)) {
                    totalConsolidationFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalConsolidationAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalConsolidationPositionCsfLeaveFteQuantity = totalConsolidationPositionCsfLeaveFteQuantity.add(new BigDecimal(bclsListEntry.getPositionCsfLeaveFteQuantity().intValue()));
                    totalConsolidationPositionCsfFullTimeEmploymentQuantity = totalConsolidationPositionCsfFullTimeEmploymentQuantity.add(new BigDecimal(bclsListEntry.getCsfFullTimeEmploymentQuantity().intValue()));
                    totalConsolidationAppointmentRequestedCsfFteQuantity = totalConsolidationAppointmentRequestedCsfFteQuantity.add(new BigDecimal(bclsListEntry.getAppointmentRequestedCsfFteQuantity().intValue()));
                    totalConsolidationAppointmentRequestedFteQuantity = totalConsolidationAppointmentRequestedFteQuantity.add(new BigDecimal(bclsListEntry.getAppointmentRequestedFteQuantity().intValue()));
                }
            }
            bcLevelTotal.setBcls(simpleBclsEntry);
            bcLevelTotal.setTotalConsolidationPositionCsfLeaveFteQuantity(totalConsolidationPositionCsfLeaveFteQuantity);
            bcLevelTotal.setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(totalConsolidationPositionCsfFullTimeEmploymentQuantity);
            bcLevelTotal.setTotalConsolidationFinancialBeginningBalanceLineAmount(totalConsolidationFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setTotalConsolidationAppointmentRequestedCsfFteQuantity(totalConsolidationAppointmentRequestedCsfFteQuantity);
            bcLevelTotal.setTotalConsolidationAppointmentRequestedFteQuantity(totalConsolidationAppointmentRequestedFteQuantity);
            bcLevelTotal.setTotalConsolidationAccountLineAnnualBalanceAmount(totalConsolidationAccountLineAnnualBalanceAmount);
            totalConsolidationAmountChange = totalConsolidationAccountLineAnnualBalanceAmount - totalConsolidationFinancialBeginningBalanceLineAmount;

            bcLevelTotal.setTotalConsolidationAmountChange(totalConsolidationAmountChange);
            BigDecimal decimalTotalConsolidationAmountChange = new BigDecimal(totalConsolidationAmountChange.intValue());
            BigDecimal decimalTotalConsolidationFinancialBeginningBalanceLineAmount = new BigDecimal(totalConsolidationFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalTotalConsolidationFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                totalConsolidationPercentChange = decimalTotalConsolidationAmountChange.divide(decimalTotalConsolidationFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcLevelTotal.setTotalConsolidationPercentChange(totalConsolidationPercentChange);
            returnList.add(bcLevelTotal);

            totalConsolidationPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalConsolidationPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalConsolidationFinancialBeginningBalanceLineAmount = new Integer(0);
            totalConsolidationAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalConsolidationAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalConsolidationAccountLineAnnualBalanceAmount = new Integer(0);
            totalConsolidationAmountChange = new Integer(0);
            totalConsolidationPercentChange = BigDecimal.ZERO;

        }

        /*
         * for (BudgetConstructionLevelSummary returnList : simpleList) { BudgetConstructionOrgLevelSummaryReportTotal bcLevelTotal =
         * new BudgetConstructionOrgLevelSummaryReportTotal(); for (BudgetConstructionLevelSummary bclsListEntry : bclsList) { if
         * (isSameLevelSummaryEntryWithoutSortCode(returnList, bclsListEntry)) { grossFinancialBeginningBalanceLineAmount }
         */

        return returnList;
    }


    public List calculateGexpAndTypeTotal(List<BudgetConstructionLevelSummary> bclsList, List<BudgetConstructionLevelSummary> simpleList) {

        Integer grossFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer grossAccountLineAnnualBalanceAmount = new Integer(0);
        Integer grossAmountChange = new Integer(0);
        BigDecimal grossPercentChange = BigDecimal.ZERO;

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
                if (isSameLevelSummaryEntryWithoutSortCode(simpleBclsEntry, bclsListEntry)) {

                    typeFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    typeAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity.add(new BigDecimal(bclsListEntry.getPositionCsfLeaveFteQuantity().intValue()));
                    typePositionCsfFullTimeEmploymentQuantity = typePositionCsfFullTimeEmploymentQuantity.add(new BigDecimal(bclsListEntry.getCsfFullTimeEmploymentQuantity().intValue()));
                    typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity.add(new BigDecimal(bclsListEntry.getAppointmentRequestedCsfFteQuantity().intValue()));
                    typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity.add(new BigDecimal(bclsListEntry.getAppointmentRequestedFteQuantity().intValue()));

                    if (bclsListEntry.getIncomeExpenseCode().equals("B") && !bclsListEntry.getFinancialObjectLevelCode().equals("CORI") && !bclsListEntry.getFinancialObjectLevelCode().equals("TRIN")) {
                        grossFinancialBeginningBalanceLineAmount += new Integer(bclsListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        grossAccountLineAnnualBalanceAmount += new Integer(bclsListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }
            bcLevelTotal.setBcls(simpleBclsEntry);

            bcLevelTotal.setGrossFinancialBeginningBalanceLineAmount(grossFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setGrossAccountLineAnnualBalanceAmount(grossAccountLineAnnualBalanceAmount);
            grossAmountChange = grossAccountLineAnnualBalanceAmount - grossFinancialBeginningBalanceLineAmount;
            bcLevelTotal.setGrossAmountChange(grossAmountChange);

            BigDecimal decimalGrossAmountChange = new BigDecimal(grossAmountChange.intValue());
            BigDecimal decimalGrossFinancialBeginningBalanceLineAmount = new BigDecimal(grossFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalGrossFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                grossPercentChange = decimalGrossAmountChange.divide(decimalGrossFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcLevelTotal.setGrossPercentChange(grossPercentChange);
            bcLevelTotal.setTypePositionCsfLeaveFteQuantity(typePositionCsfLeaveFteQuantity);
            bcLevelTotal.setTypePositionCsfFullTimeEmploymentQuantity(typePositionCsfFullTimeEmploymentQuantity);
            bcLevelTotal.setTypeFinancialBeginningBalanceLineAmount(typeFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setTypeAppointmentRequestedCsfFteQuantity(typeAppointmentRequestedCsfFteQuantity);
            bcLevelTotal.setTypeAppointmentRequestedFteQuantity(typeAppointmentRequestedFteQuantity);
            bcLevelTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);

            typeAmountChange = typeAccountLineAnnualBalanceAmount - typeFinancialBeginningBalanceLineAmount;
            bcLevelTotal.setTypeAmountChange(typeAmountChange);
            BigDecimal decimalTypeAmountChange = new BigDecimal(typeAmountChange.intValue());
            BigDecimal decimalTypeFinancialBeginningBalanceLineAmount = new BigDecimal(typeFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalTypeFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                typePercentChange = decimalTypeAmountChange.divide(decimalTypeFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcLevelTotal.setTypePercentChange(typePercentChange);
            
            returnList.add(bcLevelTotal);
            grossFinancialBeginningBalanceLineAmount = new Integer(0);
            grossAccountLineAnnualBalanceAmount = new Integer(0);
            grossAmountChange = new Integer(0);
            grossPercentChange = BigDecimal.ZERO;

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
        Integer revenueAmountChange = new Integer(0);
        BigDecimal revenuePercentChange = BigDecimal.ZERO;

        Integer expenditureFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer expenditureAccountLineAnnualBalanceAmount = new Integer(0);
        Integer expenditureAmountChange = new Integer(0);
        BigDecimal expenditurePercentChange = BigDecimal.ZERO;

        Integer differenceFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer differenceAccountLineAnnualBalanceAmount = new Integer(0);
        Integer differenceAmountChange = new Integer(0);
        BigDecimal differencePercentChange = BigDecimal.ZERO;

        List returnList = new ArrayList();

        for (BudgetConstructionLevelSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgLevelSummaryReportTotal bcLevelTotal = new BudgetConstructionOrgLevelSummaryReportTotal();
            for (BudgetConstructionLevelSummary bclsListEntry : bclsList) {
                if (isSameLevelSummaryEntryWithoutSortCodeAndExpenseCode(simpleBclsEntry, bclsListEntry)) {

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

            revenueAmountChange = revenueAccountLineAnnualBalanceAmount - revenueFinancialBeginningBalanceLineAmount;
            bcLevelTotal.setRevenueAmountChange(revenueAmountChange);
            BigDecimal decimalRevenueAmountChange = new BigDecimal(revenueAmountChange.intValue());
            BigDecimal decimalRevenueFinancialBeginningBalanceLineAmount = new BigDecimal(revenueFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalRevenueFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                revenuePercentChange = decimalRevenueAmountChange.divide(decimalRevenueFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcLevelTotal.setRevenuePercentChange(revenuePercentChange);
            bcLevelTotal.setExpenditureFinancialBeginningBalanceLineAmount(expenditureFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setExpenditureAccountLineAnnualBalanceAmount(expenditureAccountLineAnnualBalanceAmount);

            expenditureAmountChange = expenditureAccountLineAnnualBalanceAmount - expenditureFinancialBeginningBalanceLineAmount;
            bcLevelTotal.setExpenditureAmountChange(expenditureAmountChange);
            BigDecimal decimalExpenditureAmountChange = new BigDecimal(expenditureAmountChange.intValue());
            BigDecimal decimalExpenditureFinancialBeginningBalanceLineAmount = new BigDecimal(expenditureFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalExpenditureFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                expenditurePercentChange = decimalExpenditureAmountChange.divide(decimalExpenditureFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcLevelTotal.setExpenditurePercentChange(expenditurePercentChange);
            
            differenceFinancialBeginningBalanceLineAmount = revenueFinancialBeginningBalanceLineAmount - expenditureFinancialBeginningBalanceLineAmount;
            differenceAccountLineAnnualBalanceAmount = revenueAccountLineAnnualBalanceAmount - expenditureAccountLineAnnualBalanceAmount;
            bcLevelTotal.setDifferenceFinancialBeginningBalanceLineAmount(differenceFinancialBeginningBalanceLineAmount);
            bcLevelTotal.setDifferenceAccountLineAnnualBalanceAmount(differenceAccountLineAnnualBalanceAmount);

            differenceAmountChange = differenceAccountLineAnnualBalanceAmount - differenceFinancialBeginningBalanceLineAmount;
            bcLevelTotal.setDifferenceAmountChange(differenceAmountChange);
            BigDecimal decimalDifferenceAmountChange = new BigDecimal(differenceAmountChange.intValue());
            BigDecimal decimalDifferenceFinancialBeginningBalanceLineAmount = new BigDecimal(differenceFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalDifferenceFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                differencePercentChange = decimalDifferenceAmountChange.divide(decimalDifferenceFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcLevelTotal.setDifferencePercentChange(differencePercentChange);
            
            returnList.add(bcLevelTotal);

            revenueFinancialBeginningBalanceLineAmount = new Integer(0);
            revenueAccountLineAnnualBalanceAmount = new Integer(0);
            revenueAmountChange = new Integer(0);
            revenuePercentChange = BigDecimal.ZERO;

            expenditureFinancialBeginningBalanceLineAmount = new Integer(0);
            expenditureAccountLineAnnualBalanceAmount = new Integer(0);
            expenditureAmountChange = new Integer(0);
            expenditurePercentChange = BigDecimal.ZERO;

            differenceFinancialBeginningBalanceLineAmount = new Integer(0);
            differenceAccountLineAnnualBalanceAmount = new Integer(0);
            differenceAmountChange = new Integer(0);
            differencePercentChange = BigDecimal.ZERO;


        }


        return returnList;
    }


    public boolean isSameLevelSummaryEntry(BudgetConstructionLevelSummary firstBcls, BudgetConstructionLevelSummary secondBcls) {
        if (isSameLevelSummaryEntryWithoutSortCode(firstBcls, secondBcls) && firstBcls.getFinancialConsolidationSortCode().equals(secondBcls.getFinancialConsolidationSortCode())) {
            return true;
        }
        else
            return false;
    }


    public boolean isSameLevelSummaryEntryWithoutSortCode(BudgetConstructionLevelSummary firstBcls, BudgetConstructionLevelSummary secondBcls) {
        if (isSameLevelSummaryEntryWithoutSortCodeAndExpenseCode(firstBcls, secondBcls) && firstBcls.getIncomeExpenseCode().equals(secondBcls.getIncomeExpenseCode())) {
            return true;
        }
        else
            return false;
    }


    public boolean isSameLevelSummaryEntryWithoutSortCodeAndExpenseCode(BudgetConstructionLevelSummary firstBcls, BudgetConstructionLevelSummary secondBcls) {
        if (firstBcls.getOrganizationChartOfAccountsCode().equals(secondBcls.getOrganizationChartOfAccountsCode()) && firstBcls.getOrganizationCode().equals(secondBcls.getOrganizationCode()) && firstBcls.getSubFundGroupCode().equals(secondBcls.getSubFundGroupCode()) && firstBcls.getChartOfAccountsCode().equals(secondBcls.getChartOfAccountsCode())) {
            return true;
        }

        else
            return false;
    }

    /**
     * Deletes duplicated entry from list
     * 
     * @param List list
     * @return a list that all duplicated entries were deleted
     */
    public List deleteDuplicated(List list, int mode) {

        // mode 1 is for getting a list of cons
        // mode 2 is for getting a list of gexp and type
        // mode 3 is for getting a list of total

        int count = 0;
        BudgetConstructionLevelSummary levelSummaryEntry = null;
        BudgetConstructionLevelSummary levelSummaryEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            levelSummaryEntry = (BudgetConstructionLevelSummary) list.get(count);
            levelSummaryEntryAux = (BudgetConstructionLevelSummary) list.get(count);
            returnList.add(levelSummaryEntry);
            count++;
            while (count < list.size()) {
                levelSummaryEntry = (BudgetConstructionLevelSummary) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSameLevelSummaryEntry(levelSummaryEntry, levelSummaryEntryAux)) {
                            returnList.add(levelSummaryEntry);
                            levelSummaryEntryAux = levelSummaryEntry;
                        }
                    }
                    case 2: {
                        if (!isSameLevelSummaryEntryWithoutSortCode(levelSummaryEntry, levelSummaryEntryAux)) {
                            returnList.add(levelSummaryEntry);
                            levelSummaryEntryAux = levelSummaryEntry;
                        }
                    }
                    case 3: {
                        if (!isSameLevelSummaryEntryWithoutSortCodeAndExpenseCode(levelSummaryEntry, levelSummaryEntryAux)) {
                            returnList.add(levelSummaryEntry);
                            levelSummaryEntryAux = levelSummaryEntry;
                        }
                    }
                }
                count++;
            }
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

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
