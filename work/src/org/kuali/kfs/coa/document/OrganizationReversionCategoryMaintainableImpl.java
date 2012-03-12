/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.document;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.service.OrganizationReversionDetailTrickleDownInactivationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A Maintainable for the Organization Reversion Category maintenance document
 */
public class OrganizationReversionCategoryMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Determines if this maint doc is inactivating an organization reversion category
     * @return true if the document is inactivating an active organization reversioncategory, false otherwise
     */
    protected boolean isInactivatingOrganizationReversionCategory() {
        // the account has to be closed on the new side when editing in order for it to be possible that we are closing the account
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && !((OrganizationReversionCategory) getBusinessObject()).isActive()) {
            OrganizationReversionCategory existingOrganizationReversionCategoryFromDB = retrieveExistingOrganizationReversionCategory();
            if (ObjectUtils.isNotNull(existingOrganizationReversionCategoryFromDB)) {
                // now see if the original account was not closed, in which case, we are closing the account
                if (existingOrganizationReversionCategoryFromDB.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Determines if this maint doc is activating an organization reversion category
     * @return true if the document is activating an inactive organization reversion category, false otherwise
     */
    protected boolean isActivatingOrganizationReversionCategory() {
        // the account has to be closed on the new side when editing in order for it to be possible that we are closing the account
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && ((OrganizationReversionCategory) getBusinessObject()).isActive()) {
            OrganizationReversionCategory existingOrganizationReversionCategoryFromDB = retrieveExistingOrganizationReversionCategory();
            if (ObjectUtils.isNotNull(existingOrganizationReversionCategoryFromDB)) {
                // now see if the original account was not closed, in which case, we are closing the account
                if (!existingOrganizationReversionCategoryFromDB.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Grabs the old version of this org reversion category from the database
     * @return the old version of this organization reversion category
     */
    protected OrganizationReversionCategory retrieveExistingOrganizationReversionCategory() {
        final OrganizationReversionCategory orgRevCategory = (OrganizationReversionCategory)getBusinessObject();
        Map<String, Object> pkMap = new HashMap<String, Object>();
        pkMap.put("organizationReversionCategoryCode", ((OrganizationReversionCategory)getBusinessObject()).getOrganizationReversionCategoryCode());
        final OrganizationReversionCategory oldOrgRevCategory = (OrganizationReversionCategory)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationReversionCategory.class, pkMap);
        return oldOrgRevCategory;
    }

    /**
     * Overridden to trickle down inactivation or activation to details
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        final boolean isActivatingOrgReversionCategory = isActivatingOrganizationReversionCategory();
        final boolean isInactivatingOrgReversionCategory = isInactivatingOrganizationReversionCategory();
        
        super.saveBusinessObject();
        
        if (isActivatingOrgReversionCategory) {
            SpringContext.getBean(OrganizationReversionDetailTrickleDownInactivationService.class).trickleDownActiveOrganizationReversionDetails((OrganizationReversionCategory)getBusinessObject(), getDocumentNumber());
        } else if (isInactivatingOrgReversionCategory) {
            SpringContext.getBean(OrganizationReversionDetailTrickleDownInactivationService.class).trickleDownInactiveOrganizationReversionDetails((OrganizationReversionCategory)getBusinessObject(), getDocumentNumber());
        }
    }

}
