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
import org.kuali.rice.krad.rule.event.KualiDocumentEvent;

/**
 * Interface for events of Collection Activity Document.
 */
public interface CollectionActivityDocumentEvent extends KualiDocumentEvent {

    /**
     * Gets the Event object.
     * 
     * @return Returns the Event Object.
     */
    public Event getEvent();
}
