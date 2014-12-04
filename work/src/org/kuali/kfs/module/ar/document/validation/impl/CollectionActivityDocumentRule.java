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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.validation.AddCollectionActivityDocumentRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rule class for Collection Activity Document.
 */
public class CollectionActivityDocumentRule extends TransactionalDocumentRuleBase implements AddCollectionActivityDocumentRule<CollectionActivityDocument> {

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= this.validateCollectionEvents((CollectionActivityDocument) document);
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.AddCollectionActivityDocumentRule#processAddCollectionEventBusinessRules(org.kuali.rice.krad.document.TransactionalDocument, org.kuali.kfs.module.ar.businessobject.CollectionEvent)
     */
    @Override
    public boolean processAddCollectionEventBusinessRules(CollectionActivityDocument transactionalDocument, CollectionEvent collectionEvent) {
        boolean isSuccess = true;

        isSuccess &= this.validateCollectionEvent(collectionEvent);
        isSuccess &= this.validateCollectionEvents(transactionalDocument);

        return isSuccess;
    }

    /**
     * Validates the collection activity document collection event list from document.
     *
     * @param collectionActivityDocument The document which contains the list.
     * @return Returns true if all validations succeed otherwise false.
     */
    public boolean validateCollectionEvents(CollectionActivityDocument collectionActivityDocument) {
        List<CollectionEvent> collectionEvents = collectionActivityDocument.getCollectionEvents();
        boolean isSuccess = true;
        if (CollectionUtils.isNotEmpty(collectionEvents)) {
            for (CollectionEvent collectionEvent : collectionEvents) {
                isSuccess &= this.validateCollectionEvent(collectionEvent);
            }
        }
        return isSuccess;
    }

    /**
     * Validates a single collection activity document collection event object.
     *
     * @param collectionEvent The object to get validated.
     * @return Returns true if all validations succeed otherwise false.
     */
    public boolean validateCollectionEvent(CollectionEvent collectionEvent) {
        MessageMap errorMap = GlobalVariables.getMessageMap();

        boolean isValid = true;

        int originalErrorCount = errorMap.getErrorCount();
        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(collectionEvent);
        isValid = (errorMap.getErrorCount() == originalErrorCount);

        if (ObjectUtils.isNotNull(collectionEvent.isFollowup()) && collectionEvent.isFollowup() && collectionEvent.getFollowupDate() == null) {
            errorMap.putError(ArPropertyConstants.CollectionEventFields.FOLLOW_UP_DATE, ArKeyConstants.CollectionActivityDocumentErrors.ERROR_FOLLOW_UP_DATE_REQUIRED);
            isValid = false;
        }
        if (ObjectUtils.isNotNull(collectionEvent.isCompleted()) && collectionEvent.isCompleted() && collectionEvent.getCompletedDate() == null) {
            errorMap.putError(ArPropertyConstants.CollectionEventFields.COMPLETED_DATE, ArKeyConstants.CollectionActivityDocumentErrors.ERROR_COMPLETED_DATE_REQUIRED);
            isValid = false;
        }
        return isValid;
    }
}
