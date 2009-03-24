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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NonAppliedHoldingServiceImpl implements NonAppliedHoldingService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService#getNonAppliedHoldingsForCustomer(org.kuali.kfs.module.ar.businessobject.Customer)
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(Customer customer) {
        return null == customer ? null : getNonAppliedHoldingsForCustomer(customer.getCustomerNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService#getNonAppliedHoldingsForCustomer(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(String customerNumber) {
        Map args = new HashMap();
        args.put("customerNumber", customerNumber);
        args.put("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        return businessObjectService.findMatching(NonAppliedHolding.class, args);
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
