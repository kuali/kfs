/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.dao.SubAccountDao;
import org.kuali.module.chart.service.SubAccountService;

/**
 * This class is the service implementation for the SubAccount structure. This is the default implementation that gets delivered
 * with Kuali.
 * 
 * 
 */
public class SubAccountServiceImpl implements SubAccountService {
    private SubAccountDao subAccountDao;

    /**
     * @see org.kuali.module.chart.service.SubAccountService#getByPrimaryId(java.lang.String, java.lang.String, java.lang.String)
     */
    public SubAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        return subAccountDao.getByPrimaryId(chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    /**
     * @return SubAccountDao
     */
    public SubAccountDao getSubAccountDao() {
        return subAccountDao;
    }

    /**
     * @param subAccountDao
     */
    public void setSubAccountDao(SubAccountDao subAccountDao) {
        this.subAccountDao = subAccountDao;
    }
}