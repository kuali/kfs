/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc;

public class KcConstants {
    
    public static final String MODULE_TARGET_NAMESPACE = "KFS";
    private KcConstants() {}
    
    public static class AccountCreationService {
        
        public static final String WEB_SERVICE_NAME = "accountCreationService";
                
        public static final String PARAMETER_KC_ACCOUNT_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION = "RESEARCH_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION";  
        public static final String PARAMETER_KC_ACCOUNT_ADDRESS_TYPE = "ACCOUNT_ADDRESS_TYPE";
        public static final String PARAMETER_KC_ACCOUNT_CREATE_ROUTE = "ACCOUNT_AUTO_CREATE_ROUTE";
        
        public static final String ADMIN_ADDRESS_TYTPE = "ADMIN";
        public static final String PAYMENT_ADDRESS_TYTPE = "PAYMENT";
        public static final String DEFAULT_ADDRESS_TYTPE = "DEFAULT";
    
        public static final String STATUS_KC_ACCOUNT_SUCCESS = "success";
        public static final String STATUS_KC_ACCOUNT_FAILURE = "failure";
        public static final String STATUS_KC_ACCOUNT_WARNING = "warning";
        
        public static final String ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND = "error.kc.account.params.unit.notfound";
        public static final String ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.notAllowedToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.unableToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_PROCESS_ROUTING = "error.kc.document.unableToProcessRouting";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "error.kc.document.workflowException.document.actions";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_CREATE_DOCUMENT = "error.kc.document.workflowException.unableToCreateDocument";        
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_SAVE_DOCUMENT = "error.kc.document.workflowException.unableToSaveDocument";
        public static final String ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE = "error.kc.document.system.parameter.incorrect.document.action.value";
        
        public static final String WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "warning.kc.document.workflowException.document.actions";
        
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Account Document Creation";
    }    

    public static class BudgetAdjustmentService {
        
        public static final String WEB_SERVICE_NAME = "budgetAdjustmentService";
        public static final String PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE = "RESEARCH_ADMIN_BA_DOCUMENT_ROUTE_ACTION ";   
        public static final String PARAMETER_KC_ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND = "ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND";
        public static final String SECTION_ID_RESEARCH_ADMIN_ATTRIBUTES = "researchAdminAttributes";
         
        public static final String STATUS_KC_BA_SUCCESS = "success";
        public static final String STATUS_KC_ACCOUNT_FAILURE = "failure";
        public static final String STATUS_KC_ACCOUNT_WARNING = "warning";
        
        public static final String ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND = "error.kc.account.params.unit.notfound";
        public static final String ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.notAllowedToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.unableToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_PROCESS_ROUTING = "error.kc.document.unableToProcessRouting";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "error.kc.document.workflowException.document.actions";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_NOT_SAVED = "error.kc.document.workflowException.document.not.saved";          
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_CREATE_DOCUMENT = "error.kc.document.workflowException.unableToCreateDocument";        
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_SAVE_DOCUMENT = "error.kc.document.workflowException.unableToSaveDocument";
        public static final String ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE = "error.kc.document.system.parameter.incorrect.document.action.value";
        
        public static final String WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "warning.kc.document.workflowException.document.actions";
        
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic BA Document Creation";
    }    

}
