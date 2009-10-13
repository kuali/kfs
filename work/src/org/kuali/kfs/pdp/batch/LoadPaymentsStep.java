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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;

/**
 * This step will call the <code>PaymentService</code> to pick up incoming PDP payment files and process.
 */
public class LoadPaymentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadPaymentsStep.class);

    private PaymentFileService paymentFileService;
    private BatchInputFileType paymentInputFileType;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");
        paymentFileService.processPaymentFiles(paymentInputFileType);

        return true;
    }

    /**
     * Sets the paymentFileService attribute value.
     * 
     * @param paymentFileService The paymentFileService to set.
     */
    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    /**
     * Sets the paymentInputFileType attribute value.
     * 
     * @param paymentInputFileType The paymentInputFileType to set.
     */
    public void setPaymentInputFileType(BatchInputFileType paymentInputFileType) {
        this.paymentInputFileType = paymentInputFileType;
    }

}
