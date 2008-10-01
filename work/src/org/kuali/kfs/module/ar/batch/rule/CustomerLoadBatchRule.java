/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.rule;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;

public class CustomerLoadBatchRule {

    private DateTimeService dateTimeService;
    private MaintenanceDocumentDictionaryService maintDocDDService;
    private DataDictionaryService ddService;
    
    public CustomerLoadBatchRule() {
        if (dateTimeService == null) dateTimeService = SpringContext.getBean(DateTimeService.class);
        if (maintDocDDService == null) maintDocDDService = SpringContext.getBean(MaintenanceDocumentDictionaryService.class);
        if (ddService == null) ddService = SpringContext.getBean(DataDictionaryService.class);
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setMaintDocDDService(MaintenanceDocumentDictionaryService maintDocDDService) {
        this.maintDocDDService = maintDocDDService;
    }

    public void setDdService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }

}
