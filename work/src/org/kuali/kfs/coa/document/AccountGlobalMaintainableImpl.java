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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountGlobal;
import org.kuali.kfs.coa.businessobject.AccountGlobalDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global accounts
 */
public class AccountGlobalMaintainableImpl extends KualiGlobalMaintainableImpl implements GenericRoutingInfo {
    private Set<RoutingData> routingInfo;

    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AccountGlobal accountGlobal = (AccountGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();

        for (AccountGlobalDetail detail : accountGlobal.getAccountGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockrep = new StringBuffer();

            lockrep.append(Account.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockrep.append("chartOfAccountsCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getChartOfAccountsCode() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            lockrep.append("accountNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getAccountNumber());

            maintenanceLock.setDocumentNumber(accountGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockrep.toString());
            maintenanceLocks.add(maintenanceLock);
        }
        return maintenanceLocks;
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
        
        routingInfo.add(getOrgReviewData());
    }
    
    /**
     * Generates a RoutingData object with the accounts to review
     * @return a properly initialized RoutingData object for account review
     */
    protected RoutingData getOrgReviewData() {
        RoutingData routingData = new RoutingData();
        routingData.setRoutingType(KualiOrgReviewAttribute.class.getName());
        
        routingData.setRoutingSet(gatherOrgsToReview());
        
        return routingData;
    }
    
    /**
     * Generates the set of OrgReviewRoutingData objects that should be taken into account while determining Org Review
     * @return a Set of OrgReviewRoutingData objects
     */
    protected Set<OrgReviewRoutingData> gatherOrgsToReview() {
        Set<OrgReviewRoutingData> orgsToReview = new HashSet<OrgReviewRoutingData>();
        
        final AccountGlobal accountGlobal = (AccountGlobal)getBusinessObject();
        
        for (AccountGlobalDetail accountGlobalDetail : accountGlobal.getAccountGlobalDetails()) {
            accountGlobalDetail.refreshReferenceObject("account");
            final OrgReviewRoutingData orgToReview = new OrgReviewRoutingData(accountGlobalDetail.getChartOfAccountsCode(), accountGlobalDetail.getAccount().getOrganizationCode());
            orgsToReview.add(orgToReview);
        }
        
        return orgsToReview;
    }
}
