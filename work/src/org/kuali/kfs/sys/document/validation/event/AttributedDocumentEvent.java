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
package org.kuali.kfs.rule.event;

import org.kuali.core.rule.event.KualiDocumentEvent;

/**
 * An interface that allows rules to add attribues to an event as it is processed.
 */
public interface AttributedDocumentEvent extends KualiDocumentEvent {
    /**
     * Retrieves a named attribute from the event.
     * @param attributeName the name of the attribute to retrieve
     * @return the attribute's value, or null if no attribute with the given name was found.
     */
    public abstract Object getAttribute(String attributeName);
    /**
     * Sets the value of a named attribute on the event.
     * @param attributeName the name of the attribute to set
     * @param attributeValue the value of the named attribute
     */
    public abstract void setAttribute(String attributeName, Object attributeValue);
    
    /**
     * This sets an iteration subject in the case of this event going through a CollectionValidation
     * @param iterationSubject the current subject of the CollectionValidation's iteration through a collection
     */
    public abstract void setIterationSubject(Object iterationSubject);
    
    /**
     * If event is going through a collection validation, this will return the current subject of the iteration through the collection
     * @return a subject to a CollectionValidation's iteration through a collection
     */
    public abstract Object getIterationSubject();
}
