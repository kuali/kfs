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
package org.kuali.kfs.pdp.batch;

import org.kuali.kfs.pdp.batch.service.InactivatePayeeAchAccountsService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Batch step to inactivate the Payee ACH Accounts for which thes payee are inactive. 
 * Payee active status is obtained from the associated Person or Vendor, depending on the payee ID type.
 */
public class InactivatePayeeAchAccountsStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InactivatePayeeAchAccountsStep.class);

    private InactivatePayeeAchAccountsService inactivatePayeeAchAccountsService;
    
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                return inactivatePayeeAchAccountsService.inactivatePayeeAchAccounts();
            }                
        };
    }

    public void setInactivatePayeeAchAccountsService(InactivatePayeeAchAccountsService inactivatePayeeAchAccountsService) {
        this.inactivatePayeeAchAccountsService = inactivatePayeeAchAccountsService;
    }
        
}
