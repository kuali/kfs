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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.ParameterService;

public class OrganizationOptionsPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        Set<String> readOnlySectionIds = super.getConditionallyReadOnlySectionIds(document);
        setRemitToAddressSectionEditable(readOnlySectionIds);
        return readOnlySectionIds;
    }

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        setRemitToNameEditable(readOnlyPropertyNames);
        setOrgPostalZipCodeEditable(readOnlyPropertyNames);
        setBillingOrgFieldsEditable(readOnlyPropertyNames, document);
        return readOnlyPropertyNames;
    }

    /**
     * 
     * Billing Chart/Org are always read-only on an edit.  Always.
     * 
     * They are editable on an Add, but only if KIM lets you in on an Add, 
     * but thats handled elsewhere.
     * 
     * @param readOnlyPropertyNames
     * @param document
     */
    private void setBillingOrgFieldsEditable(Set<String> readOnlyPropertyNames, MaintenanceDocument document) {
        if (document.isEdit()) {
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.CHART_OF_ACCOUNTS_CODE);
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CODE);
        }
    }
    
    /**
     * 
     * Sets the Remit-To Name (ie, OrgCheckPayableToName) to read only if thats how the system parameters are 
     * configured, otherwise leave it read/write.
     * 
     * @param readOnlyPropertyNames
     */
    private void setRemitToNameEditable(Set<String> readOnlyPropertyNames) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        String nameEditable = service.getParameterValue(OrganizationOptions.class, ArConstants.REMIT_TO_NAME_EDITABLE_IND);
        if ("N".equalsIgnoreCase(nameEditable)) {
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CHECK_PAYABLE_TO_NAME);
        }
    }
    
    /**
     * 
     * Sets the OrgPostalZipCode to readonly if thats what the system parameters say, otherwise leave it 
     * read/write.
     * 
     * @param readOnlyPropertyNames
     */
    private void setOrgPostalZipCodeEditable(Set<String> readOnlyPropertyNames) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        if (!service.getIndicatorParameter(KfsParameterConstants.ACCOUNTS_RECEIVABLE_DOCUMENT.class, ArConstants.ENABLE_SALES_TAX_IND) ){
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_POSTAL_ZIP_CODE);
        }
    }
    
    /**
     * 
     * Sets the whole Remit-To Address section to read-only if thats what the system parameter says, otherwise leave
     * it read/wrtie.
     * 
     * @param readOnlySectionIds
     */
    private void setRemitToAddressSectionEditable(Set<String> readOnlySectionIds) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        String addressEditable = service.getParameterValue(OrganizationOptions.class, ArConstants.REMIT_TO_ADDRESS_EDITABLE_IND);
        if ("N".equalsIgnoreCase(addressEditable)) {
            //readOnlySectionIds.add("Edit Organization Remit To Address");
            readOnlySectionIds.add("OrganizationOptionsMaintenanceDocument-EditOrganizationRemitToAddress");
            //readOnlySectionIds.add("OrganizationOptionsMaintenanceDocument-EditOrganizationRemitToAddress-parentBean");
        }
    }
    
}
