/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * A service which basically encapsulates a Map of ContractsGrantsReportDataBuilderService to return the right one to an Action
 */
public class ContractsGrantsReportHelperServiceImpl implements ContractsGrantsReportHelperService {
    protected Map<Class<? extends BusinessObject>, ContractsGrantsReportDataBuilderService<? extends BusinessObject>> reportBuilders;

    /**
     * Builds the report data builder services map if needed; returns a service if it can find one
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#getReportBuilderService(java.lang.Class)
     */
    @Override
    public <B extends BusinessObject> ContractsGrantsReportDataBuilderService<B> getReportBuilderService(Class<B> detailClass) {
        synchronized(this) {
            if (reportBuilders == null) {
                buildReportBuildersMap();
            }
        }
        return (ContractsGrantsReportDataBuilderService<B>)reportBuilders.get(detailClass);
    }

    /**
     * Builds the Map of ReportDataBuilder services
     */
    @SuppressWarnings("rawtypes")
    protected void buildReportBuildersMap() {
        reportBuilders = new HashMap<Class<? extends BusinessObject>, ContractsGrantsReportDataBuilderService<? extends BusinessObject>>();
        final Map<String, ContractsGrantsReportDataBuilderService> services = SpringContext.getBeansOfType(ContractsGrantsReportDataBuilderService.class);
        for (ContractsGrantsReportDataBuilderService reportService: services.values()) {
            reportBuilders.put(reportService.getDetailsClass(), reportService);
        }
    }
}