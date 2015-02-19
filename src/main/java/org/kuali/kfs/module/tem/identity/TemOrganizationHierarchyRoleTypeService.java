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
package org.kuali.kfs.module.tem.identity;

import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

@SuppressWarnings("deprecation")
public abstract class TemOrganizationHierarchyRoleTypeService extends RoleTypeServiceBase {

    private TravelerService travelerService;

    public static final String PERFORM_QUALIFIER_MATCH = "performQualifierMatch";

    public boolean isParentOrg(String qualificationChartCode, String qualificationOrgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy) {
        return getTravelerService().isParentOrg(qualificationChartCode, qualificationOrgCode, roleChartCode, roleOrgCode, descendHierarchy);
    }

    /**
     * Gets the travelerService attribute.
     * @return Returns the travelerService.
     */
    public TravelerService getTravelerService() {
        return travelerService;
    }

    /**
     * Sets the travelerService attribute value.
     * @param travelerService The travelerService to set.
     */
    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }



}
