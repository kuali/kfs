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

public class BankAccountServiceImpl implements BankAccountService {

    BusinessObjectService businessObjectService;
    
    public BankAccount getByPrimaryId(String financialDocumentBankCode, String finDocumentBankAccountNumber) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("financialDocumentBankCode", financialDocumentBankCode);
        primaryKeys.put("finDocumentBankAccountNumber", finDocumentBankAccountNumber);
        return (BankAccount) businessObjectService.findByPrimaryKey(BankAccount.class, primaryKeys);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
