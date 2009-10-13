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
