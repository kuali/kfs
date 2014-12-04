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
