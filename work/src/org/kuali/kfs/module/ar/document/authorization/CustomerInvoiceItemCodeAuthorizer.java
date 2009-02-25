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

import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.maintenance.Maintainable;

public class CustomerInvoiceItemCodeAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        CustomerInvoiceItemCode itemCode = (CustomerInvoiceItemCode) businessObject;
        if (itemCode != null) {
            attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, itemCode.getChartOfAccountsCode());
            attributes.put(KfsKimAttributes.ORGANIZATION_CODE, itemCode.getOrganizationCode());
            
            //TODO should we also pull the processing org of this billing org, and then all
            //     the billing orgs for that processing org, and add all of them to the 
            //     role qualification?  Note sure yet.
            
        }
    }

    @Override
    protected void addPermissionDetails(BusinessObject businessObject, Map<String,String> attributes) {
        super.addPermissionDetails(businessObject, attributes);
        //TODO pulled this in, but not sure if we need it
    }
    
    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        Set<String> readOnlySectionIds = super.getSecurePotentiallyReadOnlySectionIds();
        
        //TODO not sure the right way to do this test below 
        
        // if currentuser is in Biller role then 
        //     readOnlySectionIds.add("CustomerInvoiceItemCodeMaintenanceDocument-EditBillingOrg");
        // end if
        
        return readOnlySectionIds;
    }
    
}
