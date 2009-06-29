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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDelegateDao;
import org.kuali.kfs.coa.dataaccess.AccountDelegateGlobalDao;
import org.kuali.kfs.coa.document.AccountDelegateGlobalMaintainableImpl;
import org.kuali.kfs.coa.document.AccountDelegateMaintainableImpl;
import org.kuali.kfs.coa.service.AccountDelegateService;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of AccountDelegateService.
 */
@NonTransactional
public class AccountDelegateServiceImpl implements AccountDelegateService {

    private AccountDelegateDao accountDelegateDao;
    private AccountDelegateGlobalDao accountDelegateGlobalDao;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;

    /**
     * 
     * @see org.kuali.kfs.coa.service.AccountDelegateService#getLockingDocumentId(org.kuali.kfs.coa.document.AccountDelegateGlobalMaintainableImpl, java.lang.String)
     */
    
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

    /**
     * 
     * @see org.kuali.kfs.coa.service.AccountDelegateService#getLockingDocumentId(org.kuali.kfs.coa.document.AccountDelegateMaintainableImpl, java.lang.String)
     */
    
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
     * @see org.kuali.kfs.coa.service.AccountDelegateService#buildMaintainableForAccountDelegate(org.kuali.kfs.coa.businessobject.AccountDelegate)
     */
    public FinancialSystemMaintainable buildMaintainableForAccountDelegate(AccountDelegate delegate) {
        FinancialSystemMaintainable maintainable = getAccountDelegateMaintainable();
        maintainable.setBoClass(delegate.getClass());
        maintainable.setBusinessObject(delegate);
        return maintainable;
    }
    
    /**
     * @return the proper class for the Maintainable associated with AccountDelegate maintenance documents
     */
    protected Class<? extends Maintainable> getAccountDelegateMaintainableClass() {
        return getDataDictionaryService().getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(AccountDelegate.class).getMaintainableClass();
    }
    
    /**
     * @return a new instance of the proper maintainable for AccountDelegate maintenance documents
     */
    protected FinancialSystemMaintainable getAccountDelegateMaintainable() {
        final Class<? extends Maintainable> maintainableClazz = getAccountDelegateMaintainableClass();
        final FinancialSystemMaintainable maintainable;
        try {
            maintainable = (FinancialSystemMaintainable)maintainableClazz.newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not instantiate maintainable for AccountDelegate maintenance document", ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Could not instantiate maintainable for AccountDelegate maintenance document", iae);
        }
        return maintainable;
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#retrieveAllActiveDelegationsForPerson(java.lang.String)
     */
    public Iterator<AccountDelegate> retrieveAllActiveDelegationsForPerson(String principalId, boolean primary) {
        return (Iterator<AccountDelegate>)getAccountDelegateDao().getAccountDelegationsForPerson(principalId, primary);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(String principalId) {
        return accountDelegateDao.isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(principalId);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(java.lang.String)
     */
    public boolean isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(String principalId) {
        return accountDelegateDao.isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(principalId);
    }
    
    /**
     * Saves the given account delegate to the persistence store
     * @param accountDelegate the account delegate to save
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveForMaintenanceDocument(AccountDelegate accountDelegate) {
        getBusinessObjectService().linkAndSave(accountDelegate);
    }
    
    /**
     * Persists the given account delegate global maintenance document inactivations
     * @param delegatesToInactivate the List of delegates to inactivate
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveInactivationsForGlobalMaintenanceDocument(List<PersistableBusinessObject> delegatesToInactivate) {
        if (delegatesToInactivate != null && !delegatesToInactivate.isEmpty()) {
            getBusinessObjectService().save(delegatesToInactivate);
        }
    }
    
    /**
     * Persists the given account delegate global maintenance document changes
     * @param delegatesToChange the List of delegates to change
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveChangesForGlobalMaintenanceDocument(List<PersistableBusinessObject> delegatesToChange) {
        if (delegatesToChange != null && !delegatesToChange.isEmpty()) {
            getBusinessObjectService().save(delegatesToChange);
        }
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

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
