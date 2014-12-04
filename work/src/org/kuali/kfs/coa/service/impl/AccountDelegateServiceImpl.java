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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.dataaccess.AccountDelegateDao;
import org.kuali.kfs.coa.dataaccess.AccountDelegateGlobalDao;
import org.kuali.kfs.coa.document.AccountDelegateGlobalMaintainableImpl;
import org.kuali.kfs.coa.document.AccountDelegateMaintainableImpl;
import org.kuali.kfs.coa.service.AccountDelegateService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of AccountDelegateService.
 */
public class AccountDelegateServiceImpl implements AccountDelegateService {

    private AccountDelegateDao accountDelegateDao;
    private AccountDelegateGlobalDao accountDelegateGlobalDao;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#getLockingDocumentId(org.kuali.kfs.coa.document.AccountDelegateGlobalMaintainableImpl,
     *      java.lang.String)
     */
    @Override
    @NonTransactional
    public String getLockingDocumentId(AccountDelegateGlobalMaintainableImpl global, String docNumber) {
        String lockingDocId = null;
        List<MaintenanceLock> maintenanceLocks = global.generateMaintenanceLocks();
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = accountDelegateGlobalDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(), docNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }
        return lockingDocId;
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#getLockingDocumentId(org.kuali.kfs.coa.document.AccountDelegateMaintainableImpl,
     *      java.lang.String)
     */
    @Override
    @NonTransactional
    public String getLockingDocumentId(AccountDelegateMaintainableImpl delegate, String docNumber) {
        String lockingDocId = null;
        List<MaintenanceLock> maintenanceLocks = delegate.generateMaintenanceLocks();
        maintenanceLocks.add(delegate.createGlobalAccountLock());

        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = accountDelegateDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(), docNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }
        return lockingDocId;
    }


    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#buildMaintainableForAccountDelegate(org.kuali.kfs.coa.businessobject.AccountDelegate)
     */
    @Override
    @NonTransactional
    public FinancialSystemMaintainable buildMaintainableForAccountDelegate(AccountDelegate delegate) {
        FinancialSystemMaintainable maintainable = getAccountDelegateMaintainable();
        maintainable.setBoClass(delegate.getClass());
        maintainable.setBusinessObject(delegate);
        return maintainable;
    }

    /**
     * @return the proper class for the Maintainable associated with AccountDelegate maintenance documents
     */
    protected Class getAccountDelegateMaintainableClass() {
        return dataDictionaryService.getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(AccountDelegate.class).getMaintainableClass();
    }

    /**
     * @return a new instance of the proper maintainable for AccountDelegate maintenance documents
     */
    protected FinancialSystemMaintainable getAccountDelegateMaintainable() {
        final Class maintainableClazz = getAccountDelegateMaintainableClass();
        final FinancialSystemMaintainable maintainable;
        try {
            maintainable = (FinancialSystemMaintainable) maintainableClazz.newInstance();
        }
        catch (Exception ie) {
            throw new RuntimeException("Could not instantiate maintainable for AccountDelegate maintenance document", ie);
        }
        return maintainable;
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#retrieveAllActiveDelegationsForPerson(java.lang.String)
     */
    @Override
    @NonTransactional
    public Iterator<AccountDelegate> retrieveAllActiveDelegationsForPerson(String principalId, boolean primary) {
        return accountDelegateDao.getAccountDelegationsForPerson(principalId, primary);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(java.lang.String)
     */
    @Override
    @NonTransactional
    public boolean isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(String principalId) {
        return accountDelegateDao.isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountDelegateService#isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(java.lang.String)
     */
    @Override
    @NonTransactional
    public boolean isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(String principalId) {
        return accountDelegateDao.isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(principalId, dateTimeService.getCurrentSqlDate());
    }

    /**
     * Saves the given account delegate to the persistence store
     *
     * @param accountDelegate the account delegate to save
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveForMaintenanceDocument(AccountDelegate accountDelegate) {
        businessObjectService.linkAndSave(accountDelegate);
    }

    /**
     * Persists the given account delegate global maintenance document inactivations
     *
     * @param delegatesToInactivate the List of delegates to inactivate
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveInactivationsForGlobalMaintenanceDocument(List<PersistableBusinessObject> delegatesToInactivate) {
        if (delegatesToInactivate != null && !delegatesToInactivate.isEmpty()) {
            businessObjectService.save(delegatesToInactivate);
        }
    }

    /**
     * Persists the given account delegate global maintenance document changes
     *
     * @param delegatesToChange the List of delegates to change
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveChangesForGlobalMaintenanceDocument(List<PersistableBusinessObject> delegatesToChange) {
        if (delegatesToChange != null && !delegatesToChange.isEmpty()) {
            businessObjectService.save(delegatesToChange);
        }
    }

    /**
     * Updates the role that this delegate is part of, to account for the changes in this delegate
     */
    @Override
    @Transactional
    public void updateDelegationRole() {
        final RoleService roleManagementService = KimApiServiceLocator.getRoleService();
        final String roleId = roleManagementService.getRoleIdByNamespaceCodeAndName(KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME);
        if (!StringUtils.isBlank(roleId)) {
            List<RoleResponsibility> newRoleResp = roleManagementService.getRoleResponsibilities(roleId);
            KEWServiceLocator.getActionRequestService().updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(newRoleResp));
        }
    }
    protected Set<String> getChangedRoleResponsibilityIds( List<RoleResponsibility> newRoleResp){
        Set<String> lRet = new HashSet<String>();
          if(ObjectUtils.isNotNull(newRoleResp)){
            for(RoleResponsibility roleResp: newRoleResp){
                lRet.add(roleResp.getResponsibilityId());
            }
        }
        return lRet;
    }

    @NonTransactional
    public void setAccountDelegateDao(AccountDelegateDao accountDelegateDao) {
        this.accountDelegateDao = accountDelegateDao;
    }

    @NonTransactional
    public void setAccountDelegateGlobalDao(AccountDelegateGlobalDao accountDelegateGlobalDao) {
        this.accountDelegateGlobalDao = accountDelegateGlobalDao;
    }

    @NonTransactional
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    @NonTransactional
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
