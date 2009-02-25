/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.service.OriginEntryLookupService;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.batch.service.ScrubberProcessObjectCodeOverride;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.ScrubberProcess;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryLiteService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of ScrubberService
 */
@Transactional
public class ScrubberServiceImpl implements ScrubberService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceImpl.class);

    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private OffsetDefinitionService offsetDefinitionService;
    private ObjectCodeService objectCodeService;
    private KualiConfigurationService kualiConfigurationService;
    private UniversityDateDao universityDateDao;
    private PersistenceService persistenceService;
    private ReportService reportService;
    private ScrubberValidator scrubberValidator;
    private ScrubberProcessObjectCodeOverride scrubberProcessObjectCodeOverride;
    private RunDateService runDateService;
    private OriginEntryLiteService originEntryLiteService;
    
    private String batchFileDirectoryName;

    /**
     * This process will call the scrubber in a read only mode. It will scrub a single group, won't create any output in origin
     * entry. It will create a the scrubber report
     * @param group the origin entry group to scrub for report
     * @param documentNumber the id of documents which generated origin entries that should be scrubbed
     * @see org.kuali.kfs.gl.service.ScrubberService#scrubGroupReportOnly(org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public void scrubGroupReportOnly(String fileName, String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

        ScrubberProcess sp = new ScrubberProcess(flexibleOffsetAccountService, originEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, reportService, scrubberValidator, scrubberProcessObjectCodeOverride, runDateService, originEntryLiteService, batchFileDirectoryName);
        sp.setReferenceLookup(SpringContext.getBean(OriginEntryLookupService.class));
        sp.scrubGroupReportOnly(fileName, documentNumber);
        sp.setReferenceLookup(null);
    }

    /**
     * Scrubs all of the entries in all origin entry groups that are up for scrubbing
     * @see org.kuali.kfs.gl.service.ScrubberService#scrubEntries()
     */
    public void scrubEntries() {
        LOG.debug("scrubEntries() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

        ScrubberProcess sp = new ScrubberProcess(flexibleOffsetAccountService, originEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, reportService, scrubberValidator, scrubberProcessObjectCodeOverride, runDateService, originEntryLiteService, batchFileDirectoryName);
        sp.setReferenceLookup(SpringContext.getBean(OriginEntryLookupService.class));
        sp.scrubEntries();
        sp.setReferenceLookup(null);
    }

    /**
     * Scrubs data read in by the Collector
     * 
     * @param batch the data read by the Collector
     * @param collectorReportData statistics about 
     * @param overrideOriginEntryService the implementation of origin entry service to use for this specific Collector scrub
     * @param overrideOriginEntryGroupService the implementation of origin entry group service to use for this specific Collector scrub
     * @return the status returned by the Scrubber
     * @see org.kuali.kfs.gl.service.ScrubberService#scrubCollectorBatch(org.kuali.kfs.gl.batch.CollectorBatch, org.kuali.kfs.gl.report.CollectorReportData, org.kuali.kfs.gl.service.OriginEntryService, org.kuali.kfs.gl.service.OriginEntryGroupService)
     */
    public ScrubberStatus scrubCollectorBatch(CollectorBatch batch, CollectorReportData collectorReportData, OriginEntryService overrideOriginEntryService, OriginEntryGroupService overrideOriginEntryGroupService, String collectorFileDirectoryName) {
        if (overrideOriginEntryService == null && overrideOriginEntryGroupService == null) {
            throw new NullPointerException("for scrubCollectorBatch, the OriginEntryService and OriginEntryGroupService services must be specified in the parameters");
        }

        // this service is especially developed to support collector scrubbing, demerger, and report generation
        ScrubberProcess sp = new ScrubberProcess(flexibleOffsetAccountService, overrideOriginEntryService, overrideOriginEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, reportService, scrubberValidator, scrubberProcessObjectCodeOverride, runDateService, originEntryLiteService, collectorFileDirectoryName);
        sp.setReferenceLookup(SpringContext.getBean(OriginEntryLookupService.class));
        ScrubberStatus result = sp.scrubCollectorBatch(batch, collectorReportData);
        sp.setReferenceLookup(null);
        return result;
    }
    
    public void performDemerger() {
        LOG.debug("performDemerger() started");
        ScrubberProcess sp = new ScrubberProcess(flexibleOffsetAccountService, originEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, reportService, scrubberValidator, scrubberProcessObjectCodeOverride, runDateService, originEntryLiteService, batchFileDirectoryName);
        sp.performDemerger();
        
    }

    /**
     * Sets the flexibleOffsetAccountService attribute value.
     * 
     * @param flexibleOffsetAccountService The flexibleOffsetAccountService to set.
     */
    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    /**
     * Sets the scrubberValidator attribute value.
     * 
     * @param sv The scrubberValidator to set.
     */
    public void setScrubberValidator(ScrubberValidator sv) {
        scrubberValidator = sv;
    }

    /**
     * Sets the scrubberProcessObjectCodeOverride attribute value.
     * 
     * @param scrubberProcessObjectCodeOverride The scrubberProcessObjectCodeOverride to set.
     */
    public void setScrubberProcessObjectCodeOverride(ScrubberProcessObjectCodeOverride scrubberProcessObjectCodeOverride) {
        this.scrubberProcessObjectCodeOverride = scrubberProcessObjectCodeOverride;
    }

    /**
     * Sets the originEntryService attribute value.
     * 
     * @param oes The OriginEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService oes) {
        this.originEntryService = oes;
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
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
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
     * Sets the runDateService attribute value.
     * 
     * @param runDateService The runDateService to set.
     */
    public RunDateService getRunDateService() {
        return runDateService;
    }

    /**
     * Sets the runDateService attribute value.
     * 
     * @param runDateService The runDateService to set.
     */
    public void setRunDateService(RunDateService runDateService) {
        this.runDateService = runDateService;
    }

    /**
     * Sets the originEntryLiteService attribute value.
     * 
     * @param originEntryLiteService The originEntryLiteService to set.
     */
    public void setOriginEntryLiteService(OriginEntryLiteService originEntryLiteService) {
        this.originEntryLiteService = originEntryLiteService;
    }

    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }


}
