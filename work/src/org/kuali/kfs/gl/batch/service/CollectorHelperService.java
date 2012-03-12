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
package org.kuali.kfs.gl.batch.service;

import java.io.PrintStream;
import java.util.List;

import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.sys.batch.BatchInputFileType;

/**
 * Provides methods for processing gl incoming batch files.
 */
public interface CollectorHelperService {

    /**
     * Loads the file given by the filename, then performs the collector process: parse, validate, store, email.
     * 
     * @param fileName - name of file to load (including path)
     * @param group the group into which to persist the origin entries for the collector batch/file
     * @param collectorReportData the object used to store all of the collector status information for reporting
     * @param collectorScrubberStatuses if the collector scrubber is able to be invoked upon this collector batch, then the status
     *        info of the collector status run is added to the end of this list
     * @param the output stream to which to store origin entries that properly pass validation
     * @return boolean - true if load was successful, false if errors were encountered
     */
    public boolean loadCollectorFile(String fileName, CollectorReportData collectorReportData, List<CollectorScrubberStatus> collectorScrubberStatuses, BatchInputFileType collectorInputFileType, PrintStream originEntryOutputPs);

    /**
     * Validates the contents of a parsed file.
     * 
     * @param batch - batch to validate
     * @return boolean - true if validation was OK, false if there were errors
     */
    public boolean performValidation(CollectorBatch batch);

    /**
     * Reconciles the trailer total count and amount to the actual parsed contents.
     * 
     * @param batch - batch to check trailer
     * @param collectorReportData if running the actual collector batch process, should be the object representing the reporting
     *        data for the batch run. Otherwise, if running in the batch upload screen or in a manner in which reporting information
     *        is not needed, then null may be passed in
     * @return boolean - true if trailer check was OK, false if totals did not match
     */
    public boolean checkTrailerTotals(CollectorBatch batch, CollectorReportData collectorReportData);

}
