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

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Suspension Category that checks to see if the invoice amount is less than the minimum allowed specified by the award.

 */
public class InvoiceAmountLessThanInvoiceMinimumSuspensionCategory extends SuspensionCategoryBase {

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        KualiDecimal invoiceMinimumAmount = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAward().getMinInvoiceAmount();
        if (invoiceMinimumAmount == null) {
            return false; // if no minimum specified, then no limit
        }
        return contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getTotalAmountBilledToDate().isLessThan(invoiceMinimumAmount);
    }

}
