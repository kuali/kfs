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
