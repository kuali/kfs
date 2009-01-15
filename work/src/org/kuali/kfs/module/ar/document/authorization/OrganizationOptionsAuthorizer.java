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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.OrganizationOptionsMaintainableImpl;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;

/**
 * 
 */
public class OrganizationOptionsAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        FinancialSystemMaintenanceDocument maintDoc = (FinancialSystemMaintenanceDocument) businessObject;
        OrganizationOptionsMaintainableImpl newMaintainable = (OrganizationOptionsMaintainableImpl) maintDoc.getNewMaintainableObject();
        
        // Only load the billing chart/org and processing chart/org into the 
        // role qualification if its an Edit.
        //
        // If its a New, then only someone 
        // in the AR Processor role can launch, and thats a non-qualified role.
        //
        // Edit launches can be done by an AR Biller or AR Processor, but only if 
        // their home org matches either the BillingOrg or ProcessingOrg.
        //
        // The KIM role config should do all that for us, as long as we load up the 
        // proper qualifications at the proper time.
        //
        if (maintDoc.isEdit()) {
            if (newMaintainable != null) {
                OrganizationOptions orgOpts = (OrganizationOptions) newMaintainable.getBusinessObject();
                if (StringUtils.isNotBlank(orgOpts.getChartOfAccountsCode()) && StringUtils.isNotBlank(orgOpts.getOrganizationCode())) {
                    attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, orgOpts.getChartOfAccountsCode());
                    attributes.put(KfsKimAttributes.ORGANIZATION_CODE, orgOpts.getOrganizationCode());
                }
                if (StringUtils.isNotBlank(orgOpts.getProcessingChartOfAccountCode()) && StringUtils.isNotBlank(orgOpts.getProcessingOrganizationCode())) {
                    attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, orgOpts.getProcessingChartOfAccountCode());
                    attributes.put(KfsKimAttributes.ORGANIZATION_CODE, orgOpts.getProcessingOrganizationCode());
                }
            }
        }
        
    }

}

