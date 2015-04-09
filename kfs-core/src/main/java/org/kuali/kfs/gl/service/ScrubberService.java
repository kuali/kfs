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
package org.kuali.kfs.gl.service;

import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.impl.ScrubberStatus;

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
    public void scrubCollectorBatch(ScrubberStatus scrubberStatus, CollectorBatch batch, CollectorReportData collectorReportData);
    
    /**
     * This process will call the scrubber in a read only mode. It will scrub a single group, won't create any output in origin
     * entry. It will create a the scrubber report
     * @param group the origin entry group to scrub for report
     * @param documentNumber the id of documents which generated origin entries that should be scrubbed
     */
    public void scrubGroupReportOnly(String fileName, String documentNumber);

    
    public void performDemerger();
}
