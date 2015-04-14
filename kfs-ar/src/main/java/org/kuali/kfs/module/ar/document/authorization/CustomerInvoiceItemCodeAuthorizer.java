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
