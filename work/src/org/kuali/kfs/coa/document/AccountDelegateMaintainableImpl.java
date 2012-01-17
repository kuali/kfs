/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.document;

import java.security.GeneralSecurityException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.businessobject.AccountDelegateGlobal;
import org.kuali.kfs.coa.service.AccountDelegateService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is a special implementation of Maintainable specifically for Account Delegates. It was created to correctly update the
 * default Start Date on edits and copies, ala JIRA #KULRNE-62.
 */
public class AccountDelegateMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDelegateMaintainableImpl.class);

    /**
     * This method will reset AccountDelegate's Start Date to the current timestamp on edits and copies
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        this.setStartDateDefault();
        super.processAfterCopy( document, parameters );
    }

    /**
     * This method will reset AccountDelegate's Start Date to the current timestamp on edits and copies
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit()
     */
    @Override
    public void processAfterEdit( MaintenanceDocument document, Map<String,String[]> parameters ) {
        this.setStartDateDefault();
        super.processAfterEdit( document, parameters );
    }

    /**
     * This method sets the start date on {@link Delegate} BO
     */
    protected void setStartDateDefault() {
        if (this.businessObject != null && this.businessObject instanceof AccountDelegate) {
            AccountDelegate delegate = (AccountDelegate) this.businessObject;
            delegate.setAccountDelegateStartDate(new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
        }
    }

    /**
     * Generates the appropriate maintenance locks for the {@link Delegate}
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AccountDelegate delegate = (AccountDelegate) this.businessObject;
        List<MaintenanceLock> locks = super.generateMaintenanceLocks();
        if (delegate.isAccountsDelegatePrmrtIndicator()) {
            locks.add(createMaintenanceLock(new String[] { "chartOfAccountsCode", "accountNumber", "financialDocumentTypeCode", "accountsDelegatePrmrtIndicator" }));
        }
        return locks;
    }

    @Override
    public String getLockingDocumentId() {
       String lock = super.getLockingDocumentId();
       if (StringUtils.isNotBlank(lock))
           return lock;
       else {
           AccountDelegateService accountDelegateService = SpringContext.getBean(AccountDelegateService.class);
           lock = accountDelegateService.getLockingDocumentId(this, getDocumentNumber());
           return lock;
       }
    }
    
    /**
     * This method creates a maintenance lock for the field names supplied
     * 
     * @param fieldNames
     * @return the maintenance lock for supplied field names
     */
    protected MaintenanceLock createMaintenanceLock(String[] fieldNames) {
        MaintenanceLock lock = new MaintenanceLock();
        lock.setDocumentNumber(getDocumentNumber());
        lock.setLockingRepresentation(createLockingRepresentation(fieldNames));
        return lock;

    }

    /**
     * This method create a locking representation for the field names supplied
     * 
     * @param fieldNames
     * @return locking representation string
     */
    protected String createLockingRepresentation(String[] fieldNames) {
        StringBuilder lockRepresentation = new StringBuilder();

        lockRepresentation.append(AccountDelegate.class.getName());
        lockRepresentation.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);

        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class); 
        EncryptionService encryptionService = SpringContext.getBean(EncryptionService.class); 

        int count = 0;
        for (String fieldName : fieldNames) {
            lockRepresentation.append(fieldName);
            lockRepresentation.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(retrieveFieldValueForLock(fieldName, dataDictionaryService, encryptionService));
            if (count < (fieldNames.length - 1)) {
                lockRepresentation.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            }
            count += 1;
        }


        return lockRepresentation.toString();
    }

    /**
     * This method returns the field value of a given field, converting the value to a String and encrypting it if necessary
     * 
     * @param fieldName
     * @param ddService
     * @return string field value for a lock
     */
    protected String retrieveFieldValueForLock(String fieldName, DataDictionaryService ddService, EncryptionService encryptionService) {
        Object fieldValue = ObjectUtils.getPropertyValue(this.businessObject, fieldName);
        if (fieldValue == null) {
            fieldValue = "";
        }

        // check if field is a secure
        if (SpringContext.getBean(BusinessObjectAuthorizationService.class).attributeValueNeedsToBeEncryptedOnFormsAndLinks(getBoClass(), fieldName)) {
            try {
                fieldValue = encryptionService.encrypt(fieldValue);
            }
            catch (GeneralSecurityException e) {
                LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
                throw new RuntimeException("Unable to encrypt secure field for locking representation " + e.getMessage());
            }
        }
        return String.valueOf(fieldValue);
    }
    
    /**
     * This method created a MaintenanceLock for the chartOfAccountsCode and accountNumber for an AccountDelegateGlobal.
     *
     * @return the MainenanceLock
     */
    
    public MaintenanceLock createGlobalAccountLock() {
        
        String[] fields = {"chartOfAccountsCode", "accountNumber"};
        MaintenanceLock lock = new MaintenanceLock();
        lock.setDocumentNumber(getDocumentNumber());
        
        StringBuilder lockRepresentation = new StringBuilder();

        lockRepresentation.append(AccountDelegateGlobal.class.getName());
        lockRepresentation.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);

        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class); 
        EncryptionService encryptionService = SpringContext.getBean(EncryptionService.class); 
        
        int count = 0;
        for (String fieldName : fields) {
            lockRepresentation.append(fieldName);
            lockRepresentation.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(retrieveFieldValueForLock(fieldName, dataDictionaryService, encryptionService));
            if (count < (fields.length - 1)) {
                lockRepresentation.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            }
            count += 1;
        }

        lock.setLockingRepresentation(lockRepresentation.toString());
        
        return lock;
    }

    /**
     * Overridden so that after account delegate is saved, it updates the proper account delegate role
     * Defers saving to a service to guarantee that the delegate saves in a separate transaction
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        final AccountDelegate accountDelegate = (AccountDelegate)getBusinessObject();
        final AccountDelegateService accountDelegateService = SpringContext.getBean(AccountDelegateService.class);
        
        accountDelegateService.saveForMaintenanceDocument(accountDelegate);
        
        accountDelegateService.updateDelegationRole();
    }

    
}
