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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.NonInvoicedDistributionService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;

public class NonInvoicedDistributionServiceImpl implements NonInvoicedDistributionService {
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.NonInvoicedDistributionService#getNonInvoicedDistributionsForInvoice(java.lang.String)
     */
    @NonTransactional
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("documentNumber", documentNumber);
        return businessObjectService.findMatching(NonInvoicedDistribution.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.NonInvoicedDistributionService#getNonInvoicedDistributionsForInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @NonTransactional
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(CustomerInvoiceDocument invoice) {
        return getNonInvoicedDistributionsForInvoice(invoice.getDocumentNumber());
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
