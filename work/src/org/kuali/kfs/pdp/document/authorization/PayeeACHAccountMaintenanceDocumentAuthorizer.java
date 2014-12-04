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
package org.kuali.kfs.pdp.document.authorization;

import java.util.Map;

import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class PayeeACHAccountMaintenanceDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    /**
     * Overridden to add the Payee ACH Account Transaction Type to the qualification
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.kns.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> qualifications) {
        super.addRoleQualification(businessObject, qualifications);

        // KFSCNTRB-1672: Necessary for "Open Document" permission
        if (businessObject instanceof MaintenanceDocument) {
            businessObject = ((MaintenanceDocument) businessObject).getNewMaintainableObject().getBusinessObject();
        }

        if (businessObject instanceof PayeeACHAccount) {
            final PayeeACHAccount payeeACHAccount = (PayeeACHAccount)businessObject;
            qualifications.put(KfsKimAttributes.ACH_TRANSACTION_TYPE_CODE, payeeACHAccount.getAchTransactionType());
        }
    }

}
