/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation;

import org.kuali.rice.krad.document.TransactionalDocument;

/**
 * Continue Purap Rule Interface
 * Defines a rule which gets invoked immediately before continuing to the next step during creation of a Transactional document.
 */
public interface ContinuePurapRule {
    
    /**
     * Checks the rules that are applicable to the Continue Event on a Transactional Document
     * 
     * @param document the document to check
     * @return true if the business rules pass
     */
    public boolean processContinuePurapBusinessRules(TransactionalDocument document);
}
