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

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectDump;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgAccountFundingDetailReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgAccountFundingDetailReportTotal;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountFundingDetailReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountFundingDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountFundingDetailReportService.
 */
@Transactional
public class BudgetConstructionAccountFundingDetailReportServiceImpl implements BudgetConstructionAccountFundingDetailReportService {

    protected BudgetConstructionAccountFundingDetailReportDao budgetConstructionAccountFundingDetailReportDao;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected ConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetReportsControlListService#updateRepotsAccountFundingDetailTable(java.lang.String)
     */
    @Override
    public void updateAccountFundingDetailTable(String principalName) {
        budgetConstructionAccountFundingDetailReportDao.updateReportsAccountFundingDetailTable(principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountFundingDetailReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    @Override
    public Collection<BudgetConstructionOrgAccountFundingDetailReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgAccountFundingDetailReport> reportSet = new ArrayList();
        List<BudgetConstructionOrgAccountFundingDetailReportTotal> orgAccountFundingDetailReportTotalList;
        BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry;
        Collection<BudgetConstructionObjectDump> accountFundingDetailList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionObjectDump.class, principalName, buildOrderByList());

        Map<BudgetConstructionObjectDump, Collection<PendingBudgetConstructionAppointmentFunding>> appointmentFundingEntireMap = new HashMap();
        for (BudgetConstructionObjectDump accountFundingDetailEntry : accountFundingDetailList) {
            appointmentFundingEntireMap.put(accountFundingDetailEntry, budgetConstructionReportsServiceHelper.getPendingBudgetConstructionAppointmentFundingList(universityFiscalYear, accountFundingDetailEntry));
        }

        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(principalName);

        List<BudgetConstructionObjectDump> listForCalculateTotalObject = BudgetConstructionReportHelper.deleteDuplicated((List) accountFundingDetailList, fieldsForObject());
        List<BudgetConstructionObjectDump> listForCalculateTotalAccount = BudgetConstructionReportHelper.deleteDuplicated((List) accountFundingDetailList, fieldsForAccount());

        // Calculate Total Section
        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalObject = calculateObjectTotal(appointmentFundingEntireMap, listForCalculateTotalObject);
        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalAccount = calculateAccountTotal(fundingDetailTotalObject, listForCalculateTotalAccount);

        for (BudgetConstructionObjectDump accountFundingDetailEntry : accountFundingDetailList) {
            Collection<PendingBudgetConstructionAppointmentFunding> appointmentFundingCollection = appointmentFundingEntireMap.get(accountFundingDetailEntry);
            for (PendingBudgetConstructionAppointmentFunding appointmentFundingEntry : appointmentFundingCollection) {

                orgAccountFundingDetailReportEntry = new BudgetConstructionOrgAccountFundingDetailReport();
                buildReportsHeader(universityFiscalYear, objectCodes, orgAccountFundingDetailReportEntry, accountFundingDetailEntry);
                buildReportsBody(universityFiscalYear, orgAccountFundingDetailReportEntry, appointmentFundingEntry);
                buildReportsTotal(orgAccountFundingDetailReportEntry, accountFundingDetailEntry, fundingDetailTotalObject, fundingDetailTotalAccount);

                reportSet.add(orgAccountFundingDetailReportEntry);
            }
        }

        return reportSet;
    }

    /**
     * builds report Header
     *
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, BudgetConstructionObjectDump accountFundingDetail) {
        String orgChartDesc = accountFundingDetail.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = accountFundingDetail.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = accountFundingDetail.getOrganization().getOrganizationName();
        String reportChartDesc = accountFundingDetail.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = accountFundingDetail.getSubFundGroup().getSubFundGroupCode();
        String subFundGroupDes = accountFundingDetail.getSubFundGroup().getSubFundGroupDescription();
        String fundGroupName = accountFundingDetail.getSubFundGroup().getFundGroupCode();
        String fundGroupDes = accountFundingDetail.getSubFundGroup().getFundGroup().getName();

        Integer prevFiscalyear = universityFiscalYear - 1;
        orgAccountFundingDetailReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgAccountFundingDetailReportEntry.setOrgChartOfAccountsCode(accountFundingDetail.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgAccountFundingDetailReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountFundingDetailReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgAccountFundingDetailReportEntry.setOrganizationCode(accountFundingDetail.getOrganizationCode());
        if (orgName == null) {
            orgAccountFundingDetailReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgAccountFundingDetailReportEntry.setOrganizationName(orgName);
        }

        orgAccountFundingDetailReportEntry.setChartOfAccountsCode(accountFundingDetail.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgAccountFundingDetailReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountFundingDetailReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgAccountFundingDetailReportEntry.setFundGroupCode(accountFundingDetail.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgAccountFundingDetailReportEntry.setFundGroupName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgAccountFundingDetailReportEntry.setFundGroupName(fundGroupDes);
        }

        orgAccountFundingDetailReportEntry.setSubFundGroupCode(accountFundingDetail.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgAccountFundingDetailReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgAccountFundingDetailReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }


        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgAccountFundingDetailReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgAccountFundingDetailReportEntry.setConsHdr("");

        orgAccountFundingDetailReportEntry.setFinancialObjectCode(accountFundingDetail.getFinancialObjectCode());
        orgAccountFundingDetailReportEntry.setFinancialObjectCodeName(accountFundingDetail.getFinancialObject().getFinancialObjectCodeName());
        // group
        orgAccountFundingDetailReportEntry.setSubAccountNumber(accountFundingDetail.getSubAccountNumber() + accountFundingDetail.getAccountNumber());
        orgAccountFundingDetailReportEntry.setObjectCodes(objectCodes);

        String subAccountName = "";
        String subAccountNumberAndName = "";
        String divider = "";
        // set accountNumber and name, subAccountNumber and name
        if (accountFundingDetail.getAccount() != null) {
            orgAccountFundingDetailReportEntry.setAccountNumberAndName(accountFundingDetail.getAccountNumber() + " " + accountFundingDetail.getAccount().getAccountName());
            orgAccountFundingDetailReportEntry.setAccountName(accountFundingDetail.getAccount().getAccountName());
        }

        if (!accountFundingDetail.getSubAccountNumber().equals(KFSConstants.getDashSubAccountNumber())) {
            divider = BCConstants.Report.DIVIDER;
            try {
                subAccountName = accountFundingDetail.getSubAccount().getSubAccountName();
                subAccountNumberAndName = accountFundingDetail.getSubAccount().getSubAccountNumber() + " " + accountFundingDetail.getSubAccount().getSubAccountName();
            }
            catch (PersistenceBrokerException e) {
                subAccountName = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION);
                subAccountNumberAndName = subAccountName;
            }
        }
        orgAccountFundingDetailReportEntry.setSubAccountName(subAccountName);
        orgAccountFundingDetailReportEntry.setSubAccountNumberAndName(subAccountNumberAndName);
        orgAccountFundingDetailReportEntry.setDivider(divider);
    }


    /**
     * builds report body
     *
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {

        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(appointmentFundingEntry);
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
        if (budgetConstructionIntendedIncumbent != null) {
            if (budgetConstructionIntendedIncumbent.getName() == null) {
                orgAccountFundingDetailReportEntry.setName(BCConstants.Report.VACANT);
            }
            else {
                int nameLength = budgetConstructionIntendedIncumbent.getName().length();
                orgAccountFundingDetailReportEntry.setName(budgetConstructionIntendedIncumbent.getName().substring(0, (nameLength > 33) ? 33 : nameLength));
            }

            orgAccountFundingDetailReportEntry.setIuClassificationLevel(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
        }
        else {
            orgAccountFundingDetailReportEntry.setName(BCConstants.Report.VACANT);
            orgAccountFundingDetailReportEntry.setIuClassificationLevel(BCConstants.Report.BLANK);
        }


        if (budgetConstructionAdministrativePost != null) {
            orgAccountFundingDetailReportEntry.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }

        if (budgetConstructionPosition != null) {
            orgAccountFundingDetailReportEntry.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            orgAccountFundingDetailReportEntry.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            orgAccountFundingDetailReportEntry.setPositionFte(BudgetConstructionReportHelper.setDecimalDigit(budgetConstructionPosition.getPositionFullTimeEquivalency(), 5, true));
            orgAccountFundingDetailReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            orgAccountFundingDetailReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
            orgAccountFundingDetailReportEntry.setPositionStandardHoursDefault(budgetConstructionPosition.getPositionStandardHoursDefault());
        }

        BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFundingEntry.getEffectiveCSFTracker();
        orgAccountFundingDetailReportEntry.setAmountChange(new Integer(0));
        orgAccountFundingDetailReportEntry.setPercentChange(BigDecimal.ZERO);
        if (csfTracker != null) {
            orgAccountFundingDetailReportEntry.setCsfTimePercent(BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfTimePercent(), 2, false));
            orgAccountFundingDetailReportEntry.setCsfAmount(new Integer(csfTracker.getCsfAmount().intValue()));
            orgAccountFundingDetailReportEntry.setCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfFullTimeEmploymentQuantity(), 5, true));

            // calculate amountChange and percentChange
            Integer amountChange = new Integer(0);
            BigDecimal percentChange = BigDecimal.ZERO;
            BigDecimal csfFte = BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfFullTimeEmploymentQuantity(), 5, false);
            BigDecimal reqFte = BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedFteQuantity(), 5, false);
            if (reqFte.compareTo(csfFte) == 0) {
                amountChange = appointmentFundingEntry.getAppointmentRequestedAmount().subtract(csfTracker.getCsfAmount()).intValue();
                percentChange = BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange.intValue()), csfTracker.getCsfAmount().bigDecimalValue());
            }
            orgAccountFundingDetailReportEntry.setAmountChange(amountChange);
            orgAccountFundingDetailReportEntry.setPercentChange(percentChange);
        }

        if (appointmentFundingEntry != null) {
            if (appointmentFundingEntry.getFinancialSubObjectCode().equals(KFSConstants.getDashFinancialSubObjectCode())) {
                orgAccountFundingDetailReportEntry.setFinancialSubObjectCode(BCConstants.Report.BLANK);
            }
            else {
                orgAccountFundingDetailReportEntry.setFinancialSubObjectCode(appointmentFundingEntry.getFinancialSubObjectCode());
            }

            orgAccountFundingDetailReportEntry.setAppointmentFundingMonth(appointmentFundingEntry.getAppointmentFundingMonth());
            orgAccountFundingDetailReportEntry.setAppointmentRequestedAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue()));
            orgAccountFundingDetailReportEntry.setAppointmentRequestedTimePercent(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedTimePercent(), 2, false));
            orgAccountFundingDetailReportEntry.setAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedFteQuantity(), 5, false));
            orgAccountFundingDetailReportEntry.setAppointmentFundingDurationCode(appointmentFundingEntry.getAppointmentFundingDurationCode());

            orgAccountFundingDetailReportEntry.setAppointmentRequestedCsfAmount(BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentRequestedCsfAmount()));
            orgAccountFundingDetailReportEntry.setAppointmentRequestedCsfTimePercent(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());
            orgAccountFundingDetailReportEntry.setAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedCsfFteQuantity(), 5, false));

            orgAccountFundingDetailReportEntry.setAppointmentTotalIntendedAmount(BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentTotalIntendedAmount()));
            orgAccountFundingDetailReportEntry.setAppointmentTotalIntendedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity(), 5, false));

            orgAccountFundingDetailReportEntry.setEmplid(appointmentFundingEntry.getEmplid());
        }


        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            orgAccountFundingDetailReportEntry.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            orgAccountFundingDetailReportEntry.setDeleteBox(BCConstants.Report.BLANK);
        }
    }

    public void buildReportsTotal(BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, BudgetConstructionObjectDump accountFundingDetail, Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalObject, Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalAccount) {
        for (BudgetConstructionOrgAccountFundingDetailReportTotal fundingDetailTotalObjectEntry : fundingDetailTotalObject) {
            if (BudgetConstructionReportHelper.isSameEntry(fundingDetailTotalObjectEntry.getBudgetConstructionObjectDump(), accountFundingDetail, fieldsForObject())) {
                if (accountFundingDetail.getFinancialObject() != null) {
                    orgAccountFundingDetailReportEntry.setTotalObjectname(accountFundingDetail.getFinancialObject().getName());
                }
                orgAccountFundingDetailReportEntry.setTotalObjectPositionCsfAmount(fundingDetailTotalObjectEntry.getTotalObjectPositionCsfAmount());
                orgAccountFundingDetailReportEntry.setTotalObjectAppointmentRequestedAmount(fundingDetailTotalObjectEntry.getTotalObjectAppointmentRequestedAmount());
                orgAccountFundingDetailReportEntry.setTotalObjectPositionCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalObjectEntry.getTotalObjectPositionCsfFteQuantity(), 5, true));
                orgAccountFundingDetailReportEntry.setTotalObjectAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalObjectEntry.getTotalObjectAppointmentRequestedFteQuantity(), 5, true));
                // calculate amountChange and percentChange
                Integer amountChange = fundingDetailTotalObjectEntry.getTotalObjectAppointmentRequestedAmount() - fundingDetailTotalObjectEntry.getTotalObjectPositionCsfAmount();
                orgAccountFundingDetailReportEntry.setTotalObjectAmountChange(amountChange);
                orgAccountFundingDetailReportEntry.setTotalObjectPercentChange(BudgetConstructionReportHelper.calculatePercent(amountChange, fundingDetailTotalObjectEntry.getTotalObjectPositionCsfAmount()));
            }
        }

        for (BudgetConstructionOrgAccountFundingDetailReportTotal fundingDetailTotalAccountEntry : fundingDetailTotalAccount) {
            if (BudgetConstructionReportHelper.isSameEntry(fundingDetailTotalAccountEntry.getBudgetConstructionObjectDump(), accountFundingDetail, fieldsForAccount())) {

                orgAccountFundingDetailReportEntry.setTotalAccountPositionCsfAmount(fundingDetailTotalAccountEntry.getTotalAccountPositionCsfAmount());
                orgAccountFundingDetailReportEntry.setTotalAccountAppointmentRequestedAmount(fundingDetailTotalAccountEntry.getTotalAccountAppointmentRequestedAmount());
                orgAccountFundingDetailReportEntry.setTotalAccountPositionCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalAccountEntry.getTotalAccountPositionCsfFteQuantity(), 5, true));
                orgAccountFundingDetailReportEntry.setTotalAccountAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalAccountEntry.getTotalAccountAppointmentRequestedFteQuantity(), 5, true));
                Integer amountChange = fundingDetailTotalAccountEntry.getTotalAccountAppointmentRequestedAmount() - fundingDetailTotalAccountEntry.getTotalAccountPositionCsfAmount();
                orgAccountFundingDetailReportEntry.setTotalAccountAmountChange(amountChange);
                orgAccountFundingDetailReportEntry.setTotalAccountPercentChange(BudgetConstructionReportHelper.calculatePercent(amountChange, fundingDetailTotalAccountEntry.getTotalAccountPositionCsfAmount()));
            }
        }
    }


    /**
     * builds report total
     *
     * @param BudgetConstructionObjectDump bcod
     * @param List reportTotalList
     */
    protected Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> calculateObjectTotal(Map appointmentFundingEntireMap, List<BudgetConstructionObjectDump> listForCalculateTotalObject) {
        Integer totalObjectPositionCsfAmount = new Integer(0);
        Integer totalObjectAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalObjectPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;

        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> returnCollection = new ArrayList();

        for (BudgetConstructionObjectDump budgetConstructionObjectDump : listForCalculateTotalObject) {
            Collection<PendingBudgetConstructionAppointmentFunding> accountFundingCollection = new ArrayList();
            accountFundingCollection = (Collection<PendingBudgetConstructionAppointmentFunding>) appointmentFundingEntireMap.get(budgetConstructionObjectDump);
            for (PendingBudgetConstructionAppointmentFunding accountFundingEntry : accountFundingCollection) {
                if (accountFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
                    BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = accountFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
                    totalObjectPositionCsfAmount = totalObjectPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
                    totalObjectPositionCsfFteQuantity = totalObjectPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
                }
                totalObjectAppointmentRequestedAmount = totalObjectAppointmentRequestedAmount + new Integer(accountFundingEntry.getAppointmentRequestedAmount().intValue());
                totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity.add(accountFundingEntry.getAppointmentRequestedFteQuantity());
            }

            BudgetConstructionOrgAccountFundingDetailReportTotal budgetConstructionOrgAccountFundingDetailReportTotal = new BudgetConstructionOrgAccountFundingDetailReportTotal();

            budgetConstructionOrgAccountFundingDetailReportTotal.setBudgetConstructionObjectDump(budgetConstructionObjectDump);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectPositionCsfAmount(totalObjectPositionCsfAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectPositionCsfFteQuantity(totalObjectPositionCsfFteQuantity);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectAppointmentRequestedAmount(totalObjectAppointmentRequestedAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectAppointmentRequestedFteQuantity(totalObjectAppointmentRequestedFteQuantity);

            returnCollection.add(budgetConstructionOrgAccountFundingDetailReportTotal);

            totalObjectPositionCsfAmount = new Integer(0);
            totalObjectAppointmentRequestedAmount = new Integer(0);
            totalObjectPositionCsfFteQuantity = BigDecimal.ZERO;
            totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        }


        return returnCollection;
    }

    protected Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> calculateAccountTotal(Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalObject, List<BudgetConstructionObjectDump> listForCalculateTotalAccount) {

        // private Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> calculateAccountTotal(Map
        // appointmentFundingEntireMap, Collection<BudgetConstructionObjectDump> accountFundingDetailList,
        // List<BudgetConstructionObjectDump> listForCalculateTotalAccount) {

        Integer totalAccountPositionCsfAmount = new Integer(0);
        Integer totalAccountAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalAccountPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalAccountAppointmentRequestedFteQuantity = BigDecimal.ZERO;

        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> returnCollection = new ArrayList();
        for (BudgetConstructionObjectDump budgetConstructionObjectDump : listForCalculateTotalAccount) {
            for (BudgetConstructionOrgAccountFundingDetailReportTotal fundingDetailTotalObjectEntry : fundingDetailTotalObject) {
                if (BudgetConstructionReportHelper.isSameEntry(budgetConstructionObjectDump, fundingDetailTotalObjectEntry.getBudgetConstructionObjectDump(), fieldsForAccount())) {
                    totalAccountPositionCsfAmount = totalAccountPositionCsfAmount + fundingDetailTotalObjectEntry.getTotalObjectPositionCsfAmount();
                    totalAccountPositionCsfFteQuantity = totalAccountPositionCsfFteQuantity.add(fundingDetailTotalObjectEntry.getTotalObjectPositionCsfFteQuantity());
                    totalAccountAppointmentRequestedAmount = totalAccountAppointmentRequestedAmount + fundingDetailTotalObjectEntry.getTotalObjectAppointmentRequestedAmount();
                    totalAccountAppointmentRequestedFteQuantity = totalAccountAppointmentRequestedFteQuantity.add(fundingDetailTotalObjectEntry.getTotalObjectAppointmentRequestedFteQuantity());
                }
            }
            BudgetConstructionOrgAccountFundingDetailReportTotal budgetConstructionOrgAccountFundingDetailReportTotal = new BudgetConstructionOrgAccountFundingDetailReportTotal();
            budgetConstructionOrgAccountFundingDetailReportTotal.setBudgetConstructionObjectDump(budgetConstructionObjectDump);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountPositionCsfAmount(totalAccountPositionCsfAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountPositionCsfFteQuantity(totalAccountPositionCsfFteQuantity);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountAppointmentRequestedAmount(totalAccountAppointmentRequestedAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountAppointmentRequestedFteQuantity(totalAccountAppointmentRequestedFteQuantity);

            returnCollection.add(budgetConstructionOrgAccountFundingDetailReportTotal);

            totalAccountPositionCsfAmount = new Integer(0);
            totalAccountAppointmentRequestedAmount = new Integer(0);
            totalAccountPositionCsfFteQuantity = BigDecimal.ZERO;
            totalAccountAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        }
        return returnCollection;
    }

    protected List<String> fieldsForObject() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForAccount());
        fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        return fieldList;
    }

    protected List<String> fieldsForAccount() {
        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        fieldList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        fieldList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        fieldList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        return fieldList;
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
        returnList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        return returnList;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionAccountFundingDetailReportDao(BudgetConstructionAccountFundingDetailReportDao budgetConstructionAccountFundingDetailReportDao) {
        this.budgetConstructionAccountFundingDetailReportDao = budgetConstructionAccountFundingDetailReportDao;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

}
