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
    
        public static final String ERROR_ACCOUNT_PARAMS_UNIT_NOTFOUND = "error.account.params.unit.notfound";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.unableToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_PROCESS_ROUTING = "error.kc.document.unableToProcessRouting";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "error.kc.document.workflowException.document.actions";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_CREATE_DOCUMENT = "error.kc.document.workflowException.unableToCreateDocument";        
        
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Account Document Creation";
    }    
    
}
