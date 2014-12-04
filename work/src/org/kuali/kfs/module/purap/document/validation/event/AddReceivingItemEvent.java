/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
