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
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionObjectSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgObjectSummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgObjectSummaryReportTotal;
import org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionObjectSummaryReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.chart.bo.ObjectCode;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionObjectSummaryReportServiceImpl implements BudgetConstructionObjectSummaryReportService {

    private BudgetConstructionObjectSummaryReportDao budgetConstructionObjectSummaryReportDao;
    private BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateRepotsLevelSummaryTable(java.lang.String)
     */
    public void updateObjectSummaryReport(String personUserIdentifier) {
        budgetConstructionObjectSummaryReportDao.cleanGeneralLedgerObjectSummaryTable(personUserIdentifier);
        budgetConstructionObjectSummaryReportDao.updateGeneralLedgerObjectSummaryTable(personUserIdentifier);
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
     * @see org.kuali.module.budget.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionOrgObjectSummaryReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgObjectSummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry;
        // build searchCriteria
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionObjectSummary> objectSummaryList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionObjectSummary.class, searchCriteria, orderList);


        // 
        List listForCalculateLevel = deleteDuplicated((List) objectSummaryList, 1);
        List listForCalculateCons = deleteDuplicated((List) objectSummaryList, 2);
        List listForCalculateGexpAndType = deleteDuplicated((List) objectSummaryList, 3);
        List listForCalculateTotal = deleteDuplicated((List) objectSummaryList, 4);


        // Calculate Total Section
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalLevelList;
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalConsList;
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalGexpAndTypeList;
        List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalList;
        
        
        objectSummaryTotalLevelList = calculateLevelTotal((List) objectSummaryList, listForCalculateLevel);
        objectSummaryTotalConsList = calculateConsTotal((List) objectSummaryList, listForCalculateCons);
        objectSummaryTotalGexpAndTypeList = calculateGexpAndTypeTotal((List) objectSummaryList, listForCalculateGexpAndType);
        objectSummaryTotalList = calculateTotal((List) objectSummaryList, listForCalculateTotal);


        for (BudgetConstructionObjectSummary objectSummaryEntry : objectSummaryList) {
            orgObjectSummaryReportEntry = new BudgetConstructionOrgObjectSummaryReport();
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
    private void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry, BudgetConstructionObjectSummary objectSummary) {
        String orgChartDesc = objectSummary.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = objectSummary.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = objectSummary.getOrganization().getOrganizationName();
        String reportChartDesc = objectSummary.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = objectSummary.getSubFundGroup().getSubFundGroupCode();
        String subFundGroupDes = objectSummary.getSubFundGroup().getSubFundGroupDescription();
        String fundGroupName = objectSummary.getSubFundGroup().getFundGroupCode();
        String fundGroupDes = objectSummary.getSubFundGroup().getFundGroup().getName();

        Integer prevFiscalyear = universityFiscalYear - 1;
        orgObjectSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgObjectSummaryReportEntry.setOrgChartOfAccountsCode(objectSummary.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgObjectSummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgObjectSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgObjectSummaryReportEntry.setOrganizationCode(objectSummary.getOrganizationCode());
        if (orgName == null) {
            orgObjectSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgObjectSummaryReportEntry.setOrganizationName(orgName);
        }

        orgObjectSummaryReportEntry.setChartOfAccountsCode(objectSummary.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgObjectSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgObjectSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgObjectSummaryReportEntry.setFundGroupCode(objectSummary.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgObjectSummaryReportEntry.setFundGroupName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgObjectSummaryReportEntry.setFundGroupName(fundGroupDes);
        }

        orgObjectSummaryReportEntry.setSubFundGroupCode(objectSummary.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgObjectSummaryReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgObjectSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgObjectSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        orgObjectSummaryReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgObjectSummaryReportEntry.setHeader1("Object Level Name");
        orgObjectSummaryReportEntry.setHeader2a("Lv. FTE");
        orgObjectSummaryReportEntry.setHeader2("FTE");
        orgObjectSummaryReportEntry.setHeader3("Amount");
        orgObjectSummaryReportEntry.setHeader31("FTE");
        orgObjectSummaryReportEntry.setHeader40("FTE");
        orgObjectSummaryReportEntry.setHeader4("Amount");
        orgObjectSummaryReportEntry.setHeader5(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgObjectSummaryReportEntry.setHeader6(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
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
    private void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry, BudgetConstructionObjectSummary objectSummary) {
        
        //To get ObjectName: There is no universityFiscalyear field in BudgetConstructionObjectSummary, 
        // so we can get ObjectName by getting ObjectCode with Primary key.
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectSummary.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectSummary.getFinancialObjectCode());
        
        ObjectCode objectCode = (ObjectCode) businessObjectService.findByPrimaryKey(ObjectCode.class, searchCriteria);
        String objectName = null; 
        
        if (objectCode == null) {
            // TODO Should changed the error message.
            orgObjectSummaryReportEntry.setFinancialObjectName("error to get blah blah");
        } else { 
            objectName = objectCode.getFinancialObjectCodeName();
            if (objectName == null) {
                //TODO Should changed the error message.
                orgObjectSummaryReportEntry.setFinancialObjectName("error to get blah blah");
            } else {
                orgObjectSummaryReportEntry.setFinancialObjectName(objectName);
            }
        }
        if (objectSummary.getPositionCsfLeaveFteQuantity() != null && !objectSummary.getPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
            orgObjectSummaryReportEntry.setPositionCsfLeaveFteQuantity(objectSummary.getPositionCsfLeaveFteQuantity().setScale(2).toString());
        }

        if (objectSummary.getCsfFullTimeEmploymentQuantity() != null && !objectSummary.getCsfFullTimeEmploymentQuantity().equals(BigDecimal.ZERO)) {
            orgObjectSummaryReportEntry.setCsfFullTimeEmploymentQuantity(objectSummary.getCsfFullTimeEmploymentQuantity().setScale(2).toString());
        }

        if (objectSummary.getAppointmentRequestedCsfFteQuantity() != null && !objectSummary.getAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
            orgObjectSummaryReportEntry.setAppointmentRequestedCsfFteQuantity(objectSummary.getAppointmentRequestedCsfFteQuantity().setScale(2).toString());
        }

        if (objectSummary.getAppointmentRequestedFteQuantity() != null && !objectSummary.getAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
            orgObjectSummaryReportEntry.setAppointmentRequestedFteQuantity(objectSummary.getAppointmentRequestedFteQuantity().setScale(2).toString());
        }

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

        BigDecimal decimalAmountChange = new BigDecimal(orgObjectSummaryReportEntry.getAmountChange());
        BigDecimal decimalFinancialBeginningBalanceLineAmount = new BigDecimal(orgObjectSummaryReportEntry.getFinancialBeginningBalanceLineAmount().intValue());

        if (!decimalFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
            orgObjectSummaryReportEntry.setPercentChange(decimalAmountChange.divide(decimalFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }

    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionObjectSummary bcas
     * @param List reportTotalList
     */
    private void buildReportsTotal(BudgetConstructionOrgObjectSummaryReport orgObjectSummaryReportEntry, BudgetConstructionObjectSummary objectSummary, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalLevelList, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalConsList, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalGexpAndTypeList, List<BudgetConstructionOrgObjectSummaryReportTotal> objectSummaryTotalList) {

        for (BudgetConstructionOrgObjectSummaryReportTotal levelTotal : objectSummaryTotalLevelList) {
            if (isSameObjectSummaryEntryForLevel(objectSummary, levelTotal.getBcos())) {
                orgObjectSummaryReportEntry.setTotalLevelDescription(objectSummary.getFinancialObjectLevel().getFinancialObjectLevelName());

                // The total part shouldn't have null value, so just checking '0'
                if (!levelTotal.getTotalLevelPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelPositionCsfLeaveFteQuantity(levelTotal.getTotalLevelPositionCsfLeaveFteQuantity().setScale(2).toString());
                }
                if (!levelTotal.getTotalLevelPositionCsfFullTimeEmploymentQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelPositionCsfFullTimeEmploymentQuantity(levelTotal.getTotalLevelPositionCsfFullTimeEmploymentQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalLevelFinancialBeginningBalanceLineAmount(levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount());

                if (!levelTotal.getTotalLevelAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedCsfFteQuantity(levelTotal.getTotalLevelAppointmentRequestedCsfFteQuantity().setScale(2).toString());
                }
                if (!levelTotal.getTotalLevelAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedFteQuantity(levelTotal.getTotalLevelAppointmentRequestedFteQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalLevelAccountLineAnnualBalanceAmount(levelTotal.getTotalLevelAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setTotalLevelAmountChange(levelTotal.getTotalLevelAmountChange());
                orgObjectSummaryReportEntry.setTotalLevelPercentChange(levelTotal.getTotalLevelPercentChange());

            }
        }
        
        for (BudgetConstructionOrgObjectSummaryReportTotal consTotal : objectSummaryTotalConsList) {
            if (isSameObjectSummaryEntryForCons(objectSummary, consTotal.getBcos())) {
                orgObjectSummaryReportEntry.setTotalConsolidationDescription(objectSummary.getFinancialConsolidationObject().getFinConsolidationObjectName());

                // The total part shouldn't have null value, so just checking '0'
                if (!consTotal.getTotalConsolidationPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalConsolidationPositionCsfLeaveFteQuantity(consTotal.getTotalConsolidationPositionCsfLeaveFteQuantity().setScale(2).toString());
                }
                if (!consTotal.getTotalConsolidationPositionCsfFullTimeEmploymentQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalConsolidationPositionCsfFullTimeEmploymentQuantity(consTotal.getTotalConsolidationPositionCsfFullTimeEmploymentQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalConsolidationFinancialBeginningBalanceLineAmount(consTotal.getTotalConsolidationFinancialBeginningBalanceLineAmount());

                if (!consTotal.getTotalConsolidationAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalConsolidationAppointmentRequestedCsfFteQuantity(consTotal.getTotalConsolidationAppointmentRequestedCsfFteQuantity().setScale(2).toString());
                }
                if (!consTotal.getTotalConsolidationAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalConsolidationAppointmentRequestedFteQuantity(consTotal.getTotalConsolidationAppointmentRequestedFteQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalConsolidationAccountLineAnnualBalanceAmount(consTotal.getTotalConsolidationAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setTotalConsolidationAmountChange(consTotal.getTotalConsolidationAmountChange());
                orgObjectSummaryReportEntry.setTotalConsolidationPercentChange(consTotal.getTotalConsolidationPercentChange());

            }
        }

        for (BudgetConstructionOrgObjectSummaryReportTotal gexpAndTypeTotal : objectSummaryTotalGexpAndTypeList) {
            if (isSameObjectSummaryEntryForGexpAndType(objectSummary, gexpAndTypeTotal.getBcos())) {

                orgObjectSummaryReportEntry.setGrossFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setGrossAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setGrossAmountChange(gexpAndTypeTotal.getGrossAmountChange());
                orgObjectSummaryReportEntry.setGrossPercentChange(gexpAndTypeTotal.getGrossPercentChange());

                if (objectSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE));
                }
                else {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR));
                }

                if (!gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypePositionCsfLeaveFteQuantity(gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity().setScale(2).toString());
                }
                if (!gexpAndTypeTotal.getTypePositionCsfFullTimeEmploymentQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypePositionCsfFullTimeEmploymentQuantity(gexpAndTypeTotal.getTypePositionCsfFullTimeEmploymentQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTypeFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount());

                if (!gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypeAppointmentRequestedCsfFteQuantity(gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity().setScale(2).toString());
                }
                if (!gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypeAppointmentRequestedFteQuantity(gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity().setScale(2).toString());
                }

                orgObjectSummaryReportEntry.setTypeAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setTypeAmountChange(gexpAndTypeTotal.getTypeAmountChange());
                orgObjectSummaryReportEntry.setTypePercentChange(gexpAndTypeTotal.getTypePercentChange());
            }
        }

        for (BudgetConstructionOrgObjectSummaryReportTotal total : objectSummaryTotalList) {
            if (isSameObjectSummaryEntryForTotal(objectSummary, total.getBcos())) {
                orgObjectSummaryReportEntry.setTotalSubFundGroupDesc(objectSummary.getSubFundGroup().getSubFundGroupDescription());
                orgObjectSummaryReportEntry.setRevenueFinancialBeginningBalanceLineAmount(total.getRevenueFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setRevenueAccountLineAnnualBalanceAmount(total.getRevenueAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setExpenditureFinancialBeginningBalanceLineAmount(total.getExpenditureFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setExpenditureAccountLineAnnualBalanceAmount(total.getExpenditureAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setRevenueAmountChange(total.getRevenueAmountChange());
                orgObjectSummaryReportEntry.setRevenuePercentChange(total.getRevenuePercentChange());

                orgObjectSummaryReportEntry.setExpenditureAmountChange(total.getExpenditureAmountChange());
                orgObjectSummaryReportEntry.setExpenditureAmountChange(total.getExpenditureAmountChange());

                orgObjectSummaryReportEntry.setDifferenceFinancialBeginningBalanceLineAmount(total.getDifferenceFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setDifferenceAccountLineAnnualBalanceAmount(total.getDifferenceAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setDifferenceAmountChange(total.getDifferenceAmountChange());
                orgObjectSummaryReportEntry.setDifferencePercentChange(total.getDifferencePercentChange());
            }
        }


     
    }
    private List calculateLevelTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

        BigDecimal totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
        Integer totalLevelAmountChange = new Integer(0);
        BigDecimal totalLevelPercentChange = BigDecimal.ZERO;
        
        List returnList = new ArrayList();
        
        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (isSameObjectSummaryEntryForLevel(simpleBcosEntry, bcosListEntry)) {
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
            totalLevelAmountChange = totalLevelAccountLineAnnualBalanceAmount - totalLevelFinancialBeginningBalanceLineAmount;

            bcObjectTotal.setTotalLevelAmountChange(totalLevelAmountChange);
            BigDecimal decimalTotalLevelAmountChange = new BigDecimal(totalLevelAmountChange.intValue());
            BigDecimal decimalTotalLevelFinancialBeginningBalanceLineAmount = new BigDecimal(totalLevelFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalTotalLevelFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                totalLevelPercentChange = decimalTotalLevelAmountChange.divide(decimalTotalLevelFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcObjectTotal.setTotalLevelPercentChange(totalLevelPercentChange);
            returnList.add(bcObjectTotal);

            totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalLevelPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
            totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
            totalLevelAmountChange = new Integer(0);
            totalLevelPercentChange = BigDecimal.ZERO;

        }
        return returnList;
        
    }

    private List calculateConsTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

        BigDecimal totalConsolidationPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationPositionCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalConsolidationFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalConsolidationAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalConsolidationAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalConsolidationAccountLineAnnualBalanceAmount = new Integer(0);
        Integer totalConsolidationAmountChange = new Integer(0);
        BigDecimal totalConsolidationPercentChange = BigDecimal.ZERO;


        List returnList = new ArrayList();


        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (isSameObjectSummaryEntryForCons(simpleBcosEntry, bcosListEntry)) {
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
            totalConsolidationAmountChange = totalConsolidationAccountLineAnnualBalanceAmount - totalConsolidationFinancialBeginningBalanceLineAmount;

            bcObjectTotal.setTotalConsolidationAmountChange(totalConsolidationAmountChange);
            BigDecimal decimalTotalConsolidationAmountChange = new BigDecimal(totalConsolidationAmountChange.intValue());
            BigDecimal decimalTotalConsolidationFinancialBeginningBalanceLineAmount = new BigDecimal(totalConsolidationFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalTotalConsolidationFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                totalConsolidationPercentChange = decimalTotalConsolidationAmountChange.divide(decimalTotalConsolidationFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcObjectTotal.setTotalConsolidationPercentChange(totalConsolidationPercentChange);
            returnList.add(bcObjectTotal);

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


    private List calculateGexpAndTypeTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

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
        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (isSameObjectSummaryEntryForGexpAndType(simpleBcosEntry, bcosListEntry)) {

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
            grossAmountChange = grossAccountLineAnnualBalanceAmount - grossFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setGrossAmountChange(grossAmountChange);

            BigDecimal decimalGrossAmountChange = new BigDecimal(grossAmountChange.intValue());
            BigDecimal decimalGrossFinancialBeginningBalanceLineAmount = new BigDecimal(grossFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalGrossFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                grossPercentChange = decimalGrossAmountChange.divide(decimalGrossFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcObjectTotal.setGrossPercentChange(grossPercentChange);
            bcObjectTotal.setTypePositionCsfLeaveFteQuantity(typePositionCsfLeaveFteQuantity);
            bcObjectTotal.setTypePositionCsfFullTimeEmploymentQuantity(typePositionCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTypeFinancialBeginningBalanceLineAmount(typeFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTypeAppointmentRequestedCsfFteQuantity(typeAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTypeAppointmentRequestedFteQuantity(typeAppointmentRequestedFteQuantity);
            bcObjectTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);

            typeAmountChange = typeAccountLineAnnualBalanceAmount - typeFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setTypeAmountChange(typeAmountChange);
            BigDecimal decimalTypeAmountChange = new BigDecimal(typeAmountChange.intValue());
            BigDecimal decimalTypeFinancialBeginningBalanceLineAmount = new BigDecimal(typeFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalTypeFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                typePercentChange = decimalTypeAmountChange.divide(decimalTypeFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcObjectTotal.setTypePercentChange(typePercentChange);
            
            returnList.add(bcObjectTotal);
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


    private List calculateTotal(List<BudgetConstructionObjectSummary> bcosList, List<BudgetConstructionObjectSummary> simpleList) {

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

        for (BudgetConstructionObjectSummary simpleBcosEntry : simpleList) {
            BudgetConstructionOrgObjectSummaryReportTotal bcObjectTotal = new BudgetConstructionOrgObjectSummaryReportTotal();
            for (BudgetConstructionObjectSummary bcosListEntry : bcosList) {
                if (isSameObjectSummaryEntryForTotal(simpleBcosEntry, bcosListEntry)) {

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

            revenueAmountChange = revenueAccountLineAnnualBalanceAmount - revenueFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setRevenueAmountChange(revenueAmountChange);
            BigDecimal decimalRevenueAmountChange = new BigDecimal(revenueAmountChange.intValue());
            BigDecimal decimalRevenueFinancialBeginningBalanceLineAmount = new BigDecimal(revenueFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalRevenueFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                revenuePercentChange = decimalRevenueAmountChange.divide(decimalRevenueFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcObjectTotal.setRevenuePercentChange(revenuePercentChange);
            bcObjectTotal.setExpenditureFinancialBeginningBalanceLineAmount(expenditureFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setExpenditureAccountLineAnnualBalanceAmount(expenditureAccountLineAnnualBalanceAmount);

            expenditureAmountChange = expenditureAccountLineAnnualBalanceAmount - expenditureFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setExpenditureAmountChange(expenditureAmountChange);
            BigDecimal decimalExpenditureAmountChange = new BigDecimal(expenditureAmountChange.intValue());
            BigDecimal decimalExpenditureFinancialBeginningBalanceLineAmount = new BigDecimal(expenditureFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalExpenditureFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                expenditurePercentChange = decimalExpenditureAmountChange.divide(decimalExpenditureFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcObjectTotal.setExpenditurePercentChange(expenditurePercentChange);
            
            differenceFinancialBeginningBalanceLineAmount = revenueFinancialBeginningBalanceLineAmount - expenditureFinancialBeginningBalanceLineAmount;
            differenceAccountLineAnnualBalanceAmount = revenueAccountLineAnnualBalanceAmount - expenditureAccountLineAnnualBalanceAmount;
            bcObjectTotal.setDifferenceFinancialBeginningBalanceLineAmount(differenceFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setDifferenceAccountLineAnnualBalanceAmount(differenceAccountLineAnnualBalanceAmount);

            differenceAmountChange = differenceAccountLineAnnualBalanceAmount - differenceFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setDifferenceAmountChange(differenceAmountChange);
            BigDecimal decimalDifferenceAmountChange = new BigDecimal(differenceAmountChange.intValue());
            BigDecimal decimalDifferenceFinancialBeginningBalanceLineAmount = new BigDecimal(differenceFinancialBeginningBalanceLineAmount.intValue());

            if (!decimalDifferenceFinancialBeginningBalanceLineAmount.equals(BigDecimal.ZERO)) {
                differencePercentChange = decimalDifferenceAmountChange.divide(decimalDifferenceFinancialBeginningBalanceLineAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            bcObjectTotal.setDifferencePercentChange(differencePercentChange);
            
            returnList.add(bcObjectTotal);

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


    private boolean isSameObjectSummaryEntryForLevel(BudgetConstructionObjectSummary firstBcos, BudgetConstructionObjectSummary secondBcos) {
        if (isSameObjectSummaryEntryForCons(firstBcos, secondBcos) && firstBcos.getFinancialLevelSortCode().equals(secondBcos.getFinancialLevelSortCode())) {
            return true;
        }
        else
            return false;
    }


    private boolean isSameObjectSummaryEntryForCons(BudgetConstructionObjectSummary firstBcos, BudgetConstructionObjectSummary secondBcos) {
        if (isSameObjectSummaryEntryForGexpAndType(firstBcos, secondBcos) && firstBcos.getFinancialConsolidationSortCode().equals(secondBcos.getFinancialConsolidationSortCode())) {
            return true;
        }
        else
            return false;
    }


    private boolean isSameObjectSummaryEntryForGexpAndType(BudgetConstructionObjectSummary firstBcos, BudgetConstructionObjectSummary secondBcos) {
        if (isSameObjectSummaryEntryForTotal(firstBcos, secondBcos) && firstBcos.getIncomeExpenseCode().equals(secondBcos.getIncomeExpenseCode())) {
            return true;
        }

        else
            return false;
    }
    
    private boolean isSameObjectSummaryEntryForTotal(BudgetConstructionObjectSummary firstBcos, BudgetConstructionObjectSummary secondBcos) {
        if (firstBcos.getOrganizationChartOfAccountsCode().equals(secondBcos.getOrganizationChartOfAccountsCode()) && firstBcos.getOrganizationCode().equals(secondBcos.getOrganizationCode()) && firstBcos.getSubFundGroupCode().equals(secondBcos.getSubFundGroupCode()) && firstBcos.getChartOfAccountsCode().equals(secondBcos.getChartOfAccountsCode())) {
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
    private List deleteDuplicated(List list, int mode) {

        // mode 1 is for getting a list of level
        // mode 2 is for getting a list of cons
        // mode 3 is for getting a list of gexp and type
        // mode 4 is for getting a list of total

        int count = 0;
        BudgetConstructionObjectSummary objectSummaryEntry = null;
        BudgetConstructionObjectSummary objectSummaryEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            objectSummaryEntry = (BudgetConstructionObjectSummary) list.get(count);
            objectSummaryEntryAux = (BudgetConstructionObjectSummary) list.get(count);
            returnList.add(objectSummaryEntry);
            count++;
            while (count < list.size()) {
                objectSummaryEntry = (BudgetConstructionObjectSummary) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSameObjectSummaryEntryForCons(objectSummaryEntry, objectSummaryEntryAux)) {
                            returnList.add(objectSummaryEntry);
                            objectSummaryEntryAux = objectSummaryEntry;
                        }
                    }
                    case 2: {
                        if (!isSameObjectSummaryEntryForLevel(objectSummaryEntry, objectSummaryEntryAux)) {
                            returnList.add(objectSummaryEntry);
                            objectSummaryEntryAux = objectSummaryEntry;
                        }
                    }
                    case 3: {
                        if (!isSameObjectSummaryEntryForGexpAndType(objectSummaryEntry, objectSummaryEntryAux)) {
                            returnList.add(objectSummaryEntry);
                            objectSummaryEntryAux = objectSummaryEntry;
                        }
                    }
                    case 4: {
                        if (!isSameObjectSummaryEntryForTotal(objectSummaryEntry, objectSummaryEntryAux)) {
                            returnList.add(objectSummaryEntry);
                            objectSummaryEntryAux = objectSummaryEntry;
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
    private List<String> buildOrderByList() {
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

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
