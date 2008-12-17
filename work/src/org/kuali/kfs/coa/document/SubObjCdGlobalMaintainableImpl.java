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

import org.kuali.kfs.coa.businessobject.AccountGlobalDetail;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCodeGlobal;
import org.kuali.kfs.coa.businessobject.SubObjectCodeGlobalDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;

/**
 * This class provides some specific functionality for the {@link SubObjCdGlobal} maintenance document generateMaintenanceLocks -
 * generates maintenance locks on {@link SubObjCd}
 */
public class SubObjCdGlobalMaintainableImpl extends KualiGlobalMaintainableImpl implements GenericRoutingInfo {
    private Set<RoutingData> routingInfo;

    /**
     * This generates maintenance locks on {@link SubObjCd}
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        // create locking rep for each combination of account and object code
        List<MaintenanceLock> maintenanceLocks = new ArrayList();
        SubObjectCodeGlobal subObjCdGlobal = (SubObjectCodeGlobal) getBusinessObject();

        for (AccountGlobalDetail accountGlobalDetail : subObjCdGlobal.getAccountGlobalDetails()) {
            for (SubObjectCodeGlobalDetail subObjCdGlobalDetail : subObjCdGlobal.getSubObjCdGlobalDetails()) {
                MaintenanceLock maintenanceLock = new MaintenanceLock();
                maintenanceLock.setDocumentNumber(subObjCdGlobal.getDocumentNumber());

                StringBuffer lockrep = new StringBuffer();
                lockrep.append(SubObjectCode.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockrep.append("fiscalYear" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(subObjCdGlobalDetail.getUniversityFiscalYear() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockrep.append("chartOfAccountsCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(accountGlobalDetail.getChartOfAccountsCode() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockrep.append("accountNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(accountGlobalDetail.getAccountNumber() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockrep.append("financialObjectCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(subObjCdGlobalDetail.getFinancialObjectCode() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockrep.append("financialSubObjectCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(subObjCdGlobal.getFinancialSubObjectCode());

                maintenanceLock.setLockingRepresentation(lockrep.toString());
                maintenanceLocks.add(maintenanceLock);
            }
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
        
        routingData.setRoutingSet(gatherAccountsToReview());
        
        return routingData;
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
     * Generates the set of RoutingAccount objects that should be taken into account while determining Account Review
     * @return a Set of RoutingAccount objects
     */
    protected Set<RoutingAccount> gatherAccountsToReview() {
       Set<RoutingAccount> accountsToReview = new HashSet<RoutingAccount>();
       
       final SubObjectCodeGlobal subObjCdGlobal = (SubObjectCodeGlobal) getBusinessObject();

       for (AccountGlobalDetail accountGlobalDetail : subObjCdGlobal.getAccountGlobalDetails()) {
           final RoutingAccount accountToReview = new RoutingAccount(accountGlobalDetail.getChartOfAccountsCode(), accountGlobalDetail.getAccountNumber());
           accountsToReview.add(accountToReview);
       }
       
       return accountsToReview;
    }
    
    /**
     * Generates the set of OrgReviewRoutingData objects that should be taken into account while determining Org Review
     * @return a Set of OrgReviewRoutingData objects
     */
    protected Set<OrgReviewRoutingData> gatherOrgsToReview() {
        Set<OrgReviewRoutingData> orgsToReview = new HashSet<OrgReviewRoutingData>();
        
        final SubObjectCodeGlobal subObjCdGlobal = (SubObjectCodeGlobal) getBusinessObject();
        
        for (AccountGlobalDetail accountGlobalDetail : subObjCdGlobal.getAccountGlobalDetails()) {
            accountGlobalDetail.refreshReferenceObject("account");
            final OrgReviewRoutingData orgToReview = new OrgReviewRoutingData(accountGlobalDetail.getChartOfAccountsCode(), accountGlobalDetail.getAccount().getOrganizationCode());
            orgsToReview.add(orgToReview);
        }
        
        return orgsToReview;
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return SubObjectCode.class;
    }
}
