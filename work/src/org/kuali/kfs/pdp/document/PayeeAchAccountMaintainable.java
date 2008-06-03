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
package org.kuali.module.pdp.document.maintenance;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.util.Guid;
import org.kuali.core.util.ObjectUtils;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

public class PayeeAchAccountMaintainable extends KualiMaintainableImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeAchAccountMaintainable.class);
    
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        StringBuffer lockRepresentation = new StringBuffer(boClass.getName());
        lockRepresentation.append(KNSConstants.Maintenance.AFTER_CLASS_DELIM);

        PersistableBusinessObject bo = getBusinessObject();
        List keyFieldNames = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getLockingKeys(KNSServiceLocator.getMaintenanceDocumentDictionaryService().getDocumentTypeName(boClass));

        for (Iterator i = keyFieldNames.iterator(); i.hasNext();) {
            String fieldName = (String) i.next();
            Object fieldValue = ObjectUtils.getPropertyValue(bo, fieldName);
            if (fieldValue == null) {
                fieldValue = new Guid().toString();
            }
            
            // check if field is a secure
            String displayWorkgroup = KNSServiceLocator.getDataDictionaryService().getAttributeDisplayWorkgroup(getBoClass(), fieldName);
            if (StringUtils.isNotBlank(displayWorkgroup)) {
                try {
                    fieldValue = KNSServiceLocator.getEncryptionService().encrypt(fieldValue);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
                    throw new RuntimeException("Unable to encrypt secure field for locking representation " + e.getMessage());
                }
            }

            lockRepresentation.append(fieldName);
            lockRepresentation.append(KNSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(String.valueOf(fieldValue));
            if (i.hasNext()) {
                lockRepresentation.append(KNSConstants.Maintenance.AFTER_VALUE_DELIM);
            }
        }

        MaintenanceLock maintenanceLock = new MaintenanceLock();
        maintenanceLock.setDocumentNumber(documentNumber);
        maintenanceLock.setLockingRepresentation(lockRepresentation.toString());
        maintenanceLocks.add(maintenanceLock);
        return maintenanceLocks;
    }

}
