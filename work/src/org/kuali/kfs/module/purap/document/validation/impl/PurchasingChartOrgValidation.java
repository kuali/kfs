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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocumentBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingChartOrgValidation extends GenericValidation
{
    private OrganizationService organizationService;
    private ChartService chartService;
    
    /**
     * Validate the chart and organization code prior to submitting the document.
     * This has to be validated to protect against a person with an invalid
     * primary department code.
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {

        //GlobalVariables.getMessageMap().addToErrorPath("document");
        
        // Initialize the valid flag to true (we assume everything will be ok).
        boolean valid = true;
        
        // Get the document.
        PurchasingDocumentBase purapDoc = (PurchasingDocumentBase) event.getDocument();
        
        // Get the chart of accounts and organization code from the document.
        String chartOfAccountsCode = purapDoc.getChartOfAccountsCode();
        String organizationCode    = purapDoc.getOrganizationCode();
        
        // Get organization business object from DB.  If a valid object is
        // returned, we know that the COA and Org codes are valid; otherwise,
        // display and error message to the user.
        Organization organization = organizationService.getByPrimaryId(
                chartOfAccountsCode, 
                organizationCode);
        
        if (organization == null) {
            GlobalVariables.getMessageMap().putError(
                    "document.documentHeader.*",
                    PurapKeyConstants.ERROR_INVALID_COA_ORG_CODE);
            
            valid = false;
        }
        
        return valid;
    }

    /**
     * Sets the organizationService attribute value.
     * @param organizationService The organizationService to set.
     */
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Sets the chartService attribute value.
     * @param chartService The chartService to set.
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

}
