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
