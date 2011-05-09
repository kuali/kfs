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
/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess;

import java.util.List;

/**
 * This class has methods for batch maintenance.
 */
public interface BatchMaintenanceDao {
    /**
     * This method checks if all payments in this batch have open status.
     * 
     * @param batchId the batch id
     * @param batchPayments a list of payments for the given batch
     * @param statusList a list of payment statuses
     * @return true if all payments have open status, false otherwise
     */
    public boolean doBatchPaymentsHaveOpenStatus(Integer batchId, List batchPayments, List statusList);

    /**
     * This method checks if all payments in this batch have held status.
     * 
     * @param batchId
     * @param batchPayments a list of payments for the given batch
     * @param statusList a list of payment statuses
     * @return true if all payments have held status, false otherwise
     */
    public boolean doBatchPaymentsHaveHeldStatus(Integer batchId, List batchPayments, List statusList);
}
