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
package org.kuali.kfs.module.ar.report.service;

import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Interface of services which want to help out with building Contracts & Grants Report Services
 */
public interface ContractsGrantsReportHelperService {
    /**
     * Looks up the implementation of ContractsGrantsReportDataBuilderService for the given detail class
     * @param detailClass the class of the detail to find a report building service for
     * @return the implementation of ContractsGrantsReportDataBuilderService
     */
    public <B extends BusinessObject> ContractsGrantsReportDataBuilderService<B> getReportBuilderService(Class<B> detailClass);
}