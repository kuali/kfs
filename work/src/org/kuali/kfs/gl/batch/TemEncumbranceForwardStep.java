/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.util.StopWatch;

public class TemEncumbranceForwardStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemEncumbranceForwardStep.class);
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private ParameterService parameterService;

    /**
     * This step looks for any gl pending entries that are currently in the 'H' (Hold) status
     * and puts it in the 'A' (Approved) status then calls the nightly out service to copy the
     * new approved pending entries.
     *
     * @param jobName the name of the job that this step is being run as part of
     * @param jobRunDate the time/date the job is run
     * @return that the job completed successfully
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
	@Override
    public boolean execute(String jobName, Date jobRunDate) {
		StopWatch stopWatch = new StopWatch();
        stopWatch.start("TemEncumbranceForwardStep");

        //If the hold new fiscal year encumbrance indicator is false then change all the help gl pending entries from 'H' (Hold) to 'A' (Approved)
        if(!parameterService.getParameterValueAsBoolean(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.HOLD_NEW_FY_ENCUMBRANCES_IND)) {
            Map fieldValues = new HashMap();
            fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.HOLD);
            Collection glpes = generalLedgerPendingEntryService.findPendingEntries(fieldValues, false);
            Iterator glpesIt = glpes.iterator();

            while(glpesIt.hasNext()) {
            	GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) glpesIt.next();
            	pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
            	generalLedgerPendingEntryService.save(pendingEntry);
            }
        }

        stopWatch.stop();
        LOG.info("TemEncumbranceForwardStep took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

        return true;
    }

	public void setGeneralLedgerPendingEntryService(
			GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
		this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
	}

    @Override
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
