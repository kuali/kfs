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
package org.kuali.kfs.module.purap.document.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValue;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.util.GlobalVariables;

public class KFSPurapDocumentSearchCustomizer extends KFSDocumentSearchCustomizer {

    /**
     * @see org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer#customizeResults(org.kuali.rice.kew.api.document.search.DocumentSearchCriteria, java.util.List)
     */
    @Override
    public DocumentSearchResultValues customizeResults(DocumentSearchCriteria documentSearchCriteria, List<DocumentSearchResult> defaultResults) {
        // since we know we are looking up POs at this time - add the warning about disclosing them
        GlobalVariables.getMessageMap().putWarning(KFSPropertyConstants.DOCUMENT_NUMBER, PurapConstants.WARNING_PURCHASEORDER_NUMBER_DONT_DISCLOSE);

        org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues.Builder customResultsBuilder = DocumentSearchResultValues.Builder.create();

        List<DocumentSearchResultValue.Builder> customResultValueBuilders = new ArrayList<DocumentSearchResultValue.Builder>();

        boolean isAuthorizedToViewPurapDocId = false;
        if ( defaultResults.size() > 0 ) {
            for (DocumentAttribute documentAttribute : defaultResults.get(0).getDocumentAttributes()) {
                if (KFSPropertyConstants.PURAP_DOC_ID.equals(documentAttribute.getName())) {
                    isAuthorizedToViewPurapDocId = isAuthorizedToViewPurapDocId();
                }
            }
        }
        for (DocumentSearchResult result : defaultResults) {
            List<DocumentAttribute.AbstractBuilder<?>> custAttrBuilders = new ArrayList<DocumentAttribute.AbstractBuilder<?>>();
            Document document = result.getDocument();
            
            for (DocumentAttribute documentAttribute : result.getDocumentAttributes()) {
                if (KFSPropertyConstants.PURAP_DOC_ID.equals(documentAttribute.getName())) {
                    if (!isAuthorizedToViewPurapDocId && !document.getStatus().getCategory().equals(DocumentStatusCategory.SUCCESSFUL) ) {
                        DocumentAttributeString.Builder builder = DocumentAttributeString.Builder.create(KFSPropertyConstants.PURAP_DOC_ID);
                        builder.setValue("********");
                        custAttrBuilders.add(builder);
                        break;
                    }
                }
            }
            DocumentSearchResultValue.Builder builder = DocumentSearchResultValue.Builder.create(document.getDocumentId());
            builder.setDocumentAttributes(custAttrBuilders);
            customResultValueBuilders.add(builder);
        }
        customResultsBuilder.setResultValues(customResultValueBuilders);

        return customResultsBuilder.build();
    }

    @Override
    public boolean isCustomizeResultsEnabled(String documentTypeName) {
        // do not mask the purapDocumentIdentifier field if the document is not PO or POSP..
        if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT.equalsIgnoreCase(documentTypeName)
                || PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT.equalsIgnoreCase(documentTypeName)) {
            return true;
        }
        return super.isCustomizeResultsEnabled(documentTypeName);
    }

    protected boolean isAuthorizedToViewPurapDocId() {
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        String namespaceCode = KFSConstants.CoreModuleNamespaces.KNS;
        String permissionTemplateName = KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD;

        Map<String, String> roleQualifiers = new HashMap<String, String>();

        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, KFSPropertyConstants.PURCHASE_ORDER_DOCUMENT_SIMPLE_NAME);
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, KFSPropertyConstants.PURAP_DOC_ID);

        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);
        return isAuthorized;
    }

}
