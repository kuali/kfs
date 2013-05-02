/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;

public class PaymentSourceToPdpExtractStep extends AbstractStep {
    private PaymentSourceExtractionService paymentSourceExtractionService;

    /**
     * Extracts the payments to PDP via the injected PaymentSourceExtractionService
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return paymentSourceExtractionService.extractPayments();
    }

    /**
     * @return the implementation of the PaymentSourceExtractionService to use for this step
     */
    public PaymentSourceExtractionService getPaymentSourceExtractionService() {
        return paymentSourceExtractionService;
    }

    /**
     * Sets the implementation of PaymentSourceExtractionService to use
     * @param paymentSourceExtractionService the implementation of PaymentSourceExtractionService to use
     */
    public void setPaymentSourceExtractionService(PaymentSourceExtractionService paymentSourceExtractionService) {
        this.paymentSourceExtractionService = paymentSourceExtractionService;
    }

}
