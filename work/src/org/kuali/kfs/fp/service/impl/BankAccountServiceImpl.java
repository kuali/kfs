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
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.service.BankAccountService;

/**
 * 
 * This is the default implementation of the BankAccountService interface.
 * 
 */
public class BankAccountServiceImpl implements BankAccountService {

    BusinessObjectService businessObjectService;

    /**
     * Retrieves the BankAccount instance from the database using the provided values as the primary keys to perform the lookup.
     * 
     * @param financialDocumentBankCode Bank code to use for retrieving the associated bank acocunt object.
     * @param finDocumentBankAccountNumber Bank account number to use for retrieving the associated bank account object.
     * @return A BankAccount object which matches the criteria provided.  
     * 
     * @see org.kuali.module.financial.service.BankAccountService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public BankAccount getByPrimaryId(String financialDocumentBankCode, String finDocumentBankAccountNumber) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("financialDocumentBankCode", financialDocumentBankCode);
        primaryKeys.put("finDocumentBankAccountNumber", finDocumentBankAccountNumber);
        return (BankAccount) businessObjectService.findByPrimaryKey(BankAccount.class, primaryKeys);
    }

    /**
     * 
     * This method is a simple getter for retrieving an instance of a BusinessObjectService.
     * @return An instance of a BusinessObjectService.
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
