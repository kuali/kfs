/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.financial.service.BankService;

/**
 * 
 * This is the default implementation of the BankService interface.
 * 
 */
public class BankServiceImpl implements BankService {

    BusinessObjectService businessObjectService;    

    /**
     * This method retrieves an instance of a Bank object by using the attributes given as the primary keys to perform the database query.
     * 
     * @param financialDocumentBankCode The bank code to be used to perform the database lookup.
     * @return The bank object which has a matching primary key to the values provided.
     * 
     * @see org.kuali.module.financial.service.BankService#getByPrimaryId(java.lang.String)
     */
    public Bank getByPrimaryId(String financialDocumentBankCode) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("financialDocumentBankCode", financialDocumentBankCode);
        return (Bank) businessObjectService.findByPrimaryKey(Bank.class, primaryKeys);
    }

    /**
     * 
     * This method is a simple getter for retrieving an instance of a BusinessObjectService.
     * @return A BusinessObjectService instance.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * 
     * This method is a simple setter for setting the local BusinessObjectService attribute.
     * @param businessObjectService The BusinessObjectService to be set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
