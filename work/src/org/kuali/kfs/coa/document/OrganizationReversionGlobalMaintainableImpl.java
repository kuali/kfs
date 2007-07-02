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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.chart.bo.OrganizationReversionGlobalDetail;
import org.kuali.module.chart.bo.OrganizationReversionGlobal;
import org.kuali.module.chart.bo.OrganizationReversionGlobalOrganization;
import org.kuali.module.chart.service.OrganizationReversionService;

public class OrganizationReversionGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionGlobalMaintainableImpl.class);
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
     * This implementation locks all organization reversions that would be accessed
     * by this global organization reversion.  It does not lock any OrganizationReversionDetail objects,
     * as we expect that those will be inaccessible 
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#generateMaintenaceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> locks = new ArrayList<MaintenanceLock>();
        OrganizationReversionGlobal globalOrgRev = (OrganizationReversionGlobal)this.getBusinessObject();
        if (globalOrgRev.getUniversityFiscalYear() != null && globalOrgRev.getOrganizationReversionGlobalOrganizations() != null && globalOrgRev.getOrganizationReversionGlobalOrganizations().size() > 0) { // only generate locks if we're going to have primary keys
            for (OrganizationReversionGlobalOrganization orgRevOrg: globalOrgRev.getOrganizationReversionGlobalOrganizations()) {
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
     * Just like OrganizationReversionMaintainableImpl's setBusinessObject method populates the list of details
     * so there is one detail per active Organization Reversion Category, this method populates a list of 
     * Organization Reversion Change details.
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#setBusinessObject(org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public void setBusinessObject(PersistableBusinessObject businessObject) {
        super.setBusinessObject(businessObject);
        OrganizationReversionService organizationReversionService = SpringServiceLocator.getOrganizationReversionService();
        OrganizationReversionGlobal globalOrgRev = (OrganizationReversionGlobal) businessObject;
        List<OrganizationReversionGlobalDetail> details = globalOrgRev.getOrganizationReversionGlobalDetails();
        LOG.debug("Details size before adding categories = "+details.size());

        if (details == null) {
            details = new TypedArrayList(OrganizationReversionGlobalDetail.class);
            globalOrgRev.setOrganizationReversionGlobalDetails(details);
        }

        if (details.size() == 0) {

            Collection<OrganizationReversionCategory> categories = organizationReversionService.getCategoryList();
            for (OrganizationReversionCategory category : categories) {
                if (category.isOrganizationReversionCategoryActiveIndicator()){
                    OrganizationReversionGlobalDetail detail = new OrganizationReversionGlobalDetail();
                    detail.setOrganizationReversionCategoryCode(category.getOrganizationReversionCategoryCode());
                    detail.setOrganizationReversionCategory(category);
                    detail.setParentGlobalOrganizationReversion(globalOrgRev);
                    details.add(detail);
                }
            }
            LOG.debug("Details size after adding categories = "+details.size());
            Collections.sort(details, new CategoryComparator());
        }
        super.setBusinessObject(businessObject);
    }

    /**
     * Prevents Organization Reversion Change Details from being refreshed by a look up (because doing that refresh before
     * a save would wipe out the list of organization reversion change details).
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#isRelationshipRefreshable(java.lang.Class, java.lang.String)
     */
    @Override
    protected boolean isRelationshipRefreshable(Class boClass, String relationshipName) {
        if (relationshipName.equals("organizationReversionGlobalDetails")) {
            return false;
        } else {
            return super.isRelationshipRefreshable(boClass, relationshipName);
        }
    }

    /**
     * The org reversion detail collection does not behave like a true collection (no add lines). The records
     * on the collection should not have the delete option.
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#processGlobalsAfterRetrieve()
     */
    @Override
    protected void processGlobalsAfterRetrieve() {
        super.processGlobalsAfterRetrieve();
        for (OrganizationReversionGlobalDetail changeDetail : ((OrganizationReversionGlobal) businessObject).getOrganizationReversionGlobalDetails()){
            changeDetail.setNewCollectionRecord(false);
        }
    }
}
