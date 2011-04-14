/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.module.external.kc.document.validation.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;
import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.service.impl.AccountCreationServiceImpl;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 */
public class AccountAutoCreateDefaultsRule extends KfsMaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountAutoCreateDefaultsRule.class);
    protected static ParameterService parameterService;    
    
    protected AccountService accountService;
    protected ContractsAndGrantsModuleService contractsAndGrantsModuleService;

    protected AccountAutoCreateDefaults oldAccountAutoCreateDefaults;
    protected AccountAutoCreateDefaults newAccountAutoCreateDefaults;

    public AccountAutoCreateDefaultsRule() {

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringContext, and configure the bean defs for spring.
         this.setContractsAndGrantsModuleService(SpringContext.getBean(ContractsAndGrantsModuleService.class));
         this.setAccountService(SpringContext.getBean(AccountService.class));
    }

    /**
     * This method sets the convenience objects like newAccountAutoCreateDefaults and oldAccountAutoCreateDefaults, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     */
    public void setupConvenienceObjects() {

        // setup oldAccountAutoCreateDefaults convenience objects, make sure all possible sub-objects are populated
        oldAccountAutoCreateDefaults = (AccountAutoCreateDefaults) super.getOldBo();

        // setup newAccountAutoCreateDefaults convenience objects, make sure all possible sub-objects are populated
        newAccountAutoCreateDefaults = (AccountAutoCreateDefaults) super.getNewBo();
    }

    /**
     * This method calls the route rules but does not fail if any of them fail (this only happens on routing)
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    
    protected AccountParametersDTO generateAccountTestCase() {
        AccountParametersDTO accountParameters = new AccountParametersDTO();
        accountParameters.setAccountName("TestAcctName"); 
        accountParameters.setExpirationDate(new Date());
        accountParameters.setEffectiveDate(new Date());
        accountParameters.setDefaultAddressStreetAddress("123 Easy Way");
        accountParameters.setDefaultAddressCityName("Indianapolis");
        accountParameters.setDefaultAddressStateCode("IN");
        accountParameters.setDefaultAddressZipCode("96818");
        accountParameters.setAdminContactAddressStreetAddress("123 Easy Way");
        accountParameters.setAdminContactAddressCityName("Indianapolis");
        accountParameters.setAdminContactAddressStateCode("IN");
        accountParameters.setAdminContactAddressZipCode("96818");
        accountParameters.setPaymentAddressStreetAddress("123 Easy Way");
        accountParameters.setPaymentAddressCityName("Indianapolis");
        accountParameters.setPaymentAddressStateCode("IN");
        accountParameters.setPaymentAddressZipCode("96818");
        accountParameters.setOffCampusIndicator(false);
        accountParameters.setCfdaNumber("");
        accountParameters.setExpenseGuidelineText("test");
        accountParameters.setIncomeGuidelineText("Test");
        accountParameters.setPurposeText("Test");
        return accountParameters;
    }
    /**
     * This method calls the following rules: checkAccountGuidelinesValidation checkEmptyValues checkGeneralRules checkCloseAccount
     * checkContractsAndGrants checkExpirationDate checkFundGroup checkSubFundGroup checkFiscalOfficerIsValidKualiUser this rule
     * will fail on routing
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;
        
        LOG.info("processCustomRouteDocumentBusinessRules called");
        AccountCreationServiceImpl accountCreationService= SpringContext.getBean(AccountCreationServiceImpl.class);
        
        AccountCreationStatusDTO accountCreationStatus = new AccountCreationStatusDTO();
        
        // create an account object        
        Account account = accountCreationService.createAccountObject(this.generateAccountTestCase(), newAccountAutoCreateDefaults);
        //create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) accountCreationService.createCGAccountMaintenanceDocument(accountCreationStatus);
        
     
        if (ObjectUtils.isNotNull(maintenanceAccountDocument)) {           
            // set document header description...
            maintenanceAccountDocument.getDocumentHeader().setDocumentDescription(ContractsAndGrantsConstants.AccountCreationService.AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION);
                                   
            // set the account object in the maintenance document.         
            maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
            maintenanceAccountDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);   
        }
        
        AccountRule accountRule = new AccountRule();
        success = accountRule.processCustomRouteDocumentBusinessRules(maintenanceAccountDocument);
        
        setupConvenienceObjects();

        success &= checkRequiredKcUnit(newAccountAutoCreateDefaults);
        return success;
    }

    /**
     * This method checks to make sure that the kcUnit field exists and is entered correctly
     * 
     * @param newAccountAutoCreateDefaults
     * @return true/false
     */

    protected boolean checkRequiredKcUnit(AccountAutoCreateDefaults newAccountAutoCreateDefaults) {
        
        boolean result = true;
        try {
            //UnitService unitService = SpringContext.getBean(UnitService.class);
            //ContractsAndGrantsUnit unitDTO = unitService.getUnit(newAccountAutoCreateDefaults.getKcUnit());
            ContractsAndGrantsUnit unitDTO = newAccountAutoCreateDefaults.getUnitDTO();
            unitDTO = (ContractsAndGrantsUnit) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsUnit.class).retrieveExternalizableBusinessObjectIfNecessary(newAccountAutoCreateDefaults, unitDTO, "unitDTO");
            if (unitDTO == null) {
                putFieldError("kcUnit", ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, newAccountAutoCreateDefaults.getKcUnit());                
                result &= false;
            }
            // check if KcUnit exists already in accountAutoCreateDefaults table - if so reject
            AccountAutoCreateDefaults accountAutoCreateDefaults = boService.findBySinglePrimaryKey(AccountAutoCreateDefaults.class, newAccountAutoCreateDefaults.getKcUnit());
            if (accountAutoCreateDefaults != null) {
                putFieldError("kcUnit", ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_ALREADY_DEFINED, newAccountAutoCreateDefaults.getKcUnit());                
                result &= false;
            }

            return result;
        } catch (Exception ex) {
            putFieldError("kcUnit", ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, newAccountAutoCreateDefaults.getKcUnit());
            return false;
        }
    }
    
  
  
    /**
     * Sets the contractsAndGrantsModuleService attribute value.
     * @param contractsAndGrantsModuleService The contractsAndGrantsModuleService to set.
     */
    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }

 

    public ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    public void setAccountService(AccountService accountService) {        
        this.accountService = accountService;
    }

}

