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

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRule.java
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
=======
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.module.ar.document.validation.AddCollectionActivityDocumentRule;
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRule.java
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rule class for Collection Activity Document.
 */
<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRule.java
public class CollectionActivityDocumentRule extends TransactionalDocumentRuleBase {
=======
public class ContractsGrantsCollectionActivityDocumentRule extends TransactionalDocumentRuleBase implements AddCollectionActivityDocumentRule<ContractsGrantsCollectionActivityDocument> {
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRule.java

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRule.java
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        isValid &= validateCollectionActivityDocument((CollectionActivityDocument) document);
=======
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= this.validateCollectionEvents((ContractsGrantsCollectionActivityDocument) document);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRule.java
        return isValid;
    }

    /**
<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRule.java
=======
     * @see org.kuali.kfs.module.ar.document.validation.AddCollectionActivityDocumentRule#processAddCollectionEventBusinessRules(org.kuali.rice.krad.document.TransactionalDocument, org.kuali.kfs.module.ar.businessobject.CollectionEvent)
     */
    @Override
    public boolean processAddCollectionEventBusinessRules(ContractsGrantsCollectionActivityDocument transactionalDocument, CollectionEvent collectionEvent) {
        boolean isSuccess = true;

        isSuccess &= this.validateCollectionEvent(collectionEvent);
        isSuccess &= this.validateCollectionEvents(transactionalDocument);

        return isSuccess;
    }

    /**
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRule.java
     * Validates the collection activity document collection event list from document.
     *
     * @param collectionActivityDocument The document which contains the list.
     * @return Returns true if all validations succeed otherwise false.
     */
<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRule.java
    public boolean validateCollectionActivityDocument(CollectionActivityDocument collectionActivityDocument) {
=======
    public boolean validateCollectionEvents(ContractsGrantsCollectionActivityDocument contractsGrantsCollectionActivityDocument) {
        List<CollectionEvent> collectionEvents = contractsGrantsCollectionActivityDocument.getCollectionEvents();
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

>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRule.java
        boolean isValid = true;

        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRule.java
        if (ObjectUtils.isNull(collectionActivityDocument.getProposalNumber())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(CollectionActivityDocument.class, KFSPropertyConstants.PROPOSAL_NUMBER));
            isValid = false;
        }

        if (CollectionUtils.isEmpty(collectionActivityDocument.getInvoiceDetails())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CollectionActivityDocumentFields.INVOICE_DETAILS, ArKeyConstants.ERROR_DOCUMENT_COLLECTION_ACTIVITY_NO_INVOICE_SELECTED);
=======
        if (ObjectUtils.isNotNull(collectionEvent.isFollowup()) && collectionEvent.isFollowup() && collectionEvent.getFollowupDate() == null) {
            errorMap.putError(ArPropertyConstants.CollectionEventFields.FOLLOW_UP_DATE, ArKeyConstants.ContractsGrantsCollectionActivityDocumentErrors.ERROR_FOLLOW_UP_DATE_REQUIRED);
            isValid = false;
        }
        if (ObjectUtils.isNotNull(collectionEvent.isCompleted()) && collectionEvent.isCompleted() && collectionEvent.getCompletedDate() == null) {
            errorMap.putError(ArPropertyConstants.CollectionEventFields.COMPLETED_DATE, ArKeyConstants.ContractsGrantsCollectionActivityDocumentErrors.ERROR_COMPLETED_DATE_REQUIRED);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRule.java
            isValid = false;
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;
    }
}
