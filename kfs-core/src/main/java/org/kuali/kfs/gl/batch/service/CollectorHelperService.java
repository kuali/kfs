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
