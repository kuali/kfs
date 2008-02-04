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
package org.kuali.module.effort.rules;

import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.GenerateSalaryExpenseTransferDocumentRule;
import org.kuali.module.effort.service.EffortCertificationDocumentService;

/**
 * To define the rules that may be applied to the effort certification document, a transactional document
 */
public class EffortCertificationDocumentRules extends TransactionalDocumentRuleBase implements GenerateSalaryExpenseTransferDocumentRule<EffortCertificationDocument> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentRules.class);

    private EffortCertificationDocumentService effortCertificationDocumentService = SpringContext.getBean(EffortCertificationDocumentService.class);

    /**
     * Constructs a EffortCertificationDocumentRules.java.
     */
    public EffortCertificationDocumentRules() {
        super();
    }

    /**
     * @see org.kuali.module.effort.rule.GenerateSalaryExpenseTransferDocumentRule#processGenerateSalaryExpenseTransferDocument(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    public boolean processGenerateSalaryExpenseTransferDocument(EffortCertificationDocument effortCertificationDocument) {
        return effortCertificationDocumentService.generateSalaryExpenseTransferDocument(effortCertificationDocument);
    }
}
