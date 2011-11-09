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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.dataaccess.NonAppliedHoldingDao;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NonAppliedHoldingServiceImpl implements NonAppliedHoldingService {

    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingDao nonAppliedHoldingDao;
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService#getNonAppliedHoldingsForCustomer(org.kuali.kfs.module.ar.businessobject.Customer)
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(Customer customer) {
        return null == customer ? null : getNonAppliedHoldingsForCustomer(customer.getCustomerNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService#getNonAppliedHoldingsForCustomer(java.lang.String)
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(String customerNumber) {
        if (StringUtils.isBlank(customerNumber)) {
            throw new IllegalArgumentException("The parameter [customerNumber] passed in was blank or null.");
        }
        List<NonAppliedHolding> nonAppliedHoldings = new ArrayList<NonAppliedHolding>();
        //TODO WARNING this is going to degrade badly performance wise as the db fills up, 
        //     this needs to be solved properly with a flag in the NonAppliedHolding 
        Collection<NonAppliedHolding> tempList = nonAppliedHoldingDao.getNonAppliedHoldingsForCustomer(customerNumber);
        for (NonAppliedHolding nonAppliedHolding : tempList) {
            if (nonAppliedHolding.getAvailableUnappliedAmount().isPositive()) {
                nonAppliedHoldings.add(nonAppliedHolding);
            }
        }
        return nonAppliedHoldings;
    }

    public Collection<NonAppliedHolding> getNonAppliedHoldingsByListOfDocumentNumbers(List<String> docNumbers) {
        if (docNumbers == null) {
            throw new IllegalArgumentException("The parameter [docNumbers] passed in was null.");
        }
        if (docNumbers.isEmpty()) {
            return new ArrayList<NonAppliedHolding>();
        }
        
        return nonAppliedHoldingDao.getNonAppliedHoldingsByListOfDocumentNumbers(docNumbers);
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setNonAppliedHoldingDao(NonAppliedHoldingDao nonAppliedHoldingDao) {
        this.nonAppliedHoldingDao = nonAppliedHoldingDao;
    }

}
