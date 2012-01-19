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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.validation.AddReceivingItemRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

public class AddReceivingItemEvent extends KualiDocumentEventBase implements KualiDocumentEvent {

    private LineItemReceivingItem item;
    
    public AddReceivingItemEvent(String errorPathPrefix, Document document, LineItemReceivingItem item) {
        
        super("adding item to document " + getDocumentId(document), errorPathPrefix, document);
        this.item = item;
    }    
    
    public Class getRuleInterfaceClass() {
        return AddReceivingItemRule.class;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {        
        return ((AddReceivingItemRule) rule).processAddReceivingItemRules((ReceivingDocument)document, item, KFSConstants.EMPTY_STRING);
    }

    public ReceivingItem getItem() {
        return item;
    }

    public void setItem(LineItemReceivingItem item) {
        this.item = item;
    }

}
