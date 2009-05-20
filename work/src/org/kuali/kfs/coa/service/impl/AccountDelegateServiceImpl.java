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
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.document.MaintenanceLock;

/**
 * The default implementation of AccountDelegateService.
 */
public class AccountDelegateServiceImpl implements AccountDelegateService {

    private AccountDelegateDao accountDelegateDao;
    private AccountDelegateGlobalDao accountDelegateGlobalDao;

    /**
     * Makes sure the doc type represented by the code either is or is a child of the
     * root doc type for the application (in the distribution, this is "KFS").
     * @see org.kuali.kfs.coa.service.AccountDelegateService#isFinancialSystemDocumentType(java.lang.String)
     */
    public boolean isFinancialSystemDocumentType(String documentTypeCode) {
        return isFinancialSystemDocumentType(documentTypeCode, new WorkflowInfo());
    }

    /**
     * Does the logic isFinancialSystemDocumentType(String documentTypeCode) is supposed to, without
     * creating a bunch of wasted WorkflowInfo objects (though at the cost of building one useless WorkflowInfo
     * object if we're handed the root type right off the bat).
     * @param documentTypeCode the document type to check
     * @param workflowInfo a workflowInfo object to help us
     * @return true if the doc type is in the Financial System document space, false otherwise
     */
    protected boolean isFinancialSystemDocumentType(String documentTypeCode, WorkflowInfo workflowInfo) {
        if (StringUtils.isBlank(documentTypeCode)) return false;
        if (documentTypeCode.equals(KFSConstants.ROOT_DOCUMENT_TYPE)) return true;
        try {
            if (!workflowInfo.isCurrentActiveDocumentType(documentTypeCode)) return false;

            final DocumentTypeDTO documentType = workflowInfo.getDocType(documentTypeCode);
            if (StringUtils.isBlank(documentType.getDocTypeParentName())) return false;
            return isFinancialSystemDocumentType(documentType.getDocTypeParentName(), workflowInfo);
        } catch (WorkflowException we) {
            throw new RuntimeException("Could not retrieve document type "+documentTypeCode, we);
        }
    }

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
