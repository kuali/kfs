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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.OrganizationReversionProcess;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;
import org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService;
import org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.report.Summary;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
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
    private ReportWriterService organizationReversionReportWriterService;
    private OrganizationReversionUnitOfWorkService orgReversionUnitOfWorkService;
    private KualiConfigurationService configurationService;
    private String batchFileDirectoryName;

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
     * Gets the organizationReversionReportWriterService attribute. 
     * @return Returns the organizationReversionReportWriterService.
     */
    public ReportWriterService getOrganizationReversionReportWriterService() {
        return organizationReversionReportWriterService;
    }

    /**
     * Sets the organizationReversionReportWriterService attribute value.
     * @param organizationReversionReportWriterService The organizationReversionReportWriterService to set.
     */
    public void setOrganizationReversionReportWriterService(ReportWriterService organizationReversionReportWriterService) {
        this.organizationReversionReportWriterService = organizationReversionReportWriterService;
    }

    /**
     * Sets the orgReversionUnitOfWorkService attribute.
     * 
     * @param orgReversionUnitOfWorkService the service to set.
     */
    public void setOrgReversionUnitOfWorkService(OrganizationReversionUnitOfWorkService orgReversionUnitOfWorkService) {
        this.orgReversionUnitOfWorkService = orgReversionUnitOfWorkService;
    }

    /**
     * Runs the Organization Reversion Year End Process for the end of a fiscal year (ie, a process that
     * runs before the fiscal year end, and thus uses current account, etc.)
     * 
     * @param outputGroup the origin entry group that this process should save entries to
     * @param jobParameters the parameters used in the process
     * @param organizationReversionCounts a Map of named statistics generated by running the process
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService#organizationReversionProcessEndOfYear(org.kuali.kfs.gl.businessobject.OriginEntryGroup, java.util.Map, java.util.Map)
     */
    public void organizationReversionProcessEndOfYear(String organizationReversionClosingFileName, Map jobParameters, Map<String, Integer> organizationReversionCounts) {
        LOG.debug("organizationReversionProcessEndOfYear() started");
        String outputFileName = batchFileDirectoryName + File.separator + organizationReversionClosingFileName;
        OrganizationReversionProcess orp = new OrganizationReversionProcess(outputFileName, true, organizationReversionService, balanceService, originEntryGroupService, originEntryService, persistenceService, dateTimeService, cashOrganizationReversionCategoryLogic, priorYearAccountService, orgReversionUnitOfWorkService, jobParameters, organizationReversionCounts);

        orp.organizationReversionProcess();
        
        writeReports(orp, jobParameters, organizationReversionCounts);
    }

    /**
     * Organization Reversion Year End Process for the beginning of a fiscal year (ie, the process as it runs
     * after the fiscal year end, thus using prior year account, etc.)
     * 
     * @param outputGroup the origin entry group that this process should save entries to
     * @param jobParameters the parameters used in the process
     * @param organizationReversionCounts a Map of named statistics generated by running the process
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService#organizationReversionProcessBeginningOfYear(org.kuali.kfs.gl.businessobject.OriginEntryGroup, java.util.Map, java.util.Map)
     */
    public void organizationReversionProcessBeginningOfYear(String organizationReversionPreClosingFileName, Map jobParameters, Map<String, Integer> organizationReversionCounts) {
        LOG.debug("organizationReversionProcessEndOfYear() started");
        String outputFileName = batchFileDirectoryName + File.separator + organizationReversionPreClosingFileName;
        OrganizationReversionProcess orp = new OrganizationReversionProcess(outputFileName, false, organizationReversionService, balanceService, originEntryGroupService, originEntryService, persistenceService, dateTimeService, cashOrganizationReversionCategoryLogic, priorYearAccountService, orgReversionUnitOfWorkService, jobParameters, organizationReversionCounts);

        orp.organizationReversionProcess();
        
        writeReports(orp, jobParameters, organizationReversionCounts);
    }

    /**
     * Returns a Map with the properly initialized parameters for an organization reversion job that is about to run
     * @return a Map holding parameters for the job
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService#getJobParameters()
     */
    public Map getJobParameters() {
        // Get job parameters
        Map jobParameters = new HashMap();
        String strTransactionDate = parameterService.getParameterValue(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM);
        jobParameters.put(KFSConstants.UNALLOC_OBJECT_CD, parameterService.getParameterValue(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.UNALLOC_OBJECT_CODE_PARM));
        jobParameters.put(KFSConstants.BEG_BUD_CASH_OBJECT_CD, parameterService.getParameterValue(OrganizationReversion.class, GeneralLedgerConstants.OrganizationReversionProcess.CARRY_FORWARD_OBJECT_CODE));
        jobParameters.put(KFSConstants.FUND_BAL_OBJECT_CD, parameterService.getParameterValue(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_CODE_PARM));
        String strUniversityFiscalYear = parameterService.getParameterValue(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM);

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
     * 
     * @param organizationReversionProcess
     * @param jobParameters
     * @param counts
     */
    public void writeReports(OrganizationReversionProcess organizationReversionProcess, Map jobParameters, Map<String, Integer> counts) {
        // write job parameters
        for (Object jobParameterKeyAsObject : jobParameters.keySet()) {
            if (jobParameterKeyAsObject != null) {
                final String jobParameterKey = jobParameterKeyAsObject.toString();
                getOrganizationReversionReportWriterService().writeParameterLine("%32s %10s", jobParameterKey, jobParameters.get(jobParameterKey));
            }
        }
        
        // write statistics
        getOrganizationReversionReportWriterService().writeStatisticLine("NUMBER OF GLBL RECORDS READ....: %10d", counts.get("balancesRead"));
        getOrganizationReversionReportWriterService().writeStatisticLine("NUMBER OF GLBL RECORDS SELECTED: %10d", counts.get("balancesSelected"));
        getOrganizationReversionReportWriterService().writeStatisticLine("NUMBER OF SEQ RECORDS WRITTEN..: %10d", counts.get("recordsWritten"));
        getOrganizationReversionReportWriterService().pageBreak();
        
        // write ledger report
        getOrganizationReversionReportWriterService().writeSubTitle(getConfigurationService().getPropertyString(KFSKeyConstants.MESSAGE_REPORT_YEAR_END_ORGANIZATION_REVERSION_LEDGER_TITLE_LINE));
        organizationReversionProcess.writeLedgerSummaryReport(getOrganizationReversionReportWriterService());
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    /**
     * Gets the configurationService attribute. 
     * @return Returns the configurationService.
     */
    public KualiConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Sets the configurationService attribute value.
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
