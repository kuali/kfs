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
package org.kuali.module.gl.service;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.ScrubberStatus;

/**
 * An interface declaring methods needed to run the scrubber
 */
public interface ScrubberService {

    /**
     * Nightly process to scrub incoming GL transactions before posting to GL tables
     */
    public void scrubEntries();

    /**
     * Scrubs data read in by the Collector
     * 
     * @param batch the data read by the Collector
     * @param collectorReportData statistics about 
     * @param overrideOriginEntryService the implementation of origin entry service to use for this specific Collector scrub
     * @param overrideOriginEntryGroupService the implementation of origin entry group service to use for this specific Collector scrub
     * @return the status returned by the Scrubber
     */
    public ScrubberStatus scrubCollectorBatch(CollectorBatch batch, CollectorReportData collectorReportData, OriginEntryService overrideOriginEntryService, OriginEntryGroupService overrideOriginEntryGroupService);

    /**
     * This process will call the scrubber in a read only mode. It will scrub a single group, won't create any output in origin
     * entry. It will create a the scrubber report
     * @param group the origin entry group to scrub for report
     * @param documentNumber the id of documents which generated origin entries that should be scrubbed
     */
    public void scrubGroupReportOnly(OriginEntryGroup group, String documentNumber);

    /**
     * Sets the dateTimeService attribute the ScrubberService implementation should use
     * 
     * @param dateTimeService an implementation of dateTimeService to set
     */
    public void setDateTimeService(DateTimeService dateTimeService);
}
