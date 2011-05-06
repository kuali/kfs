/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch;

import org.kuali.kfs.module.endow.batch.service.TicklerDeliveryService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

public class TicklerDeliveryStep extends AbstractWrappedBatchStep {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TicklerDeliveryStep.class);
    
    private TicklerDeliveryService ticklerDeliveryService;
    
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = ticklerDeliveryService.generateTicklerNotices();                
                
                return success;            
            }
        };
    }

    public void setTicklerDeliveryService(TicklerDeliveryService ticklerDeliveryService){
        this.ticklerDeliveryService = ticklerDeliveryService;
    }
}
