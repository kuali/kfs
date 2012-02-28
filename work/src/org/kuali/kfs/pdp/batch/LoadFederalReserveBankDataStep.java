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
package org.kuali.kfs.pdp.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.service.AchBankService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

public class LoadFederalReserveBankDataStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadFederalReserveBankDataStep.class);

    private AchBankService achBankService;
    private String directoryName;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return new ArrayList<String>() {{add(directoryName); }};
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");

        String filename = getParameterService().getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, PdpParameterConstants.ACH_BANK_INPUT_FILE);

        return achBankService.reloadTable(directoryName + filename);
    }

    public void setAchBankService(AchBankService achBankService) {
        this.achBankService = achBankService;
    }

    public void setDirectoryName(String dn) {
        directoryName = dn;
    }
}
