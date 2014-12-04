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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.PredeterminedBillingScheduleMaintenanceService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Default implementation of the PredeterminedBillingScheduleMaintenanceService
 */
public class PredeterminedBillingScheduleMaintenanceServiceImpl implements PredeterminedBillingScheduleMaintenanceService {
    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    /**
     *
     * @see org.kuali.kfs.module.ar.document.service.PredeterminedBillingScheduleMaintenanceService#hasBillBeenCopiedToInvoice(java.lang.Long, java.lang.String)
     */
    @Override
    public boolean hasBillBeenCopiedToInvoice(Long proposalNumber, String billId) {
        Collection<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ArPropertyConstants.BillFields.BILL_IDENTIFIER, billId);
        invoiceBills = getBusinessObjectService().findMatching(InvoiceBill.class, map);
        // but skip documents which have been canceled, disapproved, or where the document was error corrected
        Set<String> effectiveDocumentNumbers = new HashSet<String>();
        List<InvoiceBill> effectiveInvoiceBills = new ArrayList<InvoiceBill>();
        for (InvoiceBill invoiceBill : invoiceBills) {
            if (effectiveDocumentNumbers.contains(invoiceBill.getDocumentNumber()) || getContractsGrantsInvoiceDocumentService().isInvoiceDocumentEffective(invoiceBill.getDocumentNumber())) {
                effectiveInvoiceBills.add(invoiceBill);
                effectiveDocumentNumbers.add(invoiceBill.getDocumentNumber());
            }
        }

        return CollectionUtils.isNotEmpty(effectiveInvoiceBills);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }
}
