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
package org.kuali.module.purap.rules;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rules.DocumentRuleBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.rule.ContinuePurapRule;
import org.kuali.module.purap.service.ReceivingService;

public class ReceivingLineDocumentRule extends DocumentRuleBase implements ContinuePurapRule {

    public boolean processContinuePurapBusinessRules(TransactionalDocument document) {
        
        boolean valid = false;
        ReceivingLineDocument receivingLineDocument = (ReceivingLineDocument)document;
        
        valid &= canCreateReceivingLineDocument(receivingLineDocument);
        
        return valid;
    }
    
    private boolean canCreateReceivingLineDocument(ReceivingLineDocument receivingLineDocument){
        
        return SpringContext.getBean(ReceivingService.class).canCreateReceivingLineDocument(receivingLineDocument.getPurchaseOrderIdentifier());
    }

}
