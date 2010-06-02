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
package org.kuali.kfs.sys.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;

public class AccountRelatedMaintenanceDocumentPresentationControllerBase extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> props = new HashSet<String>();
        
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            
        }
        
        return props;
    }
}
