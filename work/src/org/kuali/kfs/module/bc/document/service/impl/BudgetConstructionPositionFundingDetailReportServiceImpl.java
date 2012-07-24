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
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgPositionFundingDetailReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgPositionFundingDetailReportTotal;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPositionFunding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionPositionFundingDetailReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionPositionFundingDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionPositionFundingDetailReportService.
 */
@Transactional
public class BudgetConstructionPositionFundingDetailReportServiceImpl implements BudgetConstructionPositionFundingDetailReportService {

    protected BudgetConstructionPositionFundingDetailReportDao budgetConstructionPositionFundingDetailReportDao;
    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected SalarySettingService salarySettingService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionPositionFundingDetailReportService#updatePositionFundingDetailReport(java.lang.String,
     *      org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    public void updatePositionFundingDetailReport(String principalName, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        budgetConstructionPositionFundingDetailReportDao.updateReportsPositionFundingDetailTable(principalName, applyAThreshold, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionPositionFundingDetailReportService#buildReports(java.lang.Integer,
     *      java.lang.String)
     */
    public Collection<BudgetConstructionOrgPositionFundingDetailReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgPositionFundingDetailReport> reportSet = new ArrayList();

        Collection<BudgetConstructionPositionFunding> positionFundingDetailList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionPositionFunding.class, principalName, buildOrderByList());

        List<BudgetConstructionPositionFunding> listForCalculateTotalPerson = BudgetConstructionReportHelper.deleteDuplicated((List) positionFundingDetailList, fieldsForPerson());
        List<BudgetConstructionPositionFunding> listForCalculateTotalOrg = BudgetConstructionReportHelper.deleteDuplicated((List) positionFundingDetailList, fieldsForOrg());

        // Calculate Total Section
        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalPerson = calculatePersonTotal(positionFundingDetailList, listForCalculateTotalPerson);
        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalOrg = calculateOrgTotal(positionFundingDetailList, listForCalculateTotalOrg);

        // Get selected objectCodes
        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(principalName);
        for (BudgetConstructionPositionFunding positionFundingDetailEntry : positionFundingDetailList) {
            BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry = new BudgetConstructionOrgPositionFundingDetailReport();
            buildReportsHeader(universityFiscalYear, objectCodes, orgPositionFundingDetailReportEntry, positionFundingDetailEntry);
            buildReportsBody(universityFiscalYear, orgPositionFundingDetailReportEntry, positionFundingDetailEntry);
            buildReportsTotal(orgPositionFundingDetailReportEntry, positionFundingDetailEntry, fundingDetailTotalPerson, fundingDetailTotalOrg);
            reportSet.add(orgPositionFundingDetailReportEntry);
        }
        return reportSet;
    }

    /**
     * builds report header
     * 
     * @param universityFiscalYear
     * @param objectCodes
     * @param orgPositionFundingDetailReportEntry
     * @param positionFundingDetail
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry, BudgetConstructionPositionFunding positionFundingDetail) {
        String chartDesc = positionFundingDetail.getSelectedOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = positionFundingDetail.getSelectedOrganization().getOrganizationName();
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgPositionFundingDetailReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgPositionFundingDetailReportEntry.setOrganizationCode(positionFundingDetail.getSelectedOrganizationCode());

        if (orgName == null) {
            orgPositionFundingDetailReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgPositionFundingDetailReportEntry.setOrganizationName(orgName);
        }

        orgPositionFundingDetailReportEntry.setOrgChartOfAccountsCode(positionFundingDetail.getSelectedOrganizationChartOfAccountsCode());
        if (chartDesc == null) {
            orgPositionFundingDetailReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgPositionFundingDetailReportEntry.setOrgChartOfAccountDescription(chartDesc);
        }
        orgPositionFundingDetailReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgPositionFundingDetailReportEntry.setFinancialObjectCode(positionFundingDetail.getFinancialObjectCode());
        orgPositionFundingDetailReportEntry.setObjectCodes(objectCodes);
    }

    /**
     * builds report body
     * 
     * @param universityFiscalYear
     * @param orgPositionFundingDetailReportEntry
     * @param positionFundingDetailEntry
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgPositionFundingDetailReport detailReportEntry, BudgetConstructionPositionFunding positionFundingDetailEntry) {
        PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = positionFundingDetailEntry.getPendingAppointmentFunding();
        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(appointmentFundingEntry);
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
        detailReportEntry.setChartOfAccountsCode(positionFundingDetailEntry.getChartOfAccountsCode());
        detailReportEntry.setAccountNumber(positionFundingDetailEntry.getAccountNumber());
        detailReportEntry.setSubAccountNumber(positionFundingDetailEntry.getSubAccountNumber());
        detailReportEntry.setFinancialSubObjectCode(positionFundingDetailEntry.getFinancialSubObjectCode());
        if (positionFundingDetailEntry.getName() != null) {
            int nameLength = positionFundingDetailEntry.getName().length();
            detailReportEntry.setName(positionFundingDetailEntry.getName().substring(0, (nameLength > 35) ? 35 : nameLength));
            if (budgetConstructionIntendedIncumbent != null) {
                if (budgetConstructionIntendedIncumbent.getIuClassificationLevel() == null) {
                    detailReportEntry.setCls(BCConstants.Report.UNDF);
                }
                else {
                    detailReportEntry.setCls(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
                }
            }
        }
        else {
            detailReportEntry.setName(BCConstants.Report.VACANT);
            detailReportEntry.setCls(BCConstants.Report.BLANK);
        }

        if (budgetConstructionAdministrativePost != null) {
            detailReportEntry.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }
        if (budgetConstructionPosition != null) {
            detailReportEntry.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            detailReportEntry.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            detailReportEntry.setPositionFte(BudgetConstructionReportHelper.setDecimalDigit(budgetConstructionPosition.getPositionFullTimeEquivalency(), 2, false));
            detailReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            detailReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
            detailReportEntry.setPositionStandardHoursDefault(budgetConstructionPosition.getPositionStandardHoursDefault());
        }

        BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFundingEntry.getEffectiveCSFTracker();
        detailReportEntry.setAmountChange(new Integer(0));
        detailReportEntry.setPercentChange(BigDecimal.ZERO);
        if (csfTracker != null) {
            detailReportEntry.setCsfFundingStatusCode(csfTracker.getCsfFundingStatusCode());
            detailReportEntry.setCsfTimePercent(BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfTimePercent(), 2, false));
            detailReportEntry.setCsfAmount(new Integer(csfTracker.getCsfAmount().intValue()));
            detailReportEntry.setCsfFullTimeEmploymentQuantity(BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfFullTimeEmploymentQuantity(), 5, false));

            // calculate amountChange and percentChange
            Integer amountChange = new Integer(0);
            BigDecimal percentChange = BigDecimal.ZERO;
            BigDecimal csfFte = BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfFullTimeEmploymentQuantity(), 5, false);
            BigDecimal reqFte = BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedFteQuantity(), 5, false);
            if (reqFte.compareTo(csfFte) == 0) {
                amountChange = appointmentFundingEntry.getAppointmentRequestedAmount().subtract(csfTracker.getCsfAmount()).intValue();
                percentChange = BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange.intValue()), csfTracker.getCsfAmount().bigDecimalValue());
            }
            detailReportEntry.setAmountChange(amountChange);
            detailReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange.intValue()), csfTracker.getCsfAmount().bigDecimalValue()));
        }

        if (appointmentFundingEntry != null) {
            detailReportEntry.setFinancialSubObjectCode(appointmentFundingEntry.getFinancialSubObjectCode());

            detailReportEntry.setAppointmentFundingMonth(appointmentFundingEntry.getAppointmentFundingMonth());
            detailReportEntry.setAppointmentRequestedAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue()));
            detailReportEntry.setAppointmentRequestedTimePercent(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedTimePercent(), 2, false));
            detailReportEntry.setAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedFteQuantity(), 5, false));
            if (salarySettingService.isHourlyPaidObject(appointmentFundingEntry.getUniversityFiscalYear(), appointmentFundingEntry.getChartOfAccountsCode(), appointmentFundingEntry.getFinancialObjectCode())){
                detailReportEntry.setAppointmentRequestedPayRate(appointmentFundingEntry.getAppointmentRequestedPayRate());
            }

            detailReportEntry.setAppointmentFundingDurationCode(appointmentFundingEntry.getAppointmentFundingDurationCode());
            detailReportEntry.setAppointmentRequestedCsfAmount(BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentRequestedCsfAmount()));
            detailReportEntry.setAppointmentRequestedCsfTimePercent(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent(), 2, false));
            detailReportEntry.setAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentRequestedCsfFteQuantity(), 5, false));

            detailReportEntry.setAppointmentTotalIntendedAmount(BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentTotalIntendedAmount()));
            detailReportEntry.setAppointmentTotalIntendedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity(), 5, false));

            detailReportEntry.setEmplid(appointmentFundingEntry.getEmplid());
        }

        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            detailReportEntry.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            detailReportEntry.setDeleteBox(BCConstants.Report.BLANK);
        }
    }

    /**
     * builds report total
     * 
     * @param orgPositionFundingDetailReportEntry
     * @param positionFundingDetail
     * @param fundingDetailTotalPerson
     * @param fundingDetailTotalOrg
     */
    public void buildReportsTotal(BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry, BudgetConstructionPositionFunding positionFundingDetail, Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalPerson, Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalOrg) {
        // set person part of total
        for (BudgetConstructionOrgPositionFundingDetailReportTotal fundingDetailTotalPersonEntry : fundingDetailTotalPerson) {

            if (BudgetConstructionReportHelper.isSameEntry(fundingDetailTotalPersonEntry.getBudgetConstructionPositionFunding(), positionFundingDetail, fieldsForPerson())) {
                orgPositionFundingDetailReportEntry.setTotalPersonPositionCsfAmount(fundingDetailTotalPersonEntry.getTotalPersonPositionCsfAmount());
                orgPositionFundingDetailReportEntry.setTotalPersonAppointmentRequestedAmount(fundingDetailTotalPersonEntry.getTotalPersonAppointmentRequestedAmount());
                orgPositionFundingDetailReportEntry.setTotalPersonPositionCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalPersonEntry.getTotalPersonPositionCsfFteQuantity(), 5, false));
                orgPositionFundingDetailReportEntry.setTotalPersonAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalPersonEntry.getTotalPersonAppointmentRequestedFteQuantity(), 5, false));

                // calculate amountChange and percentChange
                orgPositionFundingDetailReportEntry.setTotalPersonAmountChange(new Integer(0));
                orgPositionFundingDetailReportEntry.setTotalPersonPercentChange(BigDecimal.ZERO);
                BigDecimal csfFte = BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalPersonEntry.getTotalPersonPositionCsfFteQuantity(), 5, false);
                BigDecimal reqFte = BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalPersonEntry.getTotalPersonAppointmentRequestedFteQuantity(), 5, false);
                if (csfFte.compareTo(reqFte) == 0) {
                    Integer amountChange = fundingDetailTotalPersonEntry.getTotalPersonAppointmentRequestedAmount() - fundingDetailTotalPersonEntry.getTotalPersonPositionCsfAmount();
                    BigDecimal percentChange = BigDecimal.ZERO;
                    orgPositionFundingDetailReportEntry.setTotalPersonAmountChange(amountChange);
                    if (!fundingDetailTotalPersonEntry.getTotalPersonPositionCsfAmount().equals(new Integer(0))) {
                        percentChange = BudgetConstructionReportHelper.calculatePercent(amountChange, fundingDetailTotalPersonEntry.getTotalPersonPositionCsfAmount().intValue());
                    }
                    orgPositionFundingDetailReportEntry.setTotalPersonPercentChange(percentChange);
                }

                orgPositionFundingDetailReportEntry.setPersonSortCode(fundingDetailTotalPersonEntry.getPersonSortCode());
            }
        }
        // set org part of total
        for (BudgetConstructionOrgPositionFundingDetailReportTotal fundingDetailTotalOrgEntry : fundingDetailTotalOrg) {
            if (BudgetConstructionReportHelper.isSameEntry(fundingDetailTotalOrgEntry.getBudgetConstructionPositionFunding(), positionFundingDetail, fieldsForOrg())) {
                orgPositionFundingDetailReportEntry.setTotalOrgPositionCsfAmount(fundingDetailTotalOrgEntry.getTotalOrgPositionCsfAmount());
                orgPositionFundingDetailReportEntry.setTotalOrgAppointmentRequestedAmount(fundingDetailTotalOrgEntry.getTotalOrgAppointmentRequestedAmount());
                orgPositionFundingDetailReportEntry.setTotalOrgPositionCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalOrgEntry.getTotalOrgPositionCsfFteQuantity(), 5, false));
                orgPositionFundingDetailReportEntry.setTotalOrgAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(fundingDetailTotalOrgEntry.getTotalOrgAppointmentRequestedFteQuantity(), 5, false));
                Integer amountChange = fundingDetailTotalOrgEntry.getTotalOrgAppointmentRequestedAmount() - fundingDetailTotalOrgEntry.getTotalOrgPositionCsfAmount();
                orgPositionFundingDetailReportEntry.setTotalOrgAmountChange(amountChange);
                orgPositionFundingDetailReportEntry.setTotalOrgPercentChange(BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange.intValue()), new BigDecimal(fundingDetailTotalOrgEntry.getTotalOrgPositionCsfAmount().intValue())));
                if (orgPositionFundingDetailReportEntry.getPersonSortCode() == null) {
                    orgPositionFundingDetailReportEntry.setPersonSortCode(fundingDetailTotalOrgEntry.getPersonSortCode());
                }
            }
        }
    }

    /**
     * calculates total part of person
     * 
     * @param positionFundingDetailList
     * @param listForCalculateTotalPerson
     * @return
     */
    protected Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> calculatePersonTotal(Collection<BudgetConstructionPositionFunding> positionFundingDetailList, List<BudgetConstructionPositionFunding> listForCalculateTotalPerson) {
        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> returnCollection = new ArrayList();
        Integer totalPersonPositionCsfAmount = new Integer(0);
        Integer totalPersonAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalPersonPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalPersonAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer personSortCode = new Integer(0);
        PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding = null;
        for (BudgetConstructionPositionFunding budgetConstructionPositionFunding : listForCalculateTotalPerson) {
            for (BudgetConstructionPositionFunding positionFundingEntry : positionFundingDetailList) {
                if (BudgetConstructionReportHelper.isSameEntry(budgetConstructionPositionFunding, positionFundingEntry, fieldsForPerson())) {
                    pendingAppointmentFunding = positionFundingEntry.getPendingAppointmentFunding();
                    if (pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
                        BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().get(0);
                        totalPersonPositionCsfAmount = totalPersonPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
                        totalPersonPositionCsfFteQuantity = totalPersonPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
                    }
                    if (pendingAppointmentFunding != null) {
                        totalPersonAppointmentRequestedAmount = totalPersonAppointmentRequestedAmount + new Integer(pendingAppointmentFunding.getAppointmentRequestedAmount().intValue());
                        totalPersonAppointmentRequestedFteQuantity = totalPersonAppointmentRequestedFteQuantity.add(pendingAppointmentFunding.getAppointmentRequestedFteQuantity());
                    }
                    // sort code for person - display total part of person, when person have more than one info
                    personSortCode += 1;
                }
            }
            BudgetConstructionOrgPositionFundingDetailReportTotal budgetConstructionOrgPositionFundingDetailReportTotal = new BudgetConstructionOrgPositionFundingDetailReportTotal();
            budgetConstructionOrgPositionFundingDetailReportTotal.setBudgetConstructionPositionFunding(budgetConstructionPositionFunding);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonPositionCsfAmount(totalPersonPositionCsfAmount);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonPositionCsfFteQuantity(totalPersonPositionCsfFteQuantity);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonAppointmentRequestedAmount(totalPersonAppointmentRequestedAmount);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonAppointmentRequestedFteQuantity(totalPersonAppointmentRequestedFteQuantity);
            if (personSortCode.intValue() > 1) {
                budgetConstructionOrgPositionFundingDetailReportTotal.setPersonSortCode(new Integer(1));
            }
            returnCollection.add(budgetConstructionOrgPositionFundingDetailReportTotal);
            // set all values to zero, after the entry was added to collection
            totalPersonPositionCsfAmount = new Integer(0);
            totalPersonAppointmentRequestedAmount = new Integer(0);
            totalPersonPositionCsfFteQuantity = BigDecimal.ZERO;
            totalPersonAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            personSortCode = new Integer(0);
        }
        return returnCollection;
    }

    /**
     * calculates total part of org
     * 
     * @param positionFundingDetailList
     * @param listForCalculateTotalOrg
     * @return
     */
    protected Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> calculateOrgTotal(Collection<BudgetConstructionPositionFunding> positionFundingDetailList, List<BudgetConstructionPositionFunding> listForCalculateTotalOrg) {
        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> returnCollection = new ArrayList();
        Integer totalOrgPositionCsfAmount = new Integer(0);
        Integer totalOrgAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalOrgPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalOrgAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding = null;
        for (BudgetConstructionPositionFunding budgetConstructionPositionFunding : listForCalculateTotalOrg) {
            for (BudgetConstructionPositionFunding positionFundingEntry : positionFundingDetailList) {
                if (BudgetConstructionReportHelper.isSameEntry(budgetConstructionPositionFunding, positionFundingEntry, fieldsForOrg())) {
                    pendingAppointmentFunding = positionFundingEntry.getPendingAppointmentFunding();
                    if (pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
                        BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().get(0);
                        totalOrgPositionCsfAmount = totalOrgPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
                        totalOrgPositionCsfFteQuantity = totalOrgPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
                    }
                    if (pendingAppointmentFunding != null) {
                        totalOrgAppointmentRequestedAmount = totalOrgAppointmentRequestedAmount + new Integer(pendingAppointmentFunding.getAppointmentRequestedAmount().intValue());
                        totalOrgAppointmentRequestedFteQuantity = totalOrgAppointmentRequestedFteQuantity.add(pendingAppointmentFunding.getAppointmentRequestedFteQuantity());
                    }
                }
            }
            BudgetConstructionOrgPositionFundingDetailReportTotal budgetConstructionOrgOrgFundingDetailReportTotal = new BudgetConstructionOrgPositionFundingDetailReportTotal();
            budgetConstructionOrgOrgFundingDetailReportTotal.setBudgetConstructionPositionFunding(budgetConstructionPositionFunding);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgPositionCsfAmount(totalOrgPositionCsfAmount);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgPositionCsfFteQuantity(totalOrgPositionCsfFteQuantity);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgAppointmentRequestedAmount(totalOrgAppointmentRequestedAmount);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgAppointmentRequestedFteQuantity(totalOrgAppointmentRequestedFteQuantity);
            returnCollection.add(budgetConstructionOrgOrgFundingDetailReportTotal);
            // set all values to zero, after the entry was added to collection
            totalOrgPositionCsfAmount = new Integer(0);
            totalOrgAppointmentRequestedAmount = new Integer(0);
            totalOrgPositionCsfFteQuantity = BigDecimal.ZERO;
            totalOrgAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        }
        return returnCollection;
    }

    /**
     * builds orderByList for sort order.
     * 
     * @return List<String> returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.PERSON_NAME);
        returnList.add(KFSPropertyConstants.EMPLID);
        returnList.add(KFSPropertyConstants.POSITION_NUMBER);
        returnList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }

    /**
     * builds list of fields for comparing entry of person total
     * 
     * @return List<String>
     */
    protected List<String> fieldsForPerson() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForOrg());
        fieldList.add(KFSPropertyConstants.EMPLID);
        return fieldList;
    }

    /**
     * builds list of fields for comparing entry of org total
     * 
     * @return List<String>
     */
    protected List<String> fieldsForOrg() {
        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CODE);
        return fieldList;
    }

    public void setBudgetConstructionPositionFundingDetailReportDao(BudgetConstructionPositionFundingDetailReportDao budgetConstructionPositionFundingDetailReportDao) {
        this.budgetConstructionPositionFundingDetailReportDao = budgetConstructionPositionFundingDetailReportDao;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

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
