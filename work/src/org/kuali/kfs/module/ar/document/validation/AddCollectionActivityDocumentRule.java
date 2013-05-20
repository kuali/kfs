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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.rice.krad.document.TransactionalDocument;

/**
 * Interface for business rule of Collection Activity Document Detail.
 */
public interface AddCollectionActivityDocumentRule<F extends TransactionalDocument> extends CollectionActivityDocumentRule {

    /**
     * This method is called when a event is added
     * 
     * @param transactionalDocument the event document
     * @param event the event to be added
     * @return true if valid to be added, false otherwise
     */
    public boolean processAddCollectionActivityDocumentEventBusinessRules(F transactionalDocument, Event event);
}
