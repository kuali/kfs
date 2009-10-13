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
package org.kuali.kfs.sys.document.validation;

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;


/**
 * A declaration of methods needed to validate an accounting document based on a given event 
 */
public interface AccountingRuleEngineRule {
    
    /**
     * Validates a particular event
     * @param event the event to validate
     * @return true if validation succeeded and the process requiring validation should continue, false
     * if the validation failed and the process should quit
     */
    public abstract boolean validateForEvent(AttributedDocumentEvent event);
}
