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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.validation.AddReceivingItemRule;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.KualiDocumentEventBase;

public class AddReceivingItemEvent extends KualiDocumentEventBase implements KualiDocumentEvent {

    private ReceivingItem item;
    
    public AddReceivingItemEvent(String errorPathPrefix, Document document, ReceivingItem item) {
        super("adding item to document " + getDocumentId(document), errorPathPrefix, document);
    }    
    
    public Class getRuleInterfaceClass() {
        return AddReceivingItemRule.class;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {        
        return ((AddReceivingItemRule) rule).processAddReceivingItemRules((ReceivingDocument)document, item);
    }

    public ReceivingItem getItem() {
        return item;
    }

    public void setItem(ReceivingItem item) {
        this.item = item;
    }

}
