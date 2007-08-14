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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.chart.bo.OrganizationReversionDetail;
import org.kuali.module.chart.service.OrganizationReversionService;

public class OrganizationReversionMaintainableImpl extends KualiMaintainableImpl {

    /**
     * 
     * This comparator is used internally for sorting the list of categories
     * 
     * 
     */
    private class categoryComparator implements Comparator<OrganizationReversionDetail> {

        public int compare(OrganizationReversionDetail detail0, OrganizationReversionDetail detail1) {

            OrganizationReversionCategory category0 = detail0.getOrganizationReversionCategory();
            OrganizationReversionCategory category1 = detail1.getOrganizationReversionCategory();

            String code0 = category0.getOrganizationReversionCategoryCode();
            String code1 = category1.getOrganizationReversionCategoryCode();

            return code0.compareTo(code1);
        }

    }

    public Map populateBusinessObject(Map fieldValues) {
        Map result = super.populateBusinessObject(fieldValues);
        return result;
    }

    /**
     * pre-populate the static list of details with each category
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#setBusinessObject(org.kuali.core.bo.BusinessObject)
     */
    public void setBusinessObject(PersistableBusinessObject businessObject) {

        OrganizationReversionService organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        OrganizationReversion organizationReversion = (OrganizationReversion) businessObject;
        List<OrganizationReversionDetail> details = organizationReversion.getOrganizationReversionDetail();

        if (details == null) {
            details = new TypedArrayList(OrganizationReversionDetail.class);
            organizationReversion.setOrganizationReversionDetail(details);
        }

        if (details.size() == 0) {

            Collection<OrganizationReversionCategory> categories = organizationReversionService.getCategoryList();

            for (OrganizationReversionCategory category : categories) {
                if (category.isOrganizationReversionCategoryActiveIndicator()){
                    OrganizationReversionDetail detail = new OrganizationReversionDetail();
                    detail.setOrganizationReversionCategoryCode(category.getOrganizationReversionCategoryCode());
                    detail.setOrganizationReversionCategory(category);
                    details.add(detail);
                }
            }

            Collections.sort(details, new categoryComparator());

        }

        super.setBusinessObject(businessObject);
    }
    
    /**
     * A method that prevents lookups from refreshing the Organization Reversion Detail list (because, if it is refreshed before
     * a save...it ends up destroying the list).
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#isRelationshipRefreshable(java.lang.Class, java.lang.String)
     */
    @Override
    protected boolean isRelationshipRefreshable(Class boClass, String relationshipName) {
        if (relationshipName.equals("organizationReversionDetail")) {
            return false;
        } else {
            return super.isRelationshipRefreshable(boClass, relationshipName);
        }
    }

}
