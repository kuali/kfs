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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.validation.AddCollectionActivityDocumentRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Event class to define rules while adding new collection event.
 */
public final class AddCollectionEventEvent extends CollectionActivityDocumentEventBase {

    /**
     * @param errorPathPrefix Prefix of error file if error occurs.
     * @param document The transaction document.
     * @param event The event object which is getting added.
     */
    public AddCollectionEventEvent(String errorPathPrefix, Document document, CollectionEvent event) {
        super("Adding collection event to collection activity document " + getDocumentId(document), errorPathPrefix, document, event);
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AddCollectionActivityDocumentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddCollectionActivityDocumentRule) rule).processAddCollectionEventBusinessRules((TransactionalDocument) getDocument(), getCollectionEvent());
    }

}
