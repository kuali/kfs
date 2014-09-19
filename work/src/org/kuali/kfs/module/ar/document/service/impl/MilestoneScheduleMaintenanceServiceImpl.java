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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.MilestoneScheduleMaintenanceService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Default implementation of the Milestone Schedule maintenance service
 */
public class MilestoneScheduleMaintenanceServiceImpl implements MilestoneScheduleMaintenanceService {
    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    @Override
    public boolean hasMilestoneBeenCopiedToInvoice(Long proposalNumber, String milestoneId) {
        Collection<InvoiceMilestone> invoiceMilestones = new ArrayList<InvoiceMilestone>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ArPropertyConstants.MilestoneFields.MILESTONE_IDENTIFIER, milestoneId);
        invoiceMilestones = getBusinessObjectService().findMatching(InvoiceMilestone.class, map);
        // skip ineffective milestones, based on invoice
        Set<String> effectiveDocumentNumbers = new HashSet<String>();
        List<InvoiceMilestone> effectiveInvoiceMilestones = new ArrayList<InvoiceMilestone>();
        for (InvoiceMilestone invoiceMilestone : invoiceMilestones) {
            if (effectiveDocumentNumbers.contains(invoiceMilestone.getDocumentNumber()) || getContractsGrantsInvoiceDocumentService().isInvoiceDocumentEffective(invoiceMilestone.getDocumentNumber())) {
                effectiveInvoiceMilestones.add(invoiceMilestone);
                effectiveDocumentNumbers.add(invoiceMilestone.getDocumentNumber());
            }
        }

        return CollectionUtils.isNotEmpty(effectiveInvoiceMilestones);
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
