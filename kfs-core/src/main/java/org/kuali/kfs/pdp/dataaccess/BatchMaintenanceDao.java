/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
