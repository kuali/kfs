/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.batch;

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.financial.service.DisbursementVoucherExtractService;

public class DvToPdpExtractStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DvToPdpExtractStep.class);

    private DisbursementVoucherExtractService disbursementVoucherExtractService;

    /**
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return disbursementVoucherExtractService.extractPayments();
    }

    public void setDisbursementVoucherExtractService(DisbursementVoucherExtractService d) {
        this.disbursementVoucherExtractService = d;
    }
}
