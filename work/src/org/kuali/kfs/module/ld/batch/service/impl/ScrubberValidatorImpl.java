/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.util.Message;
import org.kuali.module.labor.bo.LaborOriginEntry;

public class ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private AccountService accountService;
    private PersistenceService persistenceService;
    private ScrubberValidator scrubberValidator;

    // TODO: those arrays should go to FS_PARAM_T
    private String[] continuationAccountBypassOriginationCodes = new String[] { "EU", "PL" };
    private String[] continuationAccountBypassBalanceTypeCodes = new String[] { "EX", "IE", "PE" };
    private String[] continuationAccountBypassDocumentTypeCodes = new String[] { "TOPS", "CD", "LOCR" };
    private int numOfAttempts = 10;
    private int numOfExtraMonthsForCGAccount = 3;

    
    /**
     * @see org.kuali.module.labor.service.LaborScrubberValidator#validateTransaction(owrg.kuali.module.labor.bo.LaborOriginEntry,
     *      org.kuali.module.labor.bo.LaborOriginEntry, org.kuali.module.gl.bo.UniversityDate)
     */
    public List<Message> validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate) {
        LOG.debug("validateTransaction() started");
        List<Message> errors = new ArrayList<Message>();
        
        LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
        
        errors = scrubberValidator.validateTransaction(laborOriginEntry, scrubbedEntry, universityRunDate);
                
        Message err;
        
        
        return errors;
    }
    
    
    public void validateForInquiry(GeneralLedgerPendingEntry entry){
        
    }

    public void setScrubberValidator(ScrubberValidator sv) {
        scrubberValidator = sv;
    }
    
}