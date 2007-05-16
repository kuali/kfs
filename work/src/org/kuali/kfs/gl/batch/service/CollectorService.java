/*
 * Copyright 2006 The Kuali Foundation.
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

import java.util.List;

import org.kuali.module.gl.batch.collector.CollectorBatch;

/**
 * Provides methods for processing gl incoming batch files.
 */
public interface CollectorService {

    /**
     * Loads the file given by the filename, then performs the collector process: parse, validate, store, email.
     * 
     * @param fileName - name of file to load (including path)
     * @return boolean - true if load was successful, false if errors were encountered
     */
    public boolean loadCollectorFile(String fileName);

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
     * @return boolean - true if trailer check was OK, false if totals did not match
     */
    public boolean checkTrailerTotals(CollectorBatch batch);

}
