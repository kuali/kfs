/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.dataaccess.AccountDelegateDao;
import org.kuali.kfs.coa.dataaccess.AccountDelegateGlobalDao;
import org.kuali.kfs.coa.document.AccountDelegateGlobalMaintainableImpl;
import org.kuali.kfs.coa.document.AccountDelegateMaintainableImpl;
import org.kuali.kfs.coa.service.AccountDelegateService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.document.MaintenanceLock;

/**
 * The default implementation of AccountDelegateService.
 */
@NonTransactional
public class AccountDelegateServiceImpl implements AccountDelegateService {

    private AccountDelegateDao accountDelegateDao;
    private AccountDelegateGlobalDao accountDelegateGlobalDao;

    public String getLockingDocumentId(AccountDelegateGlobalMaintainableImpl global, String docNumber) {
        String lockingDocId = null;
        List<MaintenanceLock> maintenanceLocks = global.generateMaintenanceLocks();
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = accountDelegateGlobalDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(),docNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }
        return lockingDocId;
    }

    public String getLockingDocumentId(AccountDelegateMaintainableImpl delegate, String docNumber) {
        String lockingDocId = null;
        List<MaintenanceLock> maintenanceLocks = delegate.generateMaintenanceLocks();
        maintenanceLocks.add(delegate.createGlobalAccountLock());
        
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = accountDelegateDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(),docNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }
        return lockingDocId;
    }
    
    
    /**
     * Gets the accountDelegateDao attribute. 
     * @return Returns the accountDelegateDao.
     */
    public AccountDelegateDao getAccountDelegateDao() {
        return accountDelegateDao;
    }

    /**
     * Sets the accountDelegateDao attribute value.
     * @param accountDelegateDao The accountDelegateDao to set.
     */
    public void setAccountDelegateDao(AccountDelegateDao accountDelegateDao) {
        this.accountDelegateDao = accountDelegateDao;
    }

    /**
     * Gets the accountDelegateGlobalDao attribute. 
     * @return Returns the accountDelegateGlobalDao.
     */
    public AccountDelegateGlobalDao getAccountDelegateGlobalDao() {
        return accountDelegateGlobalDao;
    }

    /**
     * Sets the accountDelegateGlobalDao attribute value.
     * @param accountDelegateGlobalDao The accountDelegateGlobalDao to set.
     */
    public void setAccountDelegateGlobalDao(AccountDelegateGlobalDao accountDelegateGlobalDao) {
        this.accountDelegateGlobalDao = accountDelegateGlobalDao;
    }

}
