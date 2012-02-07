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
package org.kuali.kfs.sys.document.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString.Builder;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCustomizerBase;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultSetConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.util.GlobalVariables;

public class KFSDocumentSearchCustomizer extends DocumentSearchCustomizerBase {

    @Override
    public DocumentSearchCriteria customizeCriteria(DocumentSearchCriteria documentSearchCriteria) {
        return null;
    }

    @Override
    public DocumentSearchCriteria customizeClearCriteria(DocumentSearchCriteria documentSearchCriteria) {
        return null;
    }

    @Override
    public DocumentSearchResultValues customizeResults(DocumentSearchCriteria documentSearchCriteria, List<DocumentSearchResult> defaultResults) {

        // do not mask the purapDocumentIdentifier field if the document is not PO or POSP..
        if (!PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT.equalsIgnoreCase(documentSearchCriteria.getDocumentTypeName()) && !PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT.equalsIgnoreCase(documentSearchCriteria.getDocumentTypeName())) {
            return null;
        }

        DocumentAttribute purapDocIdAttr = null;
        for (DocumentSearchResult result : defaultResults) {
            List<DocumentAttribute> docAttrs = result.getDocumentAttributes();
            for (DocumentAttribute documentAttribute : docAttrs) {
                if (KFSPropertyConstants.PURAP_DOC_ID.equals(documentAttribute.getName())) {
                    purapDocIdAttr = documentAttribute;
                    break;
                }
            }

            if (purapDocIdAttr != null) {
                // KFSMI-4576 masking PO number...
                List<DocumentStatus> documentStatuses = documentSearchCriteria.getDocumentStatuses();
                if (!documentStatuses.contains(KewApiConstants.ROUTE_HEADER_FINAL_CD)) {
                    // if document status is not FINAL then check for permission to see
                    // the value needs to be masked....
                    String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
                    String namespaceCode = KFSConstants.ParameterNamespaces.KNS;
                    String permissionTemplateName = KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD;

                    Map<String, String> roleQualifiers = new HashMap<String, String>();

                    Map<String, String> permissionDetails = new HashMap<String, String>();
                    permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, KFSPropertyConstants.PURCHASE_ORDER_DOCUMENT_SIMPLE_NAME);
                    permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, KFSPropertyConstants.PURAP_DOC_ID);

                    IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
                    Boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);
                    // the principalId is not authorized to view the value in purapDocumentIdentifier field...so mask the value...
                    if (!isAuthorized) {
                        Builder builder = DocumentAttributeString.Builder.create(KFSPropertyConstants.PURAP_DOC_ID);
                        builder.setValue("********");
                        // not authorized to see... create a string
                        purapDocIdAttr = builder.build();
                    }
                }
            }
        }

        return null;
    }

    @Override
    public DocumentSearchResultSetConfiguration customizeResultSetConfiguration(DocumentSearchCriteria documentSearchCriteria) {
        return null;
    }

    @Override
    public boolean isCustomizeCriteriaEnabled(String documentTypeName) {
        return false;
    }

    @Override
    public boolean isCustomizeClearCriteriaEnabled(String documentTypeName) {
        return false;
    }

    @Override
    public boolean isCustomizeResultsEnabled(String documentTypeName) {
        return true;
    }

    @Override
    public boolean isCustomizeResultSetFieldsEnabled(String documentTypeName) {
        return false;
    }


}
