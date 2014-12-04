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
package org.kuali.kfs.coa.service;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;

/**
 * Services needed to inactivate organization reversion details when one of their respective parents -
 * either the organization reversion or the category that they relate to - are inactivated
 */
public interface OrganizationReversionDetailTrickleDownInactivationService {
    /**
     * Inactivates organization reversion details whose containing organization reversion is being inactivated,
     * and writes notes on the Organization Reversion maintenance document about it
     * @param organizationReversion the deactivating organization reversion
     * @param documentNumber the document id of the organization reversion document causing an inactivation
     */
    public abstract void trickleDownInactiveOrganizationReversionDetails(OrganizationReversion organizationReversion, String documentNumber);
    
    /**
     * Inactivates (or "boxes") organization reversion details whose related organization reversion category is being inactivated,
     * and writes notes on the Organization Reversion Category maintenance document about it
     * @param organizationReversionCategory the deactivating organization reversion category
     * @param documentNumber the document id of the organization reversion category document causing an inactivation
     */
    public abstract void trickleDownInactiveOrganizationReversionDetails(OrganizationReversionCategory organizationReversionCategory, String documentNumber);
    
    /**
     * Activates organization reversion details whose containing organization reversion is being inactivated,
     * and writes notes on the Organization Reversion maintenance document about it
     * @param organizationReversion the deactivating organization reversion
     * @param documentNumber the document id of the organization reversion document causing an inactivation
     */
    public abstract void trickleDownActiveOrganizationReversionDetails(OrganizationReversion organizationReversion, String documentNumber);
    
    /**
     * Activates (or "boxes") organization reversion details whose related organization reversion category is being inactivated,
     * and writes notes on the Organization Reversion Category maintenance document about it
     * @param organizationReversionCategory the deactivating organization reversion category
     * @param documentNumber the document id of the organization reversion category document causing an inactivation
     */
    public abstract void trickleDownActiveOrganizationReversionDetails(OrganizationReversionCategory organizationReversionCategory, String documentNumber);
}
