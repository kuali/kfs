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
package org.kuali.module.effort.rule.event;

import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.UpdateDetailLineRule;

public class UpdateDetailLineEvent extends KualiDocumentEventBase {
    private EffortCertificationDocument effortCertificationDocument;
    private EffortCertificationDetail effortCertificationDetail;

    /**
     * Constructs a UpdateDetailLineEvent.java.
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public UpdateDetailLineEvent(String description, String errorPathPrefix, EffortCertificationDocument effortCertificationDocument, EffortCertificationDetail effortCertificationDetail) {
        super(description, errorPathPrefix, effortCertificationDocument);
        this.document = effortCertificationDocument;
        this.effortCertificationDocument = effortCertificationDocument;
        this.effortCertificationDetail = effortCertificationDetail;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return UpdateDetailLineRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((UpdateDetailLineRule<EffortCertificationDocument, EffortCertificationDetail>) rule).processUpdateDetailLineRules(effortCertificationDocument, effortCertificationDetail);
    }
}