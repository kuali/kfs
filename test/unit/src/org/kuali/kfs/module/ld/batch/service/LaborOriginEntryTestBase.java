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
package org.kuali.kfs.module.ld.batch.service;

import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

public class LaborOriginEntryTestBase extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborOriginEntryTestBase.class);

    protected LaborOriginEntryService laborOriginEntryService;
    protected LaborAccountingCycleCachingService laborAccountingCycleCachingService;

    public LaborOriginEntryTestBase() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);
        
        laborAccountingCycleCachingService = SpringContext.getBean(LaborAccountingCycleCachingService.class);
        laborAccountingCycleCachingService.initialize();
    }

    /**
     * get the name of the batch directory
     * 
     * @return the name of the batch directory
     */
    @Override
    protected String getBatchDirectoryName() {
        String stagingDirectory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory");
        return stagingDirectory + "/ld/originEntry";
    }
}
