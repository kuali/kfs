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
package org.kuali.kfs.module.purap.document;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.module.purap.document.service.ReceivingAddressService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * ReceivingAddressMaintainableImpl is a special implementation of FinancialSystemMaintainable for ReceivingAddresss. 
 * It generates extra locks for other receiving addresses related to the one being updated in the maintenance document,
 * and updates the ones affected during post-processing to enforce certain contraints among these objects. 
 */
public class ReceivingAddressMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReceivingAddressMaintainableImpl.class);
    
    /**
     * Generates the appropriate maintenance locks for {@link ReceivingAddress}
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        ReceivingAddress receivingAddress = (ReceivingAddress) this.businessObject;
        List<MaintenanceLock> locks = super.generateMaintenanceLocks();
        if ( receivingAddress.isDefaultIndicator() && receivingAddress.isActive() ) {
            locks.add(createMaintenanceLock(new String[] { "chartOfAccountsCode", "organizationCode", "defaultIndicator", "active" }));
        }
        return locks;
    }

    /**
     * Creates a maintenance lock for the field names supplied.
     * 
     * @param fieldNames
     * @return the maintenance lock for supplied field names
     */
    private MaintenanceLock createMaintenanceLock(String[] fieldNames) {
        MaintenanceLock lock = new MaintenanceLock();
        lock.setDocumentNumber(getDocumentNumber());
        lock.setLockingRepresentation(createLockingRepresentation(fieldNames));
        return lock;
    }

    /**
     * Creates a locking representation for the field names supplied.
     * 
     * @param fieldNames
     * @return locking representation string
     */
    private String createLockingRepresentation(String[] fieldNames) {
        StringBuilder lockRepresentation = new StringBuilder();

        lockRepresentation.append(ReceivingAddress.class.getName());
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
     * Returns the field value of a given field, converting the value to a String and encrypting it if necessary.
     * 
     * @param fieldName
     * @param ddService
     * @return string field value for a lock
     */
    private String retrieveFieldValueForLock(String fieldName, DataDictionaryService ddService, EncryptionService encryptionService) {
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
     * Checks if there's any active receiving address set to default other than this one; 
     * if so, set them to non-default. 
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader header) {
        super.doRouteStatusChange(header);

        ReceivingAddress ra = (ReceivingAddress) getBusinessObject();
        // proceed only if this bo is active and default.
        if ( !ra.isActive() || !ra.isDefaultIndicator() )
            return;
        
        WorkflowDocument workflowDoc = header.getWorkflowDocument();
        // this code is only executed when the final approval occurs
        if (workflowDoc.isProcessed()) {
            /*
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ra.getChartOfAccountsCode());
            criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, ra.getOrganizationCode());
            criteria.put(PurapPropertyConstants.RCVNG_ADDR_DFLT_IND, true);        
            criteria.put(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);        
            List<ReceivingAddress> addresses = (List)SpringContext.getBean(BusinessObjectService.class).findMatching(ReceivingAddress.class, criteria);
            */            
            Collection<ReceivingAddress> addresses  = SpringContext.getBean(ReceivingAddressService.class).findDefaultByChartOrg(ra.getChartOfAccountsCode(),ra.getOrganizationCode());                  
            for ( ReceivingAddress rai : addresses ) {
                if ( !rai.getReceivingAddressIdentifier().equals(ra.getReceivingAddressIdentifier()) ) {
                    rai.setDefaultIndicator(false);
                    SpringContext.getBean(BusinessObjectService.class).save(rai);
                }
            }
        }
    }

}
