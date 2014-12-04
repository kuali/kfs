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

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.PdpConstants.PayeeIdTypeCodes;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class PayeeACHAccountMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    /**
     * Adds the payeeEmailAddress field as readOnly if payee type is Employee or Entity.
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        
        PayeeACHAccount payeeAccount = (PayeeACHAccount)document.getNewMaintainableObject().getBusinessObject();
        String payeeIdTypeCode = payeeAccount.getPayeeIdentifierTypeCode();

        // make name and email address readOnly if payee type is Employee or Entity
        if (StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.EMPLOYEE) ||
                StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.ENTITY)) {
            readOnlyPropertyNames.add(PdpPropertyConstants.PAYEE_NAME);
            readOnlyPropertyNames.add(PdpPropertyConstants.PAYEE_EMAIL_ADDRESS);
        }
        // make name readOnly if payee type is Vendor
        else if (StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.VENDOR_ID)) {
            readOnlyPropertyNames.add(PdpPropertyConstants.PAYEE_NAME);
        }
        
        return readOnlyPropertyNames;                
    }

}
