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
package org.kuali.kfs.pdp.document.validation.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

public class CustomerProfileRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileRule.class);

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        isValid &= errorMap.isEmpty();

        if (isValid) {
            if (collectionName.equals(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_BANKS)) {
                CustomerBank newCustomerBank = (CustomerBank) line;

                CustomerProfile customerProfile = (CustomerProfile) document.getNewMaintainableObject().getBusinessObject();
                for (CustomerBank bank : customerProfile.getCustomerBanks()) {
                    if (bank.getDisbursementTypeCode().equalsIgnoreCase(newCustomerBank.getDisbursementTypeCode())) {
                        errorMap.putError(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_ONE_BANK_PER_DISBURSEMENT_TYPE_CODE);
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

}
