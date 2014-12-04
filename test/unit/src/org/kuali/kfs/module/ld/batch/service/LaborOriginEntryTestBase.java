/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
