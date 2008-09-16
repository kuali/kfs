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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.SubAccountTrickleDownInactivationService;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class overrides the saveBusinessObject() method which is called during post process from the KualiPostProcessor so that it
 * can automatically deactivate the Sub-Accounts related to the account It also overrides the processAfterCopy so that it sets
 * specific fields that shouldn't be copied to default values {@link KualiPostProcessor}
 */
public class KualiAccountMaintainableImpl extends KualiMaintainableImpl implements GenericRoutingInfo {
    private static final Logger LOG = Logger.getLogger(KualiAccountMaintainableImpl.class);
    
    private Set<RoutingData> routingInfo;
    
    /**
     * Automatically deactivates {@link SubAccount}s after saving the {@link Account}
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        boolean isClosingAccount = isClosingAccount();
        
        // make sure we save account first
        super.saveBusinessObject();
        
        // if we're closing the account, then rely on the trickle-down inactivation services to trickle-down inactivate the sub-accounts
        if (isClosingAccount) {
            SpringContext.getBean(SubAccountTrickleDownInactivationService.class).trickleDownInactivateSubAccounts((Account) getBusinessObject(), documentNumber);
            SpringContext.getBean(SubObjectTrickleDownInactivationService.class).trickleDownInactivateSubObjects((Account) getBusinessObject(), documentNumber);
        }
    }

    /**
     * After a copy is done set specific fields on {@link Account} to default values
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        Account account = (Account) this.getBusinessObject();
        account.setAccountCreateDate(null); // account's pre-rules will fill this field in
        account.setAccountEffectiveDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        account.setActive(true);
        super.processAfterCopy( document, parameters );
    }

    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = super.generateMaintenanceLocks();
        boolean isClosingAccount = false;
        
        if (isClosingAccount()) {
            maintenanceLocks.addAll(SpringContext.getBean(SubAccountTrickleDownInactivationService.class).generateTrickleDownMaintenanceLocks((Account) getBusinessObject(), documentNumber));
            maintenanceLocks.addAll(SpringContext.getBean(SubObjectTrickleDownInactivationService.class).generateTrickleDownMaintenanceLocks((Account) getBusinessObject(), documentNumber));
        }
        return maintenanceLocks;
    }

    protected Account retrieveExistingAccountFromDB() {
        Account newAccount = (Account) getBusinessObject();
        Account oldAccount = SpringContext.getBean(AccountService.class).getByPrimaryId(newAccount.getChartOfAccountsCode(), newAccount.getAccountNumber());
        return oldAccount;
    }
    
    protected boolean isClosingAccount() {
        // the account has to be closed on the new side when editing in order for it to be possible that we are closing the account
        if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && !((Account) getBusinessObject()).isActive()) {
            Account existingAccountFromDB = retrieveExistingAccountFromDB();
            if (ObjectUtils.isNotNull(existingAccountFromDB)) {
                // now see if the original account was not closed, in which case, we are closing the account
                if (!existingAccountFromDB.isActive()) {
                    return true;
                }
            }
        }
        return false;
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
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        if (routingInfo == null) {
            routingInfo = new HashSet<RoutingData>();
        }
        routingInfo.add(getOrgReviewRoutingInfo());
    }
    
    /**
     * Generates org review data for this account maintenance document
     * @return a RoutingData instance with the org review information for this document
     */
    protected RoutingData getOrgReviewRoutingInfo() {
        RoutingData routingData = new RoutingData();
        routingData.setRoutingType(KualiOrgReviewAttribute.class.getName());
        
        Set<OrgReviewRoutingData> routingSet = new HashSet<OrgReviewRoutingData>();
        routingSet.add(gatherOrgToReview());
        routingData.setRoutingSet(routingSet);
        return routingData;
    }
    
    /**
     * Returns the organization this account maintenance document should route to
     * @return a property initialized OrgReviewRoutingData instance
     */
    protected OrgReviewRoutingData gatherOrgToReview() {
        final Account account = (Account)getBusinessObject();
        return new OrgReviewRoutingData(account.getChartOfAccountsCode(), account.getOrganizationCode());
    }
}
