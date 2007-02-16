/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.rule.event;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.rule.UpdateCheckRule;

/**
 * This class represents the update check event.
 */
public final class UpdateCheckEvent extends CheckEventBase {
    /**
     * Constructs an UpdateCheckEvent with the given errorPathPrefix, document, and check
     * 
     * @param errorPathPrefix
     * @param document
     * @param check
     */
    public UpdateCheckEvent(String errorPathPrefix, Document document, Check check) {
        super("updating check in document " + getDocumentId(document), errorPathPrefix, document, check);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return UpdateCheckRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        UpdateCheckRule crule = (UpdateCheckRule) rule;

        return crule.processUpdateCheckRule((AccountingDocument) getDocument(), getCheck());
    }
}