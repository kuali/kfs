/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.service;

import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.rice.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;

/**
 * This service interface defines methods that a DisbursementVoucherService implementation must provide.
 */

public interface AccountingDocumentPreRuleService {

    /**
     * This method helps to Access the account override question for all accounting document.
     * 
     * @param accountingLine
     * @param infix
     * @return
     */
    boolean expiredAccountOverrideQuestion(AccountingDocumentBase document, PromptBeforeValidationBase preRule, PromptBeforeValidationEvent event);
}
