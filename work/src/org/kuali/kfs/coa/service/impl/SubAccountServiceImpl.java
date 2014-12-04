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
package org.kuali.kfs.coa.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 * This class is the service implementation for the SubAccount structure. This is the default implementation that gets delivered
 * with Kuali.
 */

@NonTransactional
public class SubAccountServiceImpl implements SubAccountService {

    protected BusinessObjectService businessObjectService;
    /**
     * @see org.kuali.kfs.coa.service.SubAccountService#getByPrimaryId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public SubAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        Map<String, Object> keys = new HashMap<String, Object>(3);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        keys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        return businessObjectService.findByPrimaryKey(SubAccount.class, keys);
    }

    /**
     * Method is used by KualiAccountAttribute to enable caching of accounts for routing.
     *
     * @see org.kuali.kfs.coa.service.impl.SubAccountServiceImpl#getByPrimaryId(String, String, String)
     */
    @Override
    @Cacheable(value=SubAccount.CACHE_NAME, key="#p0+'-'+#p1+'-'+#p2")
    public SubAccount getByPrimaryIdWithCaching(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        return getByPrimaryId(chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
