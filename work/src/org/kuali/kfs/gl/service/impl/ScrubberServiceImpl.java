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
package org.kuali.module.gl.service.impl;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.ScrubberService;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.ScrubberStatus;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ScrubberServiceImpl implements ScrubberService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceImpl.class);

    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private DocumentTypeService documentTypeService;
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

    /**
     * 
     * @see org.kuali.module.gl.service.ScrubberService#scrubGroupReportOnly(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void scrubGroupReportOnly(OriginEntryGroup group,String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

        ScrubberProcess sp = new ScrubberProcess(flexibleOffsetAccountService, documentTypeService, originEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, reportService, scrubberValidator);
        sp.scrubGroupReportOnly(group,documentNumber);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ScrubberService#scrubEntries()
     */
    public void scrubEntries() {
        LOG.debug("scrubEntries() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

        ScrubberProcess sp = new ScrubberProcess(flexibleOffsetAccountService, documentTypeService, originEntryService, originEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, reportService, scrubberValidator);
        sp.scrubEntries();
    }
    
    
    public ScrubberStatus scrubCollectorBatch(CollectorBatch batch, CollectorReportData collectorReportData, OriginEntryService overrideOriginEntryService, OriginEntryGroupService overrideOriginEntryGroupService) {
        if (overrideOriginEntryService == null && overrideOriginEntryGroupService == null) {
            throw new NullPointerException("for scrubCollectorBatch, the OriginEntryService and OriginEntryGroupService services must be specified in the parameters");
        }
        // this service is especially developed to support collector scrubbing, demerger, and report generation
        ScrubberProcess sp = new ScrubberProcess(flexibleOffsetAccountService, documentTypeService, overrideOriginEntryService, overrideOriginEntryGroupService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, reportService, scrubberValidator);
        return sp.scrubCollectorBatch(batch, collectorReportData);
    }

    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public void setScrubberValidator(ScrubberValidator sv) {
        scrubberValidator = sv;
    }

    public void setOriginEntryService(OriginEntryService oes) {
        this.originEntryService = oes;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService groupService) {
        this.originEntryGroupService = groupService;
    }

    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }

    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    public void setPersistenceService(PersistenceService ps) {
        persistenceService = ps;
    }

    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
