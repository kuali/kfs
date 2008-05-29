/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.service.impl;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.dao.A21SubAccountDao;
import org.kuali.module.chart.service.A21SubAccountService;

/**
 * 
 * This class is the default implementation of the A21SubAccountService
 */

@NonTransactional
public class A21SubAccountServiceImpl implements A21SubAccountService {

    private A21SubAccountDao a21SubAccountDao;

    public A21SubAccountServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.chart.service.A21SubAccountService#getByPrimaryKey(java.lang.String, java.lang.String, java.lang.String)
     */
    public A21SubAccount getByPrimaryKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        return a21SubAccountDao.getByPrimaryKey(chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    /**
     * @param subAccountDao The a21SubAccountDao to set.
     */
    public void setA21SubAccountDao(A21SubAccountDao subAccountDao) {
        a21SubAccountDao = subAccountDao;
    }

}
