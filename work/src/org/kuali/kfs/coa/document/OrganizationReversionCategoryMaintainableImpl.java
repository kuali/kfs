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
