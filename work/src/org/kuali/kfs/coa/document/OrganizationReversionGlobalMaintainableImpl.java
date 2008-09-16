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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalDetail;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalOrganization;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This class provides some specific functionality for the {@link OrganizationReversionGlobal} maintenance document inner class for
 * doing comparisons on {@link OrganizationReversionCategory} generateMaintenanceLocks - generates the appropriate maintenance locks
 * on {@link OrganizationReversion} setBusinessObject - populates the {@link OrganizationReversionGlobalDetail}s
 * isRelationshipRefreshable - makes sure that {@code organizationReversionGlobalDetails} isn't wiped out accidentally
 * processGlobalsAfterRetrieve - provides special handling for the details (which aren't a true collection)
 */
public class OrganizationReversionGlobalMaintainableImpl extends KualiGlobalMaintainableImpl implements GenericRoutingInfo {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionGlobalMaintainableImpl.class);
    private Set<RoutingData> routingInfo;

    /**
     * This class is an inner class for comparing two {@link OrganizationReversionCategory}s
     */
    private class CategoryComparator implements Comparator<OrganizationReversionGlobalDetail> {
        public int compare(OrganizationReversionGlobalDetail detailA, OrganizationReversionGlobalDetail detailB) {
            OrganizationReversionCategory categoryA = detailA.getOrganizationReversionCategory();
            OrganizationReversionCategory categoryB = detailB.getOrganizationReversionCategory();

            String code0 = categoryA.getOrganizationReversionCategoryCode();
            String code1 = categoryB.getOrganizationReversionCategoryCode();

            return code0.compareTo(code1);
        }
    }

    /**
     * This implementation locks all organization reversions that would be accessed by this global organization reversion. It does
     * not lock any OrganizationReversionDetail objects, as we expect that those will be inaccessible
     * 
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#generateMaintenaceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> locks = new ArrayList<MaintenanceLock>();
        OrganizationReversionGlobal globalOrgRev = (OrganizationReversionGlobal) this.getBusinessObject();
        if (globalOrgRev.getUniversityFiscalYear() != null && globalOrgRev.getOrganizationReversionGlobalOrganizations() != null && globalOrgRev.getOrganizationReversionGlobalOrganizations().size() > 0) { // only generate locks if we're going to have primary keys
            for (OrganizationReversionGlobalOrganization orgRevOrg : globalOrgRev.getOrganizationReversionGlobalOrganizations()) {
                MaintenanceLock maintenanceLock = new MaintenanceLock();
                maintenanceLock.setDocumentNumber(globalOrgRev.getDocumentNumber());

                StringBuffer lockRep = new StringBuffer();
                lockRep.append(OrganizationReversion.class.getName());
                lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockRep.append("chartOfAccountsCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(orgRevOrg.getChartOfAccountsCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("universityFiscalYear");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(globalOrgRev.getUniversityFiscalYear().toString());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("organizationCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(orgRevOrg.getOrganizationCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);

                maintenanceLock.setLockingRepresentation(lockRep.toString());
                locks.add(maintenanceLock);
            }
        }

        return locks;
    }

    /**
     * Just like OrganizationReversionMaintainableImpl's setBusinessObject method populates the list of details so there is one
     * detail per active Organization Reversion Category, this method populates a list of Organization Reversion Change details.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#setBusinessObject(org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public void setBusinessObject(PersistableBusinessObject businessObject) {
        super.setBusinessObject(businessObject);
        OrganizationReversionService organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        OrganizationReversionGlobal globalOrgRev = (OrganizationReversionGlobal) businessObject;
        List<OrganizationReversionGlobalDetail> details = globalOrgRev.getOrganizationReversionGlobalDetails();
        LOG.debug("Details size before adding categories = " + details.size());

        if (details == null) {
            details = new TypedArrayList(OrganizationReversionGlobalDetail.class);
            globalOrgRev.setOrganizationReversionGlobalDetails(details);
        }

        if (details.size() == 0) {

            Collection<OrganizationReversionCategory> categories = organizationReversionService.getCategoryList();
            for (OrganizationReversionCategory category : categories) {
                if (category.isActive()) {
                    OrganizationReversionGlobalDetail detail = new OrganizationReversionGlobalDetail();
                    detail.setOrganizationReversionCategoryCode(category.getOrganizationReversionCategoryCode());
                    detail.setOrganizationReversionCategory(category);
                    detail.setParentGlobalOrganizationReversion(globalOrgRev);
                    details.add(detail);
                }
            }
            LOG.debug("Details size after adding categories = " + details.size());
            Collections.sort(details, new CategoryComparator());
        }
        super.setBusinessObject(businessObject);
    }

    /**
     * Prevents Organization Reversion Change Details from being refreshed by a look up (because doing that refresh before a save
     * would wipe out the list of organization reversion change details).
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isRelationshipRefreshable(java.lang.Class, java.lang.String)
     */
    @Override
    protected boolean isRelationshipRefreshable(Class boClass, String relationshipName) {
        if (relationshipName.equals("organizationReversionGlobalDetails")) {
            return false;
        }
        else {
            return super.isRelationshipRefreshable(boClass, relationshipName);
        }
    }

    /**
     * The org reversion detail collection does not behave like a true collection (no add lines). The records on the collection
     * should not have the delete option.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#processGlobalsAfterRetrieve()
     */
    @Override
    protected void processGlobalsAfterRetrieve() {
        super.processGlobalsAfterRetrieve();
        for (OrganizationReversionGlobalDetail changeDetail : ((OrganizationReversionGlobal) businessObject).getOrganizationReversionGlobalDetails()) {
            changeDetail.setNewCollectionRecord(false);
        }
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
        
        final OrganizationReversionGlobal organizationReversionGlobal = (OrganizationReversionGlobal)getBusinessObject();
        
        for (OrganizationReversionGlobalOrganization orgRevOrg : organizationReversionGlobal.getOrganizationReversionGlobalOrganizations()) {
            final OrgReviewRoutingData orgToReview = new OrgReviewRoutingData(orgRevOrg.getChartOfAccountsCode(), orgRevOrg.getOrganizationCode());
            orgsToReview.add(orgToReview);
        }
        
        return orgsToReview;
    }
}
