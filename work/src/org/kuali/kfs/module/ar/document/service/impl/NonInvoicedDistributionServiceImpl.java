/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.ar.service.impl;

import java.util.Collection;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.ar.bo.NonInvoicedDistribution;
import org.kuali.module.ar.dao.NonInvoicedDistributionDao;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.NonInvoicedDistributionService;

public class NonInvoicedDistributionServiceImpl implements NonInvoicedDistributionService {
    private NonInvoicedDistributionDao nonInvoicedDistributionDao;
    
    /**
     * @see org.kuali.module.ar.service.NonInvoicedDistributionService#getNonInvoicedDistributionsForInvoice(java.lang.String)
     */
    @NonTransactional
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber) {
        return nonInvoicedDistributionDao.getNonInvoicedDistributionsForInvoice(documentNumber);
    }

    /**
     * @see org.kuali.module.ar.service.NonInvoicedDistributionService#getNonInvoicedDistributionsForInvoice(org.kuali.module.ar.document.CustomerInvoiceDocument)
     */
    @NonTransactional
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(CustomerInvoiceDocument invoice) {
        return getNonInvoicedDistributionsForInvoice(invoice.getDocumentNumber());
    }
    
    /**
     * @param nonInvoicedDistributionDao
     */
    @NonTransactional
    public void setNonInvoicedDistributionDao(NonInvoicedDistributionDao nonInvoicedDistributionDao) {
        this.nonInvoicedDistributionDao = nonInvoicedDistributionDao;
    }

}
