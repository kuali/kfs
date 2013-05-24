/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.document.validation.AddCollectionActivityDocumentRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Event class to define rules while adding new event.
 */
public final class AddCollectionActivityDocumentEvent extends CollectionActivityDocumentEventBase {

    /**
     * Constructor.
     *
     * @param errorPathPrefix Prefix of error file if error occurs.
     * @param document The transaction document.
     * @param event The event object which is getting added.
     */
    public AddCollectionActivityDocumentEvent(String errorPathPrefix, Document document, Event event) {
        super("Adding collection acitivty document detail to document " + getDocumentId(document), errorPathPrefix, document, event);
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
        // TODO Auto-generated method stub
        return false;
    }

}
