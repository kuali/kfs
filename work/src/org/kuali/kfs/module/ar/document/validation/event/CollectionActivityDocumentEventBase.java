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
package org.kuali.kfs.module.ar.document.validation.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Event class for Collection Activity Document.
 */
public abstract class CollectionActivityDocumentEventBase extends KualiDocumentEventBase implements CollectionActivityDocumentEvent {

    private static final Logger LOG = Logger.getLogger(CollectionActivityDocumentEventBase.class);

    private final CollectionEvent collectionEvent;

    /**
     * Constructor for the class.
     *
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param event
     */
    public CollectionActivityDocumentEventBase(String description, String errorPathPrefix, Document document, CollectionEvent collectionEvent) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.collectionEvent = (CollectionEvent) ObjectUtils.deepCopy(collectionEvent);

        logEvent();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.event.CollectionActivityDetailEvent#getEvent()
     */
    @Override
    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#validate()
     */
    @Override
    public void validate() {
        super.validate();
        if (ObjectUtils.isNull(getCollectionEvent())) {
            throw new IllegalArgumentException("invalid (null) collection event");
        }
    }

    /**
     * Logs the event type and some information about the associated event
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (ObjectUtils.isNull(collectionEvent)) {
            logMessage.append("null collection event");
        }
        else {
            logMessage.append(" collection event# ");
            logMessage.append(collectionEvent.getId());
        }

        LOG.debug(logMessage);
    }
}
