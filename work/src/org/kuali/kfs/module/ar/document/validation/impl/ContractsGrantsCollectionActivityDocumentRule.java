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
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rule class for Collection Activity Document.
 */
public class ContractsGrantsCollectionActivityDocumentRule extends TransactionalDocumentRuleBase {
    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        isValid &= validateCollectionActivityDocument((ContractsGrantsCollectionActivityDocument) document);
        return isValid;
    }

    /**
     * Validates the collection activity document collection event list from document.
     *
     * @param collectionActivityDocument The document which contains the list.
     * @return Returns true if all validations succeed otherwise false.
     */
    public boolean validateCollectionActivityDocument(ContractsGrantsCollectionActivityDocument collectionActivityDocument) {
       boolean isValid = true;

        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

        if (ObjectUtils.isNull(collectionActivityDocument.getProposalNumber())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(ContractsGrantsCollectionActivityDocument.class, KFSPropertyConstants.PROPOSAL_NUMBER));
            isValid = false;
        }

        if (CollectionUtils.isEmpty(collectionActivityDocument.getInvoiceDetails())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsCollectionActivityDocumentFields.INVOICE_DETAILS, ArKeyConstants.ERROR_DOCUMENT_COLLECTION_ACTIVITY_NO_INVOICE_SELECTED);
            isValid = false;
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;
    }
}
