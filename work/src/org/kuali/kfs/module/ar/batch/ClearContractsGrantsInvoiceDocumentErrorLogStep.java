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
package org.kuali.kfs.module.ar.batch;

import java.util.Date;
import java.util.HashMap;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorMessage;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.krad.service.BusinessObjectService;


/**
 * Batch step to clear Contracts & Grants Invoice Document Error Log table.
 */
public class ClearContractsGrantsInvoiceDocumentErrorLogStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearContractsGrantsInvoiceDocumentErrorLogStep.class);
    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            businessObjectService.deleteMatching(ContractsGrantsInvoiceDocumentErrorMessage.class, new HashMap<String, Object>());
            businessObjectService.deleteMatching(ContractsGrantsInvoiceDocumentErrorLog.class, new HashMap<String, Object>());
        } else {
            LOG.info("Contracts & Grants Billing enhancement not turned on; therefore, not running clearContractsGrantsInvoiceDocumentErrorLogStep");
        }

        return true;
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
