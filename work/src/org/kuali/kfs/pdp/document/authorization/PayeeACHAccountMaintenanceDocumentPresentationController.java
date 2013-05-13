/*
 * Copyright 2011 The Kuali Foundation.
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
