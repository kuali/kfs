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
package org.kuali.kfs.module.purap.document.validation.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

/**
 * Event Base class for Purchasing Item Capital Asset
 * 
 * contains the base methods for item events
 */
public abstract class AttributedPurchasingCapitalAssetLocationEventBase extends AttributedDocumentEventBase implements AttributedPurchasingCapitalAssetLocationEvent {
    private static final Logger LOG = Logger.getLogger(AttributedPurchasingCapitalAssetLocationEventBase.class);


    private final CapitalAssetLocation capitalAssetLocation;

    /**
     * Copies the item and calls the super constructor
     * 
     * @param description the description of the event
     * @param errorPathPrefix the error path
     * @param document the document the event is being called on
     * @param location the location that is having the event called on
     */
    public AttributedPurchasingCapitalAssetLocationEventBase(String description, String errorPathPrefix, Document document, CapitalAssetLocation capitalAssetLocation) {
        super(description, errorPathPrefix, document);

        this.capitalAssetLocation = capitalAssetLocation;

        logEvent();
    }

    /**     
     * @see org.kuali.kfs.module.purap.document.validation.event.PurchasingCapitalAssetLocationEvent#getCapitalAssetLocation()
     */
    public CapitalAssetLocation getCapitalAssetLocation() {
        return capitalAssetLocation;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getCapitalAssetLocation() == null) {
            throw new IllegalArgumentException("invalid (null) location");
        }
    }

    /**
     * Logs the event type and some information about the associated location
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (capitalAssetLocation == null) {
            logMessage.append("null capital asset location");
        }
        else {
            logMessage.append(" capital asset location# ");
            logMessage.append(capitalAssetLocation.getCapitalAssetLocationIdentifier());
        }

        LOG.debug(logMessage);
    }
}
