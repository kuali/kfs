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
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MessageMap;

public class CustomerProfileRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileRule.class);

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.isEmpty();

        if (isValid) {
            if (collectionName.equals(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_BANKS)) {
                CustomerBank newCustomerBank = (CustomerBank) line;

                CustomerProfile customerProfile = (CustomerProfile) document.getNewMaintainableObject().getBusinessObject();
                for (CustomerBank bank : customerProfile.getCustomerBanks()) {
                    if (bank.getDisbursementTypeCode().equalsIgnoreCase(newCustomerBank.getDisbursementTypeCode())) {
                        errorMap.putError( PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_ONE_BANK_PER_DISBURSEMENT_TYPE_CODE);
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }
    
    /**
     * KFSMI-3771
     * This method checks if the Disbursement Bank information is provided in the Customer profile.
     * If Bank information is provided; It's required that a Check bank is present.
     * If the Customer profile has ACH disbursement type information provided, it should have an ACH bank Information as well.
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     * @param MaintenanceDocument  - CustomerProfileMaintenanceDocument
     * @return boolean - true if business rules are satisfied; false otherwise.
     */    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid =true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        
        CustomerProfile customerProfile = (CustomerProfile) document.getNewMaintainableObject().getBusinessObject();
        
        boolean checkBankPresent = false;
        boolean ACHBankPresent = false;
        
        //Check if the customer profile has ACH transaction type information present.
        boolean customerHasACHType = customerProfile.getAchTransactionType() != null ? true : false;       
        
        //Check if customer profile has bank information
        if (customerProfile.getCustomerBanks().isEmpty()){
            putFieldError(KNSConstants.MAINTENANCE_ADD_PREFIX+"customerBanks."+PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_BANK_MISSING, customerProfile.getId().toString());
            
            isValid = false;
        }else{
            for (CustomerBank bank : customerProfile.getCustomerBanks()){
                
                //Check if customer profile has Check type Bank information
                if(bank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.CHECK)){
                    checkBankPresent = true;
                }                 
                //Check if customer profile has ACH type Bank information 
                if(bank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.ACH)){
                        ACHBankPresent = true;
                 }                 
                if(checkBankPresent && ACHBankPresent){
                    break;
                }            
            }
            
            //Generate error message if check bank is not present.
            if(!checkBankPresent){
                isValid = false;
                putFieldError(KNSConstants.MAINTENANCE_ADD_PREFIX+"customerBanks."+PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_CHECK_BANK_REQUIRED);
            }
            
            //Generate error message if ACH bank is not present for profile with ACH disbursement type.
            if(customerHasACHType && !ACHBankPresent){
                isValid = false;
                putFieldError(KNSConstants.MAINTENANCE_ADD_PREFIX+"customerBanks."+PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_ACH_BANK_REQUIRED); 
            }
            
        }
        
        return isValid;
    } 
    
}
