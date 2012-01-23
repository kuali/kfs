/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.document.validation.event;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.validation.AddDetailLineRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

public class AddDetailLineEvent extends KualiDocumentEventBase {
    private EffortCertificationDocument effortCertificationDocument;
    private EffortCertificationDetail effortCertificationDetail;

    /**
     * Constructs a AddDetailLineEvent.java.
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public AddDetailLineEvent(String description, String errorPathPrefix, EffortCertificationDocument effortCertificationDocument, EffortCertificationDetail effortCertificationDetail) {
        super(description, errorPathPrefix, effortCertificationDocument);
        this.document = effortCertificationDocument;
        this.effortCertificationDocument = effortCertificationDocument;
        this.effortCertificationDetail = effortCertificationDetail;        
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AddDetailLineRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddDetailLineRule<EffortCertificationDocument, EffortCertificationDetail>) rule).processAddDetailLineRules(effortCertificationDocument, effortCertificationDetail);
    }
}
