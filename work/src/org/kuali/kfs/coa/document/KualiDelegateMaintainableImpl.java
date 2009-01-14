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
package org.kuali.kfs.coa.document;

import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class is a special implementation of Maintainable specifically for Account Delegates. It was created to correctly update the
 * default Start Date on edits and copies, ala JIRA #KULRNE-62.
 */
public class KualiDelegateMaintainableImpl extends KualiMaintainableImpl implements GenericRoutingInfo {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDelegateMaintainableImpl.class);
    
    private Set<RoutingData> routingInfo;

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
            delegate.setAccountDelegateStartDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
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

    /**
     * This method creates a maintenance lock for the field names supplied
     * 
     * @param fieldNames
     * @return the maintenance lock for supplied field names
     */
    protected MaintenanceLock createMaintenanceLock(String[] fieldNames) {
        MaintenanceLock lock = new MaintenanceLock();
        lock.setDocumentNumber(this.documentNumber);
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

        DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        EncryptionService encryptionService = KNSServiceLocator.getEncryptionService();

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
     * Gets the routingInfo attribute. 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

    /**
     * Makes sure the routingInfo property is initialized and populates account review and org review data 
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        if (routingInfo == null) {
            routingInfo = new HashSet<RoutingData>();
        }
        
        routingInfo.add(getAccountReviewData());
        routingInfo.add(getOrgReviewData());
    }
    
    /**
     * Generates a RoutingData object with the accounts to review
     * @return a properly initialized RoutingData object for account review
     */
    protected RoutingData getAccountReviewData() {
        RoutingData routingData = new RoutingData();
        routingData.setRoutingType(KualiAccountAttribute.class.getName());
        
        Set<RoutingAccount> routingSet = new HashSet<RoutingAccount>();
        routingSet.add(gatherAccountToReview());
        routingData.setRoutingSet(routingSet);
        
        return routingData;
    }
    
    /**
     * @return an OrgReviewRoutingData object populated with the account information that this maintenance document should route to
     */
    protected RoutingAccount gatherAccountToReview() {
        final AccountDelegate delegate = (AccountDelegate)getBusinessObject();
        return new RoutingAccount(delegate.getChartOfAccountsCode(), delegate.getAccountNumber());
    }
    
    /**
     * Generates a RoutingData object with the accounts to review
     * @return a properly initialized RoutingData object for account review
     */
    protected RoutingData getOrgReviewData() {
        RoutingData routingData = new RoutingData();
        routingData.setRoutingType(KualiOrgReviewAttribute.class.getName());
        
        Set<OrgReviewRoutingData> routingSet = new HashSet<OrgReviewRoutingData>();
        routingSet.add(gatherOrgToReview());
        routingData.setRoutingSet(routingSet);
        
        return routingData;
    }
    
    /**
     * @return an OrgReviewRoutingData object populated with the organization information that this maintenance document should route to
     */
    protected OrgReviewRoutingData gatherOrgToReview() {
        final AccountDelegate delegate = (AccountDelegate)getBusinessObject();
        delegate.refreshReferenceObject("account");
        return new OrgReviewRoutingData(delegate.getChartOfAccountsCode(), delegate.getAccount().getOrganizationCode());
    }
}
