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
package org.kuali.module.cams.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.module.cams.CamsConstants;

/**
 * AssetAuthorizer delegates responsibility to specific action (document type) implementations since there are
 * so many for Asset Maintenance Document.
 */
public class AssetAuthorizer implements MaintenanceDocumentAuthorizer {
    private static final Map<String,Class> FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION = new HashMap();
    static {
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_TAG, AssetTagAuthorizer.class);
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_SEPERATE, AssetSeperateAuthorizer.class);
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_PAYMENT, AssetPaymentAuthorizer.class);
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_EDIT, AssetEditAuthorizer.class);
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_RETIREMENT, AssetRetireAuthorizer.class);
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_TRANSFER, AssetTransferAuthorizer.class);
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_LOAN, AssetLoanAuthorizer.class);
        FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.put(CamsConstants.DocumentTypes.ASSET_MERGE, AssetMergeAuthorizer.class);
    }
    private MaintenanceDocumentAuthorizer realAuthorizer;
    
    /**
     * Picks correct authorizer.
     * @param document
     */
    private void initialize(Document document) {
        try {
            realAuthorizer = (MaintenanceDocumentAuthorizer)FINANCIAL_DOCUMENT_TYPE_TO_AUTHORIZER_IMPLEMENTATION.get(document.getDocumentHeader().getFinancialDocumentTypeCode()).newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Caught exception while trying to initialize realMaintainable based on financialDocumentTypeCode: " + document.getDocumentHeader().getFinancialDocumentTypeCode(), e);
        }
    }

    /**
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer#getFieldAuthorizations(org.kuali.core.document.MaintenanceDocument, org.kuali.core.bo.user.UniversalUser)
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        initialize(document);
        return realAuthorizer.getFieldAuthorizations(document, user);
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#canCopy(java.lang.String, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canCopy(String documentTypeName, UniversalUser user) {
        // TODO we may have a problem with this - i don't think we'll know whether they can initiate based on workflow doc type name.  some folks will be able to maintain but not retire an asset.
        // and, the document service calls these methods when creating the new doc - so, we can't make it pass in the request params - it doesn't have them.  ideas from jonathan?
        // what is the issue with the maintainable initializeDocument method simply throwing an authorization exception in the case that a user just makes up a url - cause we're not going to draw the url for them on the 
        // lookup result set, if they can't perform more detailed action - if that's fine, then we can just return true or make this class subclass MaintenanceDocumentAuthorizerBase and defer to it?
        return true;
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.core.bo.user.UniversalUser)
     */
    public void canInitiate(String documentTypeName, UniversalUser user) {
        // TODO see notes on canCopy - same apply here
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#canViewAttachment(java.lang.String, org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean canViewAttachment(String attachmentTypeName, Document document, UniversalUser user) {
        initialize(document);
        return realAuthorizer.canViewAttachment(attachmentTypeName, document, user);
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        initialize(document);
        return realAuthorizer.getDocumentActionFlags(document, user);
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    public Map getEditMode(Document document, UniversalUser user) {
        initialize(document);
        return realAuthorizer.getEditMode(document, user);
    }
}
