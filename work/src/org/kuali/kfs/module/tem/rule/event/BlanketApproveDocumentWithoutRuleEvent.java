/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.tem.rule.event;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;

/**
 * This class represents the blanketApprove event that is part of an eDoc in Kuali. This is same as the Blanket approve document
 * event except it does not trigger the RouteDocumentEvent or invoke the rules
 *
 *
 */
public final class BlanketApproveDocumentWithoutRuleEvent extends ApproveDocumentEvent {
    /**
     * Constructs an BlanketApproveDocumentEvent with the specified errorPathPrefix and document
     *
     * @param errorPathPrefix
     * @param document
     */
    public BlanketApproveDocumentWithoutRuleEvent(String errorPathPrefix, Document document) {
        super("blanketApprove", errorPathPrefix, document);
    }

    /**
     * Constructs a BlanketApproveDocumentEvent with the given document
     *
     * @param document
     */
    public BlanketApproveDocumentWithoutRuleEvent(Document document) {
        super("blanketApprove", "", document);
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        //do not invoke rule
        return true;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#generateEvents()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List generateEvents() {
        List events = new ArrayList();
        return events;
    }
}
