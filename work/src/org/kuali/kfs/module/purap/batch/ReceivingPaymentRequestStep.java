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

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.impl.PurapAccountingServiceImpl;


public class ReceivingPaymentRequestStep extends AbstractStep {

    private PaymentRequestService paymentRequestService;
    
    public ReceivingPaymentRequestStep() {
        super();
    }

    public boolean execute(String jobName, 
                           Date jobRunDate) 
    throws InterruptedException {
        paymentRequestService.processPaymentRequestInReceivingStatus();
        return true;
    }
    
    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    


}
