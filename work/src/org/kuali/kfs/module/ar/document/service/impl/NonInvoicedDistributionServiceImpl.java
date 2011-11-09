/*
 * Copyright 2008 The Kuali Foundation
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
