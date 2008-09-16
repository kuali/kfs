/*
 * Copyright 2008 The Kuali Foundation.
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
import java.util.Set;

import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;

/**
 * A Maintainable implementation for ProjectCode; its main job is to implement GenericRoutingInfo for org review
 */
public class ProjectCodeMaintainableImpl extends KualiMaintainableImpl implements GenericRoutingInfo {
    private Set<RoutingData> routingInfo;

    /**
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#getRoutingInfo()
     */
    public Set getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Makes sure that routingInfo is properly initialized and gathers org review data for this maintainable
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        if (routingInfo == null) {
            routingInfo = new HashSet<RoutingData>();
        }
        routingInfo.add(getOrgReviewRoutingData());
    }
    
    /**
     * Creates the RoutingData object for org review on this maintained project code
     * @return the RoutingData for org review
     */
    protected RoutingData getOrgReviewRoutingData() {
        RoutingData orgsToReview = new RoutingData();
        orgsToReview.setRoutingType(KualiOrgReviewAttribute.class.getName());
        
        Set<OrgReviewRoutingData> routingSet = new HashSet<OrgReviewRoutingData>();
        routingSet.add(gatherOrgReviewRoutingData());
        orgsToReview.setRoutingSet(routingSet);
        
        return orgsToReview;
    }
    
    /**
     * Finds the organization to route to from the maintained project code
     * @return a properly initialized OrgReviewRoutingData object
     */
    protected OrgReviewRoutingData gatherOrgReviewRoutingData() {
        final ProjectCode projectCode = (ProjectCode)this.getBusinessObject();
        return new OrgReviewRoutingData(projectCode.getChartOfAccountsCode(), projectCode.getOrganizationCode());
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#setRoutingInfo(java.util.Set)
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

}
