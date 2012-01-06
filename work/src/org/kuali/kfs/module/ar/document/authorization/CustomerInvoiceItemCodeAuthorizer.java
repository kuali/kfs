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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;

public class CustomerInvoiceItemCodeAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        CustomerInvoiceItemCode itemCode = null;
        if (businessObject instanceof MaintenanceDocument) {
            FinancialSystemMaintenanceDocument maintDoc = (FinancialSystemMaintenanceDocument) businessObject;
            Maintainable newMaintainable = maintDoc.getNewMaintainableObject();
            itemCode = (CustomerInvoiceItemCode) newMaintainable.getBusinessObject();
        }
        else if (businessObject instanceof CustomerInvoiceItemCode) {
            itemCode = (CustomerInvoiceItemCode) businessObject;
        }

        if (itemCode != null) {
            attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, itemCode.getChartOfAccountsCode());
            attributes.put(KfsKimAttributes.ORGANIZATION_CODE, itemCode.getOrganizationCode());
        }
    }

}
