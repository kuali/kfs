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
package org.kuali.module.chart.maintenance;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.rice.KNSServiceLocator;

/**
 * 
 * This class is a special implementation of Maintainable specifically for Account Delegates.  It was created to correctly update the
 * default Start Date on edits and copies, ala JIRA #KULRNE-62.
 */
public class KualiDelegateMaintainableImpl extends KualiMaintainableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDelegateMaintainableImpl.class);
    /**
     * This method will reset AccountDelegate's Start Date to the current timestamp on edits and copies
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterRetrieve()
     */
    public void processAfterCopy() {
        this.setStartDateDefault();
        super.processAfterCopy();
    }
    
    public void processAfterEdit() {
        this.setStartDateDefault();
        super.processAfterEdit();
    }
    
    private void setStartDateDefault() {
        if (this.businessObject != null && this.businessObject instanceof Delegate) {
            Delegate delegate = (Delegate)this.businessObject;
            delegate.setAccountDelegateStartDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        }
    }

    /**
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        Delegate delegate = (Delegate)this.businessObject;
        List<MaintenanceLock> locks = super.generateMaintenanceLocks();
        if (delegate.isAccountsDelegatePrmrtIndicator()) {
            locks.add(createMaintenanceLock(new String[]{"chartOfAccountsCode","accountNumber","financialDocumentTypeCode","accountsDelegatePrmrtIndicator"}));
        }
        return locks;
    }
    
    /**
     * 
     * This method creates a maintenance lock for the field names supplied
     * @param fieldNames
     * @return
     */
    private MaintenanceLock createMaintenanceLock(String[] fieldNames) {
        MaintenanceLock lock = new MaintenanceLock();
        lock.setDocumentNumber(this.documentNumber);
        lock.setLockingRepresentation(createLockingRepresentation(fieldNames));
        return lock;

    }
    
    /**
     * 
     * This method create a locking representation for the field names supplied
     * @param fieldNames
     * @return
     */
    private String createLockingRepresentation(String[] fieldNames) {
        StringBuilder lockRepresentation = new StringBuilder();
        
        lockRepresentation.append(Delegate.class.getName());
        lockRepresentation.append(RiceConstants.Maintenance.AFTER_CLASS_DELIM);
        
        DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        EncryptionService encryptionService = KNSServiceLocator.getEncryptionService();
        
        int count = 0;
        for (String fieldName: fieldNames) {
            lockRepresentation.append(fieldName);
            lockRepresentation.append(RiceConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(retrieveFieldValueForLock(fieldName, dataDictionaryService, encryptionService));
            if (count < (fieldNames.length - 1)) {
                lockRepresentation.append(RiceConstants.Maintenance.AFTER_VALUE_DELIM);
            }
            count += 1;
        }
        
        
        return lockRepresentation.toString();
    }
    
    /**
     * 
     * This method returns the field value of a given field, converting the value to a String and encrypting it if necessary
     * @param fieldName
     * @param ddService
     * @return
     */
    private String retrieveFieldValueForLock(String fieldName, DataDictionaryService ddService, EncryptionService encryptionService) {
        Object fieldValue = ObjectUtils.getPropertyValue(this.businessObject, fieldName);
        if (fieldValue == null) {
            fieldValue = "";
        }
        
        // check if field is a secure
        String displayWorkgroup = ddService.getAttributeDisplayWorkgroup(getBoClass(), fieldName);
        if (StringUtils.isNotBlank(displayWorkgroup)) {
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
}
