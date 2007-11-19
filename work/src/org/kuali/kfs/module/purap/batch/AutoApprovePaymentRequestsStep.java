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
package org.kuali.module.purap.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.purap.service.PaymentRequestService;

/**
 * Step used to auto approve payment requests that meet a certain criteria
 */
public class AutoApprovePaymentRequestsStep extends AbstractStep {

    private PaymentRequestService paymentRequestService;

    public AutoApprovePaymentRequestsStep() {
        super();
    }

    /**
     * Calls service method to approve payment requests
     * 
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) throws InterruptedException {
        return paymentRequestService.autoApprovePaymentRequests();
    }

    /**
     * Invoke execute method
     * 
     * @return
     * @throws InterruptedException
     */
    public boolean execute() throws InterruptedException {
        return execute(null);
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

}
