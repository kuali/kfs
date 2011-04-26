/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.batch;

import java.util.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.sys.batch.AbstractStep;


public class ReceivingPaymentRequestStep extends AbstractStep {

    private static final Logger log = Logger.getLogger(ReceivingPaymentRequestStep.class);
    private PaymentRequestService paymentRequestService;
    
    public ReceivingPaymentRequestStep() {
        super();
    }

    public boolean execute(String jobName, 
                           Date jobRunDate) 
    throws InterruptedException {
        log.debug("Started executing the batch job.....");
        paymentRequestService.processPaymentRequestInReceivingStatus();
        return true;
    }
    
    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    


}
