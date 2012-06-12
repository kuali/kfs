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
package org.kuali.kfs.pdp.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.api.postalcode.PostalCodeService;

public class CustomerProfileRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileRule.class);

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= !errorMap.hasErrors();

        if (isValid) {
            if (collectionName.equals(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_BANKS)) {
                CustomerBank newCustomerBank = (CustomerBank) line;
                if(newCustomerBank.getDisbursementTypeCode()!=null){
                    // does the same disbursement type code exist?
                    CustomerProfile customerProfile = (CustomerProfile) document.getNewMaintainableObject().getBusinessObject();
                    for (CustomerBank bank : customerProfile.getCustomerBanks()) {
                        if (bank.getDisbursementTypeCode().equalsIgnoreCase(newCustomerBank.getDisbursementTypeCode())) {
                            errorMap.putError(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_ONE_BANK_PER_DISBURSEMENT_TYPE_CODE);
                            isValid = false;
                        }
                    }
                }
                if(newCustomerBank.getBank()!=null){
                    // check if the bank code type is allowed
                    newCustomerBank.refreshReferenceObject(PdpPropertyConstants.CUSTOMER_BANK);
                    Bank newBank = newCustomerBank.getBank();
                    if (newCustomerBank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.ACH) && !newBank.isBankAchIndicator()) {
                        errorMap.putError(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_ACH_BANK_NOT_ALLOWED, newBank.getBankCode() + " (" + newBank.getBankName() + ")");
                        isValid = false;
                    }
                    if (newCustomerBank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.CHECK) && !newBank.isBankCheckIndicator()) {
                        errorMap.putError(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_CHECK_BANK_NOT_ALLOWED, newBank.getBankCode() + " (" + newBank.getBankName() + ")");
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
        
        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        
        CustomerProfile customerProfile = (CustomerProfile) document.getNewMaintainableObject().getBusinessObject();
        
        if (customerProfile != null) {
            customerProfile.refreshNonUpdateableReferences();
            setStateFromZip(customerProfile);
        }
        
        boolean checkBankPresent = false;
        boolean ACHBankPresent = false;
        
        //Check if the customer profile has ACH transaction type information present.
        boolean customerHasACHType = customerProfile.getAchTransactionType() != null ? true : false;       
        
        //Check if customer profile has bank information
        if (customerProfile.getCustomerBanks().isEmpty()){
            putFieldError(KRADConstants.MAINTENANCE_ADD_PREFIX+"customerBanks."+PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_BANK_MISSING, customerProfile.getId().toString());
            
            isValid = false;
        }else{
            for (CustomerBank customerBank : customerProfile.getCustomerBanks()){

                // check if the bank code type is allowed
                Bank bank = customerBank.getBank();
                if (customerBank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.ACH) && !bank.isBankAchIndicator()) {
                    putFieldError(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_ACH_BANK_NOT_ALLOWED, bank.getBankCode() + " (" + bank.getBankName() + ")");
                    isValid = false;
                }
                if (customerBank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.CHECK) && !bank.isBankCheckIndicator()) {
                    putFieldError(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_CHECK_BANK_NOT_ALLOWED, bank.getBankCode() + " (" + bank.getBankName() + ")");
                    isValid = false;
                }
                
                //Check if customer profile has Check type Bank information
                if(customerBank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.CHECK)){
                    checkBankPresent = true;
                }                 
                //Check if customer profile has ACH type Bank information 
                if(customerBank.getDisbursementTypeCode().equalsIgnoreCase(PdpConstants.DisbursementTypeCodes.ACH)){
                        ACHBankPresent = true;
                 }                 
                if(checkBankPresent && ACHBankPresent){
                    break;
                }            
            }
            
            //Generate error message if check bank is not present.
            if(!checkBankPresent){
                isValid = false;
                putFieldError(KRADConstants.MAINTENANCE_ADD_PREFIX+"customerBanks."+PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_CHECK_BANK_REQUIRED);
            }
            
            //Generate error message if ACH bank is not present for profile with ACH disbursement type.
            if(customerHasACHType && !ACHBankPresent){
                isValid = false;
                putFieldError(KRADConstants.MAINTENANCE_ADD_PREFIX+"customerBanks."+PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpKeyConstants.ERROR_PDP_ACH_BANK_REQUIRED); 
            }
            
        }
        
        // KFSMI-5158 Uniqueness check only for new Customer Profiles 
        if (document.isNew()) { 
            isValid &= verifyChartUnitSubUnitIsUnique(customerProfile); 
        }
        
        return isValid;
    } 
    
    //KFSMI-8330
    /**
     * sets city name and state code on the form
     * 
     * @param customerProfile
     */
    protected void setStateFromZip(CustomerProfile customerProfile) {
        if (!StringUtils.isBlank(customerProfile.getZipCode())) {
            PostalCode zip = SpringContext.getBean(PostalCodeService.class).getPostalCode( "US"/*RICE_20_REFACTORME*/, customerProfile.getZipCode() );

            // If user enters a valid zip code, override city name and state code entered by user
            if (ObjectUtils.isNotNull(zip)) { // override old user inputs
                customerProfile.setCity(zip.getCityName());
                customerProfile.setStateCode(zip.getStateCode());
            }
        }
    }
    
    /**
     * Verifies that the chart/unit/sub-unit combination on this customer profile is unique
     * @param customerProfile the customer profile to check
     * @return true if the chart/unit/sub-unit is unique, false otherwise
     */
    protected boolean verifyChartUnitSubUnitIsUnique(CustomerProfile customerProfile) {
        boolean result = true;
        
        if (!StringUtils.isBlank(customerProfile.getChartCode()) && !StringUtils.isBlank(customerProfile.getUnitCode()) && !StringUtils.isBlank(customerProfile.getSubUnitCode())) {
            final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
            Map<String, Object> searchKeys = new HashMap<String, Object>();
            searchKeys.put(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE, customerProfile.getChartCode());
            searchKeys.put(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE, customerProfile.getUnitCode());
            searchKeys.put(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE, customerProfile.getSubUnitCode());
            
            final Collection foundCustomerProfiles = businessObjectService.findMatching(CustomerProfile.class, searchKeys);
            if (foundCustomerProfiles != null && foundCustomerProfiles.size() > 0) {
                result = false;
                putFieldError(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE, PdpKeyConstants.ERROR_CUSTOMER_PROFILE_CHART_UNIT_SUB_UNIT_NOT_UNIQUE, new String[] { customerProfile.getChartCode(), customerProfile.getUnitCode(), customerProfile.getSubUnitCode()});
            }
        }
        
        return result;
    }
    
}
