/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.PredeterminedBillingScheduleMaintenanceService;
import org.kuali.kfs.sys.KFSPropertyConstants;
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
        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        if (StringUtils.isNotBlank(billId)) {
            map.put(ArPropertyConstants.BillFields.BILL_IDENTIFIER, billId);
        }
        invoiceBills.addAll(getBusinessObjectService().findMatching(InvoiceBill.class, map));
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
