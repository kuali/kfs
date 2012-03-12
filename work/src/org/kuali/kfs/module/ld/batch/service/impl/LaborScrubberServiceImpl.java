/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch.service.impl;

import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.batch.service.LaborScrubberService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of LaborScrubberService.
 */
@Transactional
public class LaborScrubberServiceImpl implements LaborScrubberService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberServiceImpl.class);

    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private OffsetDefinitionService offsetDefinitionService;
    private ObjectCodeService objectCodeService;
    private ConfigurationService kualiConfigurationService;
    private UniversityDateDao universityDateDao;
    private PersistenceService persistenceService;
    private ScrubberValidator scrubberValidator;
    private LaborAccountingCycleCachingService laborAccountingCycleCachingService;
    private PreScrubberService laborPreScrubberService;
    private DocumentNumberAwareReportWriterService llcpPreScrubberReportWriterService;

    private DocumentNumberAwareReportWriterService laborMainReportWriterService;
    private DocumentNumberAwareReportWriterService llcpMainReportWriterService;
    private DocumentNumberAwareReportWriterService laborLedgerReportWriterService;
    private DocumentNumberAwareReportWriterService llcpLedgerReportWriterService;
    private ReportWriterService laborBadBalanceTypeReportWriterService;
    private ReportWriterService laborErrorListingReportWriterService;
    private DocumentNumberAwareReportWriterService laborGeneratedTransactionsReportWriterService;
    private ReportWriterService laborDemergerReportWriterService;
    private ParameterService parameterService;

    private String batchFileDirectoryName;

    /**
     * @see org.kuali.module.labor.service.ScrubberService#scrubGroupReportOnly(org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    synchronized public void scrubGroupReportOnly(String fileName, String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

        LaborScrubberProcess sp = new LaborScrubberProcess(flexibleOffsetAccountService, laborAccountingCycleCachingService, laborOriginEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, scrubberValidator, batchFileDirectoryName, 
                llcpMainReportWriterService, llcpLedgerReportWriterService, laborBadBalanceTypeReportWriterService, laborErrorListingReportWriterService, laborGeneratedTransactionsReportWriterService, laborDemergerReportWriterService, laborPreScrubberService, llcpPreScrubberReportWriterService, parameterService);
        sp.scrubGroupReportOnly(fileName, documentNumber);
    } 

    /**
     * @see org.kuali.module.labor.service.ScrubberService#scrubEntries()
     */
    public void scrubEntries() {
        LOG.debug("scrubEntries() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

        LaborScrubberProcess sp = new LaborScrubberProcess(flexibleOffsetAccountService, laborAccountingCycleCachingService, laborOriginEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, scrubberValidator, batchFileDirectoryName, 
                laborMainReportWriterService, laborLedgerReportWriterService, laborBadBalanceTypeReportWriterService, laborErrorListingReportWriterService, laborGeneratedTransactionsReportWriterService, laborDemergerReportWriterService, null, null, parameterService);
        sp.scrubEntries();
    }
    
    public void performDemerger() {
        LOG.debug("performDemerger() started");
        LaborScrubberProcess sp = new LaborScrubberProcess(flexibleOffsetAccountService, laborAccountingCycleCachingService, laborOriginEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, scrubberValidator, batchFileDirectoryName, 
                laborMainReportWriterService, laborLedgerReportWriterService, laborBadBalanceTypeReportWriterService, laborErrorListingReportWriterService, laborGeneratedTransactionsReportWriterService, laborDemergerReportWriterService, null, null, parameterService);
        sp.performDemerger();
                
    }

    /**
     * Sets the setScrubberValidator attribute value.
     * 
     * @param sv The setScrubberValidator to set.
     */
    public void setScrubberValidator(ScrubberValidator sv) {
        scrubberValidator = sv;
    }

    /**
     * Sets the laborOriginEntryService attribute value.
     * 
     * @param loes The laborOriginEntryService to set.
     */
    public void setLaborOriginEntryService(LaborOriginEntryService loes) {
        this.laborOriginEntryService = loes;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param groupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService groupService) {
        this.originEntryGroupService = groupService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dts The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }

    /**
     * Sets the universityDateDao attribute value.
     * 
     * @param universityDateDao The universityDateDao to set.
     */
    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param ps The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService ps) {
        persistenceService = ps;
    }

    /**
     * Sets the offsetDefinitionService attribute value.
     * 
     * @param offsetDefinitionService The offsetDefinitionService to set.
     */
    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    /**
     * Sets the objectCodeService attribute value.
     * 
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    /**
     * Sets the flexibleOffsetAccountService attribute value.
     * @param flexibleOffsetAccountService The flexibleOffsetAccountService to set.
     */
    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }


    public void setLaborAccountingCycleCachingService(LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        this.laborAccountingCycleCachingService = laborAccountingCycleCachingService;
    }

    /**
     * @param laborMainReportWriterService The laborMainReportWriterService to set.
     */
    public void setLaborMainReportWriterService(DocumentNumberAwareReportWriterService laborMainReportWriterService) {
        this.laborMainReportWriterService = laborMainReportWriterService;
    }

    /**
     * @param laborLedgerReportWriterService The laborLedgerReportWriterService to set.
     */
    public void setLaborLedgerReportWriterService(DocumentNumberAwareReportWriterService laborLedgerReportWriterService) {
        this.laborLedgerReportWriterService = laborLedgerReportWriterService;
    }

    /**
     * @param laborBadBalanceTypeReportWriterService The laborBadBalanceTypeReportWriterService to set.
     */
    public void setLaborBadBalanceTypeReportWriterService(ReportWriterService laborBadBalanceTypeReportWriterService) {
        this.laborBadBalanceTypeReportWriterService = laborBadBalanceTypeReportWriterService;
    }

    /**
     * @param laborErrorListingReportWriterService The laborErrorListingReportWriterService to set.
     */
    public void setLaborErrorListingReportWriterService(ReportWriterService laborErrorListingReportWriterService) {
        this.laborErrorListingReportWriterService = laborErrorListingReportWriterService;
    }

    /**
     * @param laborGeneratedTransactionsReportWriterService The laborGeneratedTransactionsReportWriterService to set.
     */
    public void setLaborGeneratedTransactionsReportWriterService(DocumentNumberAwareReportWriterService laborGeneratedTransactionsReportWriterService) {
        this.laborGeneratedTransactionsReportWriterService = laborGeneratedTransactionsReportWriterService;
    }

    /**
     * @param laborDemergerReportWriterService The laborDemergerReportWriterService to set.
     */
    public void setLaborDemergerReportWriterService(ReportWriterService laborDemergerReportWriterService) {
        this.laborDemergerReportWriterService = laborDemergerReportWriterService;
    }

    /**
     * Sets the llcpMainReportWriterService attribute value.
     * @param llcpMainReportWriterService The llcpMainReportWriterService to set.
     */
    public void setLlcpMainReportWriterService(DocumentNumberAwareReportWriterService llcpMainReportWriterService) {
        this.llcpMainReportWriterService = llcpMainReportWriterService;
    }

    /**
     * Sets the llcpLedgerReportWriterService attribute value.
     * @param llcpLedgerReportWriterService The llcpLedgerReportWriterService to set.
     */
    public void setLlcpLedgerReportWriterService(DocumentNumberAwareReportWriterService llcpLedgerReportWriterService) {
        this.llcpLedgerReportWriterService = llcpLedgerReportWriterService;
    }

    /**
     * Gets the laborPreScrubberService attribute. 
     * @return Returns the laborPreScrubberService.
     */
    public PreScrubberService getLaborPreScrubberService() {
        return laborPreScrubberService;
    }

    /**
     * Sets the laborPreScrubberService attribute value.
     * @param laborPreScrubberService The laborPreScrubberService to set.
     */
    public void setLaborPreScrubberService(PreScrubberService laborPreScrubberService) {
        this.laborPreScrubberService = laborPreScrubberService;
    }

    /**
     * Sets the laborPreScrubberAwareReportWriterService attribute value.
     * @param laborPreScrubberAwareReportWriterService The laborPreScrubberAwareReportWriterService to set.
     */
    public void setLlcpPreScrubberReportWriterService(DocumentNumberAwareReportWriterService llcpPreScrubberReportWriterService) {
        this.llcpPreScrubberReportWriterService = llcpPreScrubberReportWriterService;
    }
    
    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
