/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.module.chart.rules.MaintenancePreRulesBase;
import org.kuali.module.pdp.bo.PayeeAchAccount;

public class PayeeAchAccountPreRules extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeAchAccountPreRules.class);
    private PayeeAchAccount newPayeeAchAccount;

    private void setupConvenienceObjects(MaintenanceDocument document) {
        newPayeeAchAccount = (PayeeAchAccount) document.getNewMaintainableObject().getBusinessObject();
    }

    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        blankFields();

        return true;
    }

    // only 1 Payee Id field should be populated, the one chosen in Payee Type Code, the rest from the list should be blank
    private void blankFields() {
        String payeeIdTypeCd = newPayeeAchAccount.getPayeeIdentifierTypeCode();
        if ( payeeIdTypeCd != null ) {
            if (payeeIdTypeCd.equals("E")) {
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            }
            else if (payeeIdTypeCd.equals("V")) {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
            }
            else if (payeeIdTypeCd.equals("F")) {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            }
            else if (payeeIdTypeCd.equals("S")) {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            }
            else if (payeeIdTypeCd.equals("P")) {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            }
        }
    }
}
