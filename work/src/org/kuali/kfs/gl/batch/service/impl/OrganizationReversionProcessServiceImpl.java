/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.service.OrgReversionUnitOfWorkService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.module.gl.service.OrganizationReversionProcessService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.impl.orgreversion.OrganizationReversionProcess;
import org.kuali.module.gl.util.Summary;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of OrganizationReversionProcessService
 */
@Transactional
public class OrganizationReversionProcessServiceImpl implements OrganizationReversionProcessService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionProcessServiceImpl.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private OrganizationReversionService organizationReversionService;
    private ParameterService parameterService;
    private BalanceService balanceService;
    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;
    private PersistenceService persistenceService;
    private DateTimeService dateTimeService;
    private OrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic;
    private PriorYearAccountService priorYearAccountService;
    private ReportService reportService;
    private OrgReversionUnitOfWorkService orgReversionUnitOfWorkService;

    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public void setCashOrganizationReversionCategoryLogic(OrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic) {
        this.cashOrganizationReversionCategoryLogic = cashOrganizationReversionCategoryLogic;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setOrganizationReversionService(OrganizationReversionService organizationReversionService) {
        this.organizationReversionService = organizationReversionService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setPriorYearAccountService(PriorYearAccountService pyas) {
        priorYearAccountService = pyas;
    }

    /**
     * Sets the reportService attribute value.
     * 
     * @param reportService The reportService to set.
     */
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Sets the orgReversionUnitOfWorkService attribute.
     * 
     * @param orgReversionUnitOfWorkService the service to set.
     */
    public void setOrgReversionUnitOfWorkService(OrgReversionUnitOfWorkService orgReversionUnitOfWorkService) {
        this.orgReversionUnitOfWorkService = orgReversionUnitOfWorkService;
    }

    /**
     * Runs the Organization Reversion Year End Process for the end of a fiscal year (ie, a process that
     * runs before the fiscal year end, and thus uses current account, etc.)
     * 
     * @param outputGroup the origin entry group that this process should save entries to
     * @param jobParameters the parameters used in the process
     * @param organizationReversionCounts a Map of named statistics generated by running the process
     * @see org.kuali.module.gl.service.OrganizationReversionProcessService#organizationReversionProcessEndOfYear(org.kuali.module.gl.bo.OriginEntryGroup, java.util.Map, java.util.Map)
     */
    public void organizationReversionProcessEndOfYear(OriginEntryGroup outputGroup, Map jobParameters, Map<String, Integer> organizationReversionCounts) {
        LOG.debug("organizationReversionProcessEndOfYear() started");

        OrganizationReversionProcess orp = new OrganizationReversionProcess(outputGroup, true, organizationReversionService, balanceService, originEntryGroupService, originEntryService, persistenceService, dateTimeService, cashOrganizationReversionCategoryLogic, priorYearAccountService, orgReversionUnitOfWorkService, jobParameters, organizationReversionCounts);

        orp.organizationReversionProcess();
    }

    /**
     * Organization Reversion Year End Process for the beginning of a fiscal year (ie, the process as it runs
     * after the fiscal year end, thus using prior year account, etc.)
     * 
     * @param outputGroup the origin entry group that this process should save entries to
     * @param jobParameters the parameters used in the process
     * @param organizationReversionCounts a Map of named statistics generated by running the process
     * @see org.kuali.module.gl.service.OrganizationReversionProcessService#organizationReversionProcessBeginningOfYear(org.kuali.module.gl.bo.OriginEntryGroup, java.util.Map, java.util.Map)
     */
    public void organizationReversionProcessBeginningOfYear(OriginEntryGroup outputGroup, Map jobParameters, Map<String, Integer> organizationReversionCounts) {
        LOG.debug("organizationReversionProcessEndOfYear() started");

        OrganizationReversionProcess orp = new OrganizationReversionProcess(outputGroup, false, organizationReversionService, balanceService, originEntryGroupService, originEntryService, persistenceService, dateTimeService, cashOrganizationReversionCategoryLogic, priorYearAccountService, orgReversionUnitOfWorkService, jobParameters, organizationReversionCounts);

        orp.organizationReversionProcess();
    }

    /**
     * Returns a Map with the properly initialized parameters for an organization reversion job that is about to run
     * @return a Map holding parameters for the job
     * @see org.kuali.module.gl.service.OrganizationReversionProcessService#getJobParameters()
     */
    public Map getJobParameters() {
        // Get job parameters
        Map jobParameters = new HashMap();
        String strTransactionDate = parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM);
        jobParameters.put(KFSConstants.UNALLOC_OBJECT_CD, parameterService.getParameterValue(OrganizationReversion.class, GLConstants.OrganizationReversionProcess.UNALLOC_OBJECT_CODE_PARM));
        jobParameters.put(KFSConstants.BEG_BUD_CASH_OBJECT_CD, parameterService.getParameterValue(OrganizationReversion.class, GLConstants.OrganizationReversionProcess.CARRY_FORWARD_OBJECT_CODE));
        jobParameters.put(KFSConstants.FUND_BAL_OBJECT_CD, parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_CODE_PARM));
        String strUniversityFiscalYear = parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            java.util.Date jud = sdf.parse(strTransactionDate);
            jobParameters.put(KFSConstants.TRANSACTION_DT, new java.sql.Date(jud.getTime()));
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("TRANSACTION_DT is an invalid date");
        }
        try {
            jobParameters.put(KFSConstants.UNIV_FISCAL_YR, new Integer(strUniversityFiscalYear));
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("UNIV_FISCAL_YR is an invalid year");
        }
        return jobParameters;
    }

    /**
     * Generates the reports for the organization reversion process
     * @param outputGroup the origin entry group holding the origin entries created by the run
     * @param jobParameters the parameters used during the run
     * @param organizationReversionCounts the statistical counts generated by the run of the organization reversion process
     * @see org.kuali.module.gl.service.OrganizationReversionProcessService#generateOrganizationReversionProcessReports()
     */
    public void generateOrganizationReversionProcessReports(OriginEntryGroup outputGroup, Map jobParameters, Map<String, Integer> organizationReversionCounts) {
        // prepare statistics for report
        SimpleDateFormat reportDateFormat = new SimpleDateFormat(DATE_FORMAT);
        jobParameters.put(KFSConstants.TRANSACTION_DT, reportDateFormat.format(jobParameters.get(KFSConstants.TRANSACTION_DT)));

        Summary totalRecordCountSummary = new Summary();
        totalRecordCountSummary.setSortOrder(Summary.TOTAL_RECORD_COUNT_SUMMARY_SORT_ORDER);
        totalRecordCountSummary.setDescription("NUMBER OF GLBL RECORDS READ....:");
        totalRecordCountSummary.setCount(organizationReversionCounts.get("balancesRead"));

        Summary selectedRecordCountSummary = new Summary();
        selectedRecordCountSummary.setSortOrder(Summary.SELECTED_RECORD_COUNT_SUMMARY_SORT_ORDER);
        selectedRecordCountSummary.setDescription("NUMBER OF GLBL RECORDS SELECTED:");
        selectedRecordCountSummary.setCount(organizationReversionCounts.get("balancesSelected"));

        Summary sequenceRecordsWrittenSummary = new Summary();
        sequenceRecordsWrittenSummary.setSortOrder(Summary.SEQUENCE_RECORDS_WRITTEN_SUMMARY_SORT_ORDER);
        sequenceRecordsWrittenSummary.setDescription("NUMBER OF SEQ RECORDS WRITTEN..:");
        sequenceRecordsWrittenSummary.setCount(organizationReversionCounts.get("recordsWritten"));

        List<Summary> summaries = new ArrayList<Summary>();
        summaries.add(totalRecordCountSummary);
        summaries.add(selectedRecordCountSummary);
        summaries.add(sequenceRecordsWrittenSummary);

        Date runDate = new Date(dateTimeService.getCurrentDate().getTime());
        reportService.generateOrgReversionStatisticsReport(jobParameters, summaries, runDate, outputGroup);

    }

    /**
     * Creates a properly initialized group for the organization reversion process to put origin entries in
     * @return OriginEntryGroup a properly initialized origin entry group
     * @see org.kuali.module.gl.service.OrganizationReversionProcessService#createOrganizationReversionProcessOriginEntryGroup()
     */
    public OriginEntryGroup createOrganizationReversionProcessOriginEntryGroup() {
        java.util.Date runDate = dateTimeService.getCurrentDate();
        // Create output group
        return originEntryGroupService.createGroup(new java.sql.Date(runDate.getTime()), OriginEntrySource.YEAR_END_ORG_REVERSION, true, false, true);
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
