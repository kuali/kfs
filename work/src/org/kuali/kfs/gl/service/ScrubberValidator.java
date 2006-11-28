/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.impl.scrubber.Message;

public interface ScrubberValidator {
    /**
     * Validate a transaction in the scrubber
     * 
     * @param originEntry Input transaction (never changed)
     * @param scrubbedEntry Output transaction (scrubbed version of input transaction)
     * @param universityRunDate Date of scrubber run
     * @return List of Message objects based for warnings or errors that happened when validating the transaction
     */
    public List<Message> validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);

    /**
     * Validate a transaction for use in balance inquiry
     * 
     * @param entry Input transaction
     */
    public void validateForInquiry(GeneralLedgerPendingEntry entry);
}
