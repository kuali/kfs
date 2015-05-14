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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class InvoiceAmountLessThanInvoiceMinimumSuspensionCategory extends SuspensionCategoryBase {

    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        ensureTotalInvoiceAmountIsUpToDate(contractsGrantsInvoiceDocument);

        return shouldSuspend(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAward().getMinInvoiceAmount(), contractsGrantsInvoiceDocument.getTotalInvoiceAmount());
    }

    protected void ensureTotalInvoiceAmountIsUpToDate(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (!StringUtils.equalsIgnoreCase(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequencyCode(), ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !StringUtils.equalsIgnoreCase(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequencyCode(), ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            contractsGrantsInvoiceDocumentService.recalculateTotalAmountBilledToDate(contractsGrantsInvoiceDocument);
        }
    }

    protected boolean shouldSuspend(KualiDecimal minInvoiceAmount, KualiDecimal invoiceAmount) {
        if (minInvoiceAmount == null) {
            return false;
        }
        return invoiceAmount.isLessThan(minInvoiceAmount);
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

}
