/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.document.validation.event;

import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;

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
