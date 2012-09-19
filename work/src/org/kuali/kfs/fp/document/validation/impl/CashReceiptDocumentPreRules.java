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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.service.AccountingDocumentPreRuleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;

import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;


public class CashReceiptDocumentPreRules extends PromptBeforeValidationBase {
   

    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;
      
        // DTT-3163: Add warning message when account override changed externally
        //MSU Contribution DTT-3163 KFSMI-6747 KFSCNTRB-588
        preRulesOK &= SpringContext.getBean(AccountingDocumentPreRuleService.class).accessAccountOverrideQuestion((AccountingDocumentBase) document, this, this.event);

        return preRulesOK;
    }
}