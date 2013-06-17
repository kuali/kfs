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
package org.kuali.kfs.pdp.batch.service;

import java.util.List;

import org.kuali.kfs.sys.batch.InitiateDirectory;

public interface ExtractPaymentService extends InitiateDirectory {
    /**
     * Extract checks to be cut into a file to be processed by the check printing service
     */
    public void extractChecks();

    /**
     * Extract ach payments to be sent to the bank for processing
     */
    public void extractAchPayments();

    /**
     * Extract canceled checks to be used for a bank's positive pay program.
     */
    public void extractCanceledChecks();

    /**
     * Format the given check note so that we can handle
     * notes with text that is longer than the max length in the
     * database.
     *
     * @param checkNote
     * @return
     */
    public List<String> formatCheckNoteLines(String checkNote);
}
