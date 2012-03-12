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

import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Step used to auto approve purchase orders that meet a certain criteria
 */
public class AutoCloseRecurringOrdersStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoCloseRecurringOrdersStep.class);
    
    private PurchaseOrderService purchaseOrderService;

    public AutoCloseRecurringOrdersStep() {
        super();
    }

    /**
     * Calls service method to approve recurring purchase orders
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return purchaseOrderService.autoCloseRecurringOrders();
    }

    /**
     * Invoke execute method
     * 
     * @return
     * @throws InterruptedException
     */
    public boolean execute() throws InterruptedException {
        try {
            return execute(null, SpringContext.getBean(DateTimeService.class).getCurrentDate());
        }
        catch (InterruptedException e) {
            LOG.error("Exception occured executing step", e);
            throw e;
        }
        catch (RuntimeException e) {
            LOG.error("Exception occured executing step", e);
            throw e;
        }
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }



}
