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

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountObjectDetailReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountObjectDetailReportTotal;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionBalanceByAccount;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDocumentAccountObjectDetailReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionDocumentAccountObjectDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionDocumentAccountObjectDetailReportServiceImpl implements BudgetConstructionDocumentAccountObjectDetailReportService {
    protected BudgetConstructionDocumentAccountObjectDetailReportDao budgetConstructionDocumentAccountObjectDetailReportDao;
    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;

    @Override
    public void updateDocumentAccountObjectDetailReportTable(String principalName, String documentNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        budgetConstructionDocumentAccountObjectDetailReportDao.updateDocumentAccountObjectDetailReportTable(principalName, documentNumber, universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    @Override
    public Collection<BudgetConstructionAccountObjectDetailReport> buildReports(String principalName) {
        Collection<BudgetConstructionAccountObjectDetailReport> reportSet = new ArrayList();

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionBalanceByAccount> balanceByAccountList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionBalanceByAccount.class, principalName, orderList);




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
            buildReportsTotal(balanceByAccount, accountObjectDetailReport, accountObjectDetailTotalObjectList, accountObjectDetailTotalLevelList, accountObjectDetailTotalTypeList);
            reportSet.add(accountObjectDetailReport);
        }

        return reportSet;
    }

    /**
     * builds report Header
     *
     * @param BudgetConstructionObjectSummary bcas
     */
    protected void buildReportsHeader(BudgetConstructionBalanceByAccount balanceByAccount, BudgetConstructionAccountObjectDetailReport accountObjectDetailReport) {
        Integer prevFiscalyear = balanceByAccount.getUniversityFiscalYear() - 1;
        accountObjectDetailReport.setFiscalYear(prevFiscalyear.toString() + "-" + balanceByAccount.getUniversityFiscalYear().toString().substring(2, 4));

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        accountObjectDetailReport.setBaseFy(prevPrevFiscalyear.toString() + "-" + prevFiscalyear.toString().substring(2, 4));
        accountObjectDetailReport.setReqFy(prevFiscalyear.toString() + "-" + balanceByAccount.getUniversityFiscalYear().toString().substring(2, 4));

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


        if (orgName == null) {
            accountObjectDetailReport.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            accountObjectDetailReport.setOrganizationName(orgName);
        }

        if (fundGroupCode == null) {
            accountObjectDetailReport.setFundGroupCode(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_CODE));
        }
        else {
            accountObjectDetailReport.setFundGroupCode(fundGroupCode);
        }

        if (fundGroupName == null) {
            accountObjectDetailReport.setFundGroupName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            accountObjectDetailReport.setFundGroupName(fundGroupName);
        }


        if (accountName == null) {
            accountObjectDetailReport.setAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION));
        }
        else {
            accountObjectDetailReport.setAccountName(accountName);
        }

        String chartOfAccountDescription = "";
        if (balanceByAccount.getChartOfAccounts() != null){
            try {
                chartOfAccountDescription = balanceByAccount.getChartOfAccounts().getFinChartOfAccountDescription();
            }
            catch (PersistenceBrokerException e) {
                chartOfAccountDescription = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION);
            }
        } else {
            chartOfAccountDescription = BCConstants.Report.CHART + BCConstants.Report.NOT_DEFINED;
        }

        accountObjectDetailReport.setChartOfAccountDescription(chartOfAccountDescription);

        String subAccountName = "";

        if (!balanceByAccount.getSubAccountNumber().equals(KFSConstants.getDashSubAccountNumber())){
            try {
                subAccountName = balanceByAccount.getSubAccount().getSubAccountName();
            }
            catch (PersistenceBrokerException e) {
                subAccountName = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION);
            }
        }
        accountObjectDetailReport.setSubAccountName(subAccountName);


        //group
        accountObjectDetailReport.setTypeFinancialReportSortCode(balanceByAccount.getTypeFinancialReportSortCode());
        accountObjectDetailReport.setLevelFinancialReportSortCode(balanceByAccount.getLevelFinancialReportSortCode());
        accountObjectDetailReport.setFinancialSubObjectCode(balanceByAccount.getFinancialSubObjectCode());
    }

    /**
     * builds report body
     *
     * @param BudgetConstructionLevelSummary bcas
     */
    protected void buildReportsBody(BudgetConstructionBalanceByAccount balanceByAccount, BudgetConstructionAccountObjectDetailReport accountObjectDetailReport) {
        accountObjectDetailReport.setFinancialObjectCode(balanceByAccount.getFinancialObjectCode());

        if (balanceByAccount.getFinancialSubObjectCode().equals(KFSConstants.getDashFinancialSubObjectCode())){
            accountObjectDetailReport.setFinancialObjectName(balanceByAccount.getFinancialObject().getFinancialObjectCodeName());
        }
        else {
            accountObjectDetailReport.setFinancialObjectName(balanceByAccount.getFinancialSubObject().getFinancialSubObjectCodeName());
        }

        accountObjectDetailReport.setPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(balanceByAccount.getPositionCsfLeaveFteQuantity(), 2, true));

        accountObjectDetailReport.setCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(balanceByAccount.getCsfFullTimeEmploymentQuantity(), 2, true));

        accountObjectDetailReport.setAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(balanceByAccount.getAppointmentRequestedCsfFteQuantity(), 2, true));

        accountObjectDetailReport.setAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(balanceByAccount.getAppointmentRequestedFteQuantity(), 2, true));

        accountObjectDetailReport.setAccountLineAnnualBalanceAmount(BudgetConstructionReportHelper.convertKualiInteger(balanceByAccount.getAccountLineAnnualBalanceAmount()));

        accountObjectDetailReport.setFinancialBeginningBalanceLineAmount(BudgetConstructionReportHelper.convertKualiInteger(balanceByAccount.getFinancialBeginningBalanceLineAmount()));

        Integer changeAmount = BudgetConstructionReportHelper.convertKualiInteger(balanceByAccount.getAccountLineAnnualBalanceAmount()) - BudgetConstructionReportHelper.convertKualiInteger(balanceByAccount.getFinancialBeginningBalanceLineAmount());
        accountObjectDetailReport.setAmountChange(changeAmount);
        accountObjectDetailReport.setPercentChange(BudgetConstructionReportHelper.calculatePercent(changeAmount, accountObjectDetailReport.getFinancialBeginningBalanceLineAmount()));

    }





    /**
     * builds report total
     */

    protected void buildReportsTotal(BudgetConstructionBalanceByAccount balanceByAccount, BudgetConstructionAccountObjectDetailReport accountObjectDetailReport, List<BudgetConstructionAccountObjectDetailReportTotal> accountObjectDetailTotalObjectList, List<BudgetConstructionAccountObjectDetailReportTotal> accountObjectDetailTotalLevelList, List<BudgetConstructionAccountObjectDetailReportTotal> accountObjectDetailTotalTypeList) {

        for (BudgetConstructionAccountObjectDetailReportTotal objectTotal : accountObjectDetailTotalObjectList) {
            if (BudgetConstructionReportHelper.isSameEntry(balanceByAccount, objectTotal.getBudgetConstructionBalanceByAccount(), fieldsForObject())) {
                accountObjectDetailReport.setTotalObjectDescription(balanceByAccount.getFinancialObject().getFinancialObjectCodeName());

                accountObjectDetailReport.setTotalObjectPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectPositionCsfLeaveFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalObjectCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectCsfFullTimeEmploymentQuantity(), 2, true));
                accountObjectDetailReport.setTotalObjectFinancialBeginningBalanceLineAmount(objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount());
                accountObjectDetailReport.setTotalObjectAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectAppointmentRequestedCsfFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalObjectAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectAppointmentRequestedFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalObjectAccountLineAnnualBalanceAmount(objectTotal.getTotalObjectAccountLineAnnualBalanceAmount());

                Integer totalObjectAmountChange = objectTotal.getTotalObjectAccountLineAnnualBalanceAmount() - objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount();
                accountObjectDetailReport.setTotalObjectAmountChange(totalObjectAmountChange);
                accountObjectDetailReport.setTotalObjectPercentChange(BudgetConstructionReportHelper.calculatePercent(totalObjectAmountChange, objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionAccountObjectDetailReportTotal levelTotal : accountObjectDetailTotalLevelList) {
            if (BudgetConstructionReportHelper.isSameEntry(balanceByAccount, levelTotal.getBudgetConstructionBalanceByAccount(), fieldsForLevel())) {
                accountObjectDetailReport.setTotalLevelDescription(balanceByAccount.getFinancialObjectLevel().getFinancialObjectLevelName());

                accountObjectDetailReport.setTotalLevelPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelPositionCsfLeaveFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalLevelCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelCsfFullTimeEmploymentQuantity(), 2, true));
                accountObjectDetailReport.setTotalLevelFinancialBeginningBalanceLineAmount(levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount());
                accountObjectDetailReport.setTotalLevelAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelAppointmentRequestedCsfFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalLevelAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelAppointmentRequestedFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalLevelAccountLineAnnualBalanceAmount(levelTotal.getTotalLevelAccountLineAnnualBalanceAmount());

                Integer totalLevelAmountChange = levelTotal.getTotalLevelAccountLineAnnualBalanceAmount() - levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount();
                accountObjectDetailReport.setTotalLevelAmountChange(totalLevelAmountChange);
                accountObjectDetailReport.setTotalLevelPercentChange(BudgetConstructionReportHelper.calculatePercent(totalLevelAmountChange, levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount()));
            }
        }



        for (BudgetConstructionAccountObjectDetailReportTotal typeTotal : accountObjectDetailTotalTypeList) {
            if (BudgetConstructionReportHelper.isSameEntry(balanceByAccount, typeTotal.getBudgetConstructionBalanceByAccount(), fieldsForType())) {

                if (balanceByAccount.getTypeFinancialReportSortCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                    accountObjectDetailReport.setTotalTypeDescription(BCConstants.Report.TOTAL_REVENUES);
                }
                else {
                    accountObjectDetailReport.setTotalTypeDescription(BCConstants.Report.TOTAL_EXPENDITURES_MARGIN);
                }
                accountObjectDetailReport.setTotalTypePositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(typeTotal.getTotalTypePositionCsfLeaveFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalTypeCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(typeTotal.getTotalTypeCsfFullTimeEmploymentQuantity(), 2, true));
                accountObjectDetailReport.setTotalTypeFinancialBeginningBalanceLineAmount(typeTotal.getTotalTypeFinancialBeginningBalanceLineAmount());
                accountObjectDetailReport.setTotalTypeAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(typeTotal.getTotalTypeAppointmentRequestedCsfFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalTypeAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(typeTotal.getTotalTypeAppointmentRequestedFteQuantity(), 2, true));
                accountObjectDetailReport.setTotalTypeAccountLineAnnualBalanceAmount(typeTotal.getTotalTypeAccountLineAnnualBalanceAmount());

                Integer totalTypeAmountChange = typeTotal.getTotalTypeAccountLineAnnualBalanceAmount() - typeTotal.getTotalTypeFinancialBeginningBalanceLineAmount();
                accountObjectDetailReport.setTotalTypeAmountChange(totalTypeAmountChange);
                accountObjectDetailReport.setTotalTypePercentChange(BudgetConstructionReportHelper.calculatePercent(totalTypeAmountChange, typeTotal.getTotalTypeFinancialBeginningBalanceLineAmount()));
            }
        }


    }





    protected List calculateObjectTotal(List<BudgetConstructionBalanceByAccount> balanceByAccountList, List<BudgetConstructionBalanceByAccount> simpleList) {

        BigDecimal totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalObjectAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();

        for (BudgetConstructionBalanceByAccount simpleBalanceByAccountEntry : simpleList) {

            BudgetConstructionAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionAccountObjectDetailReportTotal();
            for (BudgetConstructionBalanceByAccount balanceByAccountEntry : balanceByAccountList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBalanceByAccountEntry, balanceByAccountEntry, fieldsForObject()) /* && !balanceByAccountEntry.getFinancialSubObjectCode().endsWith("---") */) {
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

            returnList.add(bcObjectTotal);

            totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalObjectCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
            totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalObjectAccountLineAnnualBalanceAmount = new Integer(0);

        }
        return returnList;

    }




    protected List calculateLevelTotal(List<BudgetConstructionBalanceByAccount> balanceByAccountList, List<BudgetConstructionBalanceByAccount> simpleList) {

        BigDecimal totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalLevelAccountLineAnnualBalanceAmount = new Integer(0);

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

            returnList.add(bcObjectTotal);

            totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalLevelCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
            totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;

    }


    protected List calculateTypeTotal(List<BudgetConstructionBalanceByAccount> balanceByAccountList, List<BudgetConstructionBalanceByAccount> simpleList) {

        BigDecimal totalTypePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalTypeCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
        Integer totalTypeFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalTypeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalTypeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalTypeAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionBalanceByAccount simpleBcosEntry : simpleList) {
            BudgetConstructionAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionAccountObjectDetailReportTotal();
            for (BudgetConstructionBalanceByAccount balanceByAccountEntry : balanceByAccountList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, balanceByAccountEntry, fieldsForType())) {

                    totalTypeFinancialBeginningBalanceLineAmount += new Integer(balanceByAccountEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalTypeAccountLineAnnualBalanceAmount += new Integer(balanceByAccountEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalTypePositionCsfLeaveFteQuantity = totalTypePositionCsfLeaveFteQuantity.add(balanceByAccountEntry.getPositionCsfLeaveFteQuantity());
                    totalTypeCsfFullTimeEmploymentQuantity = totalTypeCsfFullTimeEmploymentQuantity.add(balanceByAccountEntry.getCsfFullTimeEmploymentQuantity());
                    totalTypeAppointmentRequestedCsfFteQuantity = totalTypeAppointmentRequestedCsfFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedCsfFteQuantity());
                    totalTypeAppointmentRequestedFteQuantity = totalTypeAppointmentRequestedFteQuantity.add(balanceByAccountEntry.getAppointmentRequestedFteQuantity());
                }
            }

            bcObjectTotal.setBudgetConstructionBalanceByAccount(simpleBcosEntry);
            bcObjectTotal.setTotalTypePositionCsfLeaveFteQuantity(totalTypePositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalTypeCsfFullTimeEmploymentQuantity(totalTypeCsfFullTimeEmploymentQuantity);
            bcObjectTotal.setTotalTypeFinancialBeginningBalanceLineAmount(totalTypeFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalTypeAppointmentRequestedCsfFteQuantity(totalTypeAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalTypeAppointmentRequestedFteQuantity(totalTypeAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalTypeAccountLineAnnualBalanceAmount(totalTypeAccountLineAnnualBalanceAmount);

            returnList.add(bcObjectTotal);

            totalTypePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalTypeCsfFullTimeEmploymentQuantity = BigDecimal.ZERO;
            totalTypeFinancialBeginningBalanceLineAmount = new Integer(0);
            totalTypeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalTypeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalTypeAccountLineAnnualBalanceAmount = new Integer(0);
        }

        return returnList;
    }







    /**
     * builds orderByList for sort order.
     *
     * @return returnList
     */
    protected List<String> buildOrderByList() {
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
    protected List<String> fieldsForObject() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForLevel());
        fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        //fieldList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return fieldList;
    }

    /**
     * builds list of fields for comparing entry of Level
     * @return List<String>
     */
    protected List<String> fieldsForLevel() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForType());
        fieldList.add(KFSPropertyConstants.LEVEL_FINANCIAL_REPORT_SORT_CODE);
        return fieldList;
    }

    /**
     * builds list of fields for comparing entry of GexpAndType
     * @return List<String>
     */
    protected List<String> fieldsForType() {
        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.TYPE_FINANCIAL_REPORT_SORT_CODE);
        return fieldList;
    }

    /*protected List<String> commonFields(){
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

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

    public void setBudgetConstructionDocumentAccountObjectDetailReportDao(BudgetConstructionDocumentAccountObjectDetailReportDao budgetConstructionDocumentAccountObjectDetailReportDao) {
        this.budgetConstructionDocumentAccountObjectDetailReportDao = budgetConstructionDocumentAccountObjectDetailReportDao;
    }

}

