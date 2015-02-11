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

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step will call a service method to route cgin documents that are in 'I' status.
 */
public class ContractsGrantsInvoiceRouteDocumentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceRouteDocumentsStep.class);
    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected ContractsGrantsInvoiceCreateDocumentService cgInvoiceDocumentCreateService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            try {

                Thread.sleep(300000);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            cgInvoiceDocumentCreateService.routeContractsGrantsInvoiceDocuments();
        } else {
            LOG.info("Contracts & Grants Billing enhancement not turned on; therefore, not running contractsGrantsInvoiceRouteDocumentsStep");
        }
        return true;
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    /**
     * Sets the cgInvoiceDocumentCreateService attribute value.
     *
     * @param cgInvoiceDocumentCreateService The cgInvoiceDocumentCreateService to set.
     */
    public void setCgInvoiceDocumentCreateService(ContractsGrantsInvoiceCreateDocumentService cgInvoiceDocumentCreateService) {
        this.cgInvoiceDocumentCreateService = cgInvoiceDocumentCreateService;
    }


}
