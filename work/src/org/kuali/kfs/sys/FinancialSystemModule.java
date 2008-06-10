/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs;

import org.kuali.core.KualiModule;
import org.kuali.core.authorization.KualiModuleAuthorizer;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiModuleUserService;
import org.kuali.kfs.authorization.FinancialSystemModuleAuthorizer;

/**
 * Slim subclass to enforce class hierarchy not enforced by the parent class' contract.
 */
public class FinancialSystemModule extends KualiModule {

    public FinancialSystemModuleAuthorizer getKfsModuleAuthorizer() {
        return (FinancialSystemModuleAuthorizer)getModuleAuthorizer();
    }
    
    @Override
    public void setModuleAuthorizer(KualiModuleAuthorizer moduleAuthorizer) {
        if ( !(moduleAuthorizer instanceof FinancialSystemModuleAuthorizer) ) {
            throw new IllegalArgumentException( "Module authorizers for KfsModules must implement the FinancialSystemModuleAuthorizer interface.  Was: " + moduleAuthorizer.getClass() );
        }
        super.setModuleAuthorizer(moduleAuthorizer);
    }
    
    @Override
    public void setModuleUserRule(MaintenanceDocumentRuleBase moduleUserRule) {
        throw new UnsupportedOperationException( "Module User properties are not used on FinancialSystemModule instances.");
    }
    
    @Override
    public void setModuleUserPreRules(PreRulesContinuationBase moduleUserPreRules) {
        throw new UnsupportedOperationException( "Module User properties are not used on FinancialSystemModule instances.");
    }
    
    @Override
    public void setModuleUserService(KualiModuleUserService moduleUserService) {
        throw new UnsupportedOperationException( "Module User properties are not used on FinancialSystemModule instances.");
    }
}
