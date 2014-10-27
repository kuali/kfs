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
package org.kuali.kfs.fp.batch;

import java.util.Date;

import org.kuali.kfs.fp.service.PopulateProcurementCardDefaultIdsService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Step which populates all procurement card default records which don't have ids with id's
 */
public class PopulateProcurementCardDefaultIdStep extends AbstractStep {
    protected PopulateProcurementCardDefaultIdsService populateProcurementCardDefaultIdsService;

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        getPopulateProcurementCardDefaultIdsService().populateIdsOnProcurementCardDefaults();
        return true;
    }

    public PopulateProcurementCardDefaultIdsService getPopulateProcurementCardDefaultIdsService() {
        return populateProcurementCardDefaultIdsService;
    }

    public void setPopulateProcurementCardDefaultIdsService(PopulateProcurementCardDefaultIdsService populateProcurementCardDefaultIdsService) {
        this.populateProcurementCardDefaultIdsService = populateProcurementCardDefaultIdsService;
    }
}