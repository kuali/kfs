/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;

/**
 * This class...
 */
public class OrganizationOptionsAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#getFieldAuthorizations(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        setFieldsReadOnlyAccessMode(document, auths);
        return auths;
    }
    
    /**
     * 
     * This method...
     * @param auths
     */
    private void setFieldsReadOnlyAccessMode(MaintenanceDocument document, MaintenanceDocumentAuthorizations auths) {
    
        ParameterService service = SpringContext.getBean(ParameterService.class);
        
        String nameEditable = service.getParameterValue(OrganizationOptions.class, ArConstants.REMIT_TO_NAME_EDITABLE_IND);
        if (nameEditable.equalsIgnoreCase("N")) {
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CHECK_PAYABLE_TO_NAME);
        }
        
        String addressEditable = service.getParameterValue(OrganizationOptions.class, ArConstants.REMIT_TO_ADDRESS_EDITABLE_IND);
        if (addressEditable.equalsIgnoreCase("N")) {
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_ADDRESS_NAME);
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_LINE1_STREET_ADDRESS);
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_LINE2_STREET_ADDRESS);
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_CITY_NAME);
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_STATE_CODE);
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_REMIT_TO_ZIP_CODE);
        }
        
        if( !SpringContext.getBean(ParameterService.class).getIndicatorParameter(ParameterConstants.ACCOUNTS_RECEIVABLE_DOCUMENT.class, ArConstants.ENABLE_SALES_TAX_IND) ){
            auths.addHiddenAuthField(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_POSTAL_ZIP_CODE);
        }

        // Processing chart and org should be strictly read only when creating a new Org Options
        if(document.isNew()) {
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE);
            auths.addReadonlyAuthField(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE);
        }
        
    }
}
