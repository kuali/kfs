/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.OriginEntryGroup;

/**
 * Defines methods that must be implemented by classes providing a LaborScrubberServiceImpl.
 */
public interface LaborScrubberService {

    /**
     * Nightly process to scrub incoming Labor transactions before posting to Labor tables
     */
    public void scrubEntries();

    /**
     * This process will call the scrubber in a read only mode. It will scrub a single group, won't create any output in origin
     * entry. It will create a the scrubber report
     */
    public void scrubGroupReportOnly(OriginEntryGroup group, String documentNumber);

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService);
}
