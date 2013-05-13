/*
 * Copyright 2010 The Kuali Foundation.
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
