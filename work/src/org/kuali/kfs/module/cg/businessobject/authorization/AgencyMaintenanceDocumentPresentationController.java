/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject.authorization;

import java.util.Set;

import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Presentation Controller for Agency
 */
public class AgencyMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {


    /**
     * Overriding method to set further fields as required based on the condition that the CG Module Enhanced validations are turned
     * on.
     *
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyRequiredPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyRequiredPropertyNames(MaintenanceDocument document) {

        Set<String> requiredPropertyNames = super.getConditionallyRequiredPropertyNames(document);

        // Determine if CG and Billing enhancements are ON from system parameters
        boolean billingEnhacementsInd = SpringContext.getBean(ContractsAndGrantsModuleRetrieveService.class).isContractsGrantsBillingEnhancementsActive();

        return requiredPropertyNames;
    }

    /**
     * Hiding the Customer tab when CGB is turned off to avoid confusion.
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenSectionIds(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> hiddenSectionIds = super.getConditionallyHiddenSectionIds(businessObject);

        if(!isContractsGrantsBillingEnhancementsActive()){
            // Hide the Customer tab when Contracts and
            // Grants Billing enhancements are disabled
            hiddenSectionIds.add(CGConstants.SectionId.AGENCY_CUSTOMER_SECTION_ID);
        }

        return hiddenSectionIds;
    }

    /**
     * Checks ENABLE_CG_BILLING_ENHANCEMENTS_IND parameter to determine
     * if enhancements are active.
     *
     * @return true if Contracts and Grants Billing enhancements are enabled
     */
    private boolean isContractsGrantsBillingEnhancementsActive() {

        return SpringContext.getBean(ContractsAndGrantsModuleRetrieveService.class).isContractsGrantsBillingEnhancementsActive();
    }

}
