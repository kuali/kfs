/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;

/**
 * An interface that declares methods that would be needed to validate origin entries and transactions run through the scrubber
 */
public interface ScrubberValidator {
    /**
     * Validate a transaction in the scrubber
     * 
     * @param originEntry Input transaction (never changed)
     * @param scrubbedEntry Output transaction (scrubbed version of input transaction)
     * @param universityRunDate Date of scrubber run
     * @return List of Message objects based for warnings or errors that happened when validating the transaction
     */
    public List<Message> validateTransaction(OriginEntryInformation originEntry, OriginEntryInformation scrubbedEntry, UniversityDate universityRunDate, boolean laborIndicator, AccountingCycleCachingService accountingCycleCachingService);

    /**
     * Validate a transaction for use in balance inquiry
     * 
     * @param entry Input transaction
     */
    public void validateForInquiry(GeneralLedgerPendingEntry entry);
    
    /**
     * Performs logic to determine whether an account is expired
     * 
     * @param account Account to validate
     * @param universityRunDate Run date of process
     * @return true if account is expired, false if not
     */
    public boolean isAccountExpired(Account account, UniversityDate universityRunDate);
}
